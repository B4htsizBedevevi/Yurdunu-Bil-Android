package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Milestone(
    val current: Int,
    val target: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun AchievementCard(solved: Int, xp: Int, streak: Int, darkMode: Boolean) {
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF70847B)
    val green = Color(0xFF18C986)
    val gold = Color(0xFFFFC857)
    val track = if (darkMode) Color(0xFF21453A) else Color(0xFFE4F7ED)

    val milestones = listOf(
        Milestone(solved, 10, "İlk 10", "10 soru çöz", YBIcons.Target),
        Milestone(solved, 50, "50 Soru", "Temeli sağlamlaştır", YBIcons.Library),
        Milestone(xp, 500, "500 XP", "İlk seviyeyi tamamla", YBIcons.Star),
        Milestone(xp, 1000, "1000 XP", "İvmeni yükselt", YBIcons.Trophy),
        Milestone(streak, 3, "3 Gün Seri", "Çalışma ritmini koru", YBIcons.Flame),
        Milestone(streak, 7, "7 Gün Seri", "Bir haftayı tamamla", YBIcons.Flame)
    )

    Card(
        Modifier.fillMaxWidth(),
        RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("KÜÇÜK BAŞARILAR", color = gold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Text("Bir sonraki rozet için ne kaldı?", color = muted, fontSize = 9.sp)
                }
                Icon(YBIcons.Trophy, null, tint = gold, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))

            milestones.forEach { milestone ->
                val progress = (milestone.current.toFloat() / milestone.target.toFloat()).coerceIn(0f, 1f)
                val done = milestone.current >= milestone.target
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(
                            if (done) green.copy(alpha = .15f) else muted.copy(alpha = .10f)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            milestone.icon,
                            null,
                            tint = if (done) green else muted,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(milestone.title, color = text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "${milestone.current.coerceAtMost(milestone.target)}/${milestone.target}",
                                color = if (done) green else muted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(milestone.subtitle, color = muted, fontSize = 9.sp)
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(6.dp)),
                            color = if (done) green else gold,
                            trackColor = track
                        )
                    }
                }
            }
        }
    }
}
