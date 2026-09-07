package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.MenuBook
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
    var quiz by remember { mutableStateOf(false) }
    val labels = listOf("Ana Sayfa", "Kütüphane", "Oyunlar", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.SportsEsports, Icons.Default.SportsEsports, Icons.Default.Person)
    Surface(Modifier.fillMaxSize(), color = Background) {
        if (quiz) QuizScreen { quiz = false }
        else Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                when (tab) {
                    0 -> HomeScreen({ quiz = true }, { tab = 1 })
                    1 -> LibraryScreen { quiz = true }
                    2 -> GamesScreen { quiz = true }
                    3 -> ArenaScreen()
                    else -> ProfileScreen()
                }
            }
            NavigationBar(Modifier.navigationBarsPadding(), containerColor = Color.White) {
                labels.forEachIndexed { i, label ->
                    NavigationBarItem(tab == i, { tab = i }, icon = { Icon(icons[i], label) }, label = { Text(label, fontSize = 10.sp) })
                }
            }
        }
    }
}

@Composable private fun HomeScreen(onQuiz: () -> Unit, onLibrary: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        item {
            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Navy, Color(0xFF0B4432))), RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(22.dp)) {
                Column {
                    Text("Yurdunu Bil", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("KPSS Önlisans • Türkiye Coğrafyası", color = Mint, fontSize = 13.sp)
                    Spacer(Modifier.height(18.dp))
                    Text("Öğren • çöz • oyna • yarış", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Pill("0", "Soru"); Pill("0 XP", "Seviye 1"); Pill("0 gün", "Seri") }
                }
            }
        }
        item { Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(20.dp)) { Text("Günün Coğrafya Görevi", fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("18 soruyu tamamla", color = Color.Gray, fontSize = 13.sp); Spacer(Modifier.height(14.dp)); Action("Coğrafya Testine Başla", Icons.Default.PlayArrow, onQuiz) } } }
        item { Title("Türkiye'yi Keşfet") }
        item { Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable(onClick = onLibrary), RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(Navy)) { Column(Modifier.padding(20.dp)) { Text("🗺️ Türkiye Haritası", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("81 ili ve 7 bölgeyi keşfet", color = Color.White.copy(alpha = .7f)); Spacer(Modifier.height(10.dp)); Text("Kütüphaneyi Aç →", color = Mint, fontWeight = FontWeight.Bold) } } }
        item { Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { Feature("İller", "81 il", Icons.Default.LocationCity, Modifier.weight(1f)); Feature("Yer Şekilleri", "Dağ • ova • plato", Icons.Default.Terrain, Modifier.weight(1f)) } }
        item { Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { Feature("Sular", "Akarsu • göl", Icons.Default.Water, Modifier.weight(1f)); Feature("Bölgeler", "7 bölge", Icons.Default.Explore, Modifier.weight(1f)) } }
    }
}

@Composable private fun LibraryScreen(onQuiz: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 22.dp, 20.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Kütüphane", fontSize = 29.sp, fontWeight = FontWeight.ExtraBold); Text("KPSS Coğrafya Konu Bankası", color = Green, fontWeight = FontWeight.Bold) }
        item { MapCard() }
        item { Title("12 Ana Konu") }
        items(GeographyData.topics) { TopicCard(it, onQuiz) }
        item { Title("İllerden Öğren") }
        items(GeographyData.provinces) { ProvinceCard(it) }
    }
}

@Composable private fun MapCard() { Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Navy)) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Türkiye Haritası", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("İller ve coğrafi ipuçları", color = Color.White.copy(alpha = .65f), fontSize = 12.sp) }; Text("81 İL", color = Mint, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(15.dp)); Box(Modifier.fillMaxWidth().height(145.dp).background(Color(0xFF0E4A38), RoundedCornerShape(20.dp)), Alignment.Center) { Text("🇹🇷\nETKİLEŞİMLİ HARİTA", color = Mint, fontSize = 18.sp, fontWeight = FontWeight.Bold) } } } }
@Composable private fun TopicCard(t: Topic, onQuiz: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onQuiz), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(t.icon, fontSize = 28.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(t.title, fontSize = 17.sp, fontWeight = FontWeight.Bold); Text(t.subtitle, color = Color.Gray, fontSize = 12.sp); Text("${t.lessons.size} alt konu", color = Green, fontSize = 11.sp) }; Text("${t.progress}%", color = Green, fontWeight = FontWeight.ExtraBold) } } }
@Composable private fun ProvinceCard(p: Province) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("📍", fontSize = 20.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold); Text(p.region, color = Green, fontSize = 11.sp); Text(p.clue, color = Color.Gray, fontSize = 11.sp) }; Icon(Icons.Default.ArrowForward, null, tint = Green) } } }

@Composable private fun GamesScreen(onQuiz: () -> Unit) { LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("Oyun Merkezi", fontSize = 29.sp, fontWeight = FontWeight.ExtraBold); Text("Ezberleme. Oyna, keşfet, hatırla.", color = Green, fontWeight = FontWeight.Bold) }; items(GeographyData.games) { g -> GameCard(g, onQuiz) }; item { Title("Etkinlikler") }; item { Event("🗺️", "Günün Harita Görevi", "5 ili 60 saniyede bul", "+50 XP") }; item { Event("🔥", "7 Bölge Serisi", "Her bölgeden doğru cevap", "+100 XP") }; item { Event("🏆", "Türkiye Ustası", "Haftalık 100 soruluk meydan okuma", "+500 XP") } } }
@Composable private fun GameCard(g: GameMode, onClick: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(g.icon, fontSize = 30.sp); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(g.title, fontWeight = FontWeight.Bold, fontSize = 17.sp); Text(g.subtitle, color = Color.Gray, fontSize = 12.sp) }; Icon(Icons.Default.PlayArrow, null, tint = Green) } } }
@Composable private fun Event(icon: String, title: String, sub: String, reward: String) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color(0xFFEAF8F1))) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 25.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(sub, fontSize = 12.sp, color = Color(0xFF527065)) }; Text(reward, color = Green, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp) } } }

@Composable private fun ArenaScreen() { LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 22.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) { item { Text("Arena", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold); Text("Bilgini rakiplere karşı kanıtla.", color = Green, fontWeight = FontWeight.Bold) }; item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Navy)) { Column(Modifier.padding(20.dp)) { Text("🏆 Sezon 1", color = Mint, fontWeight = FontWeight.Bold); Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); Text("Bronz → Gümüş → Altın → Coğrafya Ustası", color = Color.White.copy(alpha = .7f)); Spacer(Modifier.height(15.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Pill("0", "Galibiyet"); Pill("0", "Puan"); Pill("#—", "Sıra") } } } }; items(GeographyData.arena) { m -> Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(m.icon, fontSize = 30.sp); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(m.title, fontWeight = FontWeight.Bold); Text(m.subtitle, color = Color.Gray, fontSize = 12.sp); Text(m.reward, color = Green, fontWeight = FontWeight.Bold, fontSize = 11.sp) }; Icon(Icons.Default.SportsEsports, null, tint = Green) } } }; item { Text("1v1, hız ve bölge modları hazır. Çevrim içi eşleştirme ve liderlik Supabase katmanıyla bağlanacak.", color = Color.Gray, fontSize = 13.sp) } } }

@Composable private fun ProfileScreen() { LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) { item { Text("Profil", fontSize = 29.sp, fontWeight = FontWeight.ExtraBold); Text("Coğrafya yolculuğun", color = Green, fontWeight = FontWeight.Bold) }; item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(Navy)) { Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(60.dp).background(Green, CircleShape), Alignment.Center) { Icon(Icons.Default.Person, null, tint = Color.White) }; Spacer(Modifier.width(14.dp)); Column { Text("Gezgin", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("Seviye 1 • 0 XP", color = Mint) } } } }; item { Stat("⭐", "XP", "0 / 100") }; item { Stat("🔥", "Günlük Seri", "0 gün") }; item { Stat("🗺️", "Keşfedilen İller", "0 / 81") }; item { Stat("🏆", "Başarımlar", "0 / 30") } } }
@Composable private fun Stat(icon: String, title: String, value: String) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 24.sp); Spacer(Modifier.width(12.dp)); Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold); Text(value, color = Green, fontWeight = FontWeight.ExtraBold) } } }

@Composable private fun QuizScreen(onExit: () -> Unit) { val engine = remember { QuizEngine(GeographyQuestions.all) }; var selected by remember { mutableStateOf<Int?>(null) }; var answered by remember { mutableStateOf(false) }; var finished by remember { mutableStateOf(false) }; val q = engine.current; if (finished) { ResultScreen(engine.result(), onExit); return }; Column(Modifier.fillMaxSize().background(Background).padding(20.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Soru ${engine.currentIndex + 1} / ${engine.size}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp); Text(q.topic, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold) }; Text("+10 XP", color = Gold, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.height(18.dp)); Text(q.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(16.dp)); LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) { itemsIndexed(q.options) { i, option -> val correct = i == q.correctIndex; val wrong = answered && selected == i && !correct; val bg = if (answered && correct) Color(0xFFDDF7E9) else if (wrong) Color(0xFFFFE2E2) else if (selected == i) Color(0xFFE4F1FF) else Color.White; Card(Modifier.fillMaxWidth().clickable(enabled = !answered) { selected = i }, RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(bg)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(35.dp).background(Color(0xFFF0F5F3), CircleShape), Alignment.Center) { Text("${'A' + i}", fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.width(11.dp)); Text(option, Modifier.weight(1f)); if (answered && correct) Icon(Icons.Default.CheckCircle, null, tint = Green) } } } }; if (answered) Card(Modifier.fillMaxWidth().padding(vertical = 9.dp), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(14.dp)) { Text(if (selected == q.correctIndex) "Doğru cevap! 🎉" else "Bu kez olmadı.", fontWeight = FontWeight.Bold, color = if (selected == q.correctIndex) Green else MaterialTheme.colorScheme.error); Spacer(Modifier.height(4.dp)); Text(q.explanation, fontSize = 13.sp) } }; Action("${if (!answered) "Cevabı Kontrol Et" else if (engine.currentIndex == engine.size - 1) "Sonucu Gör" else "Sonraki Soru"}", Icons.Default.ArrowForward) { if (!answered) { if (selected != null) { engine.answer(selected); answered = true } } else if (engine.currentIndex == engine.size - 1) finished = true else { selected = null; answered = false } } } }

@Composable private fun ResultScreen(r: QuizResult, onExit: () -> Unit) { Column(Modifier.fillMaxSize().background(Background).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Box(Modifier.size(88.dp).background(Color(0xFFE4F8ED), CircleShape), Alignment.Center) { Icon(Icons.Default.Whatshot, null, tint = Gold, modifier = Modifier.size(45.dp)) }; Spacer(Modifier.height(18.dp)); Text("Coğrafya turu tamamlandı!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(16.dp)); Text("${r.correct} doğru • ${r.wrong} yanlış • ${r.blank} boş"); Text("+${r.xp} XP", color = Green, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 9.dp)); Spacer(Modifier.height(22.dp)); Action("Ana Sayfaya Dön", Icons.Default.Home, onExit) } }

@Composable private fun Pill(value: String, label: String) { Column(Modifier.background(Color.White.copy(alpha = .1f), RoundedCornerShape(13.dp)).padding(horizontal = 13.dp, vertical = 8.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold); Text(label, color = Color.White.copy(alpha = .65f), fontSize = 9.sp) } }
@Composable private fun Title(text: String) { Text(text, Modifier.padding(horizontal = 20.dp), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold) }
@Composable private fun Action(text: String, icon: ImageVector, onClick: () -> Unit) { Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, Color(0xFF0E9B66))), RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(vertical = 14.dp), Alignment.Center) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text(text, color = Color.White, fontWeight = FontWeight.Bold) } } }
@Composable private fun Feature(title: String, sub: String, icon: ImageVector, modifier: Modifier) { Card(modifier, RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(15.dp)) { Icon(icon, null, tint = Navy, modifier = Modifier.size(25.dp)); Spacer(Modifier.height(8.dp)); Text(title, fontWeight = FontWeight.Bold); Text(sub, color = Color.Gray, fontSize = 11.sp) } } }
