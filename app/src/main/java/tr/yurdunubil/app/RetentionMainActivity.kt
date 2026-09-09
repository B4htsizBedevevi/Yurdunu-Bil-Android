package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

/**
 * Stable production entry point.
 *
 * V4 is intentionally kept in the source tree while the startup crash is isolated.
 * The known-stable V2 screen is used here so a failed experimental composition cannot
 * terminate the whole application immediately after the splash screen.
 */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { YurdunuBilMainV2() }
    }
}
