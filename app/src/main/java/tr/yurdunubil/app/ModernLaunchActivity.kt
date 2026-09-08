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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay

private val MLDeep = Color(0xFF061B16)
private val MLGreen = Color(0xFF27D996)
private val MLMute = Color(0xFF9DB8AE)

class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loggedIn = SupabaseClientProvider.client.auth.currentSessionOrNull() != null
        if (loggedIn) setContent { WelcomeSplash { openMain() } }
        else setContent { ModernLaunchScreen({ openAuth(true) }, { openAuth(false) }) }
    }
    private fun openAuth(register: Boolean) { startActivity(Intent(this, ModernAuthActivity::class.java).putExtra("register", register)) }
    private fun openMain() { startActivity(Intent(this, RetentionMainActivity::class.java)); finish() }
}

@Composable
private fun WelcomeSplash(onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(700); onDone() }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(MLDeep, Color(0xFF0A3A2E), Color(0xFF03120E))))
            .statusBarsPadding().navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.yurdunu_bil_logo), "Yurdunu Bil", Modifier.size(116.dp).clip(RoundedCornerShape(30.dp)))
            Spacer(Modifier.height(16.dp))
            Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Geleceğini Bil.", color = MLGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ModernLaunchScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF061C20), Color(0xFF0A3A2E), Color(0xFF03120E))))
            .statusBarsPadding().navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandLogo(60); Spacer(Modifier.width(13.dp))
                    Column { Text("Yurdunu Bil", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Geleceğini Bil.", color = MLGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.height(30.dp))
                Text("Türkiye coğrafyasını", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black)
                Text("oynayarak öğren.", color = MLGreen, fontSize = 29.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Text("KPSS Önlisans için konu anlatımı, soru çözümü, günlük görevler ve Arena tek bir yerde.", color = MLMute, fontSize = 14.sp, lineHeight = 21.sp)
                Spacer(Modifier.height(20.dp))
                FeatureCard(Icons.Default.MenuBook, "Kütüphane", "Önce konuyu öğren, sonra testini çöz.")
                Spacer(Modifier.height(9.dp))
                FeatureCard(Icons.Default.Quiz, "Testler", "KPSS tipi sorular ve anında açıklama.")
                Spacer(Modifier.height(9.dp))
                FeatureCard(Icons.Default.EmojiEvents, "Arena", "Hız, bölge ve Türkiye Ustası mücadeleleri.")
            }
            Column {
                Button(onClick = onRegister, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = MLGreen, contentColor = MLDeep)) {
                    Text("Keşfetmeye Başla", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.width(8.dp)); Icon(Icons.Default.ArrowForward, null)
                }
                Spacer(Modifier.height(9.dp))
                OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("Hesabımla giriş yap", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(9.dp))
                Text("Yeni hesap oluşturabilir veya mevcut hesabınla devam edebilirsin.", color = MLMute.copy(alpha = .78f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun BrandLogo(sizeDp: Int) {
    Image(painterResource(R.drawable.yurdunu_bil_logo), "Yurdunu Bil", Modifier.size(sizeDp.dp).clip(RoundedCornerShape((sizeDp / 4).dp)))
}

@Composable
private fun FeatureCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .065f))) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(MLGreen.copy(alpha = .16f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MLGreen, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp); Text(subtitle, color = MLMute, fontSize = 11.sp, lineHeight = 16.sp) }
        }
    }
}
