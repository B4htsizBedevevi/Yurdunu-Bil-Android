package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NatureMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NatureYurdunuBilApp() }
    }
}

private val C = YurdunuBilColors

@Composable
private fun NatureYurdunuBilApp() {
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf(false) }
    val labels = listOf("Ana Sayfa", "Kütüphane", "Oyunlar", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.SportsEsports, Icons.Default.SportsEsports, Icons.Default.Person)

    Surface(Modifier.fillMaxSize(), color = C.Background) {
        if (quiz) {
            NatureQuizScreen(onExit = { quiz = false })
        } else {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f)) {
                    AnimatedContent(targetState = tab, label = "tab_transition") { page ->
                        when (page) {
                            0 -> NatureHome(onQuiz = { quiz = true }, onLibrary = { tab = 1 })
                            1 -> NatureLibrary(onQuiz = { quiz = true })
                            2 -> NatureGames(onQuiz = { quiz = true })
                            3 -> NatureArena()
                            else -> NatureProfile()
                        }
                    }
                }
                NavigationBar(
                    Modifier.navigationBarsPadding(),
                    containerColor = C.Surface,
                    tonalElevation = 8.dp
                ) {
                    labels.forEachIndexed { i, label ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = { Icon(icons[i], label) },
                            label = { Text(label, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NatureHome(onQuiz: () -> Unit, onLibrary: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { NatureHero() }
        item {
            NatureCard(modifier = Modifier.padding(horizontal = 18.dp), accent = C.NaturalGreen) {
                Text("Bugün ne öğrenmek istiyorsun?", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = C.Deep)
                Text("18 soruluk günlük coğrafya görevin hazır.", color = Color(0xFF60756A), fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                NatureButton("Coğrafya Testine Başla", Icons.Default.PlayArrow, C.NaturalGreen, onQuiz)
            }
        }
        item { SectionTitle("Türkiye'yi keşfet") }
        item {
            NatureCard(
                modifier = Modifier.padding(horizontal = 18.dp),
                accent = C.Water,
                dark = true,
                onClick = onLibrary
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Türkiye Haritası", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Text("81 il • 7 bölge • sayısız coğrafya ipucu", color = C.Sky, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.Map, null, tint = C.Sky, modifier = Modifier.size(38.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text("Haritayı keşfet  →", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NatureMini("İller", "81 il", Icons.Default.LocationCity, C.Leaf, Modifier.weight(1f))
                NatureMini("Yer Şekilleri", "Dağ • ova • plato", Icons.Default.Terrain, C.Earth, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NatureMini("Sular", "Akarsu • göl", Icons.Default.Water, C.Water, Modifier.weight(1f))
                NatureMini("Bölgeler", "7 bölge", Icons.Default.Explore, C.NaturalGreen, Modifier.weight(1f))
            }
        }
        item {
            NatureCard(modifier = Modifier.padding(horizontal = 18.dp), accent = C.Warm) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 27.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Günlük seri", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                        Text("Her gün biraz daha Türkiye.", color = Color(0xFF60756A), fontSize = 12.sp)
                    }
                    Text("0 gün", color = C.Warm, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun NatureHero() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(C.Deep, C.Forest, Color(0xFF286A4C))),
                RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp)
            )
            .padding(22.dp)
    ) {
        Column {
            Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("KPSS Önlisans • Türkiye Coğrafyası", color = C.Sky, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))
            Text("Türkiye'yi keşfet.\nBilgini güçlendir.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 31.sp)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("0", "Soru")
                StatPill("0 XP", "Seviye 1")
                StatPill("0 gün", "Seri")
            }
        }
    }
}

@Composable
private fun NatureLibrary(onQuiz: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Kütüphane", fontSize = 30.sp, fontWeight = FontWeight.Black, color = C.Deep)
            Text("Coğrafyayı ezber değil, bağlantılarıyla öğren.", color = C.NaturalGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        item { MapPreview() }
        item { SectionTitle("12 ana konu") }
        items(GeographyData.topics) { topic ->
            NatureCard(accent = C.NaturalGreen, onClick = onQuiz) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topic.icon, fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(topic.title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = C.Deep)
                        Text(topic.subtitle, color = Color(0xFF687B71), fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { topic.progress / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = C.NaturalGreen,
                            trackColor = C.SurfaceSoft
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("${topic.progress}%", color = C.NaturalGreen, fontWeight = FontWeight.Black)
                }
            }
        }
        item { SectionTitle("İllerden öğren") }
        items(GeographyData.provinces) { province ->
            NatureCard(accent = C.Water) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(C.SurfaceSoft, CircleShape), Alignment.Center) {
                        Text("📍", fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(province.name, fontWeight = FontWeight.ExtraBold, color = C.Deep)
                        Text(province.region, color = C.Water, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(province.clue, color = Color(0xFF687B71), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = C.NaturalGreen)
                }
            }
        }
    }
}

@Composable
private fun MapPreview() {
    NatureCard(dark = true, accent = C.Sky) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Türkiye Haritası", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text("İller ve coğrafi ipuçları", color = C.Sky, fontSize = 12.sp)
            }
            Text("81 İL", color = C.Sky, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(14.dp))
        Box(
            Modifier.fillMaxWidth().height(130.dp).background(
                Brush.linearGradient(listOf(C.Forest, C.Water.copy(alpha = .85f))),
                RoundedCornerShape(20.dp)
            ),
            Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🗺️", fontSize = 42.sp)
                Text("ETKİLEŞİMLİ TÜRKİYE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun NatureGames(onQuiz: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Oyun Merkezi", fontSize = 30.sp, fontWeight = FontWeight.Black, color = C.Deep)
            Text("Öğren • oyna • hatırla", color = C.NaturalGreen, fontWeight = FontWeight.Bold)
        }
        items(GeographyData.games) { game ->
            NatureCard(accent = C.Water, onClick = onQuiz) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(game.icon, fontSize = 30.sp)
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(game.title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = C.Deep)
                        Text(game.subtitle, color = Color(0xFF687B71), fontSize = 12.sp)
                    }
                    Icon(Icons.Default.PlayArrow, null, tint = C.NaturalGreen)
                }
            }
        }
        item { SectionTitle("Günün etkinlikleri") }
        item { EventCard("🗺️", "Harita Görevi", "5 ili 60 saniyede bul", "+50 XP", C.Water) }
        item { EventCard("🌿", "7 Bölge Serisi", "Her bölgeden doğru cevap", "+100 XP", C.Leaf) }
        item { EventCard("🏆", "Türkiye Ustası", "Haftalık 100 soruluk meydan okuma", "+500 XP", C.Warm) }
    }
}

@Composable
private fun NatureArena() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            Text("Arena", fontSize = 30.sp, fontWeight = FontWeight.Black, color = C.Deep)
            Text("Bilgini göster, zirveye çık.", color = C.NaturalGreen, fontWeight = FontWeight.Bold)
        }
        item {
            NatureCard(dark = true, accent = C.Warm) {
                Text("SEZON 1", color = C.Sky, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Spacer(Modifier.height(5.dp))
                Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("Bronz → Gümüş → Altın → Coğrafya Ustası", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatPill("0", "Galibiyet")
                    StatPill("0", "Puan")
                    StatPill("#—", "Sıra")
                }
            }
        }
        items(GeographyData.arena) { mode ->
            NatureCard(accent = C.Warm, onClick = {}) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mode.icon, fontSize = 31.sp)
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(mode.title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = C.Deep)
                        Text(mode.subtitle, color = Color(0xFF687B71), fontSize = 12.sp)
                        Text(mode.reward, color = C.Warm, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.SportsEsports, null, tint = C.NaturalGreen)
                }
            }
        }
    }
}

@Composable
private fun NatureProfile() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            Text("Profil", fontSize = 30.sp, fontWeight = FontWeight.Black, color = C.Deep)
            Text("Coğrafya yolculuğun", color = C.NaturalGreen, fontWeight = FontWeight.Bold)
        }
        item {
            NatureCard(dark = true, accent = C.Leaf) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(62.dp).background(C.Leaf, CircleShape), Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(31.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Gezgin", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        Text("Seviye 1 • 0 XP", color = C.Sky)
                    }
                }
            }
        }
        item { ProfileStat("⭐", "XP", "0 / 100", C.Warm) }
        item { ProfileStat("🔥", "Günlük Seri", "0 gün", C.Leaf) }
        item { ProfileStat("🗺️", "Keşfedilen İller", "0 / 81", C.Water) }
        item { ProfileStat("🏆", "Başarımlar", "0 / 30", C.Warm) }
    }
}

@Composable
private fun NatureQuizScreen(onExit: () -> Unit) {
    val engine = remember { QuizEngine(GeographyQuestions.all) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var locked by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val q = engine.current

    if (finished) {
        val result = engine.result()
        Column(Modifier.fillMaxSize().background(C.Background).padding(22.dp), verticalArrangement = Arrangement.Center) {
            Text("Test tamamlandı!", fontSize = 30.sp, fontWeight = FontWeight.Black, color = C.Deep)
            Spacer(Modifier.height(8.dp))
            Text("${result.correct} doğru • ${result.wrong} yanlış • ${result.xp} XP", color = C.NaturalGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(22.dp))
            NatureButton("Kütüphaneye Dön", Icons.Default.ArrowBack, C.NaturalGreen, onExit)
        }
        return
    }

    Column(Modifier.fillMaxSize().background(C.Background)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExit) { Icon(Icons.Default.ArrowBack, "Geri", tint = C.Deep) }
            Column(Modifier.weight(1f)) {
                Text("Soru ${engine.currentIndex + 1} / ${engine.size}", fontWeight = FontWeight.Black, color = C.Deep)
                Text(q.topic, color = C.NaturalGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("+10 XP", color = C.Warm, fontWeight = FontWeight.Black)
        }
        LinearProgressIndicator(
            progress = { (engine.currentIndex + 1) / engine.size.toFloat() },
            modifier = Modifier.fillMaxWidth().height(5.dp),
            color = C.NaturalGreen,
            trackColor = C.SurfaceSoft
        )
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                NatureCard(accent = C.NaturalGreen) {
                    Text(q.text, fontSize = 19.sp, lineHeight = 27.sp, fontWeight = FontWeight.ExtraBold, color = C.Deep)
                }
            }
            items(q.options.withIndex().toList()) { (i, option) ->
                val isCorrect = i == q.correctIndex
                val selectedWrong = selected == i && !isCorrect
                val target = when {
                    !locked -> C.Surface
                    isCorrect -> Color(0xFFE4F3E9)
                    selectedWrong -> Color(0xFFF5E7E3)
                    else -> C.Surface
                }
                val bg by animateColorAsState(target, animationSpec = tween(220), label = "answer_color")
                var pressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(if (pressed) .98f else 1f, tween(120, easing = FastOutSlowInEasing), label = "answer_scale")
                Card(
                    Modifier.fillMaxWidth().scale(scale).clickable(enabled = !locked) {
                        pressed = true
                        selected = i
                        locked = true
                        engine.answer(i)
                    },
                    RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(bg),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(38.dp).background(if (isCorrect && locked) C.NaturalGreen else C.SurfaceSoft, CircleShape), Alignment.Center) {
                            Text(('A'.code + i).toChar().toString(), color = if (isCorrect && locked) Color.White else C.Deep, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(option, Modifier.weight(1f), color = C.Deep, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (locked) {
                item {
                    NatureCard(accent = if (selected == q.correctIndex) C.NaturalGreen else C.Warm) {
                        Text(if (selected == q.correctIndex) "✓ Doğru cevap" else "Dikkat! Doğru cevap farklı.", color = if (selected == q.correctIndex) C.NaturalGreen else C.Warm, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(5.dp))
                        Text(q.explanation, color = Color(0xFF60756A), fontSize = 12.sp, lineHeight = 18.sp)
                        Spacer(Modifier.height(12.dp))
                        NatureButton(if (engine.currentIndex < engine.size) "Sonraki Soru" else "Sonucu Gör", Icons.Default.ArrowForward, C.NaturalGreen) {
                            if (engine.currentIndex >= engine.size - 1) finished = true else { selected = null; locked = false }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NatureCard(
    modifier: Modifier = Modifier,
    accent: Color = C.NaturalGreen,
    dark: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) .985f else 1f, tween(130, easing = FastOutSlowInEasing), label = "card_scale")
    val bg = if (dark) C.Deep else C.Surface
    Card(
        modifier = modifier.fillMaxWidth().scale(scale).then(if (onClick != null) Modifier.clickable { pressed = true; onClick() } else Modifier),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(bg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (dark) 5.dp else 2.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Box(Modifier.width(4.dp).matchParentSize().background(accent))
            Column(Modifier.padding(17.dp).padding(start = 5.dp), content = content)
        }
    }
}

@Composable
private fun NatureButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(color, color.copy(alpha = .78f))), RoundedCornerShape(15.dp)).clickable(onClick = onClick).padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.White)
        Spacer(Modifier.width(9.dp))
        Text(text, Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Black)
        Icon(Icons.Default.ArrowForward, null, tint = Color.White)
    }
}

@Composable
private fun NatureMini(title: String, sub: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    NatureCard(modifier = modifier, accent = accent) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(27.dp))
        Spacer(Modifier.height(9.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, color = C.Deep)
        Text(sub, color = Color(0xFF687B71), fontSize = 11.sp)
    }
}

@Composable
private fun EventCard(icon: String, title: String, sub: String, reward: String, accent: Color) {
    NatureCard(accent = accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 27.sp)
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.ExtraBold, color = C.Deep)
                Text(sub, color = Color(0xFF687B71), fontSize = 12.sp)
            }
            Text(reward, color = accent, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfileStat(icon: String, title: String, value: String, accent: Color) {
    NatureCard(accent = accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 25.sp)
            Spacer(Modifier.width(12.dp))
            Text(title, Modifier.weight(1f), fontWeight = FontWeight.ExtraBold, color = C.Deep)
            Text(value, color = accent, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, Modifier.padding(horizontal = 18.dp, vertical = 2.dp), fontSize = 18.sp, fontWeight = FontWeight.Black, color = C.Deep)
}

@Composable
private fun StatPill(value: String, label: String) {
    Column(Modifier.background(Color.White.copy(alpha = .12f), RoundedCornerShape(14.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        Text(label, color = C.Sky, fontSize = 10.sp)
    }
}
