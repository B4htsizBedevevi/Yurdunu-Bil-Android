package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

private val V2Deep = Color(0xFF06221B)
private val V2Deep2 = Color(0xFF0B342B)
private val V2Green = Color(0xFF18C986)
private val V2Mint = Color(0xFFC9F8E1)
private val V2Gold = Color(0xFFFFC857)
private val V2Red = Color(0xFFE65353)
private val V2Bg = Color(0xFFF3F8F5)
private val V2Soft = Color(0xFFE4F7ED)
private val V2Line = Color(0xFFDCEAE3)
private val V2Muted = Color(0xFF70847B)

@Composable
fun YurdunuBilMainV2App() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var study by remember { mutableStateOf<Topic?>(null) }
    var province by remember { mutableStateOf<Province?>(null) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }

    fun startQuiz(title: String, mode: SharedGameMode) {
        val picked = SharedQuestionPool.pick(mode)
        if (picked.isNotEmpty()) { quizTitle = title; quiz = picked }
    }

    if (study != null) {
        V2StudyScreen(
            topic = study!!,
            onBack = { study = null },
            onQuiz = {
                val topic = study!!
                study = null
                startQuiz(
                    topic.title,
                    SharedGameMode(
                        id = "study-${topic.title}",
                        title = topic.title,
                        subtitle = topic.subtitle,
                        icon = topic.icon,
                        questions = 10,
                        seconds = 180,
                        rewardXp = 100,
                        topics = SharedQuestionPool.topicForLibrary(topic.title)
                    )
                )
            }
        )
        return
    }

    if (quiz != null) {
        V2Quiz(quizTitle, quiz!!, prefs) {
            quiz = null
            tab = 0
        }
        return
    }

    Scaffold(
        containerColor = V2Bg,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val nav = listOf(
                    Triple("Ana Sayfa", Icons.Default.Home, 0),
                    Triple("Kütüphane", Icons.Default.MenuBook, 1),
                    Triple("Etkinlikler", Icons.Default.SportsEsports, 2),
                    Triple("Ayarlar", Icons.Default.Settings, 3)
                )
                nav.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Icon(icon, label, Modifier.size(if (tab == index) 24.dp else 22.dp)) },
                        label = { Text(label, fontSize = 10.sp, fontWeight = if (tab == index) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = V2Green,
                            selectedTextColor = V2Deep,
                            indicatorColor = V2Soft,
                            unselectedIconColor = V2Muted,
                            unselectedTextColor = V2Muted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).statusBarsPadding()) {
            when (tab) {
                0 -> V2Home(prefs, onQuick = { startQuiz("Hızlı 10", SharedGameModes.quick) }, onEvents = { tab = 2 })
                1 -> V2Library(onStudy = { study = it }, onProvince = { province = it })
                2 -> V2Events { mode -> startQuiz(mode.title, mode) }
                else -> V2Settings(prefs)
            }
        }
    }

    province?.let { selected ->
        AlertDialog(
            onDismissRequest = { province = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("📍 ${selected.name}", color = V2Deep, fontSize = 23.sp, fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text(selected.region, color = V2Green, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(7.dp))
                    Text(selected.clue, color = V2Deep, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(9.dp))
                    selected.facts.forEach { Text("• $it", color = V2Muted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 3.dp)) }
                }
            },
            confirmButton = { TextButton({ province = null }) { Text("Kapat", color = V2Green, fontWeight = FontWeight.Bold) } }
        )
    }
}

@Composable
private fun V2Home(prefs: SharedPreferences, onQuick: () -> Unit, onEvents: () -> Unit) {
    val solved = prefs.getInt("solved", 0)
    val correct = prefs.getInt("correct", 0)
    val xp = prefs.getInt("xp", 0)
    val streak = prefs.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val daily = SharedQuestionPool.dailyMode()

    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(V2Deep, V2Deep2, Color(0xFF11634A))), RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .padding(21.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(46.dp).clip(CircleShape).background(V2Green.copy(.16f)), contentAlignment = Alignment.Center) { Text("🧭", fontSize = 25.sp) }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Yurdunu Bil", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
                            Text("KPSS Önlisans • Türkiye Coğrafyası", color = V2Mint, fontSize = 11.sp)
                        }
                        Box(Modifier.clip(CircleShape).background(Color.White.copy(.10f)).padding(horizontal = 9.dp, vertical = 7.dp)) {
                            Text("Lv.${1 + xp / 500}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(17.dp))
                    Text("Bugün Türkiye'yi biraz daha çöz. ✨", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Kısa bir tur bile bilgini taze tutar.", color = Color.White.copy(.66f), fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        V2Stat("$solved", "SORU", Icons.Default.MenuBook)
                        V2Stat("$xp", "XP", Icons.Default.Star)
                        V2Stat("$streak", "SERİ", Icons.Default.LocalFireDepartment)
                    }
                }
            }
        }
        item {
            V2Card(V2Green) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(CircleShape).background(V2Soft), contentAlignment = Alignment.Center) { Text("🎯", fontSize = 24.sp) }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("BUGÜNÜN GÖREVİ", color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text(daily.title, color = V2Deep, fontSize = 19.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${daily.questions} soru • +${daily.rewardXp} XP", color = V2Muted, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button({ onQuick() }, Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(V2Green)) {
                    Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Hızlıca Başla", fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                V2Tile("⚡", "Hızlı 10", "2 dk", Modifier.weight(1f), onQuick)
                V2Tile("📖", "Kütüphane", "konu çalış", Modifier.weight(1f)) { /* tab handled at parent only */ }
            }
        }
        item {
            V2Card(V2Gold, dark = true, onClick = onEvents) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚔️", fontSize = 32.sp)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ARENA", color = V2Gold, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("Bilgini sahaya çıkar.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(.64f), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = V2Gold)
                }
            }
        }
        item {
            V2Card(V2Green) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("GELİŞİMİN", color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("%$accuracy genel doğruluk", color = V2Deep, fontSize = 19.sp, fontWeight = FontWeight.Black)
                        Text(if (solved == 0) "İlk turunu tamamla ve grafiği başlat." else "$correct doğru cevapla ritmini koruyorsun.", color = V2Muted, fontSize = 11.sp)
                    }
                    Box(Modifier.size(56.dp).clip(CircleShape).background(V2Soft), contentAlignment = Alignment.Center) { Text("$accuracy%", color = V2Green, fontSize = 13.sp, fontWeight = FontWeight.Black) }
                }
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator({ accuracy / 100f }, Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = V2Green, trackColor = V2Soft)
            }
        }
    }
}

@Composable
private fun V2Library(onStudy: (Topic) -> Unit, onProvince: (Province) -> Unit) {
    var search by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    val provinces = allV2Provinces().filter { search.isBlank() || it.name.contains(search, true) || it.region.contains(search, true) }

    LazyColumn(contentPadding = PaddingValues(17.dp, 9.dp, 17.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item {
            Text("Kütüphane", color = V2Deep, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("Konuya girmeden önce oku. Sonra çöz.", color = V2Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = V2Green) },
                placeholder = { Text("Konu, il veya bölge ara…", fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = V2Green, cursorColor = V2Green)
            )
        }
        item {
            V2Card(V2Green, dark = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(50.dp).clip(CircleShape).background(V2Green.copy(.14f)), contentAlignment = Alignment.Center) { Text("🗺️", fontSize = 26.sp) }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ÇALIŞMA KÜTÜPHANESİ", color = V2Mint, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("Oku → anla → test et", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("Her konu için kısa özet, temel bilgiler ve KPSS ipucu.", color = Color.White.copy(.68f), fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
        item { Text("Konu Bankası", color = V2Deep, fontSize = 19.sp, fontWeight = FontWeight.Black) }
        items(topics, key = { it.title }) { topic -> V2TopicRow(topic) { onStudy(topic) } }
        item { Text("81 İli Keşfet", color = V2Deep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp)) }
        items(provinces, key = { it.name }) { p -> V2ProvinceRow(p) { onProvince(p) } }
    }
}

@Composable
private fun V2StudyScreen(topic: Topic, onBack: () -> Unit, onQuiz: () -> Unit) {
    val lesson = remember(topic.title) { LibraryStudyData.forTopic(topic) }
    Box(Modifier.fillMaxSize().background(V2Bg).statusBarsPadding().navigationBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(16.dp, 5.dp, 16.dp, 25.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = V2Deep) }
                    Column(Modifier.weight(1f)) {
                        Text("ÇALIŞMA", color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                        Text(topic.title, color = V2Deep, fontSize = 21.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Box(Modifier.clip(CircleShape).background(Color.White).padding(horizontal = 8.dp, vertical = 7.dp)) { Text(topic.icon, fontSize = 18.sp) }
                }
            }
            item {
                V2Card(V2Green, dark = true) {
                    Text("ÖNCE ANLA", color = V2Mint, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Spacer(Modifier.height(5.dp))
                    Text(lesson.intro, color = Color.White, fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            item { Text("Temel Bilgiler", color = V2Deep, fontSize = 18.sp, fontWeight = FontWeight.Black) }
            items(lesson.keyPoints) { point ->
                Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(12.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.size(26.dp).clip(CircleShape).background(V2Soft), contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, null, tint = V2Green, modifier = Modifier.size(16.dp)) }
                    Spacer(Modifier.width(9.dp))
                    Text(point, color = V2Deep, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                }
            }
            item {
                V2Card(V2Gold) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lightbulb, null, tint = V2Gold)
                        Spacer(Modifier.width(9.dp))
                        Column(Modifier.weight(1f)) {
                            Text("KPSS İPUCU", color = V2Gold, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text(lesson.examTip, color = V2Deep, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
            item {
                V2Card(V2Green) {
                    Text("Hazırsan şimdi pekiştir.", color = V2Deep, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text("Önce anlatımı bitir, sonra konudan soru çöz.", color = V2Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth().height(47.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(V2Green)) {
                        Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Konuyu Test Et", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun V2Events(onMode: (SharedGameMode) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(17.dp, 9.dp, 17.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Etkinlikler", color = V2Deep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("Öğrendiklerini oyunla pekiştir.", color = V2Green, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item {
            V2Card(V2Gold, dark = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 36.sp)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("SEZON 1", color = V2Gold, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        Text("Hız • bölge • bilgi • meydan okuma", color = Color.White.copy(.66f), fontSize = 11.sp)
                    }
                }
            }
        }
        item { Text("Oyun Modları", color = V2Deep, fontSize = 19.sp, fontWeight = FontWeight.Black) }
        items(SharedGameModes.games + SharedGameModes.arenaModes, key = { it.id }) { mode -> V2ModeRow(mode) { onMode(mode) } }
    }
}

@Composable
private fun V2Settings(prefs: SharedPreferences) {
    val solved = prefs.getInt("solved", 0)
    val correct = prefs.getInt("correct", 0)
    val wrong = prefs.getInt("wrong", 0)
    val xp = prefs.getInt("xp", 0)
    var reset by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(17.dp, 9.dp, 17.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Ayarlar", color = V2Deep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("İlerlemeni yönet", color = V2Green, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item {
            V2Card(V2Green, dark = true) {
                Text("GEZGİN", color = V2Mint, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text("Seviye ${1 + xp / 500}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("$xp XP • $solved soru", color = Color.White.copy(.68f), fontSize = 11.sp)
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator({ (xp % 500) / 500f }, Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = V2Green, trackColor = Color.White.copy(.12f))
            }
        }
        item { V2Setting(Icons.Default.BarChart, "İstatistikler", "$solved soru • $correct doğru • $wrong yanlış") }
        item { V2Setting(Icons.Default.Lock, "Hesap", "Yerel ilerleme • Supabase oturumu hazır") }
        item { V2Setting(Icons.Default.Info, "Sürüm", "Yurdunu Bil Android • 0.2.0") }
        item {
            V2Card(V2Red) {
                Row(Modifier.clickable { reset = true }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RestartAlt, null, tint = V2Red)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("İlerlemeyi Sıfırla", color = V2Red, fontWeight = FontWeight.ExtraBold)
                        Text("Yerel XP ve soru istatistiklerini temizle", color = V2Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
    if (reset) {
        AlertDialog(
            onDismissRequest = { reset = false },
            properties = DialogProperties(dismissOnClickOutside = false),
            title = { Text("İlerleme sıfırlansın mı?", color = V2Deep, fontWeight = FontWeight.Black) },
            text = { Text("Bu işlem cihazdaki yerel istatistikleri temizler.") },
            confirmButton = { TextButton({ prefs.edit().clear().apply(); reset = false }) { Text("Sıfırla", color = V2Red, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton({ reset = false }) { Text("Vazgeç") } }
        )
    }
}

@Composable
private fun V2Quiz(title: String, source: List<Question>, prefs: SharedPreferences, finish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var correct by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableIntStateOf(0) }
    var complete by remember { mutableStateOf(false) }

    if (complete) {
        LaunchedEffect(Unit) {
            prefs.edit().putInt("solved", prefs.getInt("solved", 0) + source.size)
                .putInt("correct", prefs.getInt("correct", 0) + correct)
                .putInt("wrong", prefs.getInt("wrong", 0) + wrong)
                .putInt("xp", prefs.getInt("xp", 0) + correct * 10)
                .apply()
        }
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            V2Card(V2Green) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(if (correct >= wrong) "🏆" else "💪", fontSize = 54.sp)
                    Text("Tur tamamlandı", color = V2Deep, fontSize = 27.sp, fontWeight = FontWeight.Black)
                    Text("$correct / ${source.size} doğru", color = V2Green, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text("$wrong yanlış • +${correct * 10} XP", color = V2Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))
                    LinearProgressIndicator({ correct / source.size.toFloat() }, Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = V2Green, trackColor = V2Soft)
                    Button(finish, Modifier.fillMaxWidth().padding(top = 18.dp).height(48.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(V2Green)) { Text("Devam Et", fontWeight = FontWeight.Bold) }
                }
            }
        }
        return
    }

    val q = source[index]
    LazyColumn(contentPadding = PaddingValues(17.dp, 6.dp, 17.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(finish) { Icon(Icons.Default.Close, "Çık") }
                Column(Modifier.weight(1f)) {
                    Text(title, color = V2Deep, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Soru ${index + 1} / ${source.size}", color = V2Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text("${correct * 10} XP", color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
        item { LinearProgressIndicator({ (index + 1f) / source.size }, Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = V2Green, trackColor = V2Soft) }
        item {
            V2Card(V2Green) {
                Text(q.topic, color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Spacer(Modifier.height(6.dp))
                Text(q.text, color = V2Deep, fontSize = 19.sp, lineHeight = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        itemsIndexed(q.options) { i, option ->
            val revealed = selected >= 0
            val isCorrect = i == q.correctIndex
            val isSelected = i == selected
            val border = when { !revealed -> V2Line; isCorrect -> V2Green; isSelected -> V2Red; else -> V2Line }
            val bg = when { !revealed -> Color.White; isCorrect -> V2Soft; isSelected -> V2Red.copy(.08f); else -> Color.White }
            Card(
                Modifier.fillMaxWidth().border(1.6.dp, border, RoundedCornerShape(16.dp)).clickable(enabled = !revealed) { selected = i; if (isCorrect) correct++ else wrong++ },
                RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(bg),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(33.dp).clip(CircleShape).background(border.copy(.10f)), contentAlignment = Alignment.Center) { Text("${('A'.code + i).toChar()}", color = border, fontWeight = FontWeight.Black) }
                    Spacer(Modifier.width(10.dp))
                    Text(option, color = V2Deep, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    if (revealed && isCorrect) Icon(Icons.Default.CheckCircle, null, tint = V2Green) else if (revealed && isSelected) Icon(Icons.Default.Cancel, null, tint = V2Red)
                }
            }
        }
        item {
            if (selected >= 0) {
                V2Card(if (selected == q.correctIndex) V2Green else V2Gold) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lightbulb, null, tint = if (selected == q.correctIndex) V2Green else V2Gold)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(if (selected == q.correctIndex) "DOĞRU!" else "DİKKAT KÖŞESİ", color = if (selected == q.correctIndex) V2Green else V2Gold, fontWeight = FontWeight.Black, fontSize = 10.sp)
                            Text(q.explanation, color = V2Deep, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Button(onClick = { if (index == source.lastIndex) complete = true else { index++; selected = -1 } }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(V2Deep)) {
                    Text(if (index == source.lastIndex) "Sonucu Gör" else "Sonraki Soru", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(5.dp))
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }
}

@Composable
private fun V2Card(accent: Color, dark: Boolean = false, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    val modifier = Modifier
        .fillMaxWidth()
        .clip(shape)
        .background(if (dark) Brush.linearGradient(listOf(V2Deep, V2Deep2)) else Color.White)
        .border(1.dp, accent.copy(if (dark) .22f else .13f), shape)
        .padding(16.dp)
    Column(if (onClick == null) modifier else modifier.clickable { onClick() }, content = content)
}

@Composable
private fun V2Stat(value: String, label: String, icon: ImageVector) {
    Row(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.08f)).padding(horizontal = 9.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = V2Mint, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(5.dp))
        Column { Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black); Text(label, color = Color.White.copy(.52f), fontSize = 7.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun V2Tile(icon: String, title: String, sub: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(14.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = V2Deep, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 6.dp)); Text(sub, color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun V2TopicRow(topic: Topic, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(V2Soft), contentAlignment = Alignment.Center) { Text(topic.icon, fontSize = 23.sp) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(topic.title, color = V2Deep, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(topic.subtitle, color = V2Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${topic.lessons.size} alt başlık • çalışma notu hazır", color = V2Green, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("ÇALIŞ", color = V2Green, fontSize = 8.sp, fontWeight = FontWeight.Black)
                Icon(Icons.Default.ArrowForward, null, tint = V2Green, modifier = Modifier.size(19.dp))
            }
        }
    }
}

@Composable
private fun V2ProvinceRow(province: Province, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(V2Soft), contentAlignment = Alignment.Center) { Text("📍", fontSize = 17.sp) }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) { Text(province.name, color = V2Deep, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold); Text(province.region, color = V2Green, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            Icon(Icons.Default.ChevronRight, null, tint = V2Muted, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun V2ModeRow(mode: SharedGameMode, onClick: () -> Unit) {
    val accent = if (mode.arena) V2Gold else V2Green
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(accent.copy(.12f)), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 24.sp) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mode.title, color = V2Deep, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (mode.arena) Text("ARENA", color = V2Gold, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
                Text(mode.subtitle, color = V2Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.Default.PlayArrow, null, tint = accent)
        }
    }
}

@Composable
private fun V2Setting(icon: ImageVector, title: String, value: String) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(V2Soft), contentAlignment = Alignment.Center) { Icon(icon, null, tint = V2Green, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) { Text(title, color = V2Deep, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold); Text(value, color = V2Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp)) }
        }
    }
}

private fun allV2Provinces(): List<Province> {
    val existing = GeographyData.provinces.associateBy { it.name }
    val groups = linkedMapOf(
        "Marmara" to listOf("Balıkesir","Bilecik","Bursa","Çanakkale","Edirne","İstanbul","Kırklareli","Kocaeli","Sakarya","Tekirdağ","Yalova"),
        "Ege" to listOf("Afyonkarahisar","Aydın","Denizli","İzmir","Kütahya","Manisa","Muğla","Uşak"),
        "Akdeniz" to listOf("Adana","Antalya","Burdur","Hatay","Isparta","Kahramanmaraş","Mersin","Osmaniye"),
        "İç Anadolu" to listOf("Aksaray","Ankara","Çankırı","Eskişehir","Karaman","Kayseri","Kırıkkale","Kırşehir","Konya","Nevşehir","Niğde","Sivas","Yozgat"),
        "Karadeniz" to listOf("Amasya","Artvin","Bartın","Bayburt","Bolu","Çorum","Düzce","Giresun","Gümüşhane","Karabük","Kastamonu","Ordu","Rize","Samsun","Sinop","Tokat","Trabzon","Zonguldak"),
        "Doğu Anadolu" to listOf("Ağrı","Ardahan","Bingöl","Bitlis","Elazığ","Erzincan","Erzurum","Hakkari","Iğdır","Kars","Malatya","Muş","Tunceli","Van"),
        "Güneydoğu Anadolu" to listOf("Adıyaman","Batman","Diyarbakır","Gaziantep","Kilis","Mardin","Siirt","Şanlıurfa","Şırnak")
    )
    return groups.flatMap { (region, names) -> names.map { name -> existing[name] ?: Province(name, region, "$region bölgesindeki önemli merkezlerden biri.", listOf("Bölge: $region", "İl-bölge bağlantısını öğren.", "Haritadaki konumunu hatırla.")) } }.distinctBy { it.name }.sortedBy { it.name }
}
