package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Runtime isolation entry point: V2 is rendered directly in the launcher activity.
 * This removes the extra Activity handoff while keeping Supabase/auth out of the test.
 */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YurdunuBilMainV2App()
        }
    }
}
