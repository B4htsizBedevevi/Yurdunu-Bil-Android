package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

private val AuthBg = Color(0xFF041611)
private val AuthPanel = Color(0xFF09231C)
private val AuthMint = Color(0xFF39E6A5)
private val AuthText = Color(0xFFF2FAF6)
private val AuthMuted = Color(0xFFAFC5BB)
private val AuthSoft = Color(0xFFB9F5DC)

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
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(AuthBg, Color(0xFF0A352A), AuthBg))
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) {
                    Icon(YBIcons.Back, "Geri", tint = AuthText)
                    Spacer(Modifier.width(4.dp))
                    Text("Geri", color = AuthText, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AuthMint.copy(alpha = .09f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuthMint.copy(alpha = .18f))
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(YBIcons.Shield, null, tint = AuthMint, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("GÜVENLİ OTURUM", color = AuthSoft, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.size(82.dp),
                shape = RoundedCornerShape(24.dp),
                color = AuthMint.copy(alpha = .10f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuthMint.copy(alpha = .34f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (register) YBIcons.TravelExplore else YBIcons.Explore,
                        contentDescription = null,
                        tint = AuthMint,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("Yurdunu Bil", color = AuthText, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Text(
                if (register) "Kendine bir profil kur, sonra Türkiye'yi keşfet."
                else "Kaldığın yerden devam et.",
                color = AuthMint,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = AuthPanel.copy(alpha = .97f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .09f))
            ) {
                Column(Modifier.fillMaxWidth().padding(19.dp)) {
                    if (recovery) {
                        AuthHeader("Yeni şifreni belirle", "Hesabının güvenliği için yeni şifreni seç.")
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
                        AuthHeader("Şifreni yenile", "E-posta adresine güvenli bir yenileme bağlantısı gönder.")
                        Spacer(Modifier.height(18.dp))
                        EmailField(email) { email = it; clearFeedback() }
                        Spacer(Modifier.height(14.dp))
                        AuthButton("Yenileme bağlantısı gönder", busy) {
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
                            Text("Giriş ekranına dön", color = AuthMint, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth().background(
                                Color.White.copy(alpha = .055f),
                                RoundedCornerShape(16.dp)
                            ).padding(4.dp)
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

                        Spacer(Modifier.height(17.dp))
                        AuthHeader(
                            if (register) "Hesabını oluştur" else "Tekrar hoş geldin",
                            if (register) "E-posta ve şifreni gir. Bir sonraki adımda kullanıcı adını ve sabit avatarını seçeceksin."
                            else "E-posta ve şifrenle güvenli şekilde devam et."
                        )
                        Spacer(Modifier.height(17.dp))

                        EmailField(email) { email = it; clearFeedback() }
                        Spacer(Modifier.height(10.dp))
                        PasswordField(
                            "Şifre",
                            password,
                            { password = it; clearFeedback() },
                            showPassword
                        ) { showPassword = !showPassword }

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
                        AuthButton(if (register) "Hesabı oluştur" else "Giriş yap", busy) {
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
                                    message = "Hesabın oluşturuldu. E-postanı doğruladıktan sonra giriş yap; profil kurulum ekranın otomatik açılacak."
                                } else {
                                    error = "Giriş tamamlanamadı. Tekrar dene."
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

            Spacer(Modifier.height(13.dp))
            Text(
                if (register) "Kayıttan sonra: kullanıcı adı → avatar → ana ekran"
                else "Profilin varsa doğrudan uygulamaya geçersin.",
                color = AuthMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun AuthHeader(title: String, subtitle: String) {
    Text(title, color = AuthText, fontSize = 23.sp, fontWeight = FontWeight.Black)
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
            Text("Profilin kayıtla tamamlanacak", color = AuthSoft, fontSize = 11.sp, fontWeight = FontWeight.Black)
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
        border = androidx.compose.foundation.BorderStroke(
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
            Icon(if (text.contains("giriş", true)) YBIcons.Explore else YBIcons.AvatarExplorer, null)
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
