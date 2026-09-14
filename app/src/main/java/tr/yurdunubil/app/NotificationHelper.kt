package tr.yurdunubil.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.util.Calendar

object NotificationHelper {
    @kotlinx.serialization.Serializable
    private data class NotificationDeviceRow(val id: Long, val user_id: String, val token: String)

    const val CHANNEL_ID = "study_reminders"
    const val ANNOUNCEMENT_CHANNEL_ID = "announcements"
    private const val DAILY_REQUEST = 4811
    const val DAILY_NOTIFICATION_ID = 4814
    private const val PREFS = "yurdunu_bil_native"
    private const val NOTIFICATIONS_ENABLED = "notifications_enabled"

    data class DailyTemplate(val title: String, val body: String)
    private val dailyTemplates = listOf(
        DailyTemplate("Bugün beraber çalışalım 👋", "10 soru bile yeter. Hadi küçük bir tur atalım, serini de canlı tutalım."),
        DailyTemplate("Biraz daha Yurdunu Bil?", "Kütüphaneden bir konu seçelim, kısa bir tekrar yapıp hemen deneyelim."),
        DailyTemplate("Haritaya bir göz atalım 🗺️", "Bir il ya da bölge seç. Bakalım aklında neler kalmış?"),
        DailyTemplate("Seriyi bırakmayalım 🔥", "Bugün birkaç soru çözelim. Dün verdiğin emeği boşa bırakmayalım."),
        DailyTemplate("10 dakikalık mini tur?", "Kısa bir test açalım. Sen çöz, biz de birlikte nedenlerini öğrenelim."),
        DailyTemplate("Bugünün harita molası", "Haritadan bir il seç. Bakalım o bölge hakkında neleri hatırlıyorsun?"),
        DailyTemplate("Arena seni bekliyor ⚔️", "Hazırsan bir maça girelim. Bakalım bugün kim daha hızlı?"),
        DailyTemplate("Bir adım daha 💚", "Bugün küçücük bir çalışma bile hedefe doğru ilerlemek demek."),
        DailyTemplate("Bilgini yokla!", "Bugün kaç soruyu doğru yapabileceğini görelim."),
        DailyTemplate("Tekrar günü!", "Yanlış yaptığın soruların konusunu yeniden gözden geçirmek için iyi bir gün."),
        DailyTemplate("Bir il, bir bilgi", "Haritadan bir il seçip coğrafi özelliklerine göz at."),
        DailyTemplate("Seri bozulmasın!", "Dünkü emeğin bugün de devam etsin. Birkaç soru yeter."),
        DailyTemplate("Kendine meydan oku", "Rastgele sorularla kısa bir Türkiye coğrafyası turuna çık."),
        DailyTemplate("Kütüphanede yeni bir tur zamanı", "Bir konu aç, bağlantıları keşfet ve öğrendiklerini sorularla pekiştir."),
        DailyTemplate("Hız + bilgi = Arena", "Kısa sürede doğru cevap ver. Rating'ini yükseltme zamanı."),
        DailyTemplate("Buradayız 😄", "Birkaç soru çözelim. Ritmi birlikte koruyalım."),
        DailyTemplate("Bugünün 10 sorusu hazır!", "Sadece birkaç dakikanı ayır. Sonuçlarını hemen gör."),
        DailyTemplate("Türkiye haritasında kaybolma", "Bugün bir bölge seç ve önemli yer şekillerini tekrar et."),
        DailyTemplate("Bir konu daha tamamla", "Kütüphanede küçük bir hedef belirle ve bugün onu bitir."),
        DailyTemplate("Rakiplerini bekletme!", "Bugün Türkiye coğrafyasında kim daha hızlı? Arena'ya gir."),
        DailyTemplate("Bugün neye takıldık? 👀", "Zayıf olduğun bir konu seçelim; önce öğrenelim, sonra birlikte test edelim."),
        DailyTemplate("Hedef uzak değil.", "Her çözdüğün soru seni biraz daha ileri götürüyor. Bugün de devam."),
        DailyTemplate("Bugünün küçük zaferini seçelim 🎯", "10 soru mu, bir konu mu, Arena mı? Hadi birinden başlayalım."),
        DailyTemplate("Düzen, bilgiden güçlüdür.", "Her gün biraz çalış. Sınav günü farkı sen göreceksin."),
        DailyTemplate("Bugün kendin için çalış.", "Kimse senin yerine öğrenemez. Küçük bir testle başla."),
        DailyTemplate("10 dakikalık mola değil, 10 soruluk tur!", "Telefonu bırakmadan önce 10 coğrafya sorusunu tamamla."),
        DailyTemplate("Takıldığın konuya dönelim", "Zorlandığın başlığı bir kez daha çalışalım. Bu sefer oturtacağız. 💪"),
        DailyTemplate("Türkiye Ustası olmak kolay değil", "Bugün bir Arena maçıyla kendini sınamaya ne dersin?")
    )

    fun ensureChannel(context: Context) {
        runCatching {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Çalışma Hatırlatmaları", NotificationManager.IMPORTANCE_DEFAULT))
            if (manager.getNotificationChannel(ANNOUNCEMENT_CHANNEL_ID) == null) manager.createNotificationChannel(NotificationChannel(ANNOUNCEMENT_CHANNEL_ID, "Duyurular", NotificationManager.IMPORTANCE_HIGH).apply { description = "Yurdunu Bil önemli duyuruları"; enableVibration(true); setShowBadge(true) })
        }
    }

    fun canNotify(context: Context): Boolean = runCatching { android.os.Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED }.getOrDefault(false)
    fun isEnabled(context: Context): Boolean = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(NOTIFICATIONS_ENABLED, false)

    suspend fun registerCurrentToken(context: Context? = null) = withContext(Dispatchers.IO) {
        runCatching {
            val token = Tasks.await(FirebaseMessaging.getInstance().token)
            registerTokenForUser(token, context)
        }
    }

    suspend fun registerTokenForUser(token: String, context: Context? = null) = withContext(Dispatchers.IO) {
        runCatching {
            if (context != null && (!isEnabled(context) || !canNotify(context))) return@runCatching
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()
            if (user == null) {
                context?.let { savePendingToken(it, token) }
                return@runCatching
            }

            val client = SupabaseClientProvider.client
            val existing = client.postgrest.from("notification_devices").select {
                filter {
                    eq("user_id", user.id)
                    eq("token", token)
                }
                limit(1)
            }.decodeSingleOrNull<NotificationDeviceRow>()

            val now = java.time.Instant.now().toString()
            if (existing != null) {
                client.postgrest.from("notification_devices").update(
                    mapOf("active" to true, "last_seen_at" to now, "updated_at" to now)
                ) {
                    filter { eq("id", existing.id) }
                }
            } else {
                client.postgrest.from("notification_devices").insert(buildJsonObject {
                    put("user_id", JsonPrimitive(user.id))
                    put("token", JsonPrimitive(token))
                    put("platform", JsonPrimitive("android"))
                    put("active", JsonPrimitive(true))
                })
            }

            context?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()
                ?.remove("pending_fcm_token")?.apply()
        }
    }

    fun savePendingToken(context: Context, token: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("pending_fcm_token", token).apply()
    }

    suspend fun deactivateCurrentToken() = withContext(Dispatchers.IO) {
        runCatching {
            val user = SupabaseClientProvider.client.auth.currentUserOrNull() ?: return@runCatching
            val token = Tasks.await(FirebaseMessaging.getInstance().token)
            SupabaseClientProvider.client.postgrest.from("notification_devices").update(
                mapOf(
                    "active" to false,
                    "last_seen_at" to java.time.Instant.now().toString(),
                    "updated_at" to java.time.Instant.now().toString()
                )
            ) {
                filter {
                    eq("user_id", user.id)
                    eq("token", token)
                }
            }
        }
    }

    fun showAnnouncement(
        context: Context,
        title: String,
        body: String,
        notificationId: Int = (System.currentTimeMillis() and 0x7fffffff).toInt(),
        action: String = "social",
        channelId: String = ANNOUNCEMENT_CHANNEL_ID
    ) {
        runCatching {
            if (!canNotify(context) || !isEnabled(context)) return
            ensureChannel(context)
            val pendingIntent = PendingIntent.getActivity(
                context,
                4820 + Math.floorMod(action.hashCode(), 1000),
                Intent(context, SocialCenterActivity::class.java).apply { putExtra("notification_action", action) },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Builder(context, channelId)
                // Android requires status-bar icons to be monochrome. This is the branded Yurdunu Bil mark,
                // not a generic bell/drawable.
                .setSmallIcon(R.drawable.ic_stat_notification_bell)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.yurdunu_bil_app_icon))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(if (channelId == ANNOUNCEMENT_CHANNEL_ID) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
                .also {
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(notificationId, it)
                }
        }
    }

    fun sendTest(context: Context) {
        if (isEnabled(context) && canNotify(context)) {
            showAnnouncement(
                context,
                "Yurdunu Bil hazır!",
                "Bildirim sistemi çalışıyor.",
                notificationId = 4999,
                action = "test",
                channelId = ANNOUNCEMENT_CHANNEL_ID
            )
        }
    }
    fun scheduleDaily(context: Context) {
        runCatching {
            if (!isEnabled(context) || !canNotify(context)) {
                cancelDaily(context)
                return
            }
            ensureChannel(context)
            NotificationAutomation.scheduleCached(context)
        }
    }

    fun cancelDaily(context: Context) {
        runCatching {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pending = PendingIntent.getBroadcast(
                context,
                DAILY_REQUEST,
                Intent(context, DailyReminderReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarm.cancel(pending)
            NotificationAutomation.cancelCached(context)
        }
    }
    fun todayTemplate(): DailyTemplate { val c=Calendar.getInstance(); return dailyTemplates[Math.floorMod(c.get(Calendar.YEAR)*37+c.get(Calendar.DAY_OF_YEAR)*17,dailyTemplates.size)] }
}

class DailyReminderReceiver : BroadcastReceiver() { override fun onReceive(context: Context, intent: Intent?) { runCatching { if(!NotificationHelper.isEnabled(context)||!NotificationHelper.canNotify(context)){NotificationHelper.cancelDaily(context);return};NotificationHelper.showAnnouncement(context,NotificationHelper.todayTemplate().title,NotificationHelper.todayTemplate().body) } } }
class NotificationBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        runCatching {
            val action = intent?.action
            if (action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_TIME_CHANGED ||
                action == Intent.ACTION_TIMEZONE_CHANGED) {
                if (NotificationHelper.isEnabled(context) && NotificationHelper.canNotify(context)) {
                    NotificationHelper.ensureChannel(context)
                    NotificationAutomation.scheduleCached(context)
                }
            }
        }
    }
}
