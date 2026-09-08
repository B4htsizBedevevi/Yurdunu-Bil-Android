package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

private val A_BG = Color(0xFF041411)
private val A_SURFACE = Color(0xFF0A211B)
private val A_TEXT = Color(0xFFF4FBF7)
private val A_MUTED = Color(0xFF9CB9AD)
private val A_GREEN = Color(0xFF2BE29B)
private val A_CYAN = Color(0xFF27D4D8)

class ModernAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val register = intent.getBooleanExtra("register", false)
        setContent {
            ModernAuthScreen(register, onBack = { finish() }, onSuccess = {
                startActivity(Intent(this, RetentionMainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
                finish()
            })
        }
    }
}

@Composable
private fun ModernAuthScreen(initialRegister: Boolean, onBack: () -> Unit, onSuccess: () -> Unit) {
    var register by remember { mutableStateOf(initialRegister) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF061C20), Color(0xFF07372B), A_BG)))) {
        AuthGlow()
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) { Icon(Icons.Default.ArrowBack, "Geri", tint = A_TEXT) }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.size(92.dp).clip(RoundedCornerShape(28.dp)).background(Color.White.copy(alpha = .07f)).border(1.dp, A_GREEN.copy(alpha = .35f), RoundedCornerShape(28.dp)).padding(7.dp)) { Image(painterResource(R.drawable.yurdunu_bil_logo), "Yurdunu Bil", Modifier.fillMaxSize().clip(RoundedCornerShape(22.dp))) }
                Spacer(Modifier.height(12.dp)); Text("Yurdunu Bil", color = A_TEXT, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("Geleceğini Bil.", color = A_GREEN, fontSize = 13.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text(if (register) "Türkiye coğrafyasını öğrenmeye hemen başla." else "Kaldığın yerden devam etmek için giriş yap.", color = A_MUTED, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp)).background(A_SURFACE.copy(alpha = .94f)).border(1.dp, Color.White.copy(alpha = .11f), RoundedCornerShape(30.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp)).background(Color.White.copy(alpha = .045f)).padding(4.dp)) { AuthModeTab("Giriş Yap", !register, Modifier.weight(1f)) { register = false; message = null }; AuthModeTab("Yeni Hesap", register, Modifier.weight(1f)) { register = true; message = null } }
                Spacer(Modifier.height(4.dp)); OutlinedTextField(email, { email = it.trimStart(); message = null }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("E-posta") }, leadingIcon = { Icon(Icons.Default.Email, null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = authFieldColors())
                OutlinedTextField(password, { password = it; message = null }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Şifre") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton({ visible = !visible }) { Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } }, visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), colors = authFieldColors())
                Text(if (register) "En az 6 karakter kullan. Hesap sonrası e-posta doğrulaması gerekebilir." else "Hesabınla ilerlemen ve çalışma durumun korunur.", color = A_MUTED, fontSize = 11.sp, lineHeight = 16.sp)
                AnimatedContent(targetState = message, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "auth-message") { msg -> if (msg != null) Text(msg, color = if (error) Color(0xFFFFA69D) else Color(0xFFBFF9DF), fontSize = 12.sp) }
                Button(onClick = { scope.launch { busy = true; message = null; error = false; try { if (register) { SupabaseClientProvider.client.auth.signUpWith(Email) { this.email = email.trim(); this.password = password }; if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onSuccess() else message = "Hesabın oluşturuldu. E-postanı doğrulayıp giriş yap." } else { SupabaseClientProvider.client.auth.signInWith(Email) { this.email = email.trim(); this.password = password }; if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onSuccess() else { error = true; message = "Giriş tamamlanamadı. Bilgilerini kontrol et." } } } catch (e: Exception) { error = true; val raw = (e.message ?: "").lowercase(); message = when { "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta veya şifre hatalı."; "email not confirmed" in raw -> "Önce e-posta adresini doğrula."; "already registered" in raw -> "Bu e-posta zaten kayıtlı. Giriş Yap sekmesine geç."; "network" in raw || "timeout" in raw -> "Bağlantı kurulamadı. İnternetini kontrol et."; else -> "İşlem tamamlanamadı. Lütfen tekrar dene." } } finally { busy = false } } }, enabled = !busy && email.contains("@") && email.length >= 5 && password.length >= 6, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = A_GREEN, contentColor = Color(0xFF03251A), disabledContainerColor = Color(0xFF25483D))) { if (busy) CircularProgressIndicator(color = Color(0xFF03251A), strokeWidth = 2.dp, modifier = Modifier.size(22.dp)) else { Text(if (register) "Hesap Oluştur" else "Giriş Yap", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.width(8.dp)); Icon(Icons.Default.ArrowForward, null) } }
            }
            Spacer(Modifier.weight(1f)); Text("KPSS • Coğrafya • Öğren • Yarış", color = A_GREEN.copy(alpha = .85f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable private fun AuthModeTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) { Box(modifier.clip(RoundedCornerShape(14.dp)).background(if (selected) Color.White.copy(alpha = .12f) else Color.Transparent).clickable(onClick = onClick).padding(vertical = 12.dp), contentAlignment = Alignment.Center) { Text(text, color = if (selected) A_TEXT else A_MUTED, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) } }
@Composable private fun authFieldColors() = OutlinedTextFieldDefaults.colors(focusedBorderColor = A_GREEN, unfocusedBorderColor = Color.White.copy(alpha = .14f), focusedLabelColor = A_GREEN, unfocusedLabelColor = A_MUTED, cursorColor = A_GREEN, focusedTextColor = A_TEXT, unfocusedTextColor = A_TEXT, focusedLeadingIconColor = A_GREEN, unfocusedLeadingIconColor = A_MUTED, focusedTrailingIconColor = A_GREEN, unfocusedTrailingIconColor = A_MUTED)
@Composable private fun AuthGlow() { Canvas(Modifier.fillMaxSize()) { drawCircle(A_GREEN.copy(alpha = .09f), size.minDimension * .58f, androidx.compose.ui.geometry.Offset(size.width * .9f, size.height * .12f)); drawCircle(A_CYAN.copy(alpha = .07f), size.minDimension * .48f, androidx.compose.ui.geometry.Offset(size.width * .02f, size.height * .5f)); drawCircle(Color(0xFFD9B35D).copy(alpha = .045f), size.minDimension * .4f, androidx.compose.ui.geometry.Offset(size.width * .92f, size.height * .88f)) } }
