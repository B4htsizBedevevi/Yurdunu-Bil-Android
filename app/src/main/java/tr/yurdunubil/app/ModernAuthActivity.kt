package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AuthBg = Color(0xFF03110D)
private val AuthSurface = Color(0xFF0A211B)
private val AuthText = Color(0xFFF4FBF7)
private val AuthMuted = Color(0xFF9CB9AD)
private val AuthGreen = Color(0xFF2BE29B)

/** Runtime-isolated auth UI. Database/network work is deliberately disabled until this screen is proven stable. */
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
    var visible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF061D21), Color(0xFF07392D), AuthBg)))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = AuthText) }
                Text("Yurdunu Bil", color = AuthText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(22.dp))
            Box(
                Modifier.size(82.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White.copy(alpha = .07f))
                    .border(1.dp, AuthGreen.copy(alpha = .35f), RoundedCornerShape(25.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("YB", color = AuthGreen, fontSize = 27.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))
            Text("Hesabına devam et", color = AuthText, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text("İlerlemeni kaydet, kaldığın yerden devam et.", color = AuthMuted, fontSize = 12.sp, textAlign = TextAlign.Center)

            Spacer(Modifier.height(22.dp))
            Column(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(AuthSurface.copy(alpha = .96f))
                    .border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(28.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = .045f)).padding(4.dp)
                ) {
                    AuthTab("Giriş Yap", !register, Modifier.weight(1f)) { register = false; message = null }
                    AuthTab("Yeni Hesap", register, Modifier.weight(1f)) { register = true; message = null }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("E-posta") },
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
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { visible = !visible }) {
                            Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
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
                            "Hesap bağlantısı sıradaki adımda etkinleştirilecek."
                        } else {
                            "Giriş bağlantısı sıradaki adımda etkinleştirilecek."
                        }
                    },
                    enabled = email.contains("@") && password.length >= 6,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuthGreen,
                        contentColor = Color(0xFF03251A),
                        disabledContainerColor = Color(0xFF25483D)
                    )
                ) {
                    Text(if (register) "Hesap Oluştur" else "Giriş Yap", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, null)
                }
            }

            Spacer(Modifier.weight(1f))
            Text("KPSS • COĞRAFYA • ÖĞREN • YARIŞ", color = AuthGreen.copy(alpha = .82f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color.White.copy(alpha = .12f) else Color.Transparent)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) AuthText else AuthMuted, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
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
