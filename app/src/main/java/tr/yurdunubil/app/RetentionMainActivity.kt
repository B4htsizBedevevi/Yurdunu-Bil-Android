package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDate

class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RetentionApp() }
    }
}

private val RC = YurdunuBilColors

@Composable
private fun RetentionApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_progress", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var activeMode by remember { mutableStateOf<SharedGameMode?>(null) }
    var totalSolved by remember { mutableIntStateOf(prefs.getInt("total_solved", 0)) }
    var xp by remember { mutableIntStateOf(prefs.getInt("xp", 0)) }
    var streak by remember { mutableIntStateOf(prefs.getInt("streak", 0)) }
    var wins by remember { mutableIntStateOf(prefs.getInt("wins", 0)) }
    var dailySolved by remember { mutableIntStateOf(prefs.getInt("daily_solved", 0)) }

    fun recordResult(correct: Int, mode: SharedGameMode) {
        val today = LocalDate.now().toString()
        val last = prefs.getString("last_study_day", null)
        val nextStreak = when {
            last == today -> streak
            last == LocalDate.now().minusDays(1).toString() -> streak + 1
            else -> 1
        }
        val nextXp = xp + correct * 10 + mode.rewardXp
        totalSolved += mode.questions
        dailySolved += mode.questions
        xp = nextXp
        streak = nextStreak
        if (mode.arena && correct >= (mode.questions / 2)) wins += 1
        prefs.edit()
            .putInt("total_solved", totalSolved)
            .putInt("daily_solved", dailySolved)
            .putInt("xp", xp)
            .putInt("streak", streak)
            .putInt("wins", wins)
            .putString("last_study_day", today)
            .apply()
    }

    Surface(Modifier.fillMaxSize(), color = RC.Background) {
        val mode = activeMode
        if (mode != null) {
            RetentionQuiz(mode = mode, onExit = { activeMode = null }) { correct ->
                recordResult(correct, mode)
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f)) {
                    AnimatedContent(targetState = tab, label = "retention_page") { page ->
                        when (page) {
                            0 -> RetentionHome(totalSolved, xp, streak, dailySolved) { activeMode = SharedQuestionPool.dailyMode() }
                            1 -> RetentionLibrary { title -> activeMode = SharedGameMode("topic-$title", title, "Konuya özel KPSS tekrar turu", "📚", 10, 180, 100, SharedQuestionPool.topicForLibrary(title)) }
                            2 -> RetentionGames { activeMode = it }
                            3 -> RetentionArena { activeMode = it }
                            else -> RetentionProfile(totalSolved, xp, streak, wins)
                        }
                    }
                }
                NavigationBar(Modifier.navigationBarsPadding(), containerColor = RC.Surface, tonalElevation = 8.dp) {
                    val labels = listOf("Ana Sayfa", "Kütüphane", "Oyunlar", "Arena", "Profil")
                    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.SportsEsports, Icons.Default.SportsEsports, Icons.Default.Person)
                    labels.forEachIndexed { index, label ->
                        NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(icons[index], label) }, label = { Text(label, fontSize = 10.sp) })
                    }
                }
            }
        }
    }
}

@Composable
private fun RetentionHome(totalSolved: Int, xp: Int, streak: Int, dailySolved: Int, onDaily: () -> Unit) {
    val facts = remember { CurrentFactFeed.all }
    var factIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(180_000)
            factIndex = (factIndex + 1) % facts.size
        }
    }
    val daily = SharedQuestionPool.dailyMode()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Hero(totalSolved, xp, streak) }
        item {
            RetentionCard(Modifier.padding(horizontal = 18.dp), RC.NaturalGreen) {
                Text("🎯 Bugünün Görevi", color = RC.Deep, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("${daily.title} • ${daily.questions} soru • ${daily.rewardXp} XP ödül", color = Color(0xFF60756A), fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(progress = { (dailySolved.coerceAtMost(daily.questions) / daily.questions.toFloat()) }, Modifier.fillMaxWidth().height(7.dp), color = RC.NaturalGreen, trackColor = RC.SurfaceSoft)
                Spacer(Modifier.height(10.dp))
                AppButton("Göreve Başla", Icons.Default.PlayArrow, RC.NaturalGreen, onDaily)
            }
        }
        item { LiveFactCard(facts[factIndex]) }
        item { Section("Bugün bunları da yap") }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniCard("⚡", "2 Dakika", "Hızlı 10", RC.Water) { }
                MiniCard("🧠", "Tekrar", "Maden + Tarım", RC.Warm) { }
            }
        }
        item {
            RetentionCard(Modifier.padding(horizontal = 18.dp), RC.Water, dark = true) {
                Text("⚔️ Arena zamanı", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Oyunlar, etkinlikler ve Arena aynı doğrulanmış soru havuzunu kullanıyor.", color = RC.Sky, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text("${SharedQuestionPool.all.size} ortak soru • seçilebilir Arena modu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun Hero(total: Int, xp: Int, streak: Int) {
    Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(RC.Deep, RC.Forest, Color(0xFF286A4C))), RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp)).padding(22.dp)) {
        Column {
            Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("KPSS Önlisans • Türkiye Coğrafyası", color = RC.Sky, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text("Her gün biraz daha Türkiye.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatPill("$total", "Soru"); StatPill("$xp XP", "XP"); StatPill("$streak gün", "Seri") }
        }
    }
}

@Composable
private fun LiveFactCard(fact: CurrentFact) {
    RetentionCard(Modifier.padding(horizontal = 18.dp), RC.Water) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("📡 VERİ AKIŞI • 3 DK", color = RC.Water, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(fact.title, color = RC.Deep, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(fact.value, color = RC.NaturalGreen, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(fact.detail, color = Color(0xFF60756A), fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text("Kaynak: ${fact.source} • ${fact.year}", color = Color(0xFF809087), fontSize = 10.sp)
            }
            Text(fact.icon, fontSize = 34.sp)
        }
    }
}

@Composable
private fun RetentionLibrary(onTopic: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Kütüphane", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep)
            Text("Konu → bilgi → soru. Aynı havuz, farklı öğrenme yolları.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        item { RetentionCard(RCmod = Modifier, accent = RC.Sky, dark = true) { Text("🗺️ 81 il • 7 bölge • maden • tarım • iklim", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold); Text("Harita destekli çalışma alanı hazır; etkileşimli harita katmanı sıradaki geliştirmede.", color = RC.Sky, fontSize = 12.sp) } }
        item { Section("12 ana konu • sınav odaklı") }
        items(LibraryContent.topics) { topic ->
            RetentionCard(accent = RC.NaturalGreen, onClick = { onTopic(topic.title) }) {
                Text("${topic.icon}  ${topic.title}", color = RC.Deep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                Text(topic.summary, color = Color(0xFF687B71), fontSize = 12.sp)
                Spacer(Modifier.height(7.dp))
                topic.cards.take(5).forEach { card ->
                    Text("• ${card.title}: ${card.body}", color = Color(0xFF687B71), fontSize = 11.sp, modifier = Modifier.padding(vertical = 2.dp))
                }
                Spacer(Modifier.height(7.dp))
                Text("Dokun → bu konudan 10 soruluk özel tur", color = RC.NaturalGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun RetentionGames(onStart: (SharedGameMode) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Oyun Merkezi", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep); Text("Hepsi aynı kaliteli soru havuzundan.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold) }
        items(SharedGameModes.games) { mode ->
            RetentionCard(accent = RC.Water, onClick = { onStart(mode) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mode.icon, fontSize = 30.sp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(mode.title, color = RC.Deep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold); Text(mode.subtitle, color = Color(0xFF687B71), fontSize = 12.sp); Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = RC.Water, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    Icon(Icons.Default.ArrowForward, null, tint = RC.NaturalGreen)
                }
            }
        }
        item { Section("Günün etkinlikleri") }
        item { EventCard(SharedGameModes.eventForToday(), onStart) }
        item { EventCard(SharedGameModes.regions, onStart) }
        item { EventCard(SharedGameModes.master, onStart) }
    }
}

@Composable
private fun EventCard(mode: SharedGameMode, onStart: (SharedGameMode) -> Unit) {
    RetentionCard(accent = RC.Warm, onClick = { onStart(mode) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(mode.icon, fontSize = 28.sp); Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) { Text("ETKİNLİK • ${mode.title}", color = RC.Deep, fontWeight = FontWeight.ExtraBold); Text(mode.subtitle, color = Color(0xFF687B71), fontSize = 11.sp); Text("${mode.questions} soru • +${mode.rewardXp} XP", color = RC.Warm, fontWeight = FontWeight.Black, fontSize = 11.sp) }
            Icon(Icons.Default.PlayArrow, null, tint = RC.NaturalGreen)
        }
    }
}

@Composable
private fun RetentionArena(onStart: (SharedGameMode) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { Text("Arena", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep); Text("Önce modu seç. Sonra aynı havuzda kapış.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold) }
        item { RetentionCard(dark = true, accent = RC.Warm) { Text("SEZON 1", color = RC.Sky, fontWeight = FontWeight.Black, fontSize = 12.sp); Spacer(Modifier.height(5.dp)); Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black); Text("Online 1v1 için Supabase Realtime altyapısı hazır tutuluyor; bu sürümde modların yerel antrenmanı oynanabilir.", color = Color.White.copy(alpha = .75f), fontSize = 12.sp); Spacer(Modifier.height(10.dp)); Text("${SharedQuestionPool.all.size} ortak soru", color = RC.Sky, fontWeight = FontWeight.Bold) } }
        items(SharedGameModes.arenaModes) { mode ->
            RetentionCard(accent = RC.Warm, onClick = { onStart(mode) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mode.icon, fontSize = 31.sp); Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) { Text(mode.title, color = RC.Deep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold); Text(mode.subtitle, color = Color(0xFF687B71), fontSize = 12.sp); Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = RC.Warm, fontSize = 11.sp, fontWeight = FontWeight.Black) }
                    Icon(Icons.Default.SportsEsports, null, tint = RC.NaturalGreen)
                }
            }
        }
    }
}

@Composable
private fun RetentionProfile(total: Int, xp: Int, streak: Int, wins: Int) {
    val level = (xp / 250) + 1
    val progress = (xp % 250) / 250f
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { Text("Profil", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep); Text("İlerlemeni takip et.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold) }
        item { RetentionCard(dark = true, accent = RC.NaturalGreen) { Text("COĞRAFYA KAŞİFİ", color = RC.Sky, fontSize = 11.sp, fontWeight = FontWeight.Black); Text("Seviye $level", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { progress }, Modifier.fillMaxWidth().height(7.dp), color = RC.Leaf, trackColor = Color.White.copy(alpha = .18f)); Text("${xp % 250}/250 XP", color = Color.White.copy(alpha = .75f), fontSize = 11.sp) } }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { StatBox("$total", "Çözülen"); StatBox("$wins", "Arena"); StatBox("$streak", "Seri") } }
        item { RetentionCard(accent = RC.Water) { Text("Soru havuzu", color = RC.Deep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold); Text("${SharedQuestionPool.all.size} doğrulanmış yerel soru • oyun, etkinlik ve Arena ortak havuzu.", color = Color(0xFF687B71), fontSize = 12.sp) } }
    }
}

@Composable
private fun RetentionQuiz(mode: SharedGameMode, onExit: () -> Unit, onComplete: (Int) -> Unit) {
    val questions = remember(mode.id) { SharedQuestionPool.pick(mode) }
    var index by remember(mode.id) { mutableIntStateOf(0) }
    var selected by remember(mode.id) { mutableIntStateOf(-1) }
    var correct by remember(mode.id) { mutableIntStateOf(0) }
    var answered by remember(mode.id) { mutableStateOf(false) }
    var secondsLeft by remember(mode.id) { mutableIntStateOf(mode.seconds) }
    var finished by remember(mode.id) { mutableStateOf(false) }
    var saved by remember(mode.id) { mutableStateOf(false) }
    val question = questions.getOrNull(index)

    LaunchedEffect(index, answered, finished) {
        if (!answered && !finished) {
            secondsLeft = mode.seconds
            while (secondsLeft > 0 && !answered && !finished) {
                delay(1000)
                secondsLeft--
            }
            if (secondsLeft == 0 && !answered && !finished) {
                answered = true
                selected = -2
            }
        }
    }

    if (finished || question == null) {
        if (!saved) { saved = true; onComplete(correct) }
        QuizResultScreen(mode, correct, questions.size, onExit)
        return
    }

    Column(Modifier.fillMaxSize().background(RC.Background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowBack, "Geri", tint = RC.Deep, modifier = Modifier.clickable { onExit() }.padding(6.dp))
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) { Text(mode.title, color = RC.Deep, fontWeight = FontWeight.ExtraBold); Text("${index + 1}/${questions.size}", color = RC.NaturalGreen, fontSize = 11.sp) }
            Text("${secondsLeft}s", color = if (secondsLeft <= 10) Color(0xFFB3261E) else RC.Water, fontWeight = FontWeight.Black)
        }
        LinearProgressIndicator(progress = { (index + 1) / questions.size.toFloat() }, Modifier.fillMaxWidth().height(5.dp), color = RC.NaturalGreen, trackColor = RC.SurfaceSoft)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 16.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(question.topic, color = RC.Water, fontSize = 12.sp, fontWeight = FontWeight.Black); Text(question.text, color = RC.Deep, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(6.dp)) }
            items(question.options.indices.toList()) { optionIndex ->
                val bg = when {
                    !answered -> RC.Surface
                    optionIndex == question.correctIndex -> Color(0xFFDDF3E4)
                    optionIndex == selected -> Color(0xFFF8D9D6)
                    else -> RC.Surface
                }
                val accent = when {
                    !answered -> RC.SurfaceSoft
                    optionIndex == question.correctIndex -> Color(0xFF2E7D32)
                    optionIndex == selected -> Color(0xFFC62828)
                    else -> RC.SurfaceSoft
                }
                Card(Modifier.fillMaxWidth().clickable(enabled = !answered) { selected = optionIndex; answered = true; if (optionIndex == question.correctIndex) correct++ }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(bg), elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("${'A' + optionIndex}", color = accent, fontWeight = FontWeight.Black, modifier = Modifier.width(28.dp)); Text(question.options[optionIndex], color = RC.Deep, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
            if (answered) {
                item { RetentionCard(accent = if (selected == question.correctIndex) Color(0xFF2E7D32) else Color(0xFFC62828)) { Text(if (selected == question.correctIndex) "✓ Doğru" else if (selected == -2) "⏱ Süre doldu" else "✕ Yanlış", fontWeight = FontWeight.Black, color = RC.Deep); Text(question.explanation, color = Color(0xFF60756A), fontSize = 12.sp); Spacer(Modifier.height(8.dp)); Text("Dikkat: ${question.topic}", color = RC.Water, fontWeight = FontWeight.Bold, fontSize = 11.sp) } }
                item { AppButton(if (index == questions.lastIndex) "Sonucu Gör" else "Sonraki Soru", Icons.Default.ArrowForward, RC.NaturalGreen) { if (index == questions.lastIndex) finished = true else { index++; selected = -1; answered = false } } }
            }
        }
    }
}

@Composable
private fun QuizResultScreen(mode: SharedGameMode, correct: Int, total: Int, onExit: () -> Unit) {
    val percent = if (total == 0) 0 else correct * 100 / total
    Column(Modifier.fillMaxSize().background(RC.Background).padding(22.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🏆", fontSize = 58.sp); Text("Tur tamamlandı", color = RC.Deep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text(mode.title, color = RC.NaturalGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(20.dp)); Text("$correct / $total", color = RC.Deep, fontSize = 40.sp, fontWeight = FontWeight.Black); Text("%$percent başarı", color = RC.Water, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); Text("+${mode.rewardXp + correct * 10} XP", color = RC.Warm, fontSize = 20.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(24.dp)); AppButton("Ana Sayfaya Dön", Icons.Default.Home, RC.NaturalGreen, onExit)
    }
}

@Composable
private fun RetentionCard(modifier: Modifier = Modifier, accent: Color = RC.NaturalGreen, dark: Boolean = false, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val click = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Card(modifier.fillMaxWidth().then(click), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(if (dark) RC.Deep else RC.Surface), elevation = CardDefaults.cardElevation(3.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(4.dp).height(1.dp).background(accent))
            Column(Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun AppButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = color), shape = RoundedCornerShape(14.dp)) { Icon(icon, null); Spacer(Modifier.width(8.dp)); Text(label, fontWeight = FontWeight.Bold) }
}

@Composable
private fun StatPill(value: String, label: String) { Card(colors = CardDefaults.cardColors(Color.White.copy(alpha = .12f)), shape = RoundedCornerShape(14.dp)) { Column(Modifier.padding(horizontal = 11.dp, vertical = 7.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp); Text(label, color = RC.Sky, fontSize = 9.sp) } } }

@Composable
private fun StatBox(value: String, label: String) { Card(Modifier.weight(1f), colors = CardDefaults.cardColors(RC.Surface), shape = RoundedCornerShape(16.dp)) { Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(value, color = RC.Deep, fontSize = 21.sp, fontWeight = FontWeight.Black); Text(label, color = Color(0xFF687B71), fontSize = 10.sp) } } }

@Composable
private fun MiniCard(icon: String, title: String, subtitle: String, accent: Color, onClick: () -> Unit) { RetentionCard(Modifier.weight(1f), accent, onClick = onClick) { Text(icon, fontSize = 24.sp); Text(title, color = RC.Deep, fontWeight = FontWeight.ExtraBold); Text(subtitle, color = Color(0xFF687B71), fontSize = 10.sp) } }

@Composable
private fun Section(title: String) { Text(title, Modifier.padding(horizontal = 18.dp), color = RC.Deep, fontSize = 18.sp, fontWeight = FontWeight.Black) }
