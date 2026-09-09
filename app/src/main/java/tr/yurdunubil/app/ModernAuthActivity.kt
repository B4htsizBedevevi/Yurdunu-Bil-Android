package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions

private val AuthBg = Color(0xFF020B08)
private val AuthBgMid = Color(0xFF073329)
private val AuthSurface = Color(0xFF09251E)
private val AuthText = Color(0xFFF4FBF7)
private val AuthMuted = Color(0xFF9CB9AD)
private val AuthGreen = Color(0xFF2BE29B)
private val AuthGreenDark = Color(0xFF0D4032)

class ModernAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialRegister = intent.getBooleanExtra("register", false)
        setContent {
            SafeAuthScreen(initialRegister = initialRegister, onBack = { finish() }, onContinue = {
                val next = Intent(this, RetentionMainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(next)
                finish()
            })
        }
    }
}

@Composable
private fun SafeAuthScreen(initialRegister: Boolean, onBack: () -> Unit, onContinue: () -> Unit) {
    var register by remember { mutableStateOf(initialRegister) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val ambient = rememberInfiniteTransition(label = "authAmbient")
    val logoScale by ambient.animateFloat(.97f, 1.035f, infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "logoPulse")
    LaunchedEffect(Unit) { contentVisible = true }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF061C17), AuthBgMid, AuthBg))).statusBarsPadding().navigationBarsPadding()) {
        Canvas(Modifier.fillMaxSize()) {
            repeat(9) { index ->
                val y = size.height * (.12f + index * .075f)
                val path = Path().apply { moveTo(-20f, y); cubicTo(size.width * .28f, y - 55f, size.width * .64f, y + 45f, size.width + 20f, y - 10f) }
                drawPath(path, AuthGreen.copy(alpha = .055f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
            }
            drawCircle(AuthGreen.copy(alpha = .08f), 2.5f, Offset(size.width * .12f, size.height * .20f))
            drawCircle(AuthGreen.copy(alpha = .06f), 2f, Offset(size.width * .86f, size.height * .70f))
        }
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = AuthText) }
                Text("Yurdunu Bil", color = AuthText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(contentVisible, enter = fadeIn(tween(550)) + slideInVertically(tween(550)) { it / 4 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(92.dp).scale(logoScale).clip(RoundedCornerShape(29.dp)).background(AuthGreen.copy(alpha = .10f)).border(1.dp, AuthGreen.copy(alpha = .50f), RoundedCornerShape(29.dp)), contentAlignment = Alignment.Center) {
                        Image(painterResource(R.drawable.yurdunu_bil_app_icon), "Yurdunu Bil logosu", Modifier.size(74.dp).clip(RoundedCornerShape(22.dp)), contentScale = ContentScale.Crop)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(if (register) "KPSS yolculuğuna katıl" else "Kaldığın yerden devam et", color = AuthText, fontSize = 27.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(5.dp))
                    Text("KPSS'ye hazırlanırken Türkiye'yi daha iyi tanı. Konuları öğren, testlerle pekiştir, günlük görevlerini tamamla ve Arena'da kendini dene.", color = AuthMuted, fontSize = 12.sp, lineHeight = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(Modifier.height(18.dp))
            AnimatedVisibility(contentVisible, enter = fadeIn(tween(600, delayMillis = 120)) + slideInVertically(tween(600, delayMillis = 120)) { it / 5 }) {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(AuthSurface.copy(alpha = .98f)).border(1.dp, AuthGreen.copy(alpha = .14f), RoundedCornerShape(28.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = .045f)).padding(4.dp)) {
                        AuthTab("Giriş Yap", !register, Modifier.weight(1f)) { register = false; message = null }
                        AuthTab("Yeni Hesap", register, Modifier.weight(1f)) { register = true; message = null }
                    }
                    Text(if (register) "Yeni hesabını oluştur" else "Hesabına giriş yap", color = AuthText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = email, onValueChange = { email = it; message = null }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("E-posta adresi") }, placeholder = { Text("ornek@mail.com") }, leadingIcon = { Icon(Icons.Default.Email, null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), shape = RoundedCornerShape(17.dp), colors = authColors())
                    OutlinedTextField(value = password, onValueChange = { password = it; message = null }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Şifre") }, placeholder = { Text("En az 6 karakter") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Şifreyi göster/gizle") } }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), shape = RoundedCornerShape(17.dp), colors = authColors())
                    if (!register) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Text("Şifremi unuttum?", color = AuthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { message = "Şifre yenileme, gerçek hesap sistemi açıldığında aktif olacak." }) }
                    else if (password.isNotEmpty()) PasswordStrength(password)
                    if (message != null) Text(message!!, color = AuthGreen, fontSize = 12.sp, lineHeight = 17.sp)
                    Button(onClick = onContinue, enabled = email.contains("@") && email.contains(".") && password.length >= 6, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthGreen, contentColor = Color(0xFF03251A), disabledContainerColor = AuthGreenDark, disabledContentColor = AuthMuted.copy(alpha = .80f))) {
                        Text(if (register) "Hesap Oluştur ve Başla" else "Giriş Yap ve Devam Et", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.width(8.dp)); Icon(Icons.Default.ArrowForward, null)
                    }
                    Text("Bu sürümde giriş bilgileri sunucuya gönderilmiyor. Buton, stabil V2 deneyimine geçiş için yerel test akışını açar.", color = AuthMuted.copy(alpha = .78f), fontSize = 10.sp, lineHeight = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(Modifier.weight(1f))
            Text("KPSS • COĞRAFYA • ÖĞREN • YARIŞ", color = AuthGreen.copy(alpha = .82f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun PasswordStrength(password: String) {
    val score = listOf(password.length >= 6, password.length >= 10, password.any { it.isUpperCase() }, password.any { it.isDigit() }).count { it }
    val label = when (score) { 0, 1 -> "Şifre zayıf"; 2 -> "Şifre orta"; 3 -> "Şifre iyi"; else -> "Şifre güçlü" }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        repeat(4) { index ->
            Box(Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(3.dp)).background(if (index < score) AuthGreen else Color.White.copy(alpha = .10f)))
            if (index != 3) Spacer(Modifier.width(4.dp))
        }
        Spacer(Modifier.width(10.dp)); Text(label, color = AuthMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.scale(1f).clip(RoundedCornerShape(15.dp)).background(if (selected) AuthGreen.copy(alpha = .15f) else Color.Transparent).border(if (selected) 1.dp else 0.dp, if (selected) AuthGreen.copy(alpha = .35f) else Color.Transparent, RoundedCornerShape(15.dp)).clickable(onClick = onClick).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Text(text, color = if (selected) AuthText else AuthMuted, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable private fun authColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AuthGreen, unfocusedBorderColor = AuthGreen.copy(alpha = .28f), focusedLabelColor = AuthGreen, unfocusedLabelColor = AuthMuted, cursorColor = AuthGreen,
    focusedTextColor = AuthText, unfocusedTextColor = AuthText, focusedContainerColor = Color.White.copy(alpha = .035f), unfocusedContainerColor = Color.White.copy(alpha = .018f), focusedLeadingIconColor = AuthGreen, unfocusedLeadingIconColor = AuthMuted, focusedTrailingIconColor = AuthGreen, unfocusedTrailingIconColor = AuthMuted
)
