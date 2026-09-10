package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.background
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

private val AuthBg = Color(0xFF041712)
private val AuthGreen = Color(0xFF35E7A1)
private val AuthText = Color(0xFFF5FAF7)
private val AuthMuted = Color(0xFFB8C9C2)
private val AuthCard = Color(0xFF0B211B)

class AuthExperienceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        val recovery = intent.dataString?.contains("type=recovery", ignoreCase = true) == true ||
            intent.dataString?.contains("recovery", ignoreCase = true) == true
        setContent {
            AuthExperienceScreen(
                initialRegister = intent.getBooleanExtra("register", false),
                recovery = recovery,
                onDone = {
                    startActivity(Intent(this, RetentionMainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                },
                onBack = { finish() }
            )
        }
    }
}

@Composable
private fun AuthExperienceScreen(
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
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun clearMessages() { message = null; error = null }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(AuthBg, Color(0xFF0A3029), AuthBg)))) {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (forgot || recovery) {
                        IconButton(onClick = { if (recovery) onBack() else { forgot = false; clearMessages() } }) { Icon(Icons.Default.ArrowBack, "Geri", tint = AuthText) }
                    } else Spacer(Modifier.width(48.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Yurdunu Bil", color = AuthText, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("KPSS • Türkiye Coğrafyası", color = AuthGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.Map, null, tint = AuthGreen)
                }
            }

            if (recovery) {
                item {
                    AuthCard {
                        Text("Yeni şifreni belirle", color = AuthText, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("Hesabın için yeni ve güçlü bir şifre oluştur.", color = AuthMuted, fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        AuthField(label = "Yeni şifre", value = newPassword, onValueChange = { newPassword = it; clearMessages() }, visible = showPassword, toggle = { showPassword = !showPassword })
                        Spacer(Modifier.height(10.dp))
                        AuthField(label = "Yeni şifre tekrar", value = confirmPassword, onValueChange = { confirmPassword = it; clearMessages() }, visible = showPassword, toggle = { showPassword = !showPassword })
                        Spacer(Modifier.height(14.dp))
                        AuthButton(text = "Şifreyi Güncelle", busy = busy, onClick = {
                            scope.launch {
                                busy = true; clearMessages()
                                runCatching {
                                    require(newPassword.length >= 6) { "Şifre en az 6 karakter olmalı." }
                                    require(newPassword == confirmPassword) { "Şifreler aynı olmalı." }
                                    SupabaseClientProvider.client.auth.updateUser { password = newPassword }
                                }.onSuccess { message = "Şifren güncellendi. Artık yeni şifrenle giriş yapabilirsin." }
                                    .onFailure { error = friendlyAuthError(it) }
                                busy = false
                            }
                        })
                        AuthMessage(message = message, error = error)
                        if (message != null) {
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = onDone) { Text("Uygulamaya devam et", color = AuthGreen, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            } else if (forgot) {
                item {
                    AuthCard {
                        Text("Şifremi unuttum", color = AuthText, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("E-posta adresini yaz. Şifre yenileme bağlantısını sana gönderelim.", color = AuthMuted, fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        EmailField(value = email, onValueChange = { email = it; clearMessages() })
                        Spacer(Modifier.height(14.dp))
                        AuthButton(text = "Şifre Yenileme Bağlantısı Gönder", busy = busy, onClick = {
                            scope.launch {
                                busy = true; clearMessages()
                                runCatching {
                                    require(email.contains("@")) { "Geçerli bir e-posta adresi gir." }
                                    SupabaseClientProvider.client.auth.resetPasswordForEmail(email = email.trim(), redirectUrl = "yurdunubil://auth")
                                }.onSuccess {
                                    message = "Şifre yenileme bağlantısı e-posta adresine gönderildi. Gelen kutunu ve spam klasörünü kontrol et."
                                }.onFailure { error = friendlyAuthError(it) }
                                busy = false
                            }
                        })
                        AuthMessage(message = message, error = error)
                    }
                }
            } else {
                item {
                    AuthCard {
                        Row(Modifier.fillMaxWidth().background(Color(0xFF17372D), RoundedCornerShape(16.dp)).padding(4.dp)) {
                            AuthTab(text = "Giriş Yap", selected = !register, modifier = Modifier.weight(1f)) { register = false; clearMessages() }
                            AuthTab(text = "Yeni Hesap", selected = register, modifier = Modifier.weight(1f)) { register = true; clearMessages() }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(if (register) "KPSS yolculuğuna katıl." else "Kaldığın yerden devam et.", color = AuthText, fontSize = 25.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(12.dp))
                        EmailField(value = email, onValueChange = { email = it; clearMessages() })
                        Spacer(Modifier.height(10.dp))
                        AuthField(label = "Şifre", value = password, onValueChange = { password = it; clearMessages() }, visible = showPassword, toggle = { showPassword = !showPassword })
                        if (!register) {
                            TextButton(onClick = { forgot = true; clearMessages() }, modifier = Modifier.align(Alignment.End)) {
                                Text("Şifremi unuttum", color = AuthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        AuthButton(text = if (register) "Hesap Oluştur" else "Giriş Yap", busy = busy, onClick = {
                            scope.launch {
                                busy = true; clearMessages()
                                runCatching {
                                    require(email.contains("@")) { "Geçerli bir e-posta adresi gir." }
                                    require(password.length >= 6) { "Şifre en az 6 karakter olmalı." }
                                    if (register) {
                                        SupabaseClientProvider.client.auth.signUpWith(Email) { this.email = email.trim(); this.password = password }
                                    } else {
                                        SupabaseClientProvider.client.auth.signInWith(Email) { this.email = email.trim(); this.password = password }
                                    }
                                }.onSuccess {
                                    if (register && SupabaseClientProvider.client.auth.currentSessionOrNull() == null) {
                                        message = "Hesabın oluşturuldu. E-posta kutundaki doğrulama bağlantısını kontrol et."
                                    } else onDone()
                                }.onFailure { error = friendlyAuthError(it) }
                                busy = false
                            }
                        })
                        AuthMessage(message = message, error = error)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthCard(content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = AuthCard)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), content = content)
    }
}

@Composable
private fun EmailField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), label = { Text("E-posta") }, placeholder = { Text("ornek@mail.com") }, leadingIcon = { Icon(Icons.Default.Email, null) }, singleLine = true)
}

@Composable
private fun AuthField(label: String, value: String, onValueChange: (String) -> Unit, visible: Boolean, toggle: () -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton(onClick = toggle) { Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } }, singleLine = true, visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation())
}

@Composable
private fun AuthButton(text: String, busy: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = !busy, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthGreen, contentColor = Color(0xFF06251B))) {
        if (busy) CircularProgressIndicator(modifier = Modifier.size(21.dp), color = Color(0xFF06251B), strokeWidth = 2.dp) else Text(text, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(13.dp)).background(if (selected) Color.White.copy(alpha = .12f) else Color.Transparent).clickable(onClick = onClick).padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
        Text(text, color = if (selected) AuthText else AuthMuted, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun AuthMessage(message: String?, error: String?) {
    val value = error ?: message
    if (value != null) {
        Spacer(Modifier.height(10.dp))
        Text(value, color = if (error == null) AuthGreen else Color(0xFFFFB5AD), fontSize = 11.sp)
    }
}

private fun friendlyAuthError(error: Throwable): String {
    val raw = (error.message ?: "").lowercase()
    return when {
        "user already registered" in raw || "already registered" in raw -> "Bu e-posta ile zaten kayıt olunmuş. Giriş Yap sekmesine geçip giriş yapmayı dene."
        "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta veya şifre hatalı. Şifreni unuttuysan aşağıdaki Şifremi Unuttum seçeneğini kullanabilirsin."
        "email not confirmed" in raw -> "E-posta adresini doğruladıktan sonra tekrar giriş yap."
        "network" in raw || "timeout" in raw || "unable to resolve host" in raw -> "Bağlantı kurulamadı. İnternetini kontrol edip tekrar dene."
        else -> "İşlem tamamlanamadı. Lütfen bilgilerini kontrol edip tekrar dene."
    }
}
