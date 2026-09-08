package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MLDeep = Color(0xFF061B16)
private val MLGreen = Color(0xFF27D996)
private val MLMute = Color(0xFF9DB8AE)

class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ModernLaunchScreen(onExplore = { openAuth(true) }, onLogin = { openAuth(false) }) }
    }

    private fun openAuth(register: Boolean) {
        startActivity(Intent(this, LaunchActivity::class.java).putExtra("register", register))
    }
}

@Composable
private fun ModernLaunchScreen(onExplore: () -> Unit, onLogin: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF061B20), Color(0xFF0A3A2E), Color(0xFF03120E)))).statusBarsPadding().navigationBarsPadding()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(true, enter = fadeIn() + slideInVertically(initialOffsetY = { -24 })) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandLogo(60)
                        Spacer(Modifier.width(13.dp))
                        Column {
                            Text("Yurdunu Bil", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black)
                            Text("Geleceğini Bil.", color = MLGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(34.dp))
                Text("Türkiye coğrafyasını", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("oynayarak öğren.", color = MLGreen, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Text("KPSS Önlisans için konu anlatımı, soru çözümü, günlük görevler ve Arena tek bir yerde.", color = MLMute, fontSize = 15.sp, lineHeight = 22.sp)
                Spacer(Modifier.height(24.dp))
                FeatureCard(Icons.Default.MenuBook, "Kütüphane", "81 il ve Türkiye coğrafyası konu bankası")
                Spacer(Modifier.height(10.dp))
                FeatureCard(Icons.Default.Quiz, "Testler", "KPSS tipi sorular ve anında Dikkat Köşesi")
                Spacer(Modifier.height(10.dp))
                FeatureCard(Icons.Default.EmojiEvents, "Arena", "Hız, bölge ve Türkiye Ustası mücadeleleri")
            }
            Column {
                Button(onClick = onExplore, Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(19.dp), colors = ButtonDefaults.buttonColors(containerColor = MLGreen, contentColor = MLDeep)) {
                    Text("Keşfetmeye Başla", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, null)
                }
                Spacer(Modifier.height(9.dp))
                Button(onClick = onLogin, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = .08f), contentColor = Color.White)) {
                    Text("Hesabımla giriş yap", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
                Text("Yeni hesap oluşturabilir veya mevcut hesabınla devam edebilirsin.", color = MLMute.copy(alpha = .78f), fontSize = 11.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun BrandLogo(sizeDp: Int) {
    Image(painter = painterResource(id = R.drawable.yurdunu_bil_logo), contentDescription = "Yurdunu Bil logosu", modifier = Modifier.size(sizeDp.dp).clip(RoundedCornerShape((sizeDp / 4).dp)))
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .065f))) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(MLGreen.copy(alpha = .16f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MLGreen, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text(subtitle, color = MLMute, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}