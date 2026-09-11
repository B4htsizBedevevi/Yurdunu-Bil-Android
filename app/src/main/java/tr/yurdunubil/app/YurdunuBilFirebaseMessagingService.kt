package tr.yurdunubil.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class YurdunuBilFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch { registerToken(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Foreground messages are not shown by FCM automatically; display them ourselves.
        val title = message.notification?.title ?: message.data["title"] ?: "Yurdunu Bil"
        val body = message.notification?.body ?: message.data["body"] ?: "Yeni bir duyurun var."
        NotificationHelper.showAnnouncement(this, title, body)
    }

    private suspend fun registerToken(token: String) {
        runCatching {
            val client = SupabaseClientProvider.client
            val user = client.auth.currentUserOrNull() ?: return
            // Keep one active row per token. Old rows are harmless and allow multi-device accounts.
            client.postgrest.from("notification_devices").insert(
                buildJsonObject {
                    put("user_id", JsonPrimitive(user.id))
                    put("token", JsonPrimitive(token))
                    put("platform", JsonPrimitive("android"))
                    put("active", JsonPrimitive(true))
                }
            )
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
