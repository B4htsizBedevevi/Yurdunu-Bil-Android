package tr.yurdunubil.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ArenaHubScreen(darkMode: Boolean, onLaunch: (SharedGameMode) -> Unit) {
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF70847B)
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    val green = Color(0xFF18C986)
    val gold = Color(0xFFFFC857)
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(visible, enter = fadeIn() + slideInVertically { it / 8 }) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Color(0xFF06221B), Color(0xFF0B4A38), Color(0xFF126B4D)))).padding(18.dp)) {
                    Column {
                        Text("⚔️ ARENA 2.0", color = Color(0xFFC9F8E1), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(5.dp))
                        Text("Türkiye'nin en hızlısı ol.", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                        Text("Süreyi yönet, seriyi bozma, XP'yi topla.", color = Color.White.copy(alpha = .72f), fontSize = 11.sp)
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ArenaStat("0", "GALİBİYET"); ArenaStat("0", "ARENA XP"); ArenaStat("∞", "LİG") }
                    }
                }
            }
            item { Text("Arena modları", color = text, fontSize = 17.sp, fontWeight = FontWeight.Black) }
            items(SharedGameModes.arenaModes.size) { index ->
                val mode = SharedGameModes.arenaModes[index]
                Card(Modifier.fillMaxWidth().clickable { onLaunch(mode) }, RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(green.copy(alpha = .12f)), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 23.sp) }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(mode.title, color = text, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text(mode.subtitle, color = muted, fontSize = 10.sp, maxLines = 2)
                            Spacer(Modifier.height(5.dp))
                            Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = gold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("BAŞLA →", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (darkMode) Color(0xFF17372D) else Color(0xFFE4F7ED))) { Column(Modifier.padding(14.dp)) { Text("🎯 Arena ipucu", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(3.dp)); Text("Hız önemli ama rastgele işaretlemek yerine bildiğin sorularda seri yakalamak daha kazançlı.", color = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) } } }
        }
    }
}

@Composable private fun ArenaStat(value: String, label: String) { Column(Modifier.clip(RoundedCornerShape(11.dp)).background(Color.White.copy(alpha = .09f)).padding(horizontal = 10.dp, vertical = 6.dp)) { Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black); Text(label, color = Color.White.copy(alpha = .55f), fontSize = 7.sp, fontWeight = FontWeight.Bold) } }
