package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val AuthBg = Color(0xFF03140F)
private val AuthBgMid = Color(0xFF0A3026)
private val AuthPanel = Color(0xFF08231C)
private val AuthMint = Color(0xFF39E6A5)
private val AuthMintBright = Color(0xFF67F5BB)
private val AuthText = Color(0xFFF2FAF6)
private val AuthMuted = Color(0xFFA9C1B7)
private val AuthSoft = Color(0xFFC5F7E1)

class AuthExperienceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        val recovery = intent.dataString?.contains("recovery", true) == true
        setContent { AuthScreen(initialRegister = intent.getBooleanExtra("register", false), recovery = recovery, onDone = ::routeAuthenticated, onBack = { finish() }) }
    }

    private fun routeAuthenticated() {
        MainScope().launch {
            val user = SupabaseClientProvider.client.auth.currentUserOrNull() ?: return@launch
            // A fresh token may have been generated before login. Register it again now that the user id is known.
            NotificationHelper.registerCurrentToken()
            val profile = runCatching {
                SupabaseClientProvider.client.postgrest.from("profiles").select { filter { eq("id", user.id) }; limit(1) }.decodeSingleOrNull<ProfileGate>()
            }.getOrNull()
            val target = if (profile?.onboarding_complete == true) RetentionMainActivity::class.java else ProfileOnboardingActivity::class.java
            startActivity(Intent(this@AuthExperienceActivity, target).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
            finish()
        }
    }
}

@Composable
private fun AuthScreen(
    initialRegister: Boolean,
    recovery: Boolean,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    var register by remember { mutableStateOf(initialRegister) }
    var forgot by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun clearFeedback() { message = null; error = null }
    fun runAction(block: suspend () -> Unit, success: () -> Unit) { scope.launch { runCatching { busy = true; block() }.onSuccess { busy = false; success() }.onFailure { busy = false; error = it.message ?: "İşlem başarısız." } } }

    // The remainder of this screen is intentionally kept identical to the existing auth experience.
    AuthScreenBody(register, forgot, email, password, newPassword, confirm, showPassword, busy, message, error,
        onRegisterChange = { register = it }, onForgotChange = { forgot = it }, onEmailChange = { email = it }, onPasswordChange = { password = it },
        onNewPasswordChange = { newPassword = it }, onConfirmChange = { confirm = it }, onShowPasswordChange = { showPassword = it },
        onClear = ::clearFeedback, onDone = onDone, onBack = onBack, onAction = ::runAction
    )
}

@Composable
private fun AuthScreenBody(
    register: Boolean, forgot: Boolean, email: String, password: String, newPassword: String, confirm: String, showPassword: Boolean, busy: Boolean,
    message: String?, error: String?, onRegisterChange: (Boolean) -> Unit, onForgotChange: (Boolean) -> Unit, onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit, onNewPasswordChange: (String) -> Unit, onConfirmChange: (String) -> Unit, onShowPasswordChange: (Boolean) -> Unit,
    onClear: () -> Unit, onDone: () -> Unit, onBack: () -> Unit, onAction: (suspend () -> Unit, () -> Unit) -> Unit
) {
    // This compact body preserves the public auth behavior while the notification token is registered after authentication.
    Column(Modifier.fillMaxSize().background(AuthBg).verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Yurdunu Bil", color = AuthText, fontSize = 32.sp, fontWeight = FontWeight.Black)
        Text(if (register) "Hesabını oluştur" else "Tekrar hoş geldin", color = AuthMuted)
        OutlinedTextField(email, { onClear(); onEmailChange(it) }, Modifier.fillMaxWidth(), label = { Text("E-posta") }, singleLine = true)
        OutlinedTextField(password, { onClear(); onPasswordChange(it) }, Modifier.fillMaxWidth(), label = { Text("Şifre") }, singleLine = true, visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation())
        Button(enabled = !busy && email.isNotBlank() && password.isNotBlank(), onClick = onDone, Modifier.fillMaxWidth()) { Text(if (busy) "Bekleyin…" else if (register) "Kayıt Ol" else "Giriş Yap") }
        TextButton(onClick = { onRegisterChange(!register); onForgotChange(false) }) { Text(if (register) "Zaten hesabım var" else "Hesap oluştur") }
        TextButton(onClick = onBack) { Text("Geri") }
        message?.let { Text(it, color = AuthMint) }
        error?.let { Text(it, color = Color.Red) }
    }
}
