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
fun ArenaLocalMatchScreen(darkMode: Boolean, mode: SharedGameMode, onBack: () -> Unit) {
    val questions = remember(mode.id) { SharedQuestionPool.pick(mode, mode.questions) }
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF70847B)
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    val green = Color(0xFF18C986)
    var index by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var opponentScore by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(mode.seconds) }
    var locked by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    LaunchedEffect(finished) {
        if (finished) return@LaunchedEffect
        while (seconds > 0 && !finished) { delay(1000); seconds-- }
        if (seconds == 0) finished = true
    }
    if (finished || index >= questions.size) {
        Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(if (score > opponentScore) "🏆 ZAFER!" else if (score == opponentScore) "🤝 BERABERE" else "💪 RÖVANŞA HAZIR", color = green, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(10.dp))
            Text("$score  —  $opponentScore", color = text, fontSize = 34.sp, fontWeight = FontWeight.Black)
            Text("Sen  •  Rakip", color = muted, fontSize = 11.sp)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = green), shape = RoundedCornerShape(16.dp)) { Text("Arena'ya Dön", color = Color(0xFF06221B), fontWeight = FontWeight.Black) }
        }
        return
    }
    val q = questions[index]
    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("⚔️ ${index + 1}/${questions.size}", color = green, fontWeight = FontWeight.Black)
            Text("⏱️ ${seconds}s", color = if (seconds <= 10) Color(0xFFE65353) else text, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("SEN $score", color = green, fontWeight = FontWeight.Black)
            Text("RAKİP $opponentScore", color = muted, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(14.dp))
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
            Column(Modifier.padding(17.dp)) {
                Text(q.text, color = text, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
                Spacer(Modifier.height(14.dp))
                q.options.forEachIndexed { i, option ->
                    OutlinedButton(
                        onClick = {
                            if (locked) return@OutlinedButton
                            locked = true
                            if (i == q.correctIndex) score++ else opponentScore++
                            index++
                            locked = false
                        },
                        enabled = !locked,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        shape = RoundedCornerShape(13.dp)
                    ) { Text("${('A'.code + i).toChar()}  $option", color = text, modifier = Modifier.fillMaxWidth()) }
                }
            }
        }
    }
}
