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
        setContent {
            AuthScreen(
                initialRegister = intent.getBooleanExtra("register", false),
                recovery = recovery,
                onDone = ::routeAuthenticated,
                onBack = { finish() }
            )
        }
    }

    private fun routeAuthenticated() {
        MainScope().launch {
            val user = SupabaseClientProvider.client.auth.currentUserOrNull() ?: return@launch
            val profile = runCatching {
                SupabaseClientProvider.client.postgrest.from("profiles").select {
                    filter { eq("id", user.id) }
                    limit(1)
                }.decodeSingleOrNull<ProfileGate>()
            }.getOrNull()

            val target = if (profile?.onboarding_complete == true) {
                RetentionMainActivity::class.java
            } else {
                ProfileOnboardingActivity::class.java
            }

            startActivity(
                Intent(this@AuthExperienceActivity, target).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
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

    fun clearFeedback() {
        message = null
        error = null
    }

    fun runAction(block: suspend () -> Unit, success: () -> Unit) {
        scope.launch {
            busy = true
            clearFeedback()
            runCatching { block() }
                .onSuccess { success() }
                .onFailure { error = friendlyAuthError(it) }
            busy = false
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AuthBg, AuthBgMid, AuthBg)))
    ) {
        AuthAmbientBackground()

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) {
                    Icon(YBIcons.Back, "Geri", tint = AuthText)
                    Spacer(Modifier.width(4.dp))
                    Text("Geri", color = AuthText, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(2.dp))
            AuthBrandHero(register)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = AuthPanel.copy(alpha = .94f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = .075f))
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    if (recovery) {
                        AuthHeader("Şifeni yeniden belirle", "Yeni bir şifre seç, sonra kaldığın yerden devam et.")
                        Spacer(Modifier.height(18.dp))
                        PasswordField("Yeni şifre", newPassword, { newPassword = it; clearFeedback() }, showPassword) {
                            showPassword = !showPassword
                        }
                        Spacer(Modifier.height(10.dp))
                        PasswordField("Yeni şifre tekrar", confirm, { confirm = it; clearFeedback() }, showPassword) {
                            showPassword = !showPassword
                        }
                        Spacer(Modifier.height(15.dp))
                        AuthButton("Şifreyi güncelle", busy) {
                            runAction({
                                require(newPassword.length >= 6) { "Şifre en az 6 karakter olmalı." }
                                require(newPassword == confirm) { "Şifreler aynı olmalı." }
                                SupabaseClientProvider.client.auth.updateUser { password = newPassword }
                            }) {
                                message = "Şifren güncellendi. Giriş ekranına dönebilirsin."
                            }
                        }
                    } else if (forgot) {
                        AuthHeader("Şifreni mi unuttun?", "Hiç sorun değil. E-postanı yaz, bağlantıyı gönderelim.")
                        Spacer(Modifier.height(18.dp))
                        EmailField(email) { email = it; clearFeedback() }
                        Spacer(Modifier.height(14.dp))
                        AuthButton("Bana bağlantıyı gönder", busy) {
                            runAction({
                                require(email.contains("@")) { "Geçerli bir e-posta adresi gir." }
                                SupabaseClientProvider.client.auth.resetPasswordForEmail(
                                    email.trim(),
                                    redirectUrl = "yurdunubil://auth"
                                )
                            }) {
                                message = "Bağlantı gönderildi. Gelen kutunu kontrol et."
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = { forgot = false; clearFeedback() }) {
                            Text("Girişe geri dön", color = AuthMint, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth()
                                .background(Color.White.copy(alpha = .045f), RoundedCornerShape(17.dp))
                                .padding(4.dp)
                        ) {
                            AuthTab("Giriş Yap", !register, Modifier.weight(1f)) {
                                register = false
                                clearFeedback()
                            }
                            AuthTab("Yeni Hesap", register, Modifier.weight(1f)) {
                                register = true
                                clearFeedback()
                            }
                        }

                        Spacer(Modifier.height(18.dp))
                        AuthHeader(
                            if (register) "Aramıza hoş geldin 👋" else "Tekrar hoş geldin 👋",
                            if (register) "Hesabını açalım. Sonra kullanıcı adını ve sana yakışan avatarı seçersin."
                            else "Kaldığın yerden devam edelim. Türkiye'nin dört bir yanında keşfedilecek çok şey var."
                        )
                        Spacer(Modifier.height(17.dp))

                        EmailField(email) { email = it; clearFeedback() }
                        Spacer(Modifier.height(10.dp))
                        PasswordField("Şifre", password, { password = it; clearFeedback() }, showPassword) {
                            showPassword = !showPassword
                        }

                        if (!register) {
                            TextButton(
                                onClick = { forgot = true; clearFeedback() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Şifremi unuttum", color = AuthMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (register) {
                            Spacer(Modifier.height(10.dp))
                            OnboardingHint()
                        }

                        Spacer(Modifier.height(12.dp))
                        AuthButton(if (register) "Hesabımı oluştur" else "Hadi başlayalım", busy) {
                            runAction({
                                require(email.contains("@")) { "Geçerli bir e-posta adresi gir." }
                                require(password.length >= 6) { "Şifre en az 6 karakter olmalı." }

                                if (register) {
                                    SupabaseClientProvider.client.auth.signUpWith(Email) {
                                        this.email = email.trim()
                                        this.password = password
                                    }
                                } else {
                                    SupabaseClientProvider.client.auth.signInWith(Email) {
                                        this.email = email.trim()
                                        this.password = password
                                    }
                                }
                            }) {
                                if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) {
                                    onDone()
                                } else if (register) {
                                    message = "Hesabın hazır. E-postanı doğruladıktan sonra giriş yap; profilini birlikte tamamlayacağız."
                                } else {
                                    error = "Giriş tamamlanamadı. Bir kez daha deneyelim."
                                }
                            }
                        }
                    }

                    if (message != null) {
                        Spacer(Modifier.height(11.dp))
                        FeedbackCard(message!!, positive = true)
                    }
                    if (error != null) {
                        Spacer(Modifier.height(11.dp))
                        FeedbackCard(error!!, positive = false)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                if (register) "Hesabını aç → avatarını seç → ilk keşfine çık"
                else "Bir soru çöz, bir il öğren, biraz daha ilerle."
                    ,
                color = AuthMuted,
                fontSize = 10.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun AuthBrandHero(register: Boolean) {
    val transition = rememberInfiniteTransition(label = "yb-auth-logo")
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val floatY by transition.animateFloat(
        initialValue = -3f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "float"
    )
    val orbit by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            tween(3200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "orbit"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(118.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(108.dp)
                    .scale(pulse)
                    .alpha(.10f)
                    .background(
                        Brush.radialGradient(listOf(AuthMintBright, Color.Transparent)),
                        RoundedCornerShape(50)
                    )
            )
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .rotate(orbit),
                shape = RoundedCornerShape(32.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, AuthMint.copy(alpha = .20f))
            ) {}
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = AuthMintBright.copy(alpha = .72f),
                modifier = Modifier.size(13.dp).offset(x = 47.dp, y = (-27).dp).alpha(.85f)
            )
            Surface(
                modifier = Modifier
                    .size(82.dp)
                    .graphicsLayer { translationY = floatY },
                shape = RoundedCornerShape(27.dp),
                color = Color(0xFF0D3C2E),
                border = BorderStroke(1.5.dp, AuthMint.copy(alpha = .62f)),
                shadowElevation = 18.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(62.dp)
                            .alpha(.16f)
                            .background(AuthMint, RoundedCornerShape(21.dp))
                    )
                    Icon(
                        YBIcons.Map,
                        contentDescription = "Yurdunu Bil logosu",
                        tint = AuthMintBright,
                        modifier = Modifier.size(47.dp)
                    )
                    Icon(
                        YBIcons.AvatarMountain,
                        contentDescription = null,
                        tint = AuthText.copy(alpha = .78f),
                        modifier = Modifier.size(21.dp).offset(x = 10.dp, y = 9.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(2.dp))
        Text("Yurdunu Bil", color = AuthText, fontSize = 31.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(2.dp))
        Text(
            if (register) "Gel, birlikte keşfedelim."
            else "Türkiye'yi öğrenmenin daha keyifli yolu.",
            color = AuthMint,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(7.dp))
        Text(
            if (register) "Sadece ders değil; harita, oyun, keşif ve biraz da eğlence."
            else "Bir soru çöz, bir il öğren, haritada biraz daha yol al.",
            color = AuthMuted,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthAmbientBackground() {
    val transition = rememberInfiniteTransition(label = "yb-auth-bg")
    val alpha by transition.animateFloat(
        initialValue = .05f,
        targetValue = .12f,
        animationSpec = infiniteRepeatable(tween(2400), RepeatMode.Reverse),
        label = "ambient-alpha"
    )
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = 105.dp)
                .size(280.dp)
                .alpha(alpha)
                .background(
                    Brush.radialGradient(listOf(AuthMint, Color.Transparent)),
                    RoundedCornerShape(50)
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-90).dp, y = 120.dp)
                .size(250.dp)
                .alpha(alpha * .7f)
                .background(
                    Brush.radialGradient(listOf(AuthMint, Color.Transparent)),
                    RoundedCornerShape(50)
                )
        )
    }
}

@Composable
private fun AuthHeader(title: String, subtitle: String) {
    Text(title, color = AuthText, fontSize = 22.sp, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(4.dp))
    Text(subtitle, color = AuthMuted, fontSize = 11.sp, lineHeight = 17.sp)
}

@Composable
private fun OnboardingHint() {
    Row(
        Modifier.fillMaxWidth()
            .background(AuthMint.copy(alpha = .07f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(YBIcons.AvatarExplorer, null, tint = AuthMint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text("Profilini sonra birlikte tamamlarız", color = AuthSoft, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Text(
                "Kullanıcı adını belirle ve geniş avatar kataloğundan birini seç.",
                color = AuthMuted,
                fontSize = 9.sp,
                lineHeight = 14.sp
            )
        }
        Text("2", color = AuthMint, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun FeedbackCard(text: String, positive: Boolean) {
    Surface(
        color = if (positive) AuthMint.copy(alpha = .09f) else Color(0xFFE85D5D).copy(alpha = .10f),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            1.dp,
            if (positive) AuthMint.copy(alpha = .20f) else Color(0xFFE85D5D).copy(alpha = .20f)
        )
    ) {
        Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (positive) YBIcons.Tip else YBIcons.Shield,
                null,
                tint = if (positive) AuthMint else Color(0xFFFFA9A1),
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text, color = if (positive) AuthSoft else Color(0xFFFFC3BD), fontSize = 10.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun EmailField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("E-posta") },
        placeholder = { Text("ornek@mail.com") },
        leadingIcon = { Icon(Icons.Default.Email, null) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        ),
        shape = RoundedCornerShape(16.dp),
        colors = authFieldColors()
    )
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    visible: Boolean,
    toggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        trailingIcon = {
            IconButton(onClick = toggle) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null
                )
            }
        },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(16.dp),
        colors = authFieldColors()
    )
}

@Composable
private fun AuthButton(text: String, busy: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !busy,
        modifier = Modifier.fillMaxWidth().height(55.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthMint,
            contentColor = Color(0xFF06251B),
            disabledContainerColor = Color(0xFF2D5045)
        )
    ) {
        if (busy) {
            CircularProgressIndicator(Modifier.size(19.dp), color = Color(0xFF06251B), strokeWidth = 2.dp)
        } else {
            Icon(if (text.contains("başla", true) || text.contains("giriş", true)) YBIcons.Explore else YBIcons.AvatarExplorer, null)
            Spacer(Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        color = if (selected) Color.White.copy(alpha = .12f) else Color.Transparent
    ) {
        Box(Modifier.fillMaxWidth().padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
            Text(
                text,
                color = if (selected) AuthText else AuthMuted,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AuthMint,
    unfocusedBorderColor = Color.White.copy(alpha = .14f),
    focusedLabelColor = AuthMint,
    unfocusedLabelColor = AuthMuted,
    cursorColor = AuthMint,
    focusedTextColor = AuthText,
    unfocusedTextColor = AuthText,
    focusedLeadingIconColor = AuthMint,
    unfocusedLeadingIconColor = AuthMuted,
    focusedTrailingIconColor = AuthMint,
    unfocusedTrailingIconColor = AuthMuted,
    focusedContainerColor = Color.White.copy(alpha = .045f),
    unfocusedContainerColor = Color.White.copy(alpha = .025f)
)

private fun friendlyAuthError(error: Throwable): String {
    val raw = (error.message ?: "").lowercase()
    return when {
        "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta veya şifre hatalı."
        "email not confirmed" in raw -> "Önce e-posta adresini doğrula, sonra tekrar giriş yap."
        "user already registered" in raw || "already registered" in raw -> "Bu e-posta zaten kayıtlı. Giriş Yap sekmesine geç."
        "network" in raw || "timeout" in raw || "unable to resolve host" in raw -> "Bağlantı kurulamadı. İnternetini kontrol edip tekrar dene."
        else -> "İşlem tamamlanamadı. Bilgilerini kontrol edip tekrar dene."
    }
}
