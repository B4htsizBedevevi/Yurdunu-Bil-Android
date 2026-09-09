package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ArenaMatchScreen(darkMode: Boolean, mode: SharedGameMode, matchId: String, onBack: () -> Unit) {
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF70847B)
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    val green = Color(0xFF18C986)
    var seconds by remember { mutableStateOf(3) }
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (seconds > 0) { delay(1000); seconds-- }
        started = true
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("‹ Arena", color = green, fontWeight = FontWeight.Bold) }
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚔️ MAÇ HAZIR", color = green, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text(mode.title, color = text, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("Maç: ${matchId.take(8)}…", color = muted, fontSize = 10.sp)
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    DuelPlayer("SEN", "0", green, text)
                    Text("VS", color = muted, fontWeight = FontWeight.Black)
                    DuelPlayer("RAKİP", "0", Color(0xFFFFC857), text)
                }
                Spacer(Modifier.height(22.dp))
                if (!started) {
                    Text("$seconds", color = green, fontSize = 46.sp, fontWeight = FontWeight.Black)
                    Text("Maç başlıyor…", color = muted, fontSize = 11.sp)
                } else {
                    Text("Rakip hazır. Gerçek zamanlı cevap senkronizasyonu sonraki adımda bağlanacak.", color = muted, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = green)) {
                        Text("Arena'ya Dön", color = Color(0xFF06221B), fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun DuelPlayer(name: String, score: String, accent: Color, text: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(58.dp).background(accent.copy(alpha = .14f), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { Text("👤", fontSize = 25.sp) }
        Spacer(Modifier.height(5.dp))
        Text(name, color = text, fontSize = 10.sp, fontWeight = FontWeight.Black)
        Text(score, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
}
