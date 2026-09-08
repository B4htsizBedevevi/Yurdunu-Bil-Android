package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Minimal, deterministic launcher.
 *
 * The previous launcher performed Supabase/session initialization before the
 * first frame. That made an external service or a corrupted session capable of
 * killing the process before the user could even see the app. Startup now
 * renders the branded auth surface first; network/session work only happens
 * after the user explicitly continues.
 */
class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrandedAuthScreen(onAuthenticated = {
                // Main navigation is entered only after an explicit successful auth action.
                setContent { NextGenerationApp() }
            })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Do not recreate the Compose tree or initialize Supabase from an intent callback.
    }
}
