package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LaunchBgTop = Color(0xFF061A17)
private val LaunchBgMid = Color(0xFF0A3027)
private val LaunchBgBottom = Color(0xFF020B09)
private val LaunchGreen = Color(0xFF27D996)
private val LaunchText = Color(0xFFF2FBF7)
private val LaunchMuted = Color(0xFFA5BCB4)

/** Startup-safe launcher. Authentication/database work intentionally starts only after a button press. */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModernLaunchScreen(
                onRegister = { openAuth(true) },
                onLogin = { openAuth(false) }
            )
        }
    }

    private fun openAuth(register: Boolean) {
        startActivity(
            Intent(this, ModernAuthActivity::class.java)
                .putExtra("register", register)
        )
    }
}

@Composable
private fun ModernLaunchScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    val background = Brush.verticalGradient(
        0f to LaunchBgTop,
        0.52f to LaunchBgMid,
        1f to LaunchBgBottom
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            Image(
                painter = painterResource(R.drawable.yurdunu_bil_logo),
                contentDescription = "Yurdunu Bil",
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(25.dp))
            )

            Spacer(Modifier.height(16.dp))
            Text("Yurdunu Bil", color = LaunchText, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Geleceğini Bil.", color = LaunchGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(26.dp))
            Text(
                "Türkiye coğrafyasını",
                color = LaunchText,
                fontSize = 29.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "oynayarak öğren.",
                color = LaunchGreen,
                fontSize = 29.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(9.dp))
            Text(
                "KPSS Önlisans için konu anlatımı, testler, günlük görevler ve Arena.",
                color = LaunchMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))
            LaunchInfoPanel()

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LaunchGreen,
                    contentColor = LaunchBgTop
                )
            ) {
                Text("Keşfetmeye Başla", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(9.dp))
            OutlinedButton(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(51.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LaunchText)
            ) {
                Text("Hesabımla giriş yap", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Ücretsiz başla • İlerlemeni kaydet • Arena'da yarış",
                color = LaunchMuted.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun LaunchInfoPanel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.055f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(vertical = 15.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LaunchStat("12+", "Konu")
        LaunchDivider()
        LaunchStat("∞", "Test")
        LaunchDivider()
        LaunchStat("⚡", "Arena")
    }
}

@Composable
private fun LaunchStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = LaunchGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(label, color = LaunchMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LaunchDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(31.dp)
            .background(Color.White.copy(alpha = 0.1f))
    )
}
