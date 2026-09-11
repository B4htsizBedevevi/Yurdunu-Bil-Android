package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LaunchBgTop = Color(0xFF041713)
private val LaunchBgMid = Color(0xFF07352A)
private val LaunchBgBottom = Color(0xFF010907)
private val LaunchGreen = Color(0xFF27D996)
private val LaunchText = Color(0xFFF2FBF7)
private val LaunchMuted = Color(0xFFA5BCB4)

/** Welcome screen stays underneath auth so Back returns here instead of closing the app. */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val seen = YBPreferences.hasSeenIntro(this)

        setContent {
            SafeLaunchScreen(
                onRegister = { markWelcomeSeen(); openAuth(true) },
                onLogin = { markWelcomeSeen(); openAuth(false) }
            )
        }

        if (seen) {
            window.decorView.post { openAuth(false) }
        }
    }

    private fun markWelcomeSeen() {
        YBPreferences.markIntroSeen(this)
    }

    private fun openAuth(register: Boolean) {
        startActivity(Intent(this, AuthExperienceActivity::class.java).putExtra("register", register))
    }
}

@Composable
private fun SafeLaunchScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    var contentVisible by remember { mutableStateOf(false) }
    val pulse = rememberInfiniteTransition(label = "launchPulse")
    val glowAlpha by pulse.animateFloat(initialValue = 0.12f, targetValue = 0.28f, animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "logoGlow")
    val logoScale by animateFloatAsState(targetValue = if (contentVisible) 1f else 0.72f, animationSpec = tween(720, easing = FastOutSlowInEasing), label = "logoScale")
    LaunchedEffect(Unit) { contentVisible = true }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LaunchBgTop, LaunchBgMid, LaunchBgBottom))).statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            repeat(10) { index -> val y = size.height * (.08f + index * .083f); val path = Path().apply { moveTo(-30f, y); cubicTo(size.width * .25f, y - 65f, size.width * .62f, y + 52f, size.width + 30f, y - 12f) }; drawPath(path, LaunchGreen.copy(alpha = .045f), style = Stroke(width = 2f)) }
            drawCircle(LaunchGreen.copy(alpha = .07f), radius = 3f, center = Offset(size.width * .10f, size.height * .16f))
            drawCircle(LaunchGreen.copy(alpha = .05f), radius = 2.5f, center = Offset(size.width * .88f, size.height * .78f))
        }
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(22.dp))
            AnimatedVisibility(visible = contentVisible, enter = fadeIn(tween(700)) + scaleIn(tween(700), initialScale = 0.72f)) {
                Box(Modifier.size(104.dp).scale(logoScale).clip(RoundedCornerShape(31.dp)).background(LaunchGreen.copy(alpha = glowAlpha)).border(1.dp, LaunchGreen.copy(alpha = 0.55f), RoundedCornerShape(31.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(R.drawable.yurdunu_bil_app_icon), contentDescription = "Yurdunu Bil logosu", modifier = Modifier.size(82.dp).clip(RoundedCornerShape(25.dp)), contentScale = ContentScale.Crop)
                }
            }
            AnimatedVisibility(visible = contentVisible, enter = fadeIn(tween(550, delayMillis = 120)) + slideInVertically(tween(550, delayMillis = 120)) { it / 3 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(14.dp)); Text("Yurdunu Bil", color = LaunchText, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Gel, birlikte keşfedelim.", color = LaunchGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(26.dp)); Text("Türkiye coğrafyasını", color = LaunchText, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("oynayarak öğren.", color = LaunchGreen, fontSize = 28.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(10.dp))
                    Text("KPSS'ye hazırlanırken Türkiye'yi daha iyi tanı. Konuları öğren, testlerle pekiştir, günlük görevlerini tamamla ve Arena'da kendini dene.", color = LaunchMuted, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.055f)).border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp)).padding(vertical = 15.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) { Stat("12+", "Konu", YBIcons.Library); Stat(SharedQuestionPool.all.size.toString(), "Soru", Icons.Default.Quiz); Stat("1v1", "Arena", YBIcons.Swords) }
                }
            }
            Spacer(Modifier.weight(1f))
            AnimatedVisibility(visible = contentVisible, enter = fadeIn(tween(650, delayMillis = 250)) + slideInVertically(tween(650, delayMillis = 250)) { it / 4 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = onRegister, modifier = Modifier.fillMaxWidth().height(57.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = LaunchGreen, contentColor = LaunchBgTop)) { Text("Hemen keşfedelim", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) }
                    Spacer(Modifier.height(10.dp)); OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = LaunchText)) { Text("Hesabımla devam et", fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(8.dp)); Text("KPSS • COĞRAFYA • ÖĞREN • YARIŞ", color = LaunchMuted.copy(alpha = 0.82f), fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun Stat(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) { Column(horizontalAlignment = Alignment.CenterHorizontally) { androidx.compose.material3.Icon(icon, null, tint = LaunchGreen, modifier = Modifier.size(18.dp)); Text(value, color = LaunchGreen, fontSize = 13.sp, fontWeight = FontWeight.Black); Text(label, color = LaunchMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold) } }
