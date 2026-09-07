package tr.yurdunubil.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

object YurdunuBilNotifications {
    const val CHANNEL_DAILY = "daily_study"
    const val CHANNEL_ARENA = "arena_events"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(CHANNEL_DAILY, "Günlük çalışma", NotificationManager.IMPORTANCE_DEFAULT))
        manager.createNotificationChannel(NotificationChannel(CHANNEL_ARENA, "Arena etkinlikleri", NotificationManager.IMPORTANCE_HIGH))
    }

    fun scheduleDaily(context: Context, hour: Int = 20, minute: Int = 0) =
        StudyNotificationScheduler.schedule(context, hour, minute)

    fun cancelDaily(context: Context) = StudyNotificationScheduler.cancel(context)
}

class DailyStudyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        YurdunuBilNotifications.ensureChannels(context)
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val launch = PendingIntent.getActivity(
            context, 2026,
            Intent(context, LaunchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(context, YurdunuBilNotifications.CHANNEL_DAILY)
            .setSmallIcon(R.drawable.ic_yurdunu_bil)
            .setContentTitle("Yurdunu Bil • Günlük görev")
            .setContentText("Bugün 10 soru çöz, serini koru ve XP kazan.")
            .setContentIntent(launch)
            .setAutoCancel(true)
            .build()
        manager.notify(2026, notification)
    }
}
