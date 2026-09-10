package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

class SmartStudyCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SmartStudyCenterScreen(onBack = { finish() }) }
    }
}

@Composable
private fun SmartStudyCenterScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    val bg = Color(0xFF06140F)
    val card = Color(0xFF10251E)
    val cardAlt = Color(0xFF15352B)
    val text = Color(0xFFF3FBF7)
    val muted = Color(0xFF95AEA5)
    val green = Color(0xFF28DE98)
    val gold = Color(0xFFFFC857)
    val today = LocalDate.now().toString()

    val solved = prefs.getInt("solved", 0)
    val correct = prefs.getInt("correct", 0)
    val wrong = prefs.getInt("wrong", 0)
    val xp = prefs.getInt("xp", 0)
    val streak = prefs.getInt("streak", 0)
    val todaySolved = if (prefs.getString("today_stats_date", "") == today) prefs.getInt("today_solved", 0) else 0
    val todayCorrect = if (prefs.getString("today_stats_date", "") == today) prefs.getInt("today_correct", 0) else 0
    val todayXp = if (prefs.getString("today_stats_date", "") == today) prefs.getInt("today_xp", 0) else 0
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).toInt()
    val target = 20
    val dailyProgress = (todaySolved.toFloat() / target).coerceIn(0f, 1f)
    val dailyDone = prefs.getString("daily_completed_date", "") == today
    val level = 1 + xp / 500
    val levelXp = xp % 500

    val priorities = GeographyData.topics.sortedBy { it.progress }.take(4)
    val challengeIndex = LocalDate.now().dayOfYear % GeographyData.provinces.size
    val challenge = GeographyData.provinces[challengeIndex]
    val optionIndexes = listOf(
        challengeIndex,
        (challengeIndex + 3) % GeographyData.provinces.size,
        (challengeIndex + 7) % GeographyData.provinces.size,
        (challengeIndex + 11) % GeographyData.provinces.size
    )
    var mapSolved by remember(today) { mutableStateOf(prefs.getString("map_challenge_date", "") == today) }

    fun markMapChallenge() {
        prefs.edit().putString("map_challenge_date", today).apply()
        mapSolved = true
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = green, background = bg, surface = card, onSurface = text)) {
        Scaffold(
            containerColor = bg,
            topBar = {
                TopAppBar(
                    title = { Text("Akıllı Çalışma", fontWeight = FontWeight.Black) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bg, titleContentColor = text, navigationIconContentColor = text)
                )
            }
        ) { pad ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(pad),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = cardAlt)) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("BUGÜNÜN ROTASI", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                    Text("Seviye $level", color = text, fontSize = 24.sp, fontWeight = FontWeight.Black)
                                    Text("$levelXp / 500 XP • toplam $xp XP", color = muted, fontSize = 10.sp)
                                }
                                Icon(Icons.Default.EmojiEvents, null, tint = gold, modifier = Modifier.size(32.dp))
                            }
                            Spacer(Modifier.height(14.dp))
                            Text("Bugün $todaySolved / $target soru", color = text, fontWeight = FontWeight.Black, fontSize = 15.sp)
                            Spacer(Modifier.height(7.dp))
                            LinearProgressIndicator(progress = { dailyProgress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = green, trackColor = bg)
                            Spacer(Modifier.height(7.dp))
                            Text(if (dailyDone) "✓ Günlük görev tamamlandı. Ritmi korudun." else "20 soruya ulaş. Küçük ama düzenli çalışma büyük fark yaratır.", color = if (dailyDone) green else muted, fontSize = 10.sp)
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricCard("$todaySolved", "BUGÜN SORU", Icons.Default.Bolt, green, text, muted, Modifier.weight(1f))
                        MetricCard("$todayCorrect", "BUGÜN DOĞRU", Icons.Default.CheckCircle, green, text, muted, Modifier.weight(1f))
                        MetricCard("$todayXp", "BUGÜN XP", Icons.Default.EmojiEvents, gold, text, muted, Modifier.weight(1f))
                    }
                }

                item {
                    Card(shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = card)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Whatshot, null, tint = gold, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Seri koruyucu", color = text, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    Text("$streak günlük çalışma serisi", color = muted, fontSize = 10.sp)
                                }
                                Text(if (streak >= 7) "Harika gidiyor" else "Devam", color = green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(Modifier.height(10.dp))
                            Text("Genel doğruluk %$accuracy • $wrong yanlış", color = muted, fontSize = 10.sp)
                        }
                    }
                }

                item {
                    Text("🎯 Çalışma öncelikleri", color = text, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Kütüphanedeki ilerleme oranı düşük konularını öne aldık.", color = muted, fontSize = 10.sp)
                }

                items(priorities, key = { it.title }) { topic ->
                    Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = card), modifier = Modifier.clickable { onBack() }) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(42.dp).background(cardAlt, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(topic.icon, fontSize = 21.sp) }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(topic.title, color = text, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                Text(topic.subtitle, color = muted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Text("%${topic.progress}", color = if (topic.progress >= 70) green else gold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }

                item {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardAlt)) {
                        Column(Modifier.padding(17.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Map, null, tint = green, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(9.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("İL AVI • BUGÜN", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    Text("İpucundan ili yakala", color = text, fontSize = 17.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(challenge.clue, color = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(Modifier.height(10.dp))
                            optionIndexes.forEach { index ->
                                val province = GeographyData.provinces[index]
                                val selectedCorrect = mapSolved && index == challengeIndex
                                OutlinedButton(
                                    onClick = { if (!mapSolved && index == challengeIndex) markMapChallenge() },
                                    enabled = !mapSolved,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(13.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (selectedCorrect) green else text),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCorrect) green else Color.White.copy(alpha = .10f))
                                ) { Text(if (selectedCorrect) "✓ ${province.name}" else province.name, fontWeight = FontWeight.Bold) }
                            }
                            if (mapSolved) Text("✓ Bugünün il avı tamamlandı.", color = green, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }

                item {
                    Card(shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = card)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = gold, modifier = Modifier.size(25.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Mini koç notu", color = gold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                Text(if (accuracy < 60) "Önce doğruluğu yükselt; hız sonra gelir." else if (accuracy < 80) "Temelin oluşuyor. Zayıf konuları kısa tekrarlarla kapat." else "Çok iyi. Şimdi zor sorular ve Arena ile seviyeyi yükselt.", color = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color, text: Color, muted: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF10251E))) {
        Column(Modifier.padding(11.dp)) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, color = text, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(label, color = muted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
        }
    }
}
