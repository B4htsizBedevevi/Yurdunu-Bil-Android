package tr.yurdunubil.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class YurdunuBilFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        FcmPushManager.registerToken(applicationContext, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Yurdunu Bil"
        val body = message.notification?.body ?: message.data["body"] ?: "Yeni bir duyuru var."
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "announcements"
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager.getNotificationChannel(channelId) == null) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Duyurular", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Yurdunu Bil duyuru ve önemli bildirimleri"
                }
            )
        }
        val intent = Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            this,
            (System.currentTimeMillis() and 0x7fffffff).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_yurdunu_bil)
            .setColor(ContextCompat.getColor(this, R.color.notification_accent))
            .setContentTitle(title.take(80))
            .setContentText(body.take(220))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body.take(220)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        manager.notify((System.currentTimeMillis() and 0x7fffffff).toInt(), notification)
    }
}
