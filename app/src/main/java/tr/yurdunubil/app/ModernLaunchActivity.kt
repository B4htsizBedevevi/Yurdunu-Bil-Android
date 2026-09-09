package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LaunchBgTop = Color(0xFF061A17)
private val LaunchBgMid = Color(0xFF0A3027)
private val LaunchBgBottom = Color(0xFF020B09)
private val LaunchGreen = Color(0xFF27D996)
private val LaunchText = Color(0xFFF2FBF7)
private val LaunchMuted = Color(0xFFA5BCB4)

/** Crash-safe launcher. No database, auth, drawable or network work is performed during startup. */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeLaunchScreen(
                onRegister = { openAuth(true) },
                onLogin = { openAuth(false) }
            )
        }
    }

    private fun openAuth(register: Boolean) {
        startActivity(Intent(this, ModernAuthActivity::class.java).putExtra("register", register))
    }
}

@Composable
private fun SafeLaunchScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LaunchBgTop, LaunchBgMid, LaunchBgBottom)))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(LaunchGreen.copy(alpha = 0.12f))
                    .border(1.dp, LaunchGreen.copy(alpha = 0.35f), RoundedCornerShape(25.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("YB", color = LaunchGreen, fontSize = 27.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(16.dp))
            Text("Yurdunu Bil", color = LaunchText, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Geleceğini Bil.", color = LaunchGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(28.dp))
            Text("Türkiye coğrafyasını", color = LaunchText, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("oynayarak öğren.", color = LaunchGreen, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(10.dp))
            Text(
                "KPSS Önlisans için konu anlatımı, testler, günlük görevler ve Arena.",
                color = LaunchMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.055f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Stat("12+", "Konu")
                Stat("∞", "Test")
                Stat("⚡", "Arena")
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LaunchGreen, contentColor = LaunchBgTop)
            ) { Text("Keşfetmeye Başla", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) }
            Spacer(Modifier.height(9.dp))
            OutlinedButton(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth().height(51.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LaunchText)
            ) { Text("Hesabımla giriş yap", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            Text("Ücretsiz başla • İlerlemeni kaydet • Arena'da yarış", color = LaunchMuted.copy(alpha = 0.8f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = LaunchGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(label, color = LaunchMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
