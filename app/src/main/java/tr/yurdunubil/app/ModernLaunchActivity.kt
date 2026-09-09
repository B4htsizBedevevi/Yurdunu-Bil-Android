package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

/**
 * Isolation launcher: uses only a plain Compose handoff to the production main Activity.
 * Supabase/auth/launch UI are intentionally bypassed during runtime diagnosis.
 */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("YURDUNU BİL")
        }
        startActivity(Intent(this, RetentionMainActivity::class.java))
        finish()
    }
}
