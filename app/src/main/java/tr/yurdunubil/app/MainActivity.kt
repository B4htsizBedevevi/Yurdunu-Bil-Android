package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { YurdunuBilApp() }
    }
}

private val Navy = Color(0xFF071A17)
private val Green = Color(0xFF18B77A)
private val Mint = Color(0xFFB9F5D8)
private val Gold = Color(0xFFFFC857)
private val Background = Color(0xFFF5F9F7)

@Composable
private fun YurdunuBilApp() {
    var tab by remember { mutableIntStateOf(0) }
    var quizOpen by remember { mutableStateOf(false) }
    val tabs = listOf("Ana Sayfa", "Harita", "Kütüphane", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.Map, Icons.Default.Explore, Icons.Default.SportsEsports, Icons.Default.Person)

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Background) {
            if (quizOpen) QuizScreen { quizOpen = false }
            else Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f)) {
                    AnimatedContent(targetState = tab, label = "main_tab") { selected ->
                        if (selected == 0) HomeScreen { quizOpen = true }
                        else PlaceholderScreen(tabs[selected], icons[selected])
                    }
                }
                NavigationBar(Modifier.navigationBarsPadding(), containerColor = Color.White) {
                    tabs.forEachIndexed { index, label ->
                        NavigationBarItem(tab == index, { tab = index }, icon = { Icon(icons[index], label, Modifier.size(23.dp)) }, label = { Text(label, fontSize = 10.sp) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(onStartQuiz: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "home_pulse")
    val scale by pulse.animateFloat(.96f, 1.04f, infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "fire_scale")

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Navy, Color(0xFF0C4434))), RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(22.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Yurdunu Bil", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Türkiye'yi keşfet • öğren • yarış", color = Mint.copy(alpha = .9f), fontSize = 13.sp)
                        }
                        Box(Modifier.size(52.dp).background(Color.White.copy(alpha = .12f), CircleShape), Alignment.Center) {
                            Icon(Icons.Default.Whatshot, null, tint = Gold, Modifier.size(29.dp).scale(scale))
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Bugün Türkiye'den bir şey öğren.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text("Haritayı aç, bir konu seç veya kendini test et.", color = Color.White.copy(alpha = .72f), fontSize = 13.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        StatPill("0", "Soru")
                        StatPill("0", "XP")
                        StatPill("0", "Günlük Seri")
                    }
                }
            }
        }
        item {
            AnimatedVisibility(true, enter = fadeIn(tween(450)) + slideInVertically(initialOffsetY = { 35 }, animationSpec = tween(450))) {
                Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Bugünün Yurdunu Bil görevi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("10 coğrafya sorusu çöz ve XP kazan.", color = Color.Gray, fontSize = 13.sp)
                        Spacer(Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.weight(1f).height(9.dp).background(Color(0xFFE4EEE9), RoundedCornerShape(8.dp)))
                            Spacer(Modifier.width(12.dp))
                            Text("0 / 10", color = Green, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(14.dp))
                        PrimaryButton("Yurdunu Bil Testi", Icons.Default.PlayArrow, onStartQuiz)
                    }
                }
            }
        }
        item { Text("Türkiye'yi Keşfet", Modifier.padding(horizontal = 20.dp), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold) }
        item { ExploreCard("Türkiye Haritası", "İlleri seç, bölgeleri keşfet", Icons.Default.Map) }
        item {
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard("İller", "81 il", Icons.Default.LocationCity, Modifier.weight(1f))
                FeatureCard("Dağlar", "Yeryüzü şekilleri", Icons.Default.Terrain, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard("Akarsular", "Nehirler & göller", Icons.Default.Water, Modifier.weight(1f))
                FeatureCard("Bölgeler", "7 coğrafi bölge", Icons.Default.Explore, Modifier.weight(1f))
            }
        }
        item {
            Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(Color(0xFFE8F8F0))) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowForward, null, tint = Green, Modifier.size(25.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Sıradaki hedef", fontWeight = FontWeight.Bold)
                        Text("81 ili tamamla ve Türkiye uzmanı ol.", color = Color(0xFF4D6B5E), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(value: String, label: String) {
    Column(Modifier.background(Color.White.copy(alpha = .11f), RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 8.dp)) {
        Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        Text(label, color = Color.White.copy(alpha = .65f), fontSize = 9.sp)
    }
}

@Composable
private fun PrimaryButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0D9D67))), RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(vertical = 14.dp), Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ExploreCard(title: String, subtitle: String, icon: ImageVector) {
    Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable { }, RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(52.dp).background(Color(0xFFE6F7EF), RoundedCornerShape(15.dp)), Alignment.Center) { Icon(icon, null, tint = Green, Modifier.size(29.dp)) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ArrowForward, null, tint = Green)
        }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier) {
    var active by remember { mutableStateOf(false) }
    val tint by animateColorAsState(if (active) Green else Navy, label = "feature_tint")
    Card(modifier.clickable { active = !active }, RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Box(Modifier.size(43.dp).background(if (active) Color(0xFFE2F8EC) else Color(0xFFF0F5F3), RoundedCornerShape(13.dp)), Alignment.Center) { Icon(icon, null, tint = tint) }
            Spacer(Modifier.height(11.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun QuizScreen(onExit: () -> Unit) {
    val engine = remember { QuizEngine(SampleQuestions.all) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<QuizResult?>(null) }
    val question = engine.current

    if (finished && result != null) { ResultScreen(result!!, onExit); return }

    Column(Modifier.fillMaxSize().background(Background).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Soru ${engine.currentIndex + 1} / ${engine.size}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(question.topic, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("+${if (selected == question.correctIndex) 10 else 0} XP", color = Gold, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(18.dp))
        Text(question.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(18.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(question.options) { index, option ->
                val correct = index == question.correctIndex
                val wrong = answered && selected == index && !correct
                val container = when { answered && correct -> Color(0xFFDDF7E9); wrong -> Color(0xFFFFE2E2); selected == index -> Color(0xFFE4F1FF); else -> Color.White }
                Card(Modifier.fillMaxWidth().clickable(enabled = !answered) { selected = index }, RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(container), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).background(Color(0xFFF0F5F3), CircleShape), Alignment.Center) { Text("${'A' + index}", fontWeight = FontWeight.ExtraBold) }
                        Spacer(Modifier.width(12.dp))
                        Text(option, Modifier.weight(1f))
                        if (answered && correct) Icon(Icons.Default.CheckCircle, null, tint = Green)
                    }
                }
            }
        }
        AnimatedVisibility(answered, enter = fadeIn() + scaleIn()) {
            Card(Modifier.fillMaxWidth().padding(vertical = 10.dp), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(15.dp)) {
                    Text(if (selected == question.correctIndex) "Doğru cevap! 🎉" else "Bu kez olmadı.", fontWeight = FontWeight.Bold, color = if (selected == question.correctIndex) Green else MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(4.dp))
                    Text(question.explanation, fontSize = 13.sp)
                }
            }
        }
        Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0D9D67))), RoundedCornerShape(16.dp)).clickable(enabled = answered || selected != null) {
            if (!answered) { engine.answer(selected); answered = true }
            else if (engine.currentIndex == engine.size - 1) { result = engine.result(); finished = true }
            else { selected = null; answered = false }
        }.padding(vertical = 15.dp), Alignment.Center) {
            Text(if (!answered) "Cevabı Kontrol Et" else if (engine.currentIndex == engine.size - 1) "Sonucu Gör" else "Sonraki Soru", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResultScreen(result: QuizResult, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Background).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(92.dp).background(Color(0xFFE4F8ED), CircleShape), Alignment.Center) { Icon(Icons.Default.Whatshot, null, tint = Gold, Modifier.size(48.dp)) }
        Spacer(Modifier.height(20.dp))
        Text("Türkiye turu tamamlandı!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text("${result.correct} doğru • ${result.wrong} yanlış • ${result.blank} boş", color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Text("+${result.xp} XP", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Green)
        Spacer(Modifier.height(26.dp))
        PrimaryButton("Haritaya Dön", Icons.Default.Map, onExit)
    }
}

@Composable
private fun PlaceholderScreen(title: String, icon: ImageVector) {
    Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(78.dp).background(Color(0xFFE7F6EF), CircleShape), Alignment.Center) { Icon(icon, null, tint = Green, Modifier.size(38.dp)) }
            Spacer(Modifier.height(18.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text("Bu alan Türkiye haritası, kütüphane, Arena ve profil özellikleriyle doldurulacak.", color = Color.Gray)
        }
    }
}
