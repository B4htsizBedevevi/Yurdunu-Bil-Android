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
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
                val now = java.time.Instant.now().toString()
                val device = buildJsonObject {
                    put("user_id", user.id)
                    put("token", token)
                    put("platform", "android")
                    put("active", true)
                    put("last_seen_at", now)
                    put("updated_at", now)
                }
                client.postgrest.from("notification_devices").upsert(
                    buildJsonArray { add(device) }
                ) {
                    onConflict = "user_id,token"
                }
                prefs.edit().remove(PENDING_TOKEN).apply()
                Log.d(TAG, "FCM cihazı Supabase'e kaydedildi")
            }.onFailure { Log.w(TAG, "FCM cihaz kaydı başarısız", it) }
        }
    }
}
