package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
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

private val MLDeep = Color(0xFF041611)
private val MLGreen = Color(0xFF27D996)
private val MLMute = Color(0xFF9DB8AE)

/** Stable launcher: no Supabase/session work is performed during startup. */
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
        startActivity(Intent(this, ModernAuthActivity::class.java).putExtra("register", register))
    }
}

@Composable
private fun ModernLaunchScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    val background = Brush.verticalGradient(listOf(Color(0xFF061C20), Color(0xFF0A3A2E), Color(0xFF03120E)))
    Box(Modifier.fillMaxSize().background(background).statusBarsPadding().navigationBarsPadding()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.yurdunu_bil_logo), "Yurdunu Bil", Modifier.size(58.dp).clip(RoundedCornerShape(17.dp)))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Yurdunu Bil", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("Geleceğini Bil.", color = MLGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text("Türkiye coğrafyasını", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("oynayarak öğren.", color = MLGreen, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(9.dp))
                Text("KPSS Önlisans için derin konu anlatımı, soru çözümü, günlük görevler ve Arena tek bir yerde.", color = MLMute, fontSize = 13.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(18.dp))
                FeatureCard(Icons.Default.MenuBook, "Kütüphane", "Konuyu uzun uzun öğren, sonra test et.")
                Spacer(Modifier.height(8.dp))
                FeatureCard(Icons.Default.Quiz, "Testler", "KPSS tipi sorular ve anında açıklama.")
                Spacer(Modifier.height(8.dp))
                FeatureCard(Icons.Default.EmojiEvents, "Arena", "Hız, bölge ve Türkiye Ustası mücadeleleri.")
            }
            Column {
                Button(onClick = onRegister, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = MLGreen, contentColor = MLDeep)) {
                    Text("Keşfetmeye Başla", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("Hesabımla giriş yap", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(7.dp))
                Text("Yeni hesap oluşturabilir veya mevcut hesabınla devam edebilirsin.", color = MLMute.copy(alpha = 0.78f), fontSize = 10.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun FeatureCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.065f))) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(39.dp).clip(RoundedCornerShape(12.dp)).background(MLGreen.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MLGreen, modifier = Modifier.size(21.dp))
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text(subtitle, color = MLMute, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
    }
}
