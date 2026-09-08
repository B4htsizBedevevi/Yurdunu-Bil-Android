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

/** Single launcher entry. Auth and main content now share one activity. */
class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        setContent { RootApp() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        setContent { RootApp() }
    }
}

@Composable
private fun RootApp() {
    var showApp by remember { mutableStateOf(false) }
    if (showApp) {
        NextGenerationApp()
    } else {
        LaunchGate(onReady = { showApp = true })
    }
}

@Composable
private fun LaunchGate(onReady: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var session by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ProfileGate?>(null) }

    LaunchedEffect(Unit) {
        delay(180)
        val current = runCatching {
            SupabaseClientProvider.client.auth.currentSessionOrNull()
        }.getOrNull()
        session = current != null
        if (current != null) profile = loadAuthProfile()
        loading = false
    }

    LaunchedEffect(loading, session, profile?.onboarding_completed) {
        if (!loading && session && profile?.onboarding_completed == true) onReady()
    }

    Box(Modifier.fillMaxSize()) {
        when {
            loading -> BrandedAuthLoading()
            !session -> BrandedAuthScreen(onAuthenticated = onReady)
            profile?.onboarding_completed != true -> BrandedProfileOnboarding(
                existing = profile,
                onComplete = onReady
            )
            else -> BrandedAuthLoading()
        }
    }
}

private suspend fun loadAuthProfile(): ProfileGate? = withContext(Dispatchers.IO) {
    runCatching {
        val user = SupabaseClientProvider.client.auth.currentSessionOrNull()?.user
            ?: return@runCatching null
        SupabaseClientProvider.client.postgrest["profiles"].select {
            filter { eq("id", user.id) }
        }.decodeList<ProfileGate>().firstOrNull()
    }.getOrNull()
}
