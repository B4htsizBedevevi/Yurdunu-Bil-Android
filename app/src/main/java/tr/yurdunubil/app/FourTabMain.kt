package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val FDeep = Color(0xFF06221B)
private val FDeep2 = Color(0xFF0B342B)
private val FGreen = Color(0xFF18C986)
private val FMint = Color(0xFFC9F8E1)
private val FGold = Color(0xFFFFC857)
private val FRed = Color(0xFFE65353)
private val FBg = Color(0xFFF3F8F5)
private val FSoft = Color(0xFFE4F7ED)
private val FLine = Color(0xFFDCEAE3)
private val FMuted = Color(0xFF70847B)

@Composable
fun FourTabMainApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var refresh by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }

    val launchQuiz: (String, List<Question>) -> Unit = { title, questions ->
        if (questions.isNotEmpty()) { quizTitle = title; quiz = questions }
    }

    if (quiz != null) {
        FourTabQuiz(quizTitle, quiz!!, prefs) {
            quiz = null
            refresh++
        }
        return
    }

    Scaffold(
        containerColor = FBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
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
                        icon = { Icon(icon, label, Modifier.size(if (tab == index) 25.dp else 23.dp)) },
                        label = { Text(label, fontSize = 10.sp, fontWeight = if (tab == index) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FGreen, selectedTextColor = FDeep,
                            indicatorColor = FSoft, unselectedIconColor = FMuted, unselectedTextColor = FMuted
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            key(refresh) {
                AnimatedContent(targetState = tab, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "tab-transition") { current ->
                    when (current) {
                        0 -> FourHome(prefs, launchQuiz) { tab = 2 }
                        1 -> FourLibrary(launchQuiz)
                        2 -> FourEvents(launchQuiz)
                        else -> FourSettings(prefs)
                    }
                }
            }
        }
    }
}

@Composable
private fun FourHome(p: SharedPreferences, launch: (String, List<Question>) -> Unit, openEvents: () -> Unit) {
    val solved = p.getInt("solved", 0)
    val correct = p.getInt("correct", 0)
    val xp = p.getInt("xp", 0)
    val streak = p.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val daily = SharedQuestionPool.dailyMode()
    val pulse = rememberInfinitePulse()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            AnimatedVisibility(visible, enter = fadeIn() + slideInHorizontally(initialOffsetX = { -40 })) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(FDeep, FDeep2, Color(0xFF11634A))), RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp)).padding(22.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(48.dp).clip(CircleShape).background(FGreen.copy(.16f)), contentAlignment = Alignment.Center) { Text("🧭", fontSize = 26.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Yurdunu Bil", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                                Text("KPSS Önlisans • Türkiye Coğrafyası", color = FMint, fontSize = 11.sp)
                            }
                            Box(Modifier.clip(CircleShape).background(Color.White.copy(.10f)).padding(horizontal = 10.dp, vertical = 8.dp)) { Text("Lv.${1 + xp / 500}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        }
                        Spacer(Modifier.height(20.dp))
                        Text("Bugün Türkiye'yi biraz daha çöz. ✨", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Kısa bir tur bile bilgini taze tutar.", color = Color.White.copy(.66f), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.height(17.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FourStatPill("$solved", "SORU", Icons.Default.MenuBook)
                            FourStatPill("$xp", "XP", Icons.Default.Star)
                            FourStatPill("$streak", "SERİ", Icons.Default.LocalFireDepartment)
                        }
                    }
                }
            }
        }
        item {
            AnimatedVisibility(visible, enter = fadeIn() + scaleIn()) {
                FourFeatureCard(FGreen) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(CircleShape).background(FSoft), contentAlignment = Alignment.Center) { Text("🎯", fontSize = 26.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("BUGÜNÜN GÖREVİ", color = FGreen, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text(daily.title, color = FDeep, fontSize = 20.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${daily.questions} soru  •  +${daily.rewardXp} XP", color = FMuted, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Button({ launch(daily.title, SharedQuestionPool.pick(daily)) }, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(FGreen)) {
                        Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Göreve Başla", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FourQuickTile("⚡", "Hızlı 10", "2 dk", Modifier.weight(1f)) { launch("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick)) }
                FourQuickTile("🔥", "Bilgi Zinciri", "seri yap", Modifier.weight(1f)) { launch("Bilgi Zinciri", SharedQuestionPool.pick(SharedGameModes.chain)) }
            }
        }
        item {
            FourFeatureCard(FGold, dark = true, onClick = openEvents) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚔️", fontSize = 34.sp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ARENA", color = FGold, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                        Text("Bilgini sahaya çıkar.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(.62f), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = FGold)
                }
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(Color.White.copy(.10f))) {
                    Box(Modifier.fillMaxWidth(.62f).fillMaxHeight().clip(CircleShape).background(FGold).graphicsLayer { alpha = .72f + pulse * .28f })
                }
            }
        }
        item {
            FourFeatureCard(FGreen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("GELİŞİMİN", color = FGreen, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("%$accuracy genel doğruluk", color = FDeep, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text(if (solved == 0) "İlk turunu tamamla ve grafiği başlat." else "$correct doğru cevapla ritmini koruyorsun.", color = FMuted, fontSize = 11.sp)
                    }
                    Box(Modifier.size(58.dp).clip(CircleShape).background(FSoft), contentAlignment = Alignment.Center) { Text("$accuracy%", color = FGreen, fontSize = 13.sp, fontWeight = FontWeight.Black) }
                }
                Spacer(Modifier.height(13.dp))
                LinearProgressIndicator({ accuracy / 100f }, Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = FGreen, trackColor = FSoft)
            }
        }
    }
}

@Composable
private fun FourLibrary(launch: (String, List<Question>) -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    val provinces = fourProvinces().filter { search.isBlank() || it.name.contains(search, true) || it.region.contains(search, true) }

    LazyColumn(contentPadding = PaddingValues(18.dp, 16.dp, 18.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Kütüphane", color = FDeep, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Oku → keşfet → çöz → pekiştir", color = FGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), leadingIcon = { Icon(Icons.Default.Search, null, tint = FGreen) }, placeholder = { Text("Konu, il veya bölge ara…") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FGreen, cursorColor = FGreen))
        }
        item {
            FourFeatureCard(FGreen, dark = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(FGreen.copy(.15f)), contentAlignment = Alignment.Center) { Text("🗺️", fontSize = 29.sp) }
                    Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) {
                        Text("TÜRKİYE ATLASI", color = FMint, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("81 il • 7 bölge", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        Text("İlleri ipuçları ve KPSS bağlantılarıyla keşfet.", color = Color.White.copy(.68f), fontSize = 11.sp)
                    }
                }
            }
        }
        item { Text("Konu Bankası", color = FDeep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 3.dp)) }
        items(topics, key = { it.title }) { topic ->
            FourTopicCard(topic) {
                val mode = SharedGameMode("topic-${topic.title}", topic.title, topic.subtitle, topic.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(topic.title))
                launch(topic.title, SharedQuestionPool.pick(mode))
            }
        }
        item { Text("81 İli Keşfet", color = FDeep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 5.dp)) }
        items(provinces, key = { it.name }) { province -> FourProvinceRow(province) { selectedProvince = province } }
    }

    selectedProvince?.let { province ->
        AlertDialog(onDismissRequest = { selectedProvince = null }, containerColor = Color.White, shape = RoundedCornerShape(24.dp), title = { Text("📍 ${province.name}", color = FDeep, fontWeight = FontWeight.Black, fontSize = 24.sp) }, text = {
            Column { Text(province.region, color = FGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text(province.clue, color = FDeep, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(10.dp)); province.facts.forEach { Text("• $it", color = FMuted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 3.dp)) } }
        }, confirmButton = { TextButton({ selectedProvince = null }) { Text("Kapat", color = FGreen, fontWeight = FontWeight.Bold) } })
    }
}

@Composable
private fun FourEvents(launch: (String, List<Question>) -> Unit) {
    val modes = SharedGameModes.games + SharedGameModes.arenaModes
    LazyColumn(contentPadding = PaddingValues(18.dp, 16.dp, 18.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Etkinlikler", color = FDeep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Öğrenirken yarış, ritmini koru.", color = FGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        item {
            FourFeatureCard(FGold, dark = true) {
                Row(verticalAlignment = Alignment.CenterVertically) { Text("🏆", fontSize = 38.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) {
                    Text("SEZON 1", color = FGold, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text("Hız • bölge • bilgi • meydan okuma", color = Color.White.copy(.65f), fontSize = 11.sp)
                } }
                Spacer(Modifier.height(14.dp)); Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { FourTinyBadge("🔥 Günlük seri"); FourTinyBadge("🏅 XP kazan"); FourTinyBadge("⚡ Süreli") }
            }
        }
        item { Text("Oyun Modları", color = FDeep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 3.dp)) }
        items(modes, key = { it.id }) { mode -> FourModeCard(mode) { launch(mode.title, SharedQuestionPool.pick(mode)) } }
    }
}

@Composable
private fun FourSettings(p: SharedPreferences) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val wrong = p.getInt("wrong", 0); val xp = p.getInt("xp", 0); val level = 1 + xp / 500; val progress = (xp % 500) / 500f
    var resetConfirm by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(18.dp, 16.dp, 18.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Ayarlar", color = FDeep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("İlerlemeni ve uygulama tercihlerini yönet", color = FGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        item {
            FourFeatureCard(FGreen, dark = true) {
                Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(58.dp).clip(CircleShape).background(FGreen.copy(.15f)), contentAlignment = Alignment.Center) { Text("🧭", fontSize = 30.sp) }; Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) {
                    Text("GEZGİN", color = FMint, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp); Text("Seviye $level", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("$xp XP • $solved soru çözüldü", color = Color.White.copy(.68f), fontSize = 11.sp)
                } }
                Spacer(Modifier.height(12.dp)); LinearProgressIndicator({ progress }, Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = FGreen, trackColor = Color.White.copy(.12f)); Text("Sonraki seviye: ${500 - (xp % 500)} XP", color = Color.White.copy(.58f), fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
            }
        }
        item { FourSettingRow(Icons.Default.BarChart, "İstatistikler", "$solved soru • $correct doğru • $wrong yanlış") }
        item { FourSettingRow(Icons.Default.Lock, "Hesap", "Yerel ilerleme • Supabase oturumu hazır") }
        item { FourSettingRow(Icons.Default.Info, "Sürüm", "Yurdunu Bil Android • 0.2.0") }
        item {
            FourFeatureCard(FRed, onClick = { resetConfirm = true }) { Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).clip(CircleShape).background(FRed.copy(.10f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.RestartAlt, null, tint = FRed) }; Spacer(Modifier.width(10.dp)); Column { Text("İlerlemeyi Sıfırla", color = FRed, fontWeight = FontWeight.ExtraBold); Text("Yerel XP ve soru istatistiklerini temizler", color = FMuted, fontSize = 11.sp) }
            } }
        }
    }
    if (resetConfirm) AlertDialog(onDismissRequest = { resetConfirm = false }, title = { Text("İlerleme sıfırlansın mı?", fontWeight = FontWeight.Black, color = FDeep) }, text = { Text("Bu işlem cihazdaki yerel XP, soru ve istatistiklerini temizler.") }, confirmButton = { TextButton({ p.edit().clear().apply(); resetConfirm = false }) { Text("Sıfırla", color = FRed, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton({ resetConfirm = false }) { Text("Vazgeç") } })
}

@Composable
private fun FourTabQuiz(title: String, source: List<Question>, p: SharedPreferences, finish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }; var selected by remember { mutableIntStateOf(-1) }; var correct by remember { mutableIntStateOf(0) }; var wrong by remember { mutableIntStateOf(0) }; var saved by remember { mutableStateOf(false) }; var answerLocked by remember { mutableStateOf(false) }
    if (saved) {
        LaunchedEffect(Unit) { p.edit().putInt("solved", p.getInt("solved", 0) + source.size).putInt("correct", p.getInt("correct", 0) + correct).putInt("wrong", p.getInt("wrong", 0) + wrong).putInt("xp", p.getInt("xp", 0) + correct * 10).apply() }
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { AnimatedVisibility(true, enter = scaleIn() + fadeIn()) { FourFeatureCard(if (correct >= wrong) FGreen else FGold) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) { Text(if (correct >= wrong) "🏆" else "💪", fontSize = 58.sp); Text("Tur tamamlandı", color = FDeep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("$correct / ${source.size} doğru", color = FGreen, fontSize = 23.sp, fontWeight = FontWeight.Black); Text("$wrong yanlış • +${correct * 10} XP", color = FMuted, fontSize = 12.sp); Spacer(Modifier.height(18.dp)); LinearProgressIndicator({ correct / source.size.toFloat() }, Modifier.fillMaxWidth().height(9.dp).clip(CircleShape), color = FGreen, trackColor = FSoft); Button(finish, Modifier.fillMaxWidth().padding(top = 20.dp).height(48.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(FGreen)) { Text("Devam Et", fontWeight = FontWeight.Bold) } }
        } } }
        return
    }
    val q = source[index]
    LazyColumn(contentPadding = PaddingValues(18.dp, 12.dp, 18.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(finish) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = FDeep, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("Soru ${index + 1} / ${source.size}", color = FGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold) }; Box(Modifier.clip(CircleShape).background(FSoft).padding(horizontal = 11.dp, vertical = 7.dp)) { Text("+${correct * 10} XP", color = FGreen, fontSize = 10.sp, fontWeight = FontWeight.Black) } } }
        item { LinearProgressIndicator({ (index + 1f) / source.size }, Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = FGreen, trackColor = FSoft) }
        item { FourFeatureCard(FGreen) { Text(q.topic, color = FGreen, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = 1.sp); Spacer(Modifier.height(7.dp)); Text(q.text, color = FDeep, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 27.sp) } }
        itemsIndexed(q.options) { i, option ->
            val isCorrect = i == q.correctIndex; val isSelected = i == selected; val revealed = selected != -1
            val border = when { !revealed -> FLine; isCorrect -> FGreen; isSelected -> FRed; else -> FLine }
            val bg = when { !revealed -> Color.White; isCorrect -> FSoft; isSelected -> FRed.copy(.08f); else -> Color.White }
            Card(Modifier.fillMaxWidth().border(1.7.dp, border, RoundedCornerShape(17.dp)).clickable(enabled = !answerLocked) { selected = i; answerLocked = true; if (isCorrect) correct++ else wrong++ }, RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(bg), elevation = CardDefaults.cardElevation(if (revealed) 1.dp else 3.dp)) {
                Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).clip(CircleShape).background(border.copy(.10f)), contentAlignment = Alignment.Center) { Text("${('A'.code + i).toChar()}", color = border, fontWeight = FontWeight.Black, fontSize = 12.sp) }
                    Spacer(Modifier.width(11.dp)); Text(option, Modifier.weight(1f), color = FDeep, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    if (revealed && isCorrect) Icon(Icons.Default.CheckCircle, null, tint = FGreen, Modifier.size(22.dp)) else if (revealed && isSelected) Icon(Icons.Default.Cancel, null, tint = FRed, Modifier.size(22.dp))
                }
            }
        }
        item { AnimatedVisibility(selected != -1, enter = fadeIn() + scaleIn()) { FourFeatureCard(if (selected == q.correctIndex) FGreen else FGold) { Row(verticalAlignment = Alignment.Top) { Icon(Icons.Default.Lightbulb, null, tint = if (selected == q.correctIndex) FGreen else FGold); Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text(if (selected == q.correctIndex) "DOĞRU!" else "DİKKAT KÖŞESİ", color = if (selected == q.correctIndex) FGreen else FGold, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp); Text(q.explanation, color = FDeep, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp)) } } } } }
        item { AnimatedVisibility(selected != -1, enter = fadeIn()) { Button({ if (index == source.lastIndex) saved = true else { index++; selected = -1; answerLocked = false } }, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(FDeep)) { Text(if (index == source.lastIndex) "Sonucu Gör" else "Sonraki Soru", fontWeight = FontWeight.Bold); Spacer(Modifier.width(5.dp)); Icon(Icons.Default.ArrowForward, null) } } }
    }
}

@Composable
private fun FourFeatureCard(accent: Color, dark: Boolean = false, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(21.dp)
    val interaction = remember { MutableInteractionSource() }
    val base = Modifier.fillMaxWidth().clip(shape).background(if (dark) Brush.linearGradient(listOf(FDeep, FDeep2)) else Brush.linearGradient(listOf(Color.White, Color.White))).border(1.dp, if (dark) accent.copy(.22f) else accent.copy(.14f), shape).padding(17.dp)
    val modifier = if (onClick != null) base.clickable(interactionSource = interaction, indication = null, onClick = onClick) else base
    Column(modifier, content = content)
}

@Composable
private fun FourStatPill(value: String, label: String, icon: ImageVector) {
    Row(Modifier.clip(RoundedCornerShape(13.dp)).background(Color.White.copy(.08f)).padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = FMint, Modifier.size(15.dp)); Spacer(Modifier.width(5.dp)); Column { Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp); Text(label, color = Color.White.copy(.52f), fontSize = 7.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun FourQuickTile(icon: String, title: String, sub: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick), RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) { Column(Modifier.padding(15.dp)) { Text(icon, fontSize = 27.sp); Text(title, color = FDeep, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, modifier = Modifier.padding(top = 7.dp)); Text(sub, color = FGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold) } }
}

@Composable
private fun FourTopicCard(topic: Topic, onClick: () -> Unit) {
    val progress = topic.progress.coerceIn(0, 100)
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(FSoft), contentAlignment = Alignment.Center) { Text(topic.icon, fontSize = 24.sp) }
            Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(topic.title, color = FDeep, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold); Text(topic.subtitle, color = FMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); LinearProgressIndicator({ progress / 100f }, Modifier.fillMaxWidth().padding(top = 7.dp).height(5.dp).clip(CircleShape), color = FGreen, trackColor = FSoft) }; Spacer(Modifier.width(8.dp)); Icon(Icons.Default.ArrowForward, null, tint = FGreen, Modifier.size(20.dp))
        }
    }
}

@Composable
private fun FourProvinceRow(province: Province, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(38.dp).clip(CircleShape).background(FSoft), contentAlignment = Alignment.Center) { Text("📍", fontSize = 18.sp) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(province.name, color = FDeep, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp); Text(province.region, color = FGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Icon(Icons.Default.ChevronRight, null, tint = FMuted, Modifier.size(20.dp)) }
    }
}

@Composable
private fun FourModeCard(mode: SharedGameMode, onClick: () -> Unit) {
    val accent = if (mode.arena) FGold else FGreen
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(.12f)), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 25.sp) }; Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(mode.title, color = FDeep, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, modifier = Modifier.weight(1f)); if (mode.arena) Text("ARENA", color = FGold, fontSize = 8.sp, fontWeight = FontWeight.Black) }; Text(mode.subtitle, color = FMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp)) }; Icon(Icons.Default.PlayArrow, null, tint = accent, Modifier.size(22.dp)) }
    }
}

@Composable
private fun FourTinyBadge(text: String) { Box(Modifier.clip(RoundedCornerShape(9.dp)).background(Color.White.copy(.08f)).padding(horizontal = 8.dp, vertical = 6.dp)) { Text(text, color = Color.White.copy(.76f), fontSize = 8.sp, fontWeight = FontWeight.Bold) } }

@Composable
private fun FourSettingRow(icon: ImageVector, title: String, value: String) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(1.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(FSoft), contentAlignment = Alignment.Center) { Icon(icon, null, tint = FGreen, Modifier.size(21.dp)) }; Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(title, color = FDeep, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp); Text(value, color = FMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp)) } } }
}

@Composable
private fun rememberInfinitePulse(): Float {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "pulse-value")
    return pulse
}

private fun fourProvinces(): List<Province> {
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
