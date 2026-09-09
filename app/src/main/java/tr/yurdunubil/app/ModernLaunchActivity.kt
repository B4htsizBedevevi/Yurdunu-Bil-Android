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
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val LaunchBgTop = Color(0xFF061A17)
private val LaunchBgMid = Color(0xFF0A3027)
private val LaunchBgBottom = Color(0xFF020B09)
private val LaunchGreen = Color(0xFF27D996)
private val LaunchText = Color(0xFFF2FBF7)
private val LaunchMuted = Color(0xFFA5BCB4)

/** Crash-safe launcher. No database, auth or network work is performed during startup. */
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
    var contentVisible by remember { mutableStateOf(false) }
    val pulse = rememberInfiniteTransition(label = "logoPulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoGlow"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0.78f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        delay(180)
        contentVisible = true
    }

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
                    .size(96.dp)
                    .scale(logoScale)
                    .clip(RoundedCornerShape(29.dp))
                    .background(LaunchGreen.copy(alpha = glowAlpha))
                    .border(1.dp, LaunchGreen.copy(alpha = 0.45f), RoundedCornerShape(29.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.yurdunu_bil_app_icon),
                    contentDescription = "Yurdunu Bil logosu",
                    modifier = Modifier.size(78.dp).clip(RoundedCornerShape(23.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(14.dp))
                    Text("Yurdunu Bil", color = LaunchText, fontSize = 31.sp, fontWeight = FontWeight.Black)
                    Text("Geleceğini Bil.", color = LaunchGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(26.dp))
                    Text("Türkiye coğrafyasını", color = LaunchText, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("oynayarak öğren.", color = LaunchGreen, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "KPSS coğrafyasını konu anlatımı, testler, görevler ve Arena ile daha eğlenceli öğren.",
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
                }
            }

            Spacer(Modifier.weight(1f))

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, delayMillis = 160)) + slideInVertically(tween(600, delayMillis = 160)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onRegister,
                        modifier = Modifier.fillMaxWidth().height(57.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LaunchGreen, contentColor = LaunchBgTop)
                    ) { Text("Hemen Başla", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onLogin,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LaunchText)
                    ) { Text("Zaten hesabım var", fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Önlisans • Lisans • Ortaöğretim • Öğren • Yarış",
                        color = LaunchMuted.copy(alpha = 0.82f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
