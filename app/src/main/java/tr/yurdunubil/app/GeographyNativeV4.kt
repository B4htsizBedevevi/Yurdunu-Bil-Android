package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private object V4Colors {
    val deep = Color(0xFF06221B)
    val forest = Color(0xFF0D5A40)
    val green = Color(0xFF18B77A)
    val mint = Color(0xFFC8F7DF)
    val gold = Color(0xFFFFC857)
    val bg = Color(0xFFF4F8F6)
    val red = Color(0xFFE65353)
    val soft = Color(0xFFE6F7EE)
}

@Composable
fun GeographyNativeV4App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_v4", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }
    var quizMode by remember { mutableStateOf("practice") }
    val startQuiz: (String, List<Question>, String) -> Unit = { title, questions, mode ->
        if (questions.isNotEmpty()) { quizTitle = title; quizMode = mode; quiz = questions }
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = V4Colors.bg) {
            if (quiz != null) {
                QuizV4(quizTitle, quiz!!, quizMode, prefs) { quiz = null }
            } else {
                Scaffold(
                    containerColor = V4Colors.bg,
                    bottomBar = {
                        NavigationBar(containerColor = Color.White) {
                            val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
                            val labels = listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil")
                            labels.forEachIndexed { index, label ->
                                NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(icons[index], label) }, label = { Text(label, fontSize = 10.sp) })
                            }
                        }
                    }
                ) { pad ->
                    Box(Modifier.fillMaxSize().padding(pad)) {
                        when (tab) {
                            0 -> HomeV4(prefs, startQuiz)
                            1 -> LibraryV4(startQuiz)
                            2 -> MapV4()
                            3 -> ArenaV4(startQuiz)
                            else -> ProfileV4(prefs)
                        }
                    }
                }
            }
        }
    }
}

private fun questions(mode: SharedGameMode) = SharedQuestionPool.pick(mode)

@Composable
private fun HomeV4(p: SharedPreferences, go: (String, List<Question>, String) -> Unit) {
    val solved = p.getInt("solved", 0)
    val correct = p.getInt("correct", 0)
    val xp = p.getInt("xp", 0)
    val streak = p.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val daily = SharedQuestionPool.dailyMode()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(Modifier.fillMaxWidth().background(V4Colors.deep, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(22.dp)) {
                Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("KPSS Önlisans • Türkiye Coğrafyası", color = V4Colors.mint, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                Text("Her gün biraz daha Türkiye.", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatPill("$solved", "Soru")
                    StatPill("$xp", "XP")
                    StatPill("$streak", "Seri")
                }
            }
        }
        item {
            V4Card(accent = V4Colors.green) {
                Text("🎯 BUGÜNÜN GÖREVİ", color = V4Colors.green, fontWeight = FontWeight.Black, fontSize = 11.sp)
                Text(daily.title, color = V4Colors.deep, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("${daily.questions} soru • +${daily.rewardXp} XP", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                Button(onClick = { go(daily.title, questions(daily), daily.id) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(V4Colors.green)) { Text("Göreve Başla") }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard("⚡", "Hızlı 10", "2 dakika", Modifier.weight(1f)) { go("Hızlı 10", questions(SharedGameModes.quick), "quick") }
                ActionCard("🔥", "Bilgi Zinciri", "Seri yap", Modifier.weight(1f)) { go("Bilgi Zinciri", questions(SharedGameModes.chain), "chain") }
            }
        }
        item {
            V4Card(accent = V4Colors.gold, dark = true) {
                Text("⚔️ ARENA", color = V4Colors.gold, fontWeight = FontWeight.Black)
                Text("Bilgini hızla kanıtla.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
            }
        }
        item {
            V4Card(accent = V4Colors.green) {
                Text("📊 GELİŞİMİN", color = V4Colors.green, fontWeight = FontWeight.Black, fontSize = 11.sp)
                Text("%$accuracy genel doğruluk", color = V4Colors.deep, fontSize = 22.sp, fontWeight = FontWeight.Black)
                LinearProgressIndicator(progress = { accuracy / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = V4Colors.green, trackColor = V4Colors.soft)
            }
        }
    }
}

@Composable
private fun LibraryV4(go: (String, List<Question>, String) -> Unit) {
    var search by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Kütüphane", color = V4Colors.deep, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("12 ana konu • sınav odaklı coğrafya", color = V4Colors.green, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(9.dp))
            OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Konu ara…") }, shape = RoundedCornerShape(16.dp))
        }
        item {
            V4Card(accent = V4Colors.green, dark = true) {
                Text("🧠 Oku → çöz → yanlışını öğren → tekrar et", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text("Bir konuya dokunduğunda doğrudan konu testine geç.", color = V4Colors.mint, fontSize = 12.sp)
            }
        }
        items(topics) { topic ->
            V4Card(accent = V4Colors.green, onClick = {
                val mode = SharedGameMode("topic-${topic.title}", topic.title, topic.subtitle, topic.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(topic.title))
                go(topic.title, questions(mode), "topic")
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topic.icon, fontSize = 29.sp)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(topic.title, color = V4Colors.deep, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(topic.subtitle, color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(progress = { topic.progress / 100f }, modifier = Modifier.fillMaxWidth().height(6.dp), color = V4Colors.green, trackColor = V4Colors.soft)
                        Text("${topic.lessons.size} alt başlık • ${topic.progress}%", color = V4Colors.green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = V4Colors.green)
                }
            }
        }
    }
}

@Composable
private fun MapV4() {
    val regions = listOf("Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var region by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Province?>(null) }
    val all = allProvincesV4()
    val visible = all.filter { (region == null || it.region == region) && (search.isBlank() || it.name.contains(search, true)) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Türkiye Haritası", color = V4Colors.deep, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("81 il • 7 bölge • KPSS bağlantıları", color = V4Colors.green, fontWeight = FontWeight.Bold)
        }
        item {
            V4Card(accent = V4Colors.green, dark = true) {
                Text("🇹🇷 81 İLİ KEŞFET", color = V4Colors.mint, fontWeight = FontWeight.Black)
                Text("Bölge seç, ili bul, kısa KPSS bilgisini öğren.", color = Color.White, fontSize = 14.sp)
            }
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = region == null, onClick = { region = null }, label = { Text("Tümü") })
                regions.forEach { r -> FilterChip(selected = region == r, onClick = { region = r }, label = { Text(r) }) }
            }
        }
        item { OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("81 il içinde ara…") }, shape = RoundedCornerShape(16.dp)) }
        item { Text("${visible.size} il gösteriliyor", color = V4Colors.deep, fontWeight = FontWeight.ExtraBold) }
        items(visible) { province ->
            V4Card(accent = V4Colors.green, onClick = { selected = province }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(42.dp).background(V4Colors.soft, CircleShape), contentAlignment = Alignment.Center) { Text(province.name.take(1), color = V4Colors.green, fontWeight = FontWeight.Black) }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(province.name, color = V4Colors.deep, fontWeight = FontWeight.ExtraBold)
                        Text(province.region, color = V4Colors.green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(province.clue, color = Color.Gray, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.LocationOn, null, tint = V4Colors.green)
                }
            }
        }
    }
    selected?.let { province ->
        AlertDialog(onDismissRequest = { selected = null }, title = { Text("📍 ${province.name}", fontWeight = FontWeight.Black) }, text = {
            Column { Text(province.region, color = V4Colors.green, fontWeight = FontWeight.Bold); Text(province.clue, color = V4Colors.deep, fontWeight = FontWeight.SemiBold); province.facts.forEach { fact -> Text("• $fact", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp)) } }
        }, confirmButton = { TextButton(onClick = { selected = null }) { Text("Kapat", color = V4Colors.green) } })
    }
}

@Composable
private fun ArenaV4(go: (String, List<Question>, String) -> Unit) {
    val modes = SharedGameModes.games + SharedGameModes.arenaModes
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Arena & Oyunlar", color = V4Colors.deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Öğrenirken yarış.", color = V4Colors.green, fontWeight = FontWeight.Bold) }
        item {
            V4Card(accent = V4Colors.gold, dark = true) {
                Text("🏆 SEZON 1", color = V4Colors.gold, fontWeight = FontWeight.Black)
                Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("Bronz → Gümüş → Altın → Coğrafya Ustası", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
            }
        }
        items(modes) { mode ->
            V4Card(accent = if (mode.arena) V4Colors.gold else V4Colors.green, onClick = { go(mode.title, questions(mode), mode.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mode.icon, fontSize = 27.sp)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(mode.title, color = V4Colors.deep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        Text(mode.subtitle, color = Color.Gray, fontSize = 11.sp)
                        Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = if (mode.arena) Color(0xFFB07B00) else V4Colors.green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = V4Colors.green)
                }
            }
        }
    }
}

@Composable
private fun ProfileV4(p: SharedPreferences) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val wrong = p.getInt("wrong", 0); val xp = p.getInt("xp", 0); val streak = p.getInt("streak", 0); val wins = p.getInt("wins", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt(); val level = 1 + xp / 500
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp, 20.dp, 18.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Profil", color = V4Colors.deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Coğrafya yolculuğun", color = V4Colors.green, fontWeight = FontWeight.Bold) }
        item { V4Card(accent = V4Colors.green, dark = true) { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(62.dp).background(V4Colors.green, CircleShape), contentAlignment = Alignment.Center) { Text("🧭", fontSize = 30.sp) }; Spacer(Modifier.width(12.dp)); Column { Text("Gezgin", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("Seviye $level • $xp XP", color = V4Colors.mint) } } } }
        item { V4Card(accent = V4Colors.green) { Text("İSTATİSTİKLER", color = V4Colors.green, fontWeight = FontWeight.Black, fontSize = 11.sp); StatRow("Çözülen soru", solved.toString()); StatRow("Doğru", correct.toString()); StatRow("Yanlış", wrong.toString()); StatRow("Doğruluk", "%$accuracy"); StatRow("Günlük seri", "$streak gün"); StatRow("Arena galibiyeti", wins.toString()) } }
        item { V4Card(accent = V4Colors.gold) { Text("🏅 BAŞARIMLAR", color = Color(0xFFB07B00), fontWeight = FontWeight.Black); Text(if (solved >= 10) "🥉 İlk 10 Soru" else "🔒 İlk 10 Soru", color = V4Colors.deep, fontWeight = FontWeight.Bold); Text(if (streak >= 3) "🔥 3 Günlük Seri" else "🔒 3 Günlük Seri", color = V4Colors.deep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)); Text(if (wins >= 1) "⚔️ İlk Arena Galibiyeti" else "🔒 İlk Arena Galibiyeti", color = V4Colors.deep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) } }
    }
}

@Composable
private fun QuizV4(title: String, source: List<Question>, mode: String, p: SharedPreferences, finish: () -> Unit) {
    var index by remember(source) { mutableIntStateOf(0) }
    var selected by remember(source) { mutableIntStateOf(-1) }
    var correct by remember(source) { mutableIntStateOf(0) }
    var wrong by remember(source) { mutableIntStateOf(0) }
    var answered by remember(source) { mutableIntStateOf(0) }
    var finished by remember(source) { mutableStateOf(false) }
    if (finished) {
        val xp = correct * 10
        LaunchedEffect(Unit) {
            p.edit().putInt("solved", p.getInt("solved", 0) + answered).putInt("correct", p.getInt("correct", 0) + correct).putInt("wrong", p.getInt("wrong", 0) + wrong).putInt("xp", p.getInt("xp", 0) + xp).apply()
        }
        ResultV4(title, source.size, correct, wrong, xp, finish)
        return
    }
    val q = source[index]
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = finish) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = V4Colors.deep, fontWeight = FontWeight.Black); Text("${index + 1} / ${source.size}", color = V4Colors.green, fontSize = 11.sp) } } }
        item { LinearProgressIndicator(progress = { (index + 1f) / source.size }, modifier = Modifier.fillMaxWidth().height(7.dp), color = V4Colors.green, trackColor = V4Colors.soft) }
        item { V4Card(accent = V4Colors.green) { Text(q.topic, color = V4Colors.green, fontSize = 11.sp, fontWeight = FontWeight.Black); Text(q.text, color = V4Colors.deep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold) } }
        items(q.options.size) { optionIndex ->
            val isCorrect = optionIndex == q.correctIndex
            val isSelected = selected == optionIndex
            val show = selected != -1
            val borderColor = when { show && isCorrect -> V4Colors.green; show && isSelected -> V4Colors.red; else -> Color(0xFFDCE7E1) }
            val fill = when { show && isCorrect -> V4Colors.soft; show && isSelected -> Color(0xFFFFE7E7); else -> Color.White }
            Card(Modifier.fillMaxWidth().border(2.dp, borderColor, RoundedCornerShape(16.dp)).clickable(enabled = selected == -1) { selected = optionIndex; answered++; if (isCorrect) correct++ else wrong++ }, colors = CardDefaults.cardColors(fill), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text("${'A'.code + optionIndex as Int | 0}", color = borderColor, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(q.options[optionIndex], color = V4Colors.deep, fontWeight = if (isSelected || (show && isCorrect)) FontWeight.Bold else FontWeight.Normal) }
            }
        }
        if (selected != -1) {
            item { V4Card(accent = V4Colors.gold) { Text("💡 DİKKAT KÖŞESİ", color = Color(0xFFB07B00), fontWeight = FontWeight.Black); Text(q.explanation, color = V4Colors.deep, fontSize = 13.sp) } }
            item { Button(onClick = { if (index + 1 >= source.size) finished = true else { index++; selected = -1 } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(V4Colors.green)) { Text(if (index + 1 >= source.size) "Sonucu Gör" else "Sonraki Soru") } }
        }
    }
}

@Composable
private fun ResultV4(title: String, total: Int, correct: Int, wrong: Int, xp: Int, finish: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🏆", fontSize = 58.sp); Text("Tur tamamlandı!", color = V4Colors.deep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text(title, color = V4Colors.green, fontWeight = FontWeight.Bold); Spacer(Modifier.height(20.dp)); Text("$correct / $total doğru", color = V4Colors.deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("$wrong yanlış • +$xp XP", color = Color.Gray); Spacer(Modifier.height(24.dp)); Button(onClick = finish, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(V4Colors.green)) { Text("Devam Et") }
    }
}

@Composable private fun V4Card(accent: Color, modifier: Modifier = Modifier, dark: Boolean = false, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val m = modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).then(if (onClick != null) Modifier.clickable { onClick() } else Modifier).background(if (dark) V4Colors.deep else Color.White).border(1.dp, accent.copy(alpha = .20f), RoundedCornerShape(20.dp)).padding(17.dp)
    Column(m, verticalArrangement = Arrangement.spacedBy(5.dp), content = content)
}

@Composable private fun StatPill(value: String, label: String) { Column(Modifier.background(Color.White.copy(alpha = .12f), RoundedCornerShape(13.dp)).padding(horizontal = 12.dp, vertical = 7.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = V4Colors.mint, fontSize = 9.sp) } }

@Composable private fun ActionCard(icon: String, title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) { Card(modifier.clickable { onClick() }, colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(14.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = V4Colors.deep, fontWeight = FontWeight.ExtraBold); Text(subtitle, color = Color.Gray, fontSize = 10.sp) } } }

@Composable private fun StatRow(label: String, value: String) { Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = Color.Gray); Text(value, color = V4Colors.deep, fontWeight = FontWeight.Black) } }

private fun allProvincesV4(): List<Province> {
    val regions = mapOf(
        "Marmara" to listOf("Balıkesir", "Bilecik", "Bursa", "Çanakkale", "Edirne", "İstanbul", "Kırklareli", "Kocaeli", "Sakarya", "Tekirdağ", "Yalova"),
        "Ege" to listOf("Afyonkarahisar", "Aydın", "Denizli", "İzmir", "Kütahya", "Manisa", "Muğla", "Uşak"),
        "Akdeniz" to listOf("Adana", "Antalya", "Burdur", "Hatay", "Isparta", "Kahramanmaraş", "Mersin", "Osmaniye"),
        "İç Anadolu" to listOf("Aksaray", "Ankara", "Çankırı", "Eskişehir", "Karaman", "Kayseri", "Kırıkkale", "Kırşehir", "Konya", "Nevşehir", "Niğde", "Sivas", "Yozgat"),
        "Karadeniz" to listOf("Amasya", "Artvin", "Bartın", "Bayburt", "Bolu", "Çorum", "Düzce", "Giresun", "Gümüşhane", "Karabük", "Kastamonu", "Ordu", "Rize", "Samsun", "Sinop", "Tokat", "Trabzon", "Zonguldak"),
        "Doğu Anadolu" to listOf("Ağrı", "Ardahan", "Bingöl", "Bitlis", "Elazığ", "Erzincan", "Erzurum", "Hakkari", "Iğdır", "Kars", "Malatya", "Muş", "Tunceli", "Van"),
        "Güneydoğu Anadolu" to listOf("Adıyaman", "Batman", "Diyarbakır", "Gaziantep", "Kilis", "Mardin", "Siirt", "Şanlıurfa", "Şırnak")
    )
    val known = GeographyData.provinces.associateBy { it.name }
    return regions.flatMap { (region, names) -> names.map { name -> known[name] ?: Province(name, region, "$region bölgesinde yer alan Türkiye ili.", listOf("Bölge: $region", "KPSS için il-bölge bağlantısını öğren.", "Haritada konumunu hatırla.")) } }
}
