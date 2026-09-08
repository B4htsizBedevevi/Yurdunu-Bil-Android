package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PlannerDeep = Color(0xFF06271F)
private val PlannerGreen = Color(0xFF16B87A)
private val PlannerMint = Color(0xFFDDF9EA)
private val PlannerGold = Color(0xFFFFC857)

@Composable
fun StudyPlannerScreen(
    state: AppProgressStore.Snapshot,
    start: (String, List<Question>) -> Unit,
    close: () -> Unit
) {
    val insights = buildStudyInsights(state)
    val weakTopics = insights.take(2).map { it.topic }
    val weakQuestions = SharedQuestionPool.all.filter { it.topic in weakTopics && it.id !in state.masteredIds }.shuffled().take(8)
    val reviewQuestions = SharedQuestionPool.all.filter { it.id in state.wrongIds }.shuffled().take(6)
    val freshQuestions = SharedQuestionPool.all.filter { it.id !in state.seenIds }.shuffled().take(6)
    val total = (if (reviewQuestions.isNotEmpty()) 6 else 0) + (if (weakQuestions.isNotEmpty()) 8 else 0) + (if (freshQuestions.isNotEmpty()) 6 else 0)
    val target = if (total == 0) 10 else total

    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Çalışma Planım", color = PlannerDeep, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text("Bugün ne çalışacağını sen düşünme. Biz hazırladık. 💚", color = PlannerGreen, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = close) { Text("Kapat") }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PlannerDeep), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("🎯 BUGÜNÜN ROTASI", color = PlannerGold, fontWeight = FontWeight.Black)
                    Text("Yaklaşık $target soru", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("Yanlışlarını toparla → zayıf konunu güçlendir → yeni bilgi keşfet.", color = PlannerMint, fontSize = 13.sp)
                }
            }
        }
        if (reviewQuestions.isNotEmpty()) {
            item { PlanStep("1", "Yanlışları toparlayalım", "Daha önce takıldığın ${reviewQuestions.size} soru", PlannerGold) { start("Yanlışları Tekrar Et", reviewQuestions) } }
        }
        if (weakQuestions.isNotEmpty()) {
            item { PlanStep("2", "Zayıf noktaya yüklenelim", weakTopics.joinToString(" • "), PlannerGreen) { start("Zayıf Konu Tekrarı", weakQuestions) } }
        }
        if (freshQuestions.isNotEmpty()) {
            item { PlanStep("3", "Yeni sorularla pekiştirelim", "Henüz görmediğin ${freshQuestions.size} soru", PlannerMint) { start("Yeni Sorular", freshQuestions) } }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("💡 Küçük taktik", color = PlannerGreen, fontWeight = FontWeight.Black)
                    Text(
                        if (state.solved == 0) "İlk hedefin mükemmel olmak değil. Bugün 10 soruyu tamamlamak. Gerisi zaten gelecek."
                        else "Yanlış yaptığın soruyu geçip gitme. Bir kez daha bakmak, aynı tuzağa tekrar düşmeni engeller.",
                        color = PlannerDeep,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanStep(number: String, title: String, subtitle: String, accent: Color, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(accent.copy(alpha = .18f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Text(number, color = PlannerDeep, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = PlannerDeep, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.PlayArrow, null, tint = PlannerGreen)
        }
    }
}
