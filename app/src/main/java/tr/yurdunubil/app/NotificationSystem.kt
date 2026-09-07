package tr.yurdunubil.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object YurdunuBilNotifications {
    const val CHANNEL_DAILY = "daily_study"
    const val CHANNEL_ARENA = "arena_events"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(CHANNEL_DAILY, "Günlük çalışma", NotificationManager.IMPORTANCE_DEFAULT))
        manager.createNotificationChannel(NotificationChannel(CHANNEL_ARENA, "Arena etkinlikleri", NotificationManager.IMPORTANCE_HIGH))
    }

    fun scheduleDaily(context: Context, hour: Int = 20, minute: Int = 0) {
        ensureChannels(context)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyStudyReceiver::class.java)
        val pending = PendingIntent.getBroadcast(context, 2026, intent, PendingIntent.FLAG_UPDATE_CURRENT or immutableFlag())
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, next.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
    }

    private fun immutableFlag(): Int = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0
}

class DailyStudyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        YurdunuBilNotifications.ensureChannels(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) return
        val notification = android.app.Notification.Builder(context, YurdunuBilNotifications.CHANNEL_DAILY)
            .setSmallIcon(tr.yurdunubil.app.R.drawable.ic_yurdunu_bil)
            .setContentTitle("Yurdunu Bil • Günlük görev")
            .setContentText("Bugün 10 soru çöz, serini koru ve XP kazan.")
            .setAutoCancel(true)
            .build()
        manager.notify(2026, notification)
    }
}
