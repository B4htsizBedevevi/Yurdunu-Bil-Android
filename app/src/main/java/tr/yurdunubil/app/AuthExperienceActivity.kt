package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

private val ABg = Color(0xFF041712)
private val ADeep = Color(0xFF071F18)
private val AGreen = Color(0xFF35E7A1)
private val AText = Color(0xFFF5FAF7)
private val AMuted = Color(0xFFB8C9C2)
private val ASoft = Color(0xFFBDF7DF)

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
        kotlinx.coroutines.MainScope().launch {
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()
            if (user == null) return@launch
            val profile = runCatching {
                SupabaseClientProvider.client.postgrest.from("profiles").select {
                    filter { eq("id", user.id) }
                    limit(1)
                }.decodeSingleOrNull<ProfileGate>()
            }.getOrNull()
            val target = if (profile?.onboarding_complete == true) RetentionMainActivity::class.java else ProfileOnboardingActivity::class.java
            startActivity(Intent(this@AuthExperienceActivity, target))
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
    var ok by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun clearMessages() {
        ok = null
        error = null
    }

    fun action(block: suspend () -> Unit, success: () -> Unit) {
        scope.launch {
            busy = true
            clearMessages()
            runCatching { block() }
                .onSuccess { success() }
                .onFailure { error = friendlyAuthError(it) }
            busy = false
        }
    }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(ABg, Color(0xFF0A3029), ABg))
        )
    ) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(YBIcons.Back, "Geri", tint = AText) }
                Column(Modifier.weight(1f)) {
                    Text("Yurdunu Bil", color = AText, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("Hesabın senin ilerlemenin merkezi", color = AGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Icon(YBIcons.Map, null, tint = AGreen, modifier = Modifier.size(27.dp))
            }

            Spacer(Modifier.height(14.dp))
            Surface(
                color = AGreen.copy(alpha = .08f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, AGreen.copy(alpha = .18f))
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(YBIcons.Map, null, tint = AGreen, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("KPSS • COĞRAFYA • ÖĞREN • ARENA", color = ASoft, fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = .7.sp)
                }
            }

            Spacer(Modifier.height(20.dp))
            AnimatedVisibility(true, enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })) {
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = ADeep.copy(alpha = .96f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = .10f))
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        if (recovery) {
                            Text("Yeni şifreni belirle", color = AText, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("Hesabının güvenliği için yeni şifreni seç.", color = AMuted, fontSize = 12.sp)
                            Spacer(Modifier.height(18.dp))
                            PasswordField("Yeni şifre", newPassword, { newPassword = it; clearMessages() }, showPassword) { showPassword = !showPassword }
                            Spacer(Modifier.height(10.dp))
                            PasswordField("Yeni şifre tekrar", confirm, { confirm = it; clearMessages() }, showPassword) { showPassword = !showPassword }
                            Spacer(Modifier.height(15.dp))
                            ActionButton("Şifreyi Güncelle", busy) {
                                action({
                                    require(newPassword.length >= 6) { "Şifre en az 6 karakter olmalı." }
                                    require(newPassword == confirm) { "Şifreler aynı olmalı." }
                                    SupabaseClientProvider.client.auth.updateUser { password = newPassword }
                                }) { ok = "Şifren güncellendi. Artık giriş yapabilirsin." }
                            }
                        } else if (forgot) {
                            Text("Şifremi unuttum", color = AText, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("E-posta adresine yenileme bağlantısı göndereceğiz.", color = AMuted, fontSize = 12.sp)
                            Spacer(Modifier.height(18.dp))
                            EmailField(email) { email = it; clearMessages() }
                            Spacer(Modifier.height(15.dp))
                            ActionButton("Yenileme bağlantısı gönder", busy) {
                                action({
                                    require(email.contains("@")) { "Geçerli bir e-posta adresi gir." }
                                    SupabaseClientProvider.client.auth.resetPasswordForEmail(email.trim(), redirectUrl = "yurdunubil://auth")
                                }) { ok = "Bağlantı gönderildi. Gelen kutunu kontrol et." }
                            }
                            Spacer(Modifier.height(4.dp))
                            TextButton(onClick = { forgot = false; clearMessages() }) { Text("Giriş ekranına dön", color = AGreen, fontWeight = FontWeight.Bold) }
                        } else {
                            Text(
                                if (register) "Yurdunu Bil'e katıl" else "Tekrar hoş geldin",
                                color = AText,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (register) "Hesabını oluştur. Sonra kullanıcı adını ve avatarını seç."
                                else "Hesabına giriş yap ve kaldığın yerden devam et.",
                                color = AMuted,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(Modifier.height(16.dp))

                            Row(
                                Modifier.fillMaxWidth().background(Color(0xFF17372D), RoundedCornerShape(16.dp)).padding(4.dp)
                            ) {
                                AuthTab("Giriş Yap", !register, Modifier.weight(1f)) { register = false; clearMessages() }
                                AuthTab("Yeni Hesap", register, Modifier.weight(1f)) { register = true; clearMessages() }
                            }

                            Spacer(Modifier.height(18.dp))
                            EmailField(email) { email = it; clearMessages() }
                            Spacer(Modifier.height(11.dp))
                            PasswordField("Şifre", password, { password = it; clearMessages() }, showPassword) { showPassword = !showPassword }

                            if (!register) {
                                TextButton(
                                    onClick = { forgot = true; clearMessages() },
                                    modifier = Modifier.align(Alignment.End)
                                ) { Text("Şifremi unuttum", color = AGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }

                            if (register && password.length in 1..5) {
                                Text("Şifre en az 6 karakter olmalı.", color = Color(0xFFFFA59B), fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                            }

                            Spacer(Modifier.height(6.dp))
                            ActionButton(if (register) "Hesabımı oluştur" else "Giriş yap", busy) {
                                action({
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
                                    if (register) {
                                        if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) {
                                            onDone()
                                        } else {
                                            ok = "Hesabın oluşturuldu. E-posta doğrulamasından sonra giriş yapınca profilini tamamlayacaksın."
                                        }
                                    } else {
                                        if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onDone()
                                        else error = "Giriş tamamlanamadı."
                                    }
                                }
                            }

                            if (register) {
                                Spacer(Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(YBIcons.AvatarShield, null, tint = AGreen, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(7.dp))
                                    Text("Kayıttan hemen sonra profil ve sabit avatar seçimi açılır.", color = AMuted, fontSize = 10.sp, lineHeight = 15.sp)
                                }
                            }
                        }

                        if (ok != null) {
                            Spacer(Modifier.height(10.dp))
                            Text(ok!!, color = ASoft, fontSize = 11.sp, lineHeight = 17.sp)
                            if (forgot || recovery) TextButton(onClick = onBack) { Text("Geri", color = AGreen, fontWeight = FontWeight.Bold) }
                        }
                        if (error != null) {
                            Spacer(Modifier.height(10.dp))
                            Text(error!!, color = Color(0xFFFFB5AD), fontSize = 11.sp, lineHeight = 17.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Text("Güvenli giriş • Sabit profil • Kişisel ilerleme", color = AMuted, fontSize = 10.sp)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun EmailField(value: String, onChange: (String) -> Unit) =
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("E-posta") },
        placeholder = { Text("ornek@mail.com") },
        leadingIcon = { Icon(Icons.Default.Email, null) },
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        colors = authFieldColors()
    )

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    visible: Boolean,
    toggle: () -> Unit
) = OutlinedTextField(
    value = value,
    onValueChange = onChange,
    modifier = Modifier.fillMaxWidth(),
    label = { Text(label) },
    leadingIcon = { Icon(Icons.Default.Lock, null) },
    trailingIcon = {
        IconButton(onClick = toggle) {
            Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
        }
    },
    singleLine = true,
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    shape = RoundedCornerShape(15.dp),
    colors = authFieldColors()
)

@Composable
private fun ActionButton(text: String, busy: Boolean, onClick: () -> Unit) =
    Button(
        onClick = onClick,
        enabled = !busy,
        modifier = Modifier.fillMaxWidth().height(55.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AGreen, contentColor = Color(0xFF06251B), disabledContainerColor = Color(0xFF29493F))
    ) {
        if (busy) CircularProgressIndicator(Modifier.size(20.dp), color = Color(0xFF06251B), strokeWidth = 2.dp)
        else Text(text, fontWeight = FontWeight.Black)
    }

@Composable
private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) =
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        color = if (selected) Color.White.copy(alpha = .12f) else Color.Transparent
    ) {
        Box(Modifier.fillMaxWidth().padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
            Text(text, color = if (selected) AText else AMuted, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AGreen,
    unfocusedBorderColor = Color.White.copy(alpha = .14f),
    focusedLabelColor = AGreen,
    unfocusedLabelColor = AMuted,
    cursorColor = AGreen,
    focusedTextColor = AText,
    unfocusedTextColor = AText,
    focusedLeadingIconColor = AGreen,
    unfocusedLeadingIconColor = AMuted,
    focusedTrailingIconColor = AGreen,
    unfocusedTrailingIconColor = AMuted,
    focusedContainerColor = Color.White.copy(alpha = .045f),
    unfocusedContainerColor = Color.White.copy(alpha = .025f)
)

private fun friendlyAuthError(error: Throwable): String {
    val raw = (error.message ?: "").lowercase()
    return when {
        "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta veya şifre hatalı."
        "email not confirmed" in raw -> "E-posta adresini doğruladıktan sonra tekrar giriş yap."
        "user already registered" in raw || "already registered" in raw -> "Bu e-posta zaten kayıtlı. Giriş Yap sekmesine geç."
        "network" in raw || "timeout" in raw || "unable to resolve host" in raw -> "Bağlantı kurulamadı. İnternetini kontrol edip tekrar dene."
        else -> "İşlem tamamlanamadı. Bilgilerini kontrol edip tekrar dene."
    }
}
