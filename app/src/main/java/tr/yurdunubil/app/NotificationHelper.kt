package tr.yurdunubil.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

object NotificationHelper {
    const val CHANNEL_ID = "study_reminders"
    private const val DAILY_REQUEST = 4811
    const val DAILY_NOTIFICATION_ID = 4814
    private const val PREFS = "yurdunu_bil_native"
    private const val NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val SMALL_ICON = tr.yurdunubil.app.R.drawable.ic_stat_yurdunu_bil

    data class DailyTemplate(val title: String, val body: String)

    private val dailyTemplates = listOf(
        DailyTemplate("Bugünün Coğrafya Görevi", "Bugün 10 soru çöz. Küçük bir çalışma bile serini canlı tutar."),
        DailyTemplate("Türkiye'yi biraz daha tanı", "Kütüphaneden bir konu seç, kısa bir tekrar yap ve kendini test et."),
        DailyTemplate("Haritayı aç", "Türkiye'nin bölgelerini, dağlarını ve ovalarını gözden geçir."),
        DailyTemplate("Serini koru!", "Bugünkü çalışmanı tamamla ve çalışma serini bir gün daha uzat."),
        DailyTemplate("Mini test zamanı", "Kendini sınamanın en hızlı yolu: 10 soru. Hazırsan başlayalım."),
        DailyTemplate("Bugünün harita molası", "Haritadan bir il seç. Bakalım o bölge hakkında neleri hatırlıyorsun?"),
        DailyTemplate("Arena hazır", "Bilgini rakiplere karşı denemek için bir Arena maçı başlat."),
        DailyTemplate("Bir gün daha, bir adım daha.", "Hedefine yaklaşmak için bugün de Türkiye coğrafyasına dokun."),
        DailyTemplate("Bilgini yokla!", "Bugün kaç soruyu doğru yapabileceğini görelim."),
        DailyTemplate("Tekrar günü!", "Yanlış yaptığın soruların konusunu yeniden gözden geçirmek için iyi bir gün."),
        DailyTemplate("Bir il, bir bilgi", "Haritadan bir il seçip coğrafi özelliklerine göz at."),
        DailyTemplate("Seri bozulmasın!", "Dünkü emeğin bugün de devam etsin. Birkaç soru yeter."),
        DailyTemplate("Kendine meydan oku", "Rastgele sorularla kısa bir Türkiye coğrafyası turuna çık."),
        DailyTemplate("Kütüphanede yeni bir tur zamanı", "Bir konu aç, bağlantıları keşfet ve öğrendiklerini sorularla pekiştir."),
        DailyTemplate("Hız + bilgi = Arena", "Kısa sürede doğru cevap ver. Rating'ini yükseltme zamanı."),
        DailyTemplate("Bugün de buradayız.", "Kısa bir test çöz, ritmini kaybetme."),
        DailyTemplate("Bugünün 10 sorusu hazır!", "Sadece birkaç dakikanı ayır. Sonuçlarını hemen gör."),
        DailyTemplate("Türkiye haritasında kaybolma", "Bugün bir bölge seç ve önemli yer şekillerini tekrar et."),
        DailyTemplate("Bir konu daha tamamla", "Kütüphanede küçük bir hedef belirle ve bugün onu bitir."),
        DailyTemplate("Rakiplerini bekletme!", "Bugün Türkiye coğrafyasında kim daha hızlı? Arena'ya gir."),
        DailyTemplate("Bugün ne çalışıyoruz?", "Bir konu seç, kısa tekrarını yap ve hemen ardından kendini test et."),
        DailyTemplate("Hedef uzak değil.", "Her çözdüğün soru seni biraz daha ileri götürüyor. Bugün de devam."),
        DailyTemplate("Bugünkü küçük zaferin ne?", "10 soru çözmek bile ilerlemedir. Hadi başlayalım."),
        DailyTemplate("Düzen, bilgiden güçlüdür.", "Her gün biraz çalış. Sınav günü farkı sen göreceksin."),
        DailyTemplate("Bugün kendin için çalış.", "Kimse senin yerine öğrenemez. Küçük bir testle başla."),
        DailyTemplate("10 dakikalık mola değil, 10 soruluk tur!", "Telefonu bırakmadan önce 10 coğrafya sorusunu tamamla."),
        DailyTemplate("Konu seni bekliyor", "Daha önce zorlandığın bir başlığa geri dön ve bilgini tazele."),
        DailyTemplate("Türkiye Ustası olmak kolay değil", "Bugün bir Arena maçıyla kendini sınamaya ne dersin?")
    )

    fun ensureChannel(context: Context) {
        runCatching {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Çalışma Hatırlatmaları", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Yurdunu Bil günlük çalışma ve ilerleme hatırlatmaları"
                })
            }
        }
    }

    fun canNotify(context: Context): Boolean = runCatching {
        android.os.Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

    fun isEnabled(context: Context): Boolean = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(NOTIFICATIONS_ENABLED, false)

    fun sendNow(context: Context, title: String, body: String, openSocial: Boolean = true) {
        runCatching {
            if (!canNotify(context)) return
            ensureChannel(context)
            val intent = if (openSocial) Intent(context, SocialModernActivity::class.java) else Intent(context, RetentionMainActivity::class.java)
            val requestCode = (System.currentTimeMillis() and 0x7fffffff).toInt()
            val pending = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(SMALL_ICON)
                .setContentTitle(title.take(80))
                .setContentText(body.take(220))
                .setStyle(NotificationCompat.BigTextStyle().bigText(body.take(220)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .build()
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(requestCode, notification)
        }
    }

    fun sendTest(context: Context) = sendNow(context, "Yurdunu Bil hazır!", "Bildirim sistemi çalışıyor. Bildirime dokunarak uygulamaya dönebilirsin.")

    fun scheduleDaily(context: Context) {
        runCatching {
            if (!isEnabled(context) || !canNotify(context)) { cancelDaily(context); return }
            ensureChannel(context)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pending = PendingIntent.getBroadcast(context, DAILY_REQUEST, Intent(context, DailyReminderReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val now = Calendar.getInstance()
            val first = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 20); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
            }
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, first.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
        }
    }

    fun cancelDaily(context: Context) {
        runCatching {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pending = PendingIntent.getBroadcast(context, DAILY_REQUEST, Intent(context, DailyReminderReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarm.cancel(pending)
        }
    }

    fun todayTemplate(): DailyTemplate {
        val calendar = Calendar.getInstance(); val year = calendar.get(Calendar.YEAR); val day = calendar.get(Calendar.DAY_OF_YEAR)
        return dailyTemplates[Math.floorMod(year * 37 + day * 17, dailyTemplates.size)]
    }
}

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        runCatching {
            if (!NotificationHelper.isEnabled(context) || !NotificationHelper.canNotify(context)) { NotificationHelper.cancelDaily(context); return }
            NotificationHelper.ensureChannel(context)
            val template = NotificationHelper.todayTemplate()
            val pending = PendingIntent.getActivity(context, 4815, Intent(context, SocialModernActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(NotificationHelper.SMALL_ICON).setContentTitle(template.title).setContentText(template.body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(template.body)).setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true).setContentIntent(pending).build()
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NotificationHelper.DAILY_NOTIFICATION_ID, notification)
        }
    }
}

class NotificationBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        runCatching { if (intent?.action == Intent.ACTION_BOOT_COMPLETED && NotificationHelper.isEnabled(context) && NotificationHelper.canNotify(context)) NotificationHelper.scheduleDaily(context) }
    }
}
