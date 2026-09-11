package tr.yurdunubil.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class YurdunuBilFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        NotificationHelper.savePendingToken(this, token)
        scope.launch {
            NotificationHelper.registerTokenForUser(token, this@YurdunuBilFirebaseMessagingService)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Yurdunu Bil"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Yeni bir duyurun var."
        val action = message.data["action"] ?: "social"

        NotificationHelper.showAnnouncement(
            context = this,
            title = title,
            body = body,
            notificationId = (System.currentTimeMillis() and 0x7fffffff).toInt(),
            action = action
        )
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
