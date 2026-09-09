package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay

private val MLDeep = Color(0xFF041611)
private val MLGreen = Color(0xFF27D996)
private val MLMute = Color(0xFF9DB8AE)

class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("yurdunu_bil_native", 0)
        setContent {
            LaunchExperience(
                signedInHint = prefs.getBoolean("signed_in", false),
                onMain = ::openMain,
                onRegister = { openAuth(true) },
                onLogin = { openAuth(false) }
            )
        }
    }

    private fun openAuth(register: Boolean) {
        startActivity(Intent(this, ModernAuthActivity::class.java).putExtra("register", register))
    }

    private fun openMain() {
        startActivity(Intent(this, RetentionMainActivity::class.java))
        finish()
    }
}

@Composable
private fun LaunchExperience(
    signedInHint: Boolean,
    onMain: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    var splashDone by remember { mutableStateOf(false) }
    var sessionChecked by remember { mutableStateOf(false) }
    var hasSession by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1150)
        hasSession = runCatching {
            SupabaseClientProvider.client.auth.currentSessionOrNull()
        }.getOrNull() != null
        sessionChecked = true
        splashDone = true
        if (hasSession || signedInHint) onMain()
    }

    if (!splashDone) {
        AnimatedSplash()
    } else if (sessionChecked && !hasSession && !signedInHint) {
        ModernLaunchScreen(onRegister, onLogin)
    }
}

@Composable
private fun AnimatedSplash() {
    val scale = remember { Animatable(.78f) }
    val alpha = remember { Animatable(0f) }
    val glow = remember { Animatable(.2f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.coroutineScope {
            launch { scale.animateTo(1f, tween(850, easing = FastOutSlowInEasing)) }
            launch { alpha.animateTo(1f, tween(650)) }
            launch { glow.animateTo(1f, tween(1000)) }
        }
    }
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF071F1B), Color(0xFF0B4B3A), MLDeep)))
            .statusBarsPadding().navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(142.dp).scale(scale.value).alpha(alpha.value).clip(RoundedCornerShape(38.dp)).background(MLGreen.copy(alpha = .07f + glow.value * .08f)).padding(10.dp)) {
                Image(painterResource(R.drawable.yurdunu_bil_logo), "Yurdunu Bil", Modifier.fillMaxSize().clip(RoundedCornerShape(29.dp)))
            }
            Spacer(Modifier.height(19.dp))
            Text("Yurdunu Bil", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, modifier = Modifier.alpha(alpha.value))
            Text("Geleceğini Bil.", color = MLGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(alpha.value))
            Spacer(Modifier.height(18.dp))
            Text("KPSS Önlisans • Türkiye Coğrafyası", color = MLMute, fontSize = 11.sp, modifier = Modifier.alpha(alpha.value))
        }
    }
}

@Composable
private fun ModernLaunchScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF061C20), Color(0xFF0A3A2E), Color(0xFF03120E)))
            .statusBarsPadding().navigationBarsPadding()
    ) {
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
                    Icon(Icons.Default.ArrowForward, null)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("Hesabımla giriş yap", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(7.dp))
                Text("Yeni hesap oluşturabilir veya mevcut hesabınla devam edebilirsin.", color = MLMute.copy(alpha = .78f), fontSize = 10.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun FeatureCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .065f))) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(39.dp).clip(RoundedCornerShape(12.dp)).background(MLGreen.copy(alpha = .16f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MLGreen, modifier = Modifier.size(21.dp))
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text(subtitle, color = MLMute, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
    }
}
