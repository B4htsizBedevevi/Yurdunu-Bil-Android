package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

private val MADeep = Color(0xFF061B16)
private val MAGreen = Color(0xFF2CE6A0)
private val MAMint = Color(0xFFD2F8E5)
private val MAMuted = Color(0xFFA9BEB6)
private val MAGlass = Color(0xFF112923).copy(alpha = .94f)
private val MAError = Color(0xFFFF8D83)

class ModernAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val register = intent.getBooleanExtra("register", false)
        setContent { ModernAuthScreen(register, onBack = { finish() }, onSuccess = ::continueToGate) }
    }

    private fun continueToGate() {
        startActivity(Intent(this, LaunchActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
        finish()
    }
}

@Composable
private fun ModernAuthScreen(initialRegister: Boolean, onBack: () -> Unit, onSuccess: () -> Unit) {
    var register by remember { mutableStateOf(initialRegister) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var resetMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (busy) return
        scope.launch {
            busy = true; error = null; message = null
            try {
                if (resetMode) {
                    require(email.contains("@")) { "Geçerli bir e-posta adresi yaz." }
                    SupabaseClientProvider.client.auth.resetPasswordForEmail(email.trim())
                    message = "Şifre sıfırlama bağlantısı e-posta adresine gönderildi."
                } else if (register) {
                    require(email.contains("@")) { "Geçerli bir e-posta adresi yaz." }
                    require(password.length >= 6) { "Şifre en az 6 karakter olmalı." }
                    SupabaseClientProvider.client.auth.signUpWith(Email) {
                        this.email = email.trim(); this.password = password
                    }
                    if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) {
                        message = "Hesabın hazır. Profilini oluşturmaya geçiyoruz."
                        kotlinx.coroutines.delay(450)
                        onSuccess()
                    } else message = "Hesabın oluşturuldu. E-postandaki doğrulama bağlantısını kontrol et."
                } else {
                    require(email.contains("@")) { "Geçerli bir e-posta adresi yaz." }
                    require(password.length >= 6) { "Şifre en az 6 karakter olmalı." }
                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        this.email = email.trim(); this.password = password
                    }
                    if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) {
                        message = "Giriş başarılı. Hoş geldin!"
                        kotlinx.coroutines.delay(300)
                        onSuccess()
                    } else error = "Giriş tamamlanamadı. Bilgilerini kontrol et."
                }
            } catch (e: Exception) { error = friendlyModernAuthError(e) }
            finally { busy = false }
        }
    }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF061A20), Color(0xFF0A352B), Color(0xFF03130F)))).statusBarsPadding().navigationBarsPadding()) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = MAMint) }
                    Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Keşfet • Öğren • Yarış • Geliş", color = MAGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    BrandAuthMark()
                }
            }
            item {
                Column(Modifier.padding(horizontal = 4.dp)) {
                    Text(when { resetMode -> "Şifreni yenile."; register -> "Yeni bir hesap oluştur."; else -> "Kaldığın yerden devam et." }, color = Color.White, fontSize = 28.sp, lineHeight = 33.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(5.dp))
                    Text(when { resetMode -> "Kayıtlı e-posta adresini gir, sıfırlama bağlantısını gönderelim."; register -> "Hemen katıl, Türkiye'yi keşfetmeye başla."; else -> "Hesabına giriş yap ve ilerlemeni kaldığın yerden sürdür." }, color = MAMuted, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MAGlass), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(.12f))) {
                    Column(Modifier.padding(18.dp)) {
                        if (!resetMode) {
                            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp)).background(Color.White.copy(.05f)).padding(5.dp)) {
                                AuthSwitch("Giriş Yap", !register, Modifier.weight(1f)) { register = false; error = null; message = null }
                                AuthSwitch("Yeni Hesap", register, Modifier.weight(1f)) { register = true; error = null; message = null }
                            }
                            Spacer(Modifier.height(17.dp))
                        }
                        AnimatedContent(targetState = resetMode, transitionSpec = { fadeIn() + slideInHorizontally { it / 3 } togetherWith fadeIn() }, label = "auth-fields") { reset ->
                            Column {
                                OutlinedTextField(email, { email = it.trimStart(); error = null; message = null }, Modifier.fillMaxWidth(), label = { Text("E-posta") }, leadingIcon = { Icon(Icons.Default.Email, null) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = authFieldColors())
                                if (!reset) {
                                    Spacer(Modifier.height(11.dp))
                                    OutlinedTextField(password, { password = it; error = null; message = null }, Modifier.fillMaxWidth(), label = { Text("Şifre") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton({ passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } }, singleLine = true, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), colors = authFieldColors())
                                    if (!register) {
                                        Spacer(Modifier.height(10.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(Modifier.size(22.dp).clip(RoundedCornerShape(6.dp)).background(if (rememberMe) MAGreen else Color.Transparent).border(1.dp, if (rememberMe) MAGreen else MAMuted, RoundedCornerShape(6.dp)).clickable { rememberMe = !rememberMe }, contentAlignment = Alignment.Center) { if (rememberMe) Icon(Icons.Default.CheckCircle, null, tint = MADeep, modifier = Modifier.size(16.dp)) }
                                            Spacer(Modifier.width(7.dp)); Text("Beni hatırla", color = MAMuted, fontSize = 11.sp, modifier = Modifier.weight(1f)); Text("Şifremi unuttum?", color = MAGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { resetMode = true; error = null; message = null })
                                        }
                                    }
                                }
                            }
                        }
                        if (error != null || message != null) {
                            Spacer(Modifier.height(11.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircle, null, tint = if (error == null) MAGreen else MAError, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(7.dp)); Text(error ?: message.orEmpty(), color = if (error == null) MAMint else MAError, fontSize = 12.sp, lineHeight = 17.sp) }
                        }
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = { submit() }, enabled = !busy, modifier = Modifier.fillMaxWidth().height(55.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = MAGreen, contentColor = MADeep, disabledContainerColor = Color(0xFF2B4B41))) {
                            if (busy) androidx.compose.material3.CircularProgressIndicator(color = MADeep, strokeWidth = 2.dp, modifier = Modifier.size(22.dp)) else { Text(if (resetMode) "Sıfırlama Bağlantısı Gönder" else if (register) "Hesap Oluştur" else "Giriş Yap", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.width(7.dp)); Icon(Icons.Default.ArrowForward, null) }
                        }
                        if (resetMode) { Spacer(Modifier.height(10.dp)); Text("← Giriş ekranına dön", color = MAGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally).clickable { resetMode = false; error = null; message = null }) }
                    }
                }
            }
            item { Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { Text(if (resetMode) "Bağlantı birkaç dakika içinde ulaşabilir." else "E-posta ile güvenli giriş", color = MAMuted, fontSize = 11.sp); Spacer(Modifier.height(5.dp)); Text("KPSS • Türkiye Coğrafyası • Yurdunu Bil", color = MAGreen.copy(.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold) } }
        }
    }
}

@Composable
private fun BrandAuthMark() {
    Box(Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(.08f)).border(1.dp, MAGreen.copy(.25f), CircleShape), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Image(painter = androidx.compose.ui.res.painterResource(R.drawable.yurdunu_bil_logo), contentDescription = "Yurdunu Bil", modifier = Modifier.size(36.dp).clip(CircleShape))
    }
}

@Composable
private fun AuthSwitch(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(if (selected) Color.White.copy(.12f) else Color.Transparent).clickable(onClick = onClick).padding(vertical = 11.dp), contentAlignment = Alignment.Center) { Text(text, color = if (selected) Color.White else MAMuted, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(focusedBorderColor = MAGreen, unfocusedBorderColor = Color.White.copy(.15f), focusedLabelColor = MAGreen, unfocusedLabelColor = MAMuted, cursorColor = MAGreen, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedLeadingIconColor = MAGreen, unfocusedLeadingIconColor = MAMuted, focusedTrailingIconColor = MAGreen, unfocusedTrailingIconColor = MAMuted)

private fun friendlyModernAuthError(e: Throwable): String {
    val raw = (e.message ?: "").lowercase()
    return when {
        "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta veya şifre hatalı."
        "email not confirmed" in raw -> "Önce e-posta adresini doğrula."
        "user already registered" in raw || "already registered" in raw -> "Bu e-posta zaten kayıtlı. Giriş Yap sekmesine geç."
        "network" in raw || "timeout" in raw || "unable to resolve host" in raw -> "Bağlantı kurulamadı. İnternetini kontrol et."
        else -> "İşlem tamamlanamadı. Lütfen tekrar dene."
    }
}
