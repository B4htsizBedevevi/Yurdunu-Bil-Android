package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailConfirmationScreen(
    onContinue: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val accent = Color(0xFF35E7A1)
    val ink = Color(0xFFF5FAF7)
    val muted = Color(0xFFB7C9C1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF061A16), Color(0xFF0A3028), Color(0xFF04120F))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = .14f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF06251B), modifier = Modifier.size(38.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("E-posta adresin doğrulandı!", color = ink, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(
                "Harika, hesabın artık hazır. Yurdunu Bil'e dönüp coğrafya yolculuğuna başlayabilirsin.",
                color = muted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = .07f))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = accent, modifier = Modifier.size(30.dp))
                Spacer(Modifier.height(8.dp))
                Text("Doğrulama tamamlandı", color = ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Artık hesabına güvenle giriş yapabilirsin.", color = muted, fontSize = 12.sp, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxSize().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color(0xFF06251B))
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Text("Yurdunu Bil'e devam et", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onBackToLogin,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = muted)
            ) {
                Text("Giriş ekranına dön")
            }
        }
    }
}
