package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * The old local-test auth screen could never create a Supabase session.
 * Keep this activity as a compatibility entry point, but route users to the
 * real Supabase Auth flow implemented by LaunchActivity.
 */
class ModernAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LaunchActivity::class.java).apply {
            putExtra("register", intent.getBooleanExtra("register", false))
        })
        finish()
    }
}
