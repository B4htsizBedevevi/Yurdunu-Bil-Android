package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Auth/launch flow enters here after the existing secure PKCE callback.
 * The product itself is intentionally geography-only and is rendered natively
 * with Kotlin + Jetpack Compose; no WebView is used.
 */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GeographyNativeV3App() }
    }
}
