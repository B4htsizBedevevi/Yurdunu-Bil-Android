package tr.yurdunubil.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
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
import kotlinx.coroutines.delay

private val ArenaBg = Color(0xFF06100D)
private val ArenaSurface = Color(0xFF10221C)
private val ArenaSurface2 = Color(0xFF163229)
private val ArenaGreen = Color(0xFF18C986)
private val ArenaGold = Color(0xFFFFC857)
private val ArenaText = Color(0xFFF1F7F4)
private val ArenaMuted = Color(0xFF92ADA2)

@Composable
fun ArenaMatchScreen(darkMode: Boolean, mode: SharedGameMode, matchId: String, onBack: () -> Unit) {
    BackHandler { onBack() }
    val text = if (darkMode) ArenaText else Color(0xFF06221B)
    val muted = if (darkMode) ArenaMuted else Color(0xFF70847B)
    val bg = if (darkMode) ArenaBg else Color(0xFFF2F6F4)
    var seconds by remember { mutableStateOf(3) }
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (seconds > 0) {
            delay(1000)
            seconds--
        }
        started = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                if (darkMode) Brush.verticalGradient(listOf(Color(0xFF08231B), ArenaBg, Color(0xFF04100D)))
                else Brush.verticalGradient(listOf(Color(0xFFE9F8F1), Color.White, Color(0xFFF2F6F4)))
            )
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Arena'ya dön", tint = text)
                }
                Column(Modifier.weight(1f)) {
                    Text("ARENA", color = ArenaGold, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                    Text("Maç odası", color = text, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (darkMode) Color.White.copy(alpha = .08f) else Color.Black.copy(alpha = .04f)
                ) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = ArenaGreen, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Güvenli", color = muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = if (darkMode) ArenaSurface.copy(alpha = .96f) else Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (darkMode) Color.White.copy(alpha = .06f) else Color.Black.copy(alpha = .05f))
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(ArenaGold.copy(alpha = .12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsEsports, null, tint = ArenaGold, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text("KARŞILAŞMA", color = ArenaGreen, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.1.sp)
                            Text(mode.title, color = text, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text("Oda ${matchId.take(8)}…", color = muted, fontSize = 9.sp)
                        }
                        Surface(color = ArenaGreen.copy(alpha = .10f), shape = RoundedCornerShape(12.dp)) {
                            Text("${mode.questions} SORU", color = ArenaGreen, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp))
                        }
                    }

                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        DuelPlayer("SEN", "0", ArenaGreen, text, true, Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(42.dp).clip(CircleShape).background(if (darkMode) ArenaBg else Color(0xFFE8F0EC)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Bolt, null, tint = ArenaGold, modifier = Modifier.size(22.dp))
                            }
                            Text("VS", color = muted, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 5.dp))
                        }
                        DuelPlayer("RAKİP", "0", ArenaGold, text, false, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(20.dp))
                    if (!started) {
                        Surface(
                            color = ArenaGreen.copy(alpha = .07f),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$seconds", color = ArenaGreen, fontSize = 42.sp, fontWeight = FontWeight.Black)
                                Text("Karşılaşma hazırlanıyor", color = muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        AnimatedVisibility(true, enter = fadeIn() + scaleIn()) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("RAKİP HAZIR", color = ArenaGreen, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("Gerçek zamanlı soru eşleştirmesi sırada.", color = muted, fontSize = 11.sp)
                                Spacer(Modifier.height(14.dp))
                                Button(
                                    onClick = onBack,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(17.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ArenaGreen, contentColor = Color(0xFF06221B))
                                ) {
                                    Text("Arena'ya Dön", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Eşleşme hazırlığı", color = text, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MatchInfo(Icons.Default.EmojiEvents, "Ödül", "+${mode.rewardXp} XP", ArenaGold, darkMode, Modifier.weight(1f))
                MatchInfo(Icons.Default.Bolt, "Tempo", "Hızlı tur", ArenaGreen, darkMode, Modifier.weight(1f))
                MatchInfo(Icons.Default.Shield, "Mod", "Düello", Color(0xFF6FAEFF), darkMode, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DuelPlayer(name: String, score: String, accent: Color, text: Color, self: Boolean, modifier: Modifier) {
    Card(
        modifier,
        RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = .08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = .20f))
    ) {
        Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(52.dp).clip(CircleShape).background(accent.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
                Icon(if (self) Icons.Default.Person else Icons.Default.Person, null, tint = accent, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(name, color = text, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(score, color = accent, fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun MatchInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, accent: Color, darkMode: Boolean, modifier: Modifier) {
    val surface = if (darkMode) ArenaSurface2 else Color.White
    val text = if (darkMode) ArenaText else Color(0xFF06221B)
    Card(modifier, RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
        Column(Modifier.fillMaxWidth().padding(11.dp)) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(7.dp))
            Text(title, color = text, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(value, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}
