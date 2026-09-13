package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FirstRunTutorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FirstRunTutorial(onFinish = { finish() }) }
    }
}

private data class TutorialStep(
    val title: String,
    val body: String,
    val hint: String,
    val icon: ImageVector
)

@Composable
private fun FirstRunTutorial(onFinish: () -> Unit) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    val steps = listOf(
        TutorialStep("Hoş geldin! 👋", "Yurdunu Bil, KPSS Türkiye Coğrafyası çalışmanı kısa turlar ve akıllı tekrarlarla düzenler.", "Önce uygulamanın temelini 1 dakikada gösterelim.", Icons.Default.Explore),
        TutorialStep("Ana Sayfa", "Günlük hedefini, serini, doğruluğunu ve sana özel çalışma rotanı burada görürsün.", "Başla düğmesi günlük testini açar; zayıf konuların da öne çıkar.", Icons.Default.Home),
        TutorialStep("Kütüphane 📚", "Konuları ders sayfalarından öğren, önemli bilgileri oku ve ardından kendini yokla.", "Konuya dokun → dersi oku → konu testini çöz → tekrar et.", Icons.Default.MenuBook),
        TutorialStep("Etkinlikler 🎮", "Hızlı 10, Bilgi Zinciri, Güncel Bilgi Turu ve konu oyunlarıyla farklı şekillerde çalış.", "Aynı bilgiyi farklı oyunlarla tekrar etmek öğrenmeyi güçlendirir.", Icons.Default.SportsEsports),
        TutorialStep("Hızlı Erişim ⚡", "Sağ alttaki tek butondan Akıllı Çalışma Merkezi'ne ve Sosyal & Arkadaşlar alanına ulaşabilirsin.", "Admin yetkin varsa Admin Merkezi de burada görünür. Artık hazırsın! 🚀", Icons.Default.Speed)
    )
    val current = steps[step]
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accent = if (dark) Color(0xFF2BDEA0) else Color(0xFF0B8F69)

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.yurdunu_bil_app_icon), "Yurdunu Bil", Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)))
                Spacer(Modifier.width(10.dp))
                Text("Yurdunu Bil", fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                Text("${step + 1}/${steps.size}", color = accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.height(26.dp))
            Box(
                Modifier.size(108.dp).clip(RoundedCornerShape(30.dp)).background(Brush.linearGradient(listOf(accent.copy(alpha = .18f), accent.copy(alpha = .07f)))),
                contentAlignment = Alignment.Center
            ) { Icon(current.icon, null, tint = accent, modifier = Modifier.size(44.dp)) }
            Spacer(Modifier.height(24.dp))
            Text(current.title, fontSize = 27.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(current.body, fontSize = 15.sp, lineHeight = 22.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = .82f))
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                Text(current.hint, Modifier.padding(15.dp), fontSize = 12.sp, lineHeight = 18.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(steps.size) { i -> Box(Modifier.size(if (i == step) 22.dp else 7.dp, 7.dp).clip(RoundedCornerShape(8.dp)).background(if (i == step) accent else MaterialTheme.colorScheme.outlineVariant)) }
            }
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    if (step == steps.lastIndex) {
                        getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE).edit().putBoolean("first_run_tutorial_seen", true).apply()
                        onFinish()
                    } else step++
                },
                Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.White)
            ) { Text(if (step == steps.lastIndex) "Yurdunu Bil'e Başla" else "Devam et", fontWeight = FontWeight.Black) }
            TextButton(onClick = {
                getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE).edit().putBoolean("first_run_tutorial_seen", true).apply()
                onFinish()
            }) { Text("Şimdilik geç", color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)) }
        }
    }
}
