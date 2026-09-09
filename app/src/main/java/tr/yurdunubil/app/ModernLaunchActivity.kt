package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay

/** Production launcher with crash-safe session routing. */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE)
        setContent {
            LaunchExperience(
                signedInHint = prefs.getBoolean("signed_in", false),
                onSessionInvalid = { prefs.edit().putBoolean("signed_in", false).apply() },
                onMain = {
                    startActivity(Intent(this, RetentionMainActivity::class.java))
                    finish()
                },
                onRegister = { openAuth(true) },
                onLogin = { openAuth(false) }
            )
        }
    }

    private fun openAuth(register: Boolean) {
        startActivity(Intent(this, ModernAuthActivity::class.java).putExtra("register", register))
    }
}

@androidx.compose.runtime.Composable
private fun LaunchExperience(
    signedInHint: Boolean,
    onSessionInvalid: () -> Unit,
    onMain: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    var splashDone by remember { mutableStateOf(false) }
    var sessionChecked by remember { mutableStateOf(false) }
    var hasSession by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1150)
        hasSession = runCatching {
            io.github.jan.supabase.auth.auth.currentSessionOrNull()
        }.getOrNull() != null
        if (!hasSession && signedInHint) onSessionInvalid()
        sessionChecked = true
        splashDone = true
        if (hasSession) onMain()
    }

    when {
        !splashDone -> Text("YURDUNU BİL")
        sessionChecked && !hasSession -> Text("YURDUNU BİL")
    }
}
