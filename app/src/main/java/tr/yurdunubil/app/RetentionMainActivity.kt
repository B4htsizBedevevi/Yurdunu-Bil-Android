package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

/** Native Kotlin + Jetpack Compose main experience. */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keep Compose responsible for safe insets on Android versions enforcing edge-to-edge.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { FourTabMainApp() }
    }
}
