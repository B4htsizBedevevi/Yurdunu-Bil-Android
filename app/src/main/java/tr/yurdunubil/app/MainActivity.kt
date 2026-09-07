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
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
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

@Composable
private fun YurdunuBilApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var quizOpen by remember { mutableStateOf(false) }
    val tabs = listOf("Ana Sayfa", "Dersler", "Arena", "İstatistik", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.SportsEsports, Icons.Default.BarChart, Icons.Default.Person)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF7FAF9)) {
            if (quizOpen) {
                QuizScreen(onExit = { quizOpen = false })
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        AnimatedContent(targetState = selectedTab, label = "tab_content") { tab ->
                            if (tab == 0) HomeScreen(onStartQuiz = { quizOpen = true })
                            else PlaceholderScreen(tabs[tab], icons[tab])
                        }
                    }
                    NavigationBar(modifier = Modifier.navigationBarsPadding(), containerColor = Color.White) {
                        tabs.forEachIndexed { index, label ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Icon(icons[index], contentDescription = label, modifier = Modifier.size(23.dp)) },
                                label = { Text(label, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(onStartQuiz: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val glow by pulse.animateFloat(0.96f, 1.04f, infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "glow")
    var showTip by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF7FAF9)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Navy, Color(0xFF0C3A2D))),
                    RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                ).padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 26.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Yurdunu Bil", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                            Text("KPSS Önlisans 2026", color = Mint.copy(alpha = .85f), fontSize = 14.sp)
                        }
                        Box(modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalFireDepartment, null, tint = Gold, modifier = Modifier.size(28.dp).scale(glow))
                        }
                    }
                    Spacer(Modifier.height(22.dp))
                    Text("Bugün de bir adım öne geç.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("10 soru çözerek serini koru ve XP kazan.", color = Color.White.copy(alpha = .72f), fontSize = 13.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatPill("0", "Soru")
                        StatPill("0", "XP")
                        StatPill("0 gün", "Seri")
                    }
                }
            }
        }
        item {
            AnimatedVisibility(visible = true, enter = fadeIn(tween(450)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(450))) {
                Card(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Günlük görev", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Bugünkü 10 soruyu tamamla", color = Color.Gray, fontSize = 13.sp)
                            }
                            Text("0 / 10", color = Green, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(14.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(9.dp).background(Color(0xFFE6EFEB), RoundedCornerShape(8.dp)))
                        Spacer(Modifier.height(14.dp))
                        PrimaryButton("Teste Başla", Icons.Default.PlayArrow, onStartQuiz)
                        AnimatedVisibility(visible = showTip, enter = fadeIn() + scaleIn(), exit = fadeOut()) {
                            Text("Soru motoru hazır. Şimdi gerçek içerik ve ilerleme sistemiyle büyütüyoruz.", modifier = Modifier.padding(top = 12.dp), color = Color(0xFF39705C), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item { Text("Hızlı Başla", modifier = Modifier.padding(horizontal = 20.dp), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold) }
        item {
            Row(modifier = Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SubjectCard("Türkçe", "Paragraf", Icons.Default.MenuBook, Modifier.weight(1f))
                SubjectCard("Coğrafya", "Harita", Icons.Default.Explore, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SubjectCard("Tarih", "Kronoloji", Icons.Default.EmojiEvents, Modifier.weight(1f))
                SubjectCard("Arena", "Düello", Icons.Default.SportsEsports, Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(Color(0xFFE9F8F0))) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Whatshot, null, tint = Color(0xFFE58A22)) }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Serini bugün başlat", fontWeight = FontWeight.Bold)
                        Text("Her gün küçük bir adım, büyük fark yaratır.", fontSize = 12.sp, color = Color(0xFF4C685C))
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = Green)
                }
            }
        }
    }
}

@Composable
private fun StatPill(value: String, label: String) {
    Column(modifier = Modifier.background(Color.White.copy(alpha = .10f), RoundedCornerShape(14.dp)).padding(horizontal = 15.dp, vertical = 9.dp)) {
        Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        Text(label, color = Color.White.copy(alpha = .65f), fontSize = 10.sp)
    }
}

@Composable
private fun PrimaryButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0F9E68))), RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SubjectCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier) {
    var pressed by remember { mutableStateOf(false) }
    val iconColor by animateColorAsState(if (pressed) Green else Navy, label = "icon_color")
    Card(modifier = modifier.clickable { pressed = !pressed }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(42.dp).background(if (pressed) Color(0xFFE3F8ED) else Color(0xFFF0F5F3), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor) }
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
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

    if (finished && result != null) {
        ResultScreen(result!!, onExit)
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7FAF9)).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Soru ${engine.currentIndex + 1} / ${engine.size}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(question.subject, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("+${if (selected == question.correctIndex) 10 else 0} XP", color = Gold, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(18.dp))
        Text(question.topic, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(question.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            itemsIndexed(question.options) { index, option ->
                val correct = index == question.correctIndex
                val selectedWrong = answered && selected == index && !correct
                val container = when {
                    answered && correct -> Color(0xFFDDF7E9)
                    selectedWrong -> Color(0xFFFFE3E3)
                    selected == index -> Color(0xFFE4F1FF)
                    else -> Color.White
                }
                Card(modifier = Modifier.fillMaxWidth().clickable(enabled = !answered) { selected = index }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(container), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFF0F5F3), CircleShape), contentAlignment = Alignment.Center) { Text("${'A' + index}", fontWeight = FontWeight.ExtraBold) }
                        Spacer(Modifier.width(12.dp))
                        Text(option, modifier = Modifier.weight(1f))
                        if (answered && correct) Icon(Icons.Default.CheckCircle, null, tint = Green)
                    }
                }
            }
        }
        AnimatedVisibility(answered, enter = fadeIn() + scaleIn()) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (selected == question.correctIndex) "Doğru cevap! 🎉" else "Bu kez olmadı.", fontWeight = FontWeight.Bold, color = if (selected == question.correctIndex) Green else MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(5.dp))
                    Text(question.explanation, fontSize = 13.sp)
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0F9E68))), RoundedCornerShape(16.dp)).clickable(enabled = answered || selected != null) {
            if (!answered) { engine.answer(selected); answered = true }
            else if (engine.currentIndex == engine.size - 1) { result = engine.result(); finished = true }
            else { selected = null; answered = false }
        }.padding(vertical = 15.dp), contentAlignment = Alignment.Center) {
            Text(if (!answered) "Cevabı Kontrol Et" else if (engine.currentIndex == engine.size - 1) "Sonucu Gör" else "Sonraki Soru", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResultScreen(result: QuizResult, onExit: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7FAF9)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(90.dp).background(Color(0xFFE4F8ED), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(46.dp)) }
        Spacer(Modifier.height(20.dp))
        Text("Test Tamamlandı!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text("${result.correct} doğru • ${result.wrong} yanlış • ${result.blank} boş", color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Text("+${result.xp} XP", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Green)
        Spacer(Modifier.height(26.dp))
        Box(modifier = Modifier.fillMaxWidth().background(Navy, RoundedCornerShape(18.dp)).clickable(onClick = onExit).padding(vertical = 15.dp), contentAlignment = Alignment.Center) { Text("Ana Sayfaya Dön", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PlaceholderScreen(title: String, icon: ImageVector) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(76.dp).background(Color(0xFFE7F6EF), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Green, modifier = Modifier.size(36.dp)) }
            Spacer(Modifier.height(18.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text("Bu bölüm native uygulamanın sıradaki özellikleriyle doldurulacak.", color = Color.Gray)
        }
    }
}
