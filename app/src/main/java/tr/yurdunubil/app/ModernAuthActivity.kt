package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AuthBg = Color(0xFF03110D)
private val AuthSurface = Color(0xFF0A211B)
private val AuthText = Color(0xFFF4FBF7)
private val AuthMuted = Color(0xFF9CB9AD)
private val AuthGreen = Color(0xFF2BE29B)
private val AuthGreenDark = Color(0xFF0D4032)

/** Runtime-isolated auth UI. Database/network work remains disabled until this screen is proven stable. */
class ModernAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialRegister = intent.getBooleanExtra("register", false)
        setContent {
            SafeAuthScreen(
                initialRegister = initialRegister,
                onBack = { finish() }
            )
        }
    }
}

@Composable
private fun SafeAuthScreen(initialRegister: Boolean, onBack: () -> Unit) {
    var register by remember { mutableStateOf(initialRegister) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF061D21), Color(0xFF07392D), AuthBg)))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Geri", tint = AuthText)
                }
                Text("Hesabın", color = AuthText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(Color.White.copy(alpha = .07f))
                            .border(1.dp, AuthGreen.copy(alpha = .40f), RoundedCornerShape(27.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.yurdunu_bil_app_icon),
                            contentDescription = "Yurdunu Bil logosu",
                            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (register) "Yurdunu Bil'e katıl" else "Yurdunu Bil'e geri dön",
                        color = AuthText,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (register) "İlerlemeni kaydet, günlük görevlerini tamamla ve Arena'da yüksel."
                        else "Kaldığın yerden devam et, istatistiklerini ve ilerlemeni koru.",
                        color = AuthMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(AuthSurface.copy(alpha = .97f))
                    .border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(28.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(17.dp))
                        .background(Color.White.copy(alpha = .045f))
                        .padding(4.dp)
                ) {
                    AuthTab("Giriş Yap", !register, Modifier.weight(1f)) {
                        register = false
                        message = null
                    }
                    AuthTab("Yeni Hesap", register, Modifier.weight(1f)) {
                        register = true
                        message = null
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("E-posta") },
                    placeholder = { Text("ornek@mail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp),
                    colors = authColors()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Şifre") },
                    placeholder = { Text("En az 6 karakter") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                "Şifreyi göster/gizle"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(16.dp),
                    colors = authColors()
                )

                if (message != null) {
                    Text(message!!, color = AuthGreen, fontSize = 12.sp, lineHeight = 17.sp)
                }

                Button(
                    onClick = {
                        message = if (register) {
                            "Hesap bağlantısı sonraki adımda etkinleştirilecek. Bu ekran hazır."
                        } else {
                            "Giriş bağlantısı sonraki adımda etkinleştirilecek. Bu ekran hazır."
                        }
                    },
                    enabled = email.contains("@") && email.contains(".") && password.length >= 6,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuthGreen,
                        contentColor = Color(0xFF03251A),
                        disabledContainerColor = AuthGreenDark,
                        disabledContentColor = AuthMuted
                    )
                ) {
                    Text(if (register) "Hesap Oluştur" else "Giriş Yap", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, null)
                }

                Text(
                    if (register) "Hesap oluşturduğunda ilerlemeni cihazların arasında da taşıyabileceğiz."
                    else "Giriş yaptığında kayıtlı konu, test ve Arena ilerlemen seni bekler.",
                    color = AuthMuted.copy(alpha = .78f),
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.weight(1f))
            Text(
                "KPSS • COĞRAFYA • ÖĞREN • YARIŞ",
                color = AuthGreen.copy(alpha = .82f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color.White.copy(alpha = .13f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) AuthText else AuthMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun authColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AuthGreen,
    unfocusedBorderColor = Color.White.copy(alpha = .14f),
    focusedLabelColor = AuthGreen,
    unfocusedLabelColor = AuthMuted,
    cursorColor = AuthGreen,
    focusedTextColor = AuthText,
    unfocusedTextColor = AuthText,
    focusedLeadingIconColor = AuthGreen,
    unfocusedLeadingIconColor = AuthMuted,
    focusedTrailingIconColor = AuthGreen,
    unfocusedTrailingIconColor = AuthMuted
)
