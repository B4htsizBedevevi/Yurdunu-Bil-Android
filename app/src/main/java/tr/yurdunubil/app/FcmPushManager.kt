package tr.yurdunubil.app

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FcmPushManager {
    private const val TAG = "YBFcm"
    private const val PREFS = "yurdunu_bil_native"
    private const val PENDING_TOKEN = "pending_fcm_token"

    fun sync(context: Context) {
        runCatching {
            if (FirebaseApp.getApps(context).isEmpty()) return
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) registerToken(context, task.result)
                else Log.w(TAG, "FCM token alınamadı", task.exception)
            }
        }.onFailure { Log.w(TAG, "FCM henüz yapılandırılmamış", it) }
    }

    fun registerToken(context: Context, token: String?) {
        if (token.isNullOrBlank()) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(PENDING_TOKEN, token).apply()
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val client = SupabaseClientProvider.client
                val user = client.auth.currentUserOrNull() ?: return@runCatching
                client.postgrest.from("notification_devices").upsert(
                    mapOf(
                        "user_id" to user.id,
                        "token" to token,
                        "platform" to "android",
                        "active" to true,
                        "last_seen_at" to java.time.Instant.now().toString(),
                        "updated_at" to java.time.Instant.now().toString()
                    ),
                    onConflict = "user_id,token"
                )
                prefs.edit().remove(PENDING_TOKEN).apply()
                Log.d(TAG, "FCM cihazı Supabase'e kaydedildi")
            }.onFailure { Log.w(TAG, "FCM cihaz kaydı başarısız", it) }
        }
    }
}
