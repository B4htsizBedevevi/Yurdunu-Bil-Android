package tr.yurdunubil.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
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
    val text = if (darkMode) YBColors.DarkText else YBColors.LightText
    val muted = if (darkMode) YBColors.DarkMuted else YBColors.LightMuted
    val surface = if (darkMode) YBColors.DarkSurface else YBColors.LightSurface
    val soft = if (darkMode) YBColors.DarkSoftGreen else YBColors.LightSoftGreen
    val green = YBColors.Green
    val gold = YBColors.Gold
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(visible, enter = fadeIn() + slideInVertically { it / 8 }) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = YBSpacing.lg, vertical = YBSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(YBSpacing.md)
        ) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(YBRadius.hero))
                        .background(Brush.linearGradient(listOf(YBColors.Deep, YBColors.Deep2, Color(0xFF126B4D))))
                        .padding(YBSpacing.xl)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(34.dp).clip(RoundedCornerShape(YBRadius.small))
                                    .background(green.copy(alpha = .15f)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.SportsEsports, null, tint = green) }
                            Spacer(Modifier.width(YBSpacing.sm))
                            Column {
                                Text("ÇEVRİMİÇİ ARENA", color = YBColors.Mint, fontSize = YBTypography.label, fontWeight = FontWeight.Black)
                                Text("Canlı mücadele", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(YBSpacing.md))
                        Text("Bilgini rakibine karşı göster.", color = Color.White, fontSize = YBTypography.hero, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(4.dp))
                        Text("Rakibini bul, soruları cevapla ve Arena'da yüksel.", color = Color.White.copy(alpha = .72f), fontSize = YBTypography.caption)
                        Spacer(Modifier.height(YBSpacing.lg))
                        Row(horizontalArrangement = Arrangement.spacedBy(YBSpacing.sm)) {
                            ArenaStat("1v1", "MÜCADELE", Icons.Default.SportsEsports)
                            ArenaStat("⚡", "HIZ", Icons.Default.Bolt)
                            ArenaStat("XP", "ÖDÜL", Icons.Default.EmojiEvents)
                        }
                    }
                }
            }

            item {
                Column {
                    Text("Bir mod seç", color = text, fontSize = YBTypography.section, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(2.dp))
                    Text("Her mod farklı bir oyun temposu sunar.", color = muted, fontSize = YBTypography.caption)
                }
            }

            items(SharedGameModes.arenaModes.size) { index ->
                val mode = SharedGameModes.arenaModes[index]
                Card(
                    Modifier.fillMaxWidth().clickable { onLaunch(mode) },
                    RoundedCornerShape(YBRadius.card),
                    colors = CardDefaults.cardColors(containerColor = surface)
                ) {
                    Row(Modifier.padding(YBSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(52.dp).clip(RoundedCornerShape(YBRadius.medium))
                                .background(green.copy(alpha = .11f)),
                            contentAlignment = Alignment.Center
                        ) { Text(mode.icon, fontSize = 24.sp) }
                        Spacer(Modifier.width(YBSpacing.md))
                        Column(Modifier.weight(1f)) {
                            Text(mode.title, color = text, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(2.dp))
                            Text(mode.subtitle, color = muted, fontSize = YBTypography.caption, maxLines = 2)
                            Spacer(Modifier.height(7.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ArenaMeta("${mode.questions} soru", gold)
                                ArenaMeta("${mode.seconds} sn", muted)
                                ArenaMeta("+${mode.rewardXp} XP", green)
                            }
                        }
                        Spacer(Modifier.width(YBSpacing.sm))
                        Text("OYNA", color = green, fontSize = YBTypography.label, fontWeight = FontWeight.Black)
                    }
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth(),
                    RoundedCornerShape(YBRadius.card),
                    colors = CardDefaults.cardColors(containerColor = soft)
                ) {
                    Row(Modifier.padding(YBSpacing.lg), verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 22.sp)
                        Spacer(Modifier.width(YBSpacing.md))
                        Column {
                            Text("Arena ipucu", color = green, fontSize = YBTypography.label, fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(3.dp))
                            Text("Hız önemli; ama doğru cevap serisi daha değerlidir. Bildiğin sorularda seri yakalamaya odaklan.", color = text, fontSize = YBTypography.caption, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArenaStat(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        Modifier.clip(RoundedCornerShape(YBRadius.small)).background(Color.White.copy(alpha = .09f)).padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.White.copy(alpha = .78f), modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(5.dp))
        Column {
            Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Text(label, color = Color.White.copy(alpha = .50f), fontSize = 6.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ArenaMeta(value: String, color: Color) {
    Text(value, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
}
