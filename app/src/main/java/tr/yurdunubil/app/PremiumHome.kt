package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val PDeep = Color(0xFF06271F)
private val PGreen = Color(0xFF16B87A)
private val PMint = Color(0xFFDDF9EA)
private val PGold = Color(0xFFFFC857)
private val PRed = Color(0xFFE45454)
private val PBg = Color(0xFFF4F8F6)

@Composable
fun PremiumHomeScreen(state: AppProgressStore.Snapshot, start: (String, List<Question>) -> Unit, onPlanner: () -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in state.wrongIds }
    val recommended = recommendedQuestions(state, 10)
    val weak = buildStudyInsights(state).firstOrNull()?.topic ?: "Başlangıç rotası"
    val today = state.todaySolved.coerceAtMost(10)
    val dailyProgress = (today / 10f).coerceIn(0f, 1f)
    val level = 1 + state.xp / 500
    val levelProgress = ((state.xp % 500) / 500f).coerceIn(0f, 1f)

    LazyColumn(modifier = Modifier.fillMaxSize().background(PBg), contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Box(Modifier.fillMaxWidth().background(PDeep, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("YURDUNU BİL", color = PMint, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
                            Text("Bugün de Türkiye'yi keşfet.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("KPSS Önlisans • 2026 Coğrafya", color = PMint, fontSize = 12.sp)
                        }
                        Surface(color = PGreen.copy(alpha = .18f), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(horizontal = 13.dp, vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LVL", color = PMint, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                Text("$level", color = PGold, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatChip("${state.xp} XP")
                        StatChip("🔥 ${state.streak} gün")
                        StatChip("%$accuracy")
                    }
                    LinearProgressIndicator(progress = { levelProgress }, modifier = Modifier.fillMaxWidth().height(6.dp), color = PGold, trackColor = Color.White.copy(alpha = .12f))
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(if (today >= 10) "BUGÜN TAMAMLANDI 🎉" else "BUGÜNÜN MİSYONU", color = PGreen, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text(if (today >= 10) "Seriyi koruduk!" else "10 soru ile ritmi yakala.", color = PDeep, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Text("$today / 10 soru", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text("${(dailyProgress * 100).roundToInt()}%", color = PGreen, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                    LinearProgressIndicator(progress = { dailyProgress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = PGreen, trackColor = PMint)
                    Button(onClick = { start("Bugünün 10 Sorusu", recommended.ifEmpty { SharedQuestionPool.all.shuffled().take(10) }) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = PDeep)) {
                        Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text(if (today >= 10) "Ekstra pratik yap" else "Hemen başla", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionTile("⚡", "Hızlı 10", "Karışık test", PGreen, Modifier.weight(1f)) { start("Hızlı 10", SharedQuestionPool.all.shuffled().take(10)) }
                ActionTile("🧠", "Akıllı Tekrar", "Sana özel", PGold, Modifier.weight(1f)) { start("Akıllı Tekrar", recommended.ifEmpty { SharedQuestionPool.all.shuffled().take(10) }) }
            }
        }
        item { WideAction("🧭", "Bugün ne çalışmalıyım?", "Zayıf konuna göre çalışma rotası oluştur.", PGold, onPlanner) }
        item { Text("Sana özel", color = PDeep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 18.dp)) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PDeep), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 28.sp, modifier = Modifier.padding(end = 14.dp))
                    Column(Modifier.weight(1f)) { Text("ÖNCELİKLİ KONU", color = PGold, fontSize = 10.sp, fontWeight = FontWeight.Black); Text(weak, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("Akıllı Tekrar bunu önceliklendiriyor.", color = PMint, fontSize = 12.sp) }
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Metric("📚", "${state.solved}", "Çözülen", Modifier.weight(1f)); Metric("✓", "${state.correct}", "Doğru", Modifier.weight(1f)); Metric("✕", "${wrong.size}", "Yanlış", Modifier.weight(1f))
            }
        }
        item { WideAction("🔁", "Yanlışlarını temizle", if (wrong.isEmpty()) "Henüz yanlış soru birikmedi. Böyle devam!" else "${wrong.size} yanlış soru tekrarını bekliyor.", PRed) { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.take(20)) } }
    }
}

@Composable private fun StatChip(text: String) { Surface(color = Color.White.copy(alpha = .12f), shape = RoundedCornerShape(50.dp)) { Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) } }

@Composable private fun ActionTile(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier, onClick: () -> Unit) { Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), modifier = modifier.height(122.dp)) { Column(Modifier.fillMaxSize().padding(15.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 24.sp); Spacer(Modifier.weight(1f)); Icon(Icons.Default.ArrowForward, null, tint = accent) }; Spacer(Modifier.weight(1f)); Text(title, color = PDeep, fontWeight = FontWeight.Black); Text(subtitle, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold) } } }

@Composable private fun WideAction(icon: String, title: String, subtitle: String, accent: Color, onClick: () -> Unit) { Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Surface(color = accent.copy(alpha = .13f), shape = RoundedCornerShape(15.dp)) { Text(icon, fontSize = 25.sp, modifier = Modifier.padding(11.dp)) }; Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, color = PDeep, fontWeight = FontWeight.Black, fontSize = 16.sp); Text(subtitle, color = Color.Gray, fontSize = 11.sp) }; Icon(Icons.Default.ChevronRight, null, tint = accent) } } }

@Composable private fun Metric(icon: String, value: String, label: String, modifier: Modifier) { Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp), modifier = modifier) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) { Text(icon, fontSize = 17.sp); Text(value, color = PDeep, fontSize = 20.sp, fontWeight = FontWeight.Black); Text(label, color = Color.Gray, fontSize = 10.sp) } } }
