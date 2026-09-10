package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Compatibility auth entry point.
 *
 * Auth storage is initialized asynchronously by supabase-kt. Wait for that
 * initialization before deciding that the user is signed out; otherwise every
 * cold start can incorrectly show the login screen.
 */
class ModernAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val auth = SupabaseClientProvider.client.auth
            auth.awaitInitialization()
            val hasSession = auth.currentSessionOrNull() != null

            val next = if (hasSession) {
                Intent(this@ModernAuthActivity, RetentionMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            } else {
                Intent(this@ModernAuthActivity, LaunchActivity::class.java).apply {
                    putExtra("register", intent.getBooleanExtra("register", false))
                }
            }

            startActivity(next)
            finish()
        }
    }
}
