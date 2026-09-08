package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import kotlinx.coroutines.delay

class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        setContent { LaunchGate(onReady = ::openApp) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
    }

    private fun openApp() {
        startActivity(Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

@Composable
private fun LaunchGate(onReady: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var session by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ProfileGate?>(null) }
    var confirmationLink by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(180)
        val currentIntent = (androidx.compose.ui.platform.LocalContext.current as? android.content.Context)
        val current = SupabaseClientProvider.client.auth.currentSessionOrNull()
        session = current != null
        confirmationLink = false
        if (current != null) {
            profile = loadAuthProfile()
            if (profile?.onboarding_completed == true) onReady()
        }
        loading = false
    }

    Box(Modifier.fillMaxSize()) {
        when {
            loading -> BrandedAuthLoading()
            confirmationLink && session -> EmailConfirmationScreen(onContinue = onReady, onBackToLogin = onReady)
            !session -> BrandedAuthScreen(onAuthenticated = onReady)
            profile?.onboarding_completed != true -> BrandedProfileOnboarding(existing = profile, onComplete = onReady)
            else -> onReady()
        }
    }
}
