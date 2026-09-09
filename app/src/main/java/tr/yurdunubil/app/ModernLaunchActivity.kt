package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * Diagnostic launcher: bypasses Supabase, splash Compose, notifications, and auth routing.
 * It opens the production main activity directly so runtime failures can be isolated.
 */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, RetentionMainActivity::class.java))
        finish()
    }
}
