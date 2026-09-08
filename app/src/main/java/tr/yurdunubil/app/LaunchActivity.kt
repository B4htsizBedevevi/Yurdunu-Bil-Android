package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isAuthDeepLink = intent?.data?.scheme == "yurdunubil" && intent?.data?.host == "auth"
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        setContent { LaunchGate(onReady = ::openApp, showConfirmation = isAuthDeepLink) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        val isAuthDeepLink = intent.data?.scheme == "yurdunubil" && intent.data?.host == "auth"
        setContent { LaunchGate(onReady = ::openApp, showConfirmation = isAuthDeepLink) }
    }

    private fun openApp() {
        startActivity(Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

@Composable
private fun LaunchGate(onReady: () -> Unit, showConfirmation: Boolean) {
    var loading by remember { mutableStateOf(true) }
    var session by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ProfileGate?>(null) }
    var authCheckFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(250)
        val current = runCatching {
            SupabaseClientProvider.client.auth.currentSessionOrNull()
        }.getOrNull()
        session = current != null
        if (current != null) {
            profile = loadAuthProfile()
        } else if (authCheckFailed) {
            authCheckFailed = true
        }
        loading = false
    }

    // Navigation is deliberately performed from an effect, never directly from
    // the composition. This prevents repeated startActivity() calls during
    // recomposition, which can cause the launcher activity to open/close in a loop.
    LaunchedEffect(loading, session, profile?.onboarding_completed, showConfirmation) {
        if (!loading && session && !showConfirmation && profile?.onboarding_completed == true) {
            onReady()
        }
    }

    Box(Modifier.fillMaxSize()) {
        when {
            loading -> BrandedAuthLoading()
            showConfirmation && session -> EmailConfirmationScreen(onContinue = onReady, onBackToLogin = onReady)
            !session -> BrandedAuthScreen(onAuthenticated = onReady)
            profile?.onboarding_completed != true -> BrandedProfileOnboarding(existing = profile, onComplete = onReady)
            else -> BrandedAuthLoading()
        }
    }
}

private suspend fun loadAuthProfile(): ProfileGate? = withContext(Dispatchers.IO) {
    runCatching {
        val user = SupabaseClientProvider.client.auth.currentSessionOrNull()?.user ?: return@runCatching null
        SupabaseClientProvider.client.postgrest["profiles"].select {
            filter { eq("id", user.id) }
        }.decodeList<ProfileGate>().firstOrNull()
    }.getOrNull()
}
