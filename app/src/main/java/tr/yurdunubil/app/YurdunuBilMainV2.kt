package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val LightBg = Color(0xFFF2F6F4)
private val LightSurface = Color.White
private val LightText = Color(0xFF06221B)
private val LightMuted = Color(0xFF70847B)
private val DarkBg = Color(0xFF07110E)
private val DarkSurface = Color(0xFF10221C)
private val DarkSurface2 = Color(0xFF153129)
private val DarkText = Color(0xFFF1F7F4)
private val DarkMuted = Color(0xFF9AB4A9)
private val Deep = Color(0xFF06221B)
private val Deep2 = Color(0xFF0B342B)
private val Green = Color(0xFF18C986)
private val Mint = Color(0xFFC9F8E1)
private val Gold = Color(0xFFFFC857)
private val SoftGreen = Color(0xFFE4F7ED)
private val DarkSoftGreen = Color(0xFF17372D)
private val Red = Color(0xFFE65353)

@Composable
fun YurdunuBilMainV2App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var darkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
    var tab by remember { mutableIntStateOf(0) }
    var study by remember { mutableStateOf<Topic?>(null) }
    var province by remember { mutableStateOf<Province?>(null) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }
    var quizMode by remember { mutableStateOf(SharedGameModes.quick) }

    val bg = if (darkMode) DarkBg else LightBg
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val soft = if (darkMode) DarkSoftGreen else SoftGreen

    fun startQuiz(title: String, mode: SharedGameMode) {
        val picked = SharedQuestionPool.pick(mode)
        if (picked.isNotEmpty()) {
            quizTitle = title
            quizMode = mode
            quiz = picked
        }
    }

    BackHandler(enabled = study != null || province != null || quiz != null) {
        when {
            quiz != null -> quiz = null
            study != null -> study = null
            province != null -> province = null
        }
    }

    if (study != null) {
        val selected = study!!
        LibraryStudyScreen(selected, { study = null }) {
            study = null
            startQuiz(selected.title, SharedGameMode("study-${selected.title}", selected.title, selected.subtitle, selected.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(selected.title)))
        }
        return
    }
    if (province != null) {
        ProvinceDetailScreen(province!!, onBack = { province = null }, darkMode = darkMode)
        return
    }
    if (quiz != null) {
        V2QuizScreen(quizTitle, quizMode, quiz!!, prefs, darkMode) { quiz = null }
        return
    }

    Scaffold(
        containerColor = bg,
        bottomBar = {
            NavigationBar(containerColor = surface, tonalElevation = if (darkMode) 2.dp else 3.dp) {
                val nav = listOf(
                    "Ana Sayfa" to Icons.Default.Home,
                    "Kütüphane" to Icons.Default.MenuBook,
                    "Etkinlikler" to Icons.Default.SportsEsports,
                    "Ayarlar" to Icons.Default.Settings
                )
                nav.forEachIndexed { i, item ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(item.second, item.first) },
                        label = { Text(item.first, fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Green,
                            selectedTextColor = Green,
                            indicatorColor = if (darkMode) DarkSurface2 else SoftGreen,
                            unselectedIconColor = muted,
                            unselectedTextColor = muted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> HomeScreen(
                    prefs = prefs,
                    darkMode = darkMode,
                    quick = { startQuiz("Hızlı 10", SharedGameModes.quick) },
                    daily = { startQuiz(SharedQuestionPool.dailyMode().title, SharedQuestionPool.dailyMode()) },
                    events = { tab = 2 },
                    library = { tab = 1 }
                )
                1 -> LibraryScreen(darkMode, { study = it }, { province = it })
                2 -> EventsScreen(darkMode) { startQuiz(it.title, it) }
                else -> SettingsScreen(prefs, darkMode) {
                    prefs.edit().putBoolean("dark_mode", it).apply()
                    darkMode = it
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    prefs: SharedPreferences,
    darkMode: Boolean,
    quick: () -> Unit,
    daily: () -> Unit,
    events: () -> Unit,
    library: () -> Unit
) {
    val solved = prefs.getInt("solved", 0)
    val correct = prefs.getInt("correct", 0)
    val xp = prefs.getInt("xp", 0)
    val streak = prefs.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val dailyMode = SharedQuestionPool.dailyMode()
    val dailyDone = prefs.getString("daily_completed_date", "") == java.time.LocalDate.now().toString()
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val surface = if (darkMode) DarkSurface else LightSurface
    val soft = if (darkMode) DarkSoftGreen else SoftGreen

    LazyColumn(
        contentPadding = PaddingValues(bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { HomeHeader(xp, solved, streak, darkMode) }
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Bugün ne yapalım?", color = text, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Text("Kısa bir tur bile ilerleme sayılır.", color = muted, fontSize = 11.sp)
            }
        }
        item {
            CompactFeatureCard(
                icon = "🎯",
                eyebrow = "BUGÜNÜN GÖREVİ",
                title = dailyMode.title,
                subtitle = if (dailyDone) "Tamamlandı ✓ • Yarın yeni görev açılacak." else dailyMode.subtitle,
                accent = Green,
                darkMode = darkMode,
                action = daily,
                button = if (dailyDone) "Tekrar Çöz" else "Göreve Başla"
            )
        }
        item {
            Text("Hızlı erişim", color = text, fontSize = 15.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
        }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("⚡", "Hızlı 10", "10 soru", Green, darkMode, quick, Modifier.weight(1f))
                QuickCard("📚", "Kütüphane", "Konuları aç", Color(0xFF5AA8FF), darkMode, library, Modifier.weight(1f))
                QuickCard("⚔️", "Arena", "Yarış", Gold, darkMode, events, Modifier.weight(1f))
            }
        }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                Column(Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("GELİŞİMİN", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Text("%$accuracy doğruluk", color = text, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        Text("$xp XP", color = Gold, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(9.dp))
                    LinearProgressIndicator(progress = { accuracy / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = Green, trackColor = soft)
                    Spacer(Modifier.height(7.dp))
                    Text(if (solved == 0) "İlk testini çözerek ilerleme çubuğunu başlat." else "$correct doğru cevapla devam ediyorsun.", color = muted, fontSize = 10.sp)
                }
            }
        }
        item { LastActivityCard(prefs, darkMode) }
    }
}

@Composable
private fun HomeHeader(xp: Int, solved: Int, streak: Int, darkMode: Boolean) {
    val text = if (darkMode) DarkText else Color.White
    Box(
        Modifier.fillMaxWidth().background(
            Brush.linearGradient(if (darkMode) listOf(Color(0xFF0B2A21), Color(0xFF0A4434)) else listOf(Deep, Deep2, Color(0xFF11634A)))
        ).padding(horizontal = 18.dp, vertical = 17.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Yurdunu Bil", color = text, fontSize = 26.sp, fontWeight = FontWeight.Black)
                    Text("KPSS • Türkiye Coğrafyası", color = Mint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Surface(color = Color.White.copy(alpha = .10f), shape = RoundedCornerShape(13.dp)) {
                    Text("Lv.${1 + xp / 500}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Bugün Türkiye'yi biraz daha çöz. ✨", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(11.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("$solved", "SORU")
                StatPill("$xp", "XP")
                StatPill("$streak", "SERİ")
            }
        }
    }
}

@Composable private fun StatPill(value: String, label: String) {
    Column(Modifier.clip(RoundedCornerShape(11.dp)).background(Color.White.copy(alpha = .085f)).padding(horizontal = 11.dp, vertical = 6.dp)) {
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = .55f), fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CompactFeatureCard(icon: String, eyebrow: String, title: String, subtitle: String, accent: Color, darkMode: Boolean, action: () -> Unit, button: String) {
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = action), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(47.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha = .13f)), contentAlignment = Alignment.Center) { Text(icon, fontSize = 23.sp) }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(eyebrow, color = accent, fontSize = 8.sp, fontWeight = FontWeight.Black)
                Text(title, color = text, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = muted, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Text(button, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun QuickCard(icon: String, title: String, subtitle: String, accent: Color, darkMode: Boolean, action: () -> Unit, modifier: Modifier) {
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    Card(modifier.clickable(onClick = action), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Column(Modifier.padding(12.dp)) {
            Text(icon, fontSize = 21.sp)
            Spacer(Modifier.height(4.dp))
            Text(title, color = text, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text(subtitle, color = muted, fontSize = 9.sp)
            Spacer(Modifier.height(6.dp))
            Text("Aç →", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable private fun LastActivityCard(prefs: SharedPreferences, darkMode: Boolean) {
    val activity = prefs.getString("last_activity", null) ?: return
    val surface = if (darkMode) DarkSoftGreen else SoftGreen
    val text = if (darkMode) DarkText else LightText
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, null, tint = Green)
            Spacer(Modifier.width(9.dp))
            Column {
                Text("SON ÇALIŞMAN", color = Green, fontSize = 8.sp, fontWeight = FontWeight.Black)
                Text(activity, color = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun LibraryScreen(darkMode: Boolean, onStudy: (Topic) -> Unit, onProvince: (Province) -> Unit) {
    var search by remember { mutableStateOf("") }
    var section by remember { mutableIntStateOf(0) }
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val surface = if (darkMode) DarkSurface else LightSurface
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    val provinces = GeographyData.provinces.filter { it.name.contains(search, true) || it.region.contains(search, true) || it.clue.contains(search, true) }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)) {
            Text("Kütüphane", color = text, fontSize = 27.sp, fontWeight = FontWeight.Black)
            Text("Kartlardan çalış • sonra test et", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(9.dp))
            OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text(if (section == 0) "Konu ara" else "İl ara") }, leadingIcon = { Icon(Icons.Default.Search, null) })
            Spacer(Modifier.height(8.dp))
            TabRow(selectedTabIndex = section, containerColor = Color.Transparent, contentColor = Green) {
                Tab(selected = section == 0, onClick = { section = 0 }, text = { Text("Konu Kartları", fontWeight = FontWeight.Bold) })
                Tab(selected = section == 1, onClick = { section = 1 }, text = { Text("İl Kartları", fontWeight = FontWeight.Bold) })
            }
        }

        if (section == 0) {
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(topics, key = { it.title }) { topic -> TopicCard(topic, darkMode) { onStudy(topic) } }
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(provinces, key = { it.name }) { city -> ProvinceCard(city, darkMode) { onProvince(city) } }
            }
        }
    }
}

@Composable private fun TopicCard(topic: Topic, darkMode: Boolean, onClick: () -> Unit) {
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Column(Modifier.padding(13.dp)) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Green.copy(alpha = .12f)), contentAlignment = Alignment.Center) { Text(topic.icon, fontSize = 22.sp) }
            Spacer(Modifier.height(8.dp))
            Text(topic.title, color = text, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Text(topic.subtitle, color = muted, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Text("Çalış →", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable private fun ProvinceCard(city: Province, darkMode: Boolean, onClick: () -> Unit) {
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Column(Modifier.padding(13.dp)) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color(0xFF5AA8FF).copy(alpha = .12f)), contentAlignment = Alignment.Center) { Text("📍", fontSize = 21.sp) }
            Spacer(Modifier.height(8.dp))
            Text(city.name, color = text, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(city.region, color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(city.clue, color = muted, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Text("Detaya git →", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable private fun ProvinceDetailScreen(city: Province, onBack: () -> Unit, darkMode: Boolean) {
    val bg = if (darkMode) DarkBg else LightBg
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    Column(Modifier.fillMaxSize().background(bg)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) }
            Column {
                Text(city.name, color = text, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text(city.region, color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp, 2.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Deep)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("COĞRAFYA KARTI", color = Mint, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(5.dp))
                        Text(city.clue, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            item { Text("Bilmen Gerekenler", color = text, fontSize = 18.sp, fontWeight = FontWeight.Black) }
            items(city.facts) { fact ->
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                    Row(Modifier.padding(13.dp), verticalAlignment = Alignment.Top) {
                        Text("✓", color = Green, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(9.dp))
                        Text(fact, color = text, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }
            item { Text("İpucu: Bu ili başka hangi konu başlığıyla eşleştirebilirsin?", color = muted, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp)) }
        }
    }
}

@Composable private fun EventsScreen(darkMode: Boolean, onLaunch: (SharedGameMode) -> Unit) {
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val modes = SharedGameModes.games + SharedGameModes.arenaModes
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp, 12.dp, 16.dp, 5.dp)) {
            Text("Etkinlikler", color = text, fontSize = 27.sp, fontWeight = FontWeight.Black)
            Text("Öğren, yarış, tekrar et.", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("Kısa oyunlar ve Arena modları burada.", color = muted, fontSize = 10.sp)
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(modes, key = { it.id }) { mode -> GameCard(mode, darkMode) { onLaunch(mode) } }
        }
    }
}

@Composable private fun GameCard(mode: SharedGameMode, darkMode: Boolean, onClick: () -> Unit) {
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val accent = if (mode.arena) Gold else Green
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (mode.arena) Deep else surface)) {
        Column(Modifier.padding(13.dp)) {
            Text(mode.icon, fontSize = 24.sp)
            Spacer(Modifier.height(5.dp))
            Text(mode.title, color = if (mode.arena) Color.White else text, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(mode.subtitle, color = if (mode.arena) Color.White.copy(alpha = .65f) else muted, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(9.dp))
            Text("${mode.questions} soru  •  +${mode.rewardXp} XP", color = accent, fontSize = 8.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable private fun SettingsScreen(prefs: SharedPreferences, darkMode: Boolean, onDarkModeChange: (Boolean) -> Unit) {
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val surface = if (darkMode) DarkSurface else LightSurface
    val soft = if (darkMode) DarkSoftGreen else SoftGreen
    LazyColumn(contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Ayarlar", color = text, fontSize = 27.sp, fontWeight = FontWeight.Black)
            Text("Deneyimini ve ilerlemeni yönet.", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(if (darkMode) Color.White.copy(alpha = .08f) else Color.Black.copy(alpha = .05f)), contentAlignment = Alignment.Center) { Text(if (darkMode) "🌙" else "☀️", fontSize = 19.sp) }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Karanlık mod", color = text, fontWeight = FontWeight.ExtraBold)
                        Text(if (darkMode) "Göz yormayan koyu tema aktif." else "Aydınlık tema aktif.", color = muted, fontSize = 9.sp)
                    }
                    Switch(checked = darkMode, onCheckedChange = onDarkModeChange, colors = SwitchDefaults.colors(checkedThumbColor = Deep, checkedTrackColor = Green))
                }
            }
        }
        item { Text("İLERLEMEN", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp)) }
        item { StatSetting("Çözülen soru", prefs.getInt("solved", 0).toString(), Icons.Default.MenuBook, darkMode) }
        item { StatSetting("Kazanılan XP", prefs.getInt("xp", 0).toString(), Icons.Default.Star, darkMode) }
        item { StatSetting("Doğru cevap", prefs.getInt("correct", 0).toString(), Icons.Default.CheckCircle, darkMode) }
        item { StatSetting("Yanlış cevap", prefs.getInt("wrong", 0).toString(), Icons.Default.Close, darkMode) }
        item { StatSetting("Günlük seri", prefs.getInt("streak", 0).toString(), Icons.Default.LocalFireDepartment, darkMode) }
        item {
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = soft)) {
                Column(Modifier.padding(14.dp)) {
                    Text("ÇALIŞMA İPUCU", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(4.dp))
                    Text("Kütüphanede kartı oku → aynı konunun testini çöz → yanlışlarına tekrar bak. Bu döngü kısa çalışmalarda bile çok daha verimli.", color = text, fontSize = 11.sp, lineHeight = 17.sp)
                }
            }
        }
    }
}

@Composable private fun StatSetting(title: String, value: String, icon: ImageVector, darkMode: Boolean) {
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Green)
            Spacer(Modifier.width(10.dp))
            Text(title, color = text, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(value, color = Green, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun V2QuizScreen(title: String, mode: SharedGameMode, questions: List<Question>, prefs: SharedPreferences, darkMode: Boolean, onFinish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var correct by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var finishing by remember { mutableStateOf(false) }
    var earnedXp by remember { mutableIntStateOf(0) }

    val bg = if (darkMode) DarkBg else LightBg
    val surface = if (darkMode) DarkSurface else LightSurface
    val text = if (darkMode) DarkText else LightText
    val muted = if (darkMode) DarkMuted else LightMuted
    val soft = if (darkMode) DarkSoftGreen else SoftGreen

    if (finished) {
        val blank = questions.size - correct - wrong
        ResultScreen(title, correct, wrong, blank, earnedXp, darkMode, onFinish)
        return
    }
    val q = questions[index]
    val revealed = selected >= 0

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onFinish) { Icon(Icons.Default.Close, "Kapat", tint = text) }
                    Text(title, color = text, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${index + 1}/${questions.size}", color = Green, fontWeight = FontWeight.Bold)
                }
            }
            item { LinearProgressIndicator(progress = { (index + 1f) / questions.size }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = Green, trackColor = soft) }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(q.topic, color = Green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(6.dp))
                        Text(q.text, color = text, fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            items(q.options.indices.toList()) { optionIndex ->
                val isCorrect = optionIndex == q.correctIndex
                val isChosen = optionIndex == selected
                val targetScale = if (revealed && (isCorrect || isChosen)) 1.012f else 1f
                val optionScale by animateFloatAsState(targetScale, tween(180, easing = FastOutSlowInEasing), label = "optionScale")
                val optionBg = when {
                    revealed && isCorrect -> soft
                    revealed && isChosen -> if (darkMode) Color(0xFF3A1C1C) else Color(0xFFFFE8E8)
                    else -> surface
                }
                val optionTint = when { revealed && isCorrect -> Green; revealed && isChosen -> Red; else -> text }
                Card(Modifier.fillMaxWidth().scale(optionScale).clickable(enabled = !revealed) { selected = optionIndex; if (isCorrect) correct++ else wrong++ }, RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = optionBg)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(('A'.code + optionIndex).toChar().toString(), color = optionTint, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(11.dp))
                        Text(q.options[optionIndex], color = text, modifier = Modifier.weight(1f), fontSize = 12.sp, lineHeight = 17.sp)
                        if (revealed && isCorrect) Icon(Icons.Default.CheckCircle, null, tint = Green) else if (revealed && isChosen) Icon(Icons.Default.Cancel, null, tint = Red)
                    }
                }
            }
            if (revealed) {
                item {
                    AnimatedVisibility(true, enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 5 }) {
                        Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = soft)) {
                            Column(Modifier.padding(14.dp)) {
                                Text("💡 DİKKAT KÖŞESİ", color = Green, fontWeight = FontWeight.Black, fontSize = 9.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(q.explanation, color = text, fontSize = 11.sp, lineHeight = 17.sp)
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            if (finishing) return@Button
                            if (index == questions.lastIndex) {
                                finishing = true
                                earnedXp = ProgressTracker.recordQuiz(prefs, mode, questions.size, correct, wrong)
                                if (mode.id == SharedQuestionPool.dailyMode().id) prefs.edit().putString("daily_completed_date", java.time.LocalDate.now().toString()).apply()
                                finished = true
                            } else {
                                index++
                                selected = -1
                            }
                        },
                        enabled = !finishing,
                        modifier = Modifier.fillMaxWidth().height(49.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (darkMode) Green else Deep, contentColor = if (darkMode) Deep else Color.White)
                    ) { Text(if (index == questions.lastIndex) "Sonucu Gör" else "Sonraki Soru", fontWeight = FontWeight.Black) }
                }
            }
        }
    }
}

@Composable private fun ResultScreen(title: String, correct: Int, wrong: Int, blank: Int, xp: Int, darkMode: Boolean, onExit: () -> Unit) {
    val bg = if (darkMode) DarkBg else LightBg
    val text = if (darkMode) DarkText else LightText
    val total = correct + wrong + blank
    Column(Modifier.fillMaxSize().background(bg).padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(true, enter = fadeIn(tween(450)) + scaleIn(tween(450), initialScale = .88f)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TEST TAMAMLANDI", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(6.dp))
                Text(title, color = text, fontSize = 27.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = Deep)) {
                    Column(Modifier.padding(21.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$correct / $total", color = Color.White, fontSize = 37.sp, fontWeight = FontWeight.Black)
                        Text("Doğru cevap", color = Mint, fontSize = 11.sp)
                        Spacer(Modifier.height(14.dp))
                        Text("✓ $correct doğru   •   ✕ $wrong yanlış   •   – $blank boş", color = Color.White.copy(alpha = .73f), fontSize = 10.sp)
                        Text("+$xp XP", color = Gold, fontSize = 17.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 9.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onExit, modifier = Modifier.fillMaxWidth().height(51.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Deep)) { Text("Ana Ekrana Dön", fontWeight = FontWeight.Black) }
            }
        }
    }
}
