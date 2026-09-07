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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
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
private val Page = Color(0xFFF5F9F7)

@Composable
private fun YurdunuBilApp() {
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf(false) }
    val labels = listOf("Ana Sayfa", "Keşfet", "Oyunlar", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.Map, Icons.Default.Explore, Icons.Default.SportsEsports, Icons.Default.Person)
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Page) {
            if (quiz) QuizScreen { quiz = false } else Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f)) {
                    AnimatedContent(targetState = tab, label = "main_tab") { page ->
                        when (page) {
                            0 -> HomeScreen({ quiz = true }, { tab = 1 })
                            1 -> ExploreScreen { quiz = true }
                            2 -> GamesScreen { quiz = true }
                            3 -> ArenaScreen()
                            else -> ProfileScreen()
                        }
                    }
                }
                NavigationBar(Modifier.navigationBarsPadding(), containerColor = Color.White) {
                    labels.forEachIndexed { i, label -> NavigationBarItem(tab == i, { tab = i }, icon = { Icon(icons[i], label, Modifier.size(23.dp)) }, label = { Text(label, fontSize = 10.sp) }) }
                }
            }
        }
    }
}

@Composable private fun HomeScreen(onStart: () -> Unit, onExplore: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(.96f, 1.05f, infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "fire")
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        item { Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Navy, Color(0xFF0B4432))), RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(22.dp)) { Column { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold); Text("KPSS Önlisans • Türkiye Coğrafyası", color = Mint.copy(alpha = .85f), fontSize = 13.sp) }; Box(Modifier.size(52.dp).background(Color.White.copy(alpha = .12f), CircleShape), Alignment.Center) { Icon(Icons.Default.Whatshot, null, tint = Gold, Modifier.size(29.dp).scale(scale)) } }; Spacer(Modifier.height(20.dp)); Text("Bugün Türkiye'yi biraz daha iyi tanı.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold); Text("18 soruluk coğrafya rotanı tamamla.", color = Color.White.copy(alpha = .7f), fontSize = 13.sp); Spacer(Modifier.height(15.dp)); Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { StatPill("0", "Soru"); StatPill("0 XP", "Seviye 1"); StatPill("0 gün", "Seri") } } } }
        item { Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)) { Column(Modifier.padding(20.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Günün Coğrafya Görevi", fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("18 soruyu tamamla", color = Color.Gray, fontSize = 13.sp) }; Text("0 / 18", color = Green, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.height(13.dp)); Box(Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFE4EEE9), RoundedCornerShape(8.dp))); Spacer(Modifier.height(14.dp)); ActionButton("Coğrafya Testine Başla", Icons.Default.PlayArrow, onStart) } } }
        item { SectionTitle("Türkiye'yi Keşfet") }
        item { Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable(onClick = onExplore), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Navy)) { Column(Modifier.padding(20.dp)) { Text("🗺️  Türkiye Haritası", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(6.dp)); Text("81 ili, 7 bölgeyi ve coğrafi özellikleri keşfet.", color = Color.White.copy(alpha = .72f)); Spacer(Modifier.height(14.dp)); Text("Haritayı Aç  →", color = Mint, fontWeight = FontWeight.Bold) } } }
        item { SectionTitle("Hızlı Rotalar") }
        item { QuickRow("📚", "Konu Bankası", "12 ana konu • harita destekli", onExplore) }
        item { QuickRow("🎮", "Oyun Merkezi", "Haritada Bul • Bölge Avı • Eşleştir", onExplore) }
        item { QuickRow("🏆", "Arena", "1v1 • Hız Arenası • Bölge Savaşı", {}) }
    }
}

@Composable private fun StatPill(value: String, label: String) { Column(Modifier.background(Color.White.copy(alpha = .1f), RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 8.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold); Text(label, color = Color.White.copy(alpha = .62f), fontSize = 9.sp) } }
@Composable private fun SectionTitle(text: String) { Text(text, Modifier.padding(horizontal = 20.dp), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold) }
@Composable private fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) { Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0E9B66))), RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(vertical = 14.dp), Alignment.Center) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text(text, color = Color.White, fontWeight = FontWeight.Bold) } } }
@Composable private fun QuickRow(icon: String, title: String, subtitle: String, onClick: () -> Unit) { Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 27.sp); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(subtitle, color = Color.Gray, fontSize = 12.sp) }; Icon(Icons.Default.ArrowForward, null, tint = Green) } } }

@Composable private fun ExploreScreen(onQuiz: () -> Unit) {
    var selected by remember { mutableStateOf<Province?>(null) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 22.dp, 20.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Keşfet", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold); Text("KPSS Coğrafya Kütüphanesi", color = Green, fontWeight = FontWeight.Bold) }
        item { MapPreview { selected = it } }
        item { Text("Konu Bankası", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold) }
        items(GeographyData.topics) { TopicCard(it, onQuiz) }
        item { Text("İllerden Öğren", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp)) }
        items(GeographyData.provinces) { ProvinceRow(it) { selected = it } }
        item { selected?.let { ProvinceDetail(it) } }
    }
}

@Composable private fun MapPreview(onProvince: (Province) -> Unit) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Navy)) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Türkiye Haritası", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("İl seç, ipucunu öğren", color = Color.White.copy(alpha = .65f), fontSize = 12.sp) }; Text("81 İL", color = Mint, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(16.dp)); Box(Modifier.fillMaxWidth().height(150.dp).background(Color(0xFF0E4A38), RoundedCornerShape(20.dp)), Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("🇹🇷", fontSize = 58.sp); Text("ETKİLEŞİMLİ HARİTA", color = Mint, fontSize = 11.sp, fontWeight = FontWeight.Bold) } }; Spacer(Modifier.height(12.dp)); Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { GeographyData.provinces.take(5).forEach { p -> Box(Modifier.background(Color.White.copy(alpha = .09f), RoundedCornerShape(10.dp)).clickable { onProvince(p) }.padding(horizontal = 9.dp, vertical = 7.dp)) { Text(p.name, color = Color.White, fontSize = 10.sp) } } } } } }
@Composable private fun TopicCard(topic: Topic, onQuiz: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onQuiz), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(17.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(topic.icon, fontSize = 28.sp); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(topic.title, fontSize = 17.sp, fontWeight = FontWeight.Bold); Text(topic.subtitle, color = Color.Gray, fontSize = 12.sp) }; Text("${topic.progress}%", color = Green, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.height(11.dp)); Box(Modifier.fillMaxWidth().height(7.dp).background(Color(0xFFE7EFEB), RoundedCornerShape(7.dp))); Spacer(Modifier.height(9.dp)); Text("${topic.lessons.size} alt konu • Öğren ve test et", color = Color(0xFF527065), fontSize = 11.sp) } } }
@Composable private fun ProvinceRow(p: Province, onClick: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("📍", fontSize = 20.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold); Text(p.region, color = Green, fontSize = 11.sp) }; Icon(Icons.Default.ArrowForward, null, tint = Green) } } }
@Composable private fun ProvinceDetail(p: Province) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(Color(0xFFE8F7EF))) { Column(Modifier.padding(18.dp)) { Text(p.name, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold); Text(p.clue, color = Color(0xFF32614E)); Spacer(Modifier.height(10.dp)); p.facts.forEach { Text("• $it", fontSize = 13.sp, modifier = Modifier.padding(vertical = 2.dp)) } } } }

@Composable private fun GamesScreen(onQuiz: () -> Unit) { LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 22.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) { item { Text("Oyun Merkezi", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold); Text("Ezberleme. Oyna, keşfet, hatırla.", color = Green, fontWeight = FontWeight.Bold) }; items(GeographyData.games) { GameCard(it, onQuiz) }; item { Text("Etkinlikler", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp)) }; item { EventCard("🗺️", "Günün Harita Görevi", "5 ili 60 saniyede bul", "+50 XP") }; item { EventCard("🔥", "7 Bölge Serisi", "Her bölgeden bir doğru cevap", "+100 XP") }; item { EventCard("🏆", "Türkiye Ustası", "Haftalık 100 soruluk meydan okuma", "+500 XP") } } }
@Composable private fun GameCard(game: GameMode, onClick: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(game.icon, fontSize = 31.sp); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(game.title, fontSize = 17.sp, fontWeight = FontWeight.Bold); Text(game.subtitle, color = Color.Gray, fontSize = 12.sp) }; Icon(Icons.Default.PlayArrow, null, tint = Green) } } }
@Composable private fun EventCard(icon: String, title: String, subtitle: String, reward: String) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(Color(0xFFEAF8F1))) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 26.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, fontSize = 12.sp, color = Color(0xFF527065)) }; Text(reward, color = Green, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp) } } }

@Composable private fun ArenaScreen() { LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { item { Text("Arena", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold); Text("Bilgini rakiplere karşı kanıtla.", color = Green, fontWeight = FontWeight.Bold) }; item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(25.dp), colors = CardDefaults.cardColors(Navy)) { Column(Modifier.padding(20.dp)) { Text("🏆 Sezon 1", color = Mint, fontWeight = FontWeight.Bold); Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); Text("Bronz → Gümüş → Altın → Coğrafya Ustası", color = Color.White.copy(alpha = .68f)); Spacer(Modifier.height(15.dp)); Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { StatPill("0", "Galibiyet"); StatPill("0", "Puan"); StatPill("#—", "Sıra") } } } }; items(GeographyData.arena) { ArenaCard(it) }; item { Text("Arena kuralları", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("Doğru cevap puan kazandırır. Seri cevaplar çarpan getirir. Günlük ve sezonluk sıralamalar çevrim içi hesap sistemi bağlandığında senkronize edilecek.", color = Color.Gray, fontSize = 13.sp) } } }
@Composable private fun ArenaCard(mode: ArenaMode) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(mode.icon, fontSize = 30.sp); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(mode.title, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(mode.subtitle, color = Color.Gray, fontSize = 12.sp); Text(mode.reward, color = Green, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp)) }; Icon(Icons.Default.SportsEsports, null, tint = Green) } } }

@Composable private fun ProfileScreen() { LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { item { Text("Profil", fontSize = 29.sp, fontWeight = FontWeight.ExtraBold); Text("Coğrafya yolculuğun", color = Green, fontWeight = FontWeight.Bold) }; item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Navy)) { Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(62.dp).background(Green, CircleShape), Alignment.Center) { Icon(Icons.Default.Person, null, tint = Color.White, Modifier.size(34.dp)) }; Spacer(Modifier.width(15.dp)); Column { Text("Gezgin", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("Seviye 1 • 0 XP", color = Mint) } } } }; item { ProfileStat("⭐", "XP", "0 / 100") }; item { ProfileStat("🔥", "Günlük Seri", "0 gün") }; item { ProfileStat("🗺️", "Keşfedilen İller", "0 / 81") }; item { ProfileStat("🏆", "Başarımlar", "0 / 30") } } }
@Composable private fun ProfileStat(icon: String, title: String, value: String) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 25.sp); Spacer(Modifier.width(13.dp)); Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold); Text(value, color = Green, fontWeight = FontWeight.ExtraBold) } } }

@Composable private fun QuizScreen(onExit: () -> Unit) { val engine = remember { QuizEngine(SampleQuestions.all) }; var selected by remember { mutableStateOf<Int?>(null) }; var answered by remember { mutableStateOf(false) }; var result by remember { mutableStateOf<QuizResult?>(null) }; val q = engine.current; if (result != null) { ResultScreen(result!!, onExit); return }; Column(Modifier.fillMaxSize().background(Page).padding(20.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Soru ${engine.currentIndex + 1} / ${engine.size}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp); Text(q.topic, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold) }; Text("+${if (selected == q.correctIndex) 10 else 0} XP", color = Gold, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.height(18.dp)); Text(q.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(18.dp)); LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) { itemsIndexed(q.options) { i, option -> val correct = i == q.correctIndex; val wrong = answered && selected == i && !correct; val bg = when { answered && correct -> Color(0xFFDDF7E9); wrong -> Color(0xFFFFE2E2); selected == i -> Color(0xFFE4F1FF); else -> Color.White }; Card(Modifier.fillMaxWidth().clickable(enabled = !answered) { selected = i }, RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(bg)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(36.dp).background(Color(0xFFF0F5F3), CircleShape), Alignment.Center) { Text("${'A' + i}", fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.width(12.dp)); Text(option, Modifier.weight(1f)); if (answered && correct) Icon(Icons.Default.CheckCircle, null, tint = Green) } } } }; AnimatedVisibility(answered, enter = fadeIn() + scaleIn()) { Card(Modifier.fillMaxWidth().padding(vertical = 10.dp), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(15.dp)) { Text(if (selected == q.correctIndex) "Doğru cevap! 🎉" else "Bu kez olmadı.", fontWeight = FontWeight.Bold, color = if (selected == q.correctIndex) Green else MaterialTheme.colorScheme.error); Spacer(Modifier.height(4.dp)); Text(q.explanation, fontSize = 13.sp) } } }; Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0D9D67))), RoundedCornerShape(16.dp)).clickable(enabled = answered || selected != null) { if (!answered) { engine.answer(selected); answered = true } else if (engine.currentIndex == engine.size - 1) result = engine.result() else { selected = null; answered = false } }.padding(vertical = 15.dp), Alignment.Center) { Text(if (!answered) "Cevabı Kontrol Et" else if (engine.currentIndex == engine.size - 1) "Sonucu Gör" else "Sonraki Soru", color = Color.White, fontWeight = FontWeight.Bold) } } }

@Composable private fun ResultScreen(result: QuizResult, onExit: () -> Unit) { Column(Modifier.fillMaxSize().background(Page).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Box(Modifier.size(94.dp).background(Color(0xFFE7F8EF), CircleShape), Alignment.Center) { Icon(Icons.Default.Star, null, tint = Gold, Modifier.size(50.dp)) }; Spacer(Modifier.height(18.dp)); Text("Türkiye turu tamamlandı!", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); Text("${result.correct} doğru • ${result.wrong} yanlış • ${result.blank} boş", color = Color.Gray); Spacer(Modifier.height(7.dp)); Text("+${result.xp} XP", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Green); Spacer(Modifier.height(25.dp)); ActionButton("Ana Sayfaya Dön", Icons.Default.Home, onExit) }
}
