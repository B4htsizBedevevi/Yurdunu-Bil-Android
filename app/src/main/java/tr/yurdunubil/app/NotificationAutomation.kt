package tr.yurdunubil.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

object NotificationAutomation {
    private const val PREFS = "yurdunu_bil_native"
    private const val AUTOMATIONS_CACHE = "notification_automations_cache_v2"
    private const val TEMPLATES_CACHE = "notification_template_cache_v2"
    private const val IDS_CACHE = "notification_automation_ids"
    private const val REQUEST_OFFSET = 61000

    @Serializable
    data class AutomationRow(
        val id: Long,
        val key: String,
        val title: String,
        val category: String,
        val time_local: String,
        val active: Boolean = true
    )

    @Serializable
    data class TemplateRow(
        val id: Long,
        val category: String,
        val title: String,
        val body: String,
        val action: String = "home",
        val active: Boolean = true,
        val weight: Int = 1
    )

    suspend fun syncAndSchedule(context: Context) = withContext(Dispatchers.IO) {
        runCatching {
            val client = SupabaseClientProvider.client
            val automations = client.postgrest.from("notification_automations").select {
                filter { eq("active", true) }
            }.decodeList<AutomationRow>()
            val templates = client.postgrest.from("notification_templates").select {
                filter { eq("active", true) }
            }.decodeList<TemplateRow>()

            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            NotificationHelper.cancelDaily(context)

            prefs.edit()
                .putString(
                    AUTOMATIONS_CACHE,
                    JSONArray().apply {
                        automations.forEach { put(JSONObject().apply {
                            put("id", it.id)
                            put("key", it.key)
                            put("title", it.title)
                            put("category", it.category)
                            put("time_local", it.time_local)
                            put("active", it.active)
                        }) }
                    }.toString()
                )
                .putString(
                    TEMPLATES_CACHE,
                    JSONArray().apply {
                        templates.forEach { put(JSONObject().apply {
                            put("id", it.id)
                            put("category", it.category)
                            put("title", it.title)
                            put("body", it.body)
                            put("action", it.action)
                            put("weight", it.weight)
                        }) }
                    }.toString()
                )
                .putString(
                    IDS_CACHE,
                    JSONArray().apply { automations.forEach { put(it.id) } }.toString()
                )
                .apply()

            if (NotificationHelper.isEnabled(context) && NotificationHelper.canNotify(context)) {
                scheduleCached(context)
            }
        }
    }

    fun scheduleCached(context: Context) {
        if (!NotificationHelper.isEnabled(context) || !NotificationHelper.canNotify(context)) return
        NotificationHelper.ensureChannel(context)

        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(AUTOMATIONS_CACHE, "[]") ?: "[]"
        val array = runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            if (!item.optBoolean("active", true)) continue

            val parts = item.optString("time_local").split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: continue
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val id = item.optLong("id")
            val first = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (!after(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
            }

            alarm.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                first.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent(context, requestCode(id), id)
            )
        }
    }

    fun cancelCached(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(IDS_CACHE, "[]") ?: "[]"
        runCatching {
            val ids = JSONArray(raw)
            for (i in 0 until ids.length()) {
                val id = ids.getLong(i)
                alarm.cancel(pendingIntent(context, requestCode(id), id))
            }
        }
    }

    fun fire(context: Context, automationId: Long) {
        if (!NotificationHelper.isEnabled(context) || !NotificationHelper.canNotify(context)) return

        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val automation = runCatching {
            val array = JSONArray(prefs.getString(AUTOMATIONS_CACHE, "[]") ?: "[]")
            (0 until array.length())
                .map { array.getJSONObject(it) }
                .firstOrNull { it.optLong("id") == automationId }
        }.getOrNull() ?: return

        val category = automation.optString("category", "motivation")
        val template = pickTemplate(context, category) ?: NotificationHelperFallback.forCategory(category)

        NotificationHelper.showAnnouncement(
            context = context,
            title = template.title,
            body = template.body,
            notificationId = 70000 + requestCode(automationId) % 10000,
            action = "automation:$category"
        )
    }

    private fun pickTemplate(context: Context, category: String): NotificationHelperFallback.Template? {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(TEMPLATES_CACHE, "[]") ?: "[]"
        val list = mutableListOf<JSONObject>()
        runCatching {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                if (item.optString("category").equals(category, true)) list += item
            }
        }
        if (list.isEmpty()) return null

        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val item = list[Math.floorMod(day * 31 + category.hashCode(), list.size)]
        return NotificationHelperFallback.Template(item.optString("title"), item.optString("body"))
    }

    private fun requestCode(id: Long): Int =
        REQUEST_OFFSET + (Math.floorMod(id, 5000)).toInt()

    private fun pendingIntent(context: Context, requestCode: Int, id: Long): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, NotificationAutomationReceiver::class.java).apply {
                putExtra("automation_id", id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}

class NotificationAutomationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val id = intent?.getLongExtra("automation_id", -1L) ?: -1L
        if (id > 0L) NotificationAutomation.fire(context, id)
    }
}

class NotificationAutomationSystemReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        runCatching {
            when (intent?.action) {
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED -> NotificationAutomation.scheduleCached(context)
            }
        }
    }
}

private object NotificationHelperFallback {
    data class Template(val title: String, val body: String)

    fun forCategory(category: String): Template = when (category) {
        "study" -> Template("Bugünün coğrafya görevi", "Bugün 10 soru çöz. Küçük bir çalışma bile serini canlı tutar.")
        "quiz" -> Template("Mini test zamanı", "Kendini sınamanın en hızlı yolu: 10 soru. Hazırsan başlayalım.")
        "geography" -> Template("Haritayı aç", "Türkiye'nin bölgelerini, dağlarını ve ovalarını gözden geçir.")
        "arena" -> Template("Arena hazır", "Bilgini rakiplere karşı denemek için bir Arena maçı başlat.")
        "streak" -> Template("Serini koru!", "Bugünkü çalışmanı tamamla ve çalışma serini bir gün daha uzat.")
        "content" -> Template("Kütüphanede yeni tur", "Bir konu aç, kısa tekrar yap ve öğrendiklerini sorularla pekiştir.")
        else -> Template("Bugün de buradayız.", "Kısa bir test çöz, ritmini kaybetme.")
    }
}
