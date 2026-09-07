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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Water
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RetentionApp() }
    }
}

private val RC = YurdunuBilColors

@Composable
private fun RetentionApp() {
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf(false) }
    val labels = listOf("Ana Sayfa", "Kütüphane", "Oyunlar", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.SportsEsports, Icons.Default.SportsEsports, Icons.Default.Person)

    Surface(Modifier.fillMaxSize(), color = RC.Background) {
        if (quiz) {
            RetentionQuiz(onExit = { quiz = false })
        } else {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f)) {
                    AnimatedContent(targetState = tab, label = "retention_page") { page ->
                        when (page) {
                            0 -> RetentionHome(onQuiz = { quiz = true }, onLibrary = { tab = 1 })
                            1 -> RetentionLibrary(onQuiz = { quiz = true })
                            2 -> RetentionGames(onQuiz = { quiz = true })
                            3 -> RetentionArena()
                            else -> RetentionProfile()
                        }
                    }
                }
                NavigationBar(
                    Modifier.navigationBarsPadding(),
                    containerColor = RC.Surface,
                    tonalElevation = 8.dp
                ) {
                    labels.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            icon = { Icon(icons[index], label) },
                            label = { Text(label, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RetentionHome(onQuiz: () -> Unit, onLibrary: () -> Unit) {
    val facts = remember { CurrentFactFeed.all }
    var factIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(180_000)
            factIndex = (factIndex + 1) % facts.size
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { RetentionHero() }
        item {
            RetentionCard(modifier = Modifier.padding(horizontal = 18.dp), accent = RC.NaturalGreen) {
                Text("🎯 Bugünün Coğrafya Görevi", color = RC.Deep, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("18 soruluk KPSS tipi görev • yaklaşık 10 dakika", color = Color(0xFF60756A), fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier.fillMaxWidth().height(7.dp),
                    color = RC.NaturalGreen,
                    trackColor = RC.SurfaceSoft
                )
                Spacer(Modifier.height(12.dp))
                RetentionButton("Göreve Başla", Icons.Default.PlayArrow, RC.NaturalGreen, onQuiz)
            }
        }
        item { LiveFactCard(facts[factIndex]) }
        item { RetentionSectionTitle("Bugün bunları unutma") }
        item {
            RetentionCard(modifier = Modifier.padding(horizontal = 18.dp), accent = RC.Warm) {
                Text("🧠 3 dakikalık tekrar", color = RC.Deep, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Text("Maden • tarım • nüfus • yüzölçümü gibi ezber bilgilerini hızlıca tazele.", color = Color(0xFF60756A), fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                RetentionButton("Kütüphaneye Git", Icons.Default.MenuBook, RC.Warm, onLibrary)
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RetentionMini("Soru Bankası", "${FullQuestionBank.all.size} soru", Icons.Default.Quiz, RC.NaturalGreen, Modifier.weight(1f))
                RetentionMini("Konu Bankası", "${LibraryContent.topics.size} konu • 60+ bilgi", Icons.Default.MenuBook, RC.Water, Modifier.weight(1f))
            }
        }
        item {
            RetentionCard(modifier = Modifier.padding(horizontal = 18.dp), accent = RC.Water, dark = true) {
                Text("⚔️ Arena'ya hazırlan", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Aynı kaliteli bilgi havuzuyla önce kendini ölç, sonra 1v1'e gir.", color = RC.Sky, fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                Text("10 soru • hız bonusu • XP", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RetentionHero() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(RC.Deep, RC.Forest, Color(0xFF286A4C))),
                RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp)
            )
            .padding(22.dp)
    ) {
        Column {
            Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("KPSS Önlisans • Türkiye Coğrafyası", color = RC.Sky, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(14.dp))
            Text("Bugün de Türkiye'yi\nbiraz daha iyi bil.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RetentionStat("0", "Soru")
                RetentionStat("0 XP", "Seviye 1")
                RetentionStat("0 gün", "Seri")
            }
        }
    }
}

@Composable
private fun LiveFactCard(fact: CurrentFact) {
    RetentionCard(modifier = Modifier.padding(horizontal = 18.dp), accent = RC.Water) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("📡 VERİ AKIŞI • 3 DK", color = RC.Water, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(fact.title, color = RC.Deep, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(fact.value, color = RC.NaturalGreen, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(fact.detail, color = Color(0xFF60756A), fontSize = 12.sp)
                Spacer(Modifier.height(7.dp))
                Text("Kaynak: ${fact.source} • ${fact.year}", color = Color(0xFF809087), fontSize = 10.sp)
            }
            Text(fact.icon, fontSize = 34.sp)
        }
    }
}

@Composable
private fun RetentionLibrary(onQuiz: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Kütüphane", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep)
            Text("Konu → bilgi → harita → soru. Hepsi aynı yerde.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        item { RetentionMapCard() }
        item { RetentionSectionTitle("Konu bankası • 12 ana alan") }
        items(LibraryContent.topics) { topic ->
            RetentionCard(accent = RC.NaturalGreen) {
                Text("${topic.icon}  ${topic.title}", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = RC.Deep)
                Text(topic.summary, color = Color(0xFF687B71), fontSize = 12.sp)
                Spacer(Modifier.height(9.dp))
                topic.cards.forEach { card ->
                    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Text("•", color = RC.NaturalGreen, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(7.dp))
                        Column {
                            Text(card.title, color = RC.Deep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(card.body, color = Color(0xFF687B71), fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                RetentionButton("Bu konudan soru çöz", Icons.Default.Quiz, RC.NaturalGreen, onQuiz)
            }
        }
        item { RetentionSectionTitle("İllerden öğren") }
        items(GeographyData.provinces) { province ->
            RetentionCard(accent = RC.Water) {
                Text("📍 ${province.name}", fontWeight = FontWeight.ExtraBold, color = RC.Deep)
                Text(province.region, color = RC.Water, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(province.clue, color = Color(0xFF687B71), fontSize = 11.sp)
                province.facts.forEach { Text("• $it", color = Color(0xFF687B71), fontSize = 11.sp) }
            }
        }
    }
}

@Composable
private fun RetentionMapCard() {
    RetentionCard(accent = RC.Sky, dark = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Türkiye Haritası", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text("İller • bölgeler • madenler • tarım", color = RC.Sky, fontSize = 12.sp)
            }
            Text("81 İL", color = RC.Sky, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.fillMaxWidth().height(115.dp).background(
                Brush.linearGradient(listOf(RC.Forest, RC.Water.copy(alpha = .85f))),
                RoundedCornerShape(18.dp)
            ),
            Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, null, tint = RC.Sky, modifier = Modifier.size(42.dp))
                Text("HARİTA DESTEKLİ ÖĞRENME", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RetentionGames(onQuiz: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Oyun Merkezi", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep)
            Text("Öğren • oyna • hatırla", color = RC.NaturalGreen, fontWeight = FontWeight.Bold)
        }
        val games = listOf(
            Triple("🗺️", "Haritada Bul", "İl, bölge ve madenleri haritada yakala"),
            Triple("⚡", "Hızlı 10", "10 KPSS tipi soruyu süreyle çöz"),
            Triple("🎯", "Doğru mu Yanlış mı?", "Bilgiyi saniyeler içinde değerlendir"),
            Triple("🧩", "Eşleştir", "İl • ürün • maden • özellik eşleştir"),
            Triple("🔥", "Zincir", "Doğru cevaplarla çarpanı büyüt")
        )
        items(games) { game ->
            RetentionCard(accent = RC.Water, onClick = onQuiz) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(game.first, fontSize = 30.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(game.second, color = RC.Deep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        Text(game.third, color = Color(0xFF687B71), fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = RC.NaturalGreen)
                }
            }
        }
        item { RetentionSectionTitle("Günün etkinlikleri") }
        item { EventCard("🗺️", "Harita Görevi", "5 ili 60 saniyede bul", "+50 XP", RC.Water) }
        item { EventCard("🌿", "7 Bölge Serisi", "Her bölgeden doğru cevap", "+100 XP", RC.Leaf) }
        item { EventCard("🏆", "Türkiye Ustası", "Haftalık 100 soruluk meydan okuma", "+500 XP", RC.Warm) }
    }
}

@Composable
private fun RetentionArena() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            Text("Arena", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep)
            Text("Bilgini göster, zirveye çık.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold)
        }
        item {
            RetentionCard(dark = true, accent = RC.Warm) {
                Text("SEZON 1", color = RC.Sky, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Spacer(Modifier.height(5.dp))
                Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("Bronz → Gümüş → Altın → Coğrafya Ustası", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))
                Text("1v1 multiplayer altyapısı Supabase Realtime ile bağlanacak.", color = RC.Sky, fontSize = 12.sp)
            }
        }
        listOf(
            Triple("⚔️", "1v1 Bilgi Düellosu", "+100 XP"),
            Triple("🗺️", "Bölge Savaşı", "+150 XP"),
            Triple("⚡", "Hız Arenası", "+200 XP"),
            Triple("🏆", "Türkiye Ustası", "+250 XP")
        ).forEach { mode ->
            item {
                RetentionCard(accent = RC.Warm) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(mode.first, fontSize = 31.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(mode.second, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = RC.Deep)
                            Text(mode.third, color = RC.Warm, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.SportsEsports, null, tint = RC.NaturalGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun RetentionProfile() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            Text("Profil", fontSize = 30.sp, fontWeight = FontWeight.Black, color = RC.Deep)
            Text("İlerlemeni gör, eksiklerini kapat.", color = RC.NaturalGreen, fontWeight = FontWeight.Bold)
        }
        item {
            RetentionCard(dark = true, accent = RC.Leaf) {
                Text("COĞRAFYA ÖĞRENCİSİ", color = RC.Sky, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text("Seviye 1", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("0 XP • 0 soru • 0 gün seri", color = Color.White.copy(alpha = .75f), fontSize = 12.sp)
            }
        }
        item {
            RetentionCard(accent = RC.Warm) {
                Text("🎯 Hedef", color = RC.Deep, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text("Her gün en az 18 coğrafya sorusu + 1 kısa tekrar.", color = Color(0xFF687B71), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RetentionQuiz(onExit: () -> Unit) {
    val questions = remember { FullQuestionBank.all.shuffled().take(18) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var correct by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    if (finished) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("Test tamamlandı 🎉", color = RC.Deep, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("$correct / ${questions.size} doğru", color = RC.NaturalGreen, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
            item {
                RetentionCard(accent = RC.NaturalGreen) {
                    Text("Kazandığın XP", color = RC.Deep, fontWeight = FontWeight.Bold)
                    Text("${correct * 10} XP", color = RC.NaturalGreen, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("Yanlışlarını tekrar ederek aynı konuyu yeniden çöz.", color = Color(0xFF687B71), fontSize = 12.sp)
                }
            }
            item { RetentionButton("Ana sayfaya dön", Icons.Default.Home, RC.NaturalGreen, onExit) }
        }
        return
    }

    val question = questions[index]
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Coğrafya Testi", color = RC.NaturalGreen, fontWeight = FontWeight.Black)
            Text("Soru ${index + 1} / ${questions.size}", color = RC.Deep, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        item {
            RetentionCard(accent = RC.NaturalGreen) {
                Text(question.topic, color = RC.Water, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(5.dp))
                Text(question.text, color = RC.Deep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        items(question.options.indices.toList()) { optionIndex ->
            val isSelected = selected == optionIndex
            val isCorrect = optionIndex == question.correctIndex
            val accent = when {
                selected == null -> RC.Water
                isCorrect -> RC.NaturalGreen
                isSelected -> Color(0xFFC94F4F)
                else -> RC.SurfaceSoft
            }
            RetentionCard(accent = accent, onClick = { if (selected == null) { selected = optionIndex; if (isCorrect) correct++ } }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${'A' + optionIndex}", color = accent, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(question.options[optionIndex], color = RC.Deep, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
        if (selected != null) {
            item {
                RetentionCard(accent = if (selected == question.correctIndex) RC.NaturalGreen else Color(0xFFC94F4F)) {
                    Text(if (selected == question.correctIndex) "✓ Doğru" else "✕ Yanlış", color = RC.Deep, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(5.dp))
                    Text(question.explanation, color = Color(0xFF687B71), fontSize = 12.sp)
                }
            }
            item {
                RetentionButton(
                    if (index == questions.lastIndex) "Sonucu Gör" else "Sonraki Soru",
                    Icons.Default.ArrowForward,
                    RC.NaturalGreen
                ) {
                    if (index == questions.lastIndex) finished = true else {
                        index++
                        selected = null
                    }
                }
            }
        }
    }
}

@Composable
private fun RetentionCard(
    modifier: Modifier = Modifier,
    accent: Color = RC.NaturalGreen,
    dark: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val background = if (dark) RC.Deep else RC.Surface
    Card(
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().height(5.dp).background(accent))
            Column(Modifier.padding(17.dp), content = content)
        }
    }
}

@Composable
private fun RetentionButton(text: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = accent),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(7.dp))
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RetentionMini(title: String, value: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    RetentionCard(modifier = modifier, accent = accent) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(25.dp))
        Spacer(Modifier.height(7.dp))
        Text(title, color = RC.Deep, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        Text(value, color = Color(0xFF687B71), fontSize = 11.sp)
    }
}

@Composable
private fun RetentionStat(value: String, label: String) {
    Surface(shape = RoundedCornerShape(13.dp), color = Color.White.copy(alpha = .12f)) {
        Column(Modifier.padding(horizontal = 11.dp, vertical = 7.dp)) {
            Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(label, color = RC.Sky, fontSize = 9.sp)
        }
    }
}

@Composable
private fun RetentionSectionTitle(text: String) {
    Text(text, modifier = Modifier.padding(horizontal = 18.dp), color = RC.Deep, fontSize = 19.sp, fontWeight = FontWeight.Black)
}

@Composable
private fun EventCard(icon: String, title: String, subtitle: String, reward: String, accent: Color) {
    RetentionCard(modifier = Modifier.padding(horizontal = 18.dp), accent = accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = RC.Deep, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = Color(0xFF687B71), fontSize = 11.sp)
            }
            Text(reward, color = accent, fontWeight = FontWeight.Black, fontSize = 11.sp)
        }
    }
}
