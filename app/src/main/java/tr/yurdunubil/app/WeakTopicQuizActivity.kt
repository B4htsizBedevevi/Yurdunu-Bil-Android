package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class WeakTopicQuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WeakTopicQuizScreen(onExit = { finish() }) }
    }
}

@Composable
private fun WeakTopicQuizScreen(onExit: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    val requestedTopic = remember { intentTopic(context) }
    val topic = remember(requestedTopic) {
        requestedTopic?.takeIf { SharedQuestionPool.all.any { q -> q.topic == it } }
            ?: SmartQuestionSelector.weakestTopic(prefs, SharedQuestionPool.all)
            ?: SharedQuestionPool.all.groupingBy { it.topic }.eachCount().minByOrNull { it.value }?.key
            ?: "Coğrafya"
    }
    val mode = remember(topic) {
        SharedGameMode("weak-$topic", "$topic Mini Test", "Zayıf konuyu kapat • 10 soru", "target", 10, 180, 120, setOf(topic))
    }
    val questions = remember(topic) { SmartQuestionSelector.pick(mode, prefs).take(10) }
    var index by rememberSaveable(topic) { mutableIntStateOf(0) }
    var selected by rememberSaveable(topic) { mutableIntStateOf(-1) }
    var correct by rememberSaveable(topic) { mutableIntStateOf(0) }
    var wrong by rememberSaveable(topic) { mutableIntStateOf(0) }
    var finished by rememberSaveable(topic) { mutableStateOf(false) }

    BackHandler { onExit() }
    val bg = Color(0xFF06140F); val card = Color(0xFF10251E); val green = Color(0xFF28DE98); val text = Color(0xFFF3FBF7); val muted = Color(0xFF95AEA5); val red = Color(0xFFFF7777)
    MaterialTheme(colorScheme = darkColorScheme(primary = green, background = bg, surface = card, onSurface = text)) {
        if (finished || questions.isEmpty()) {
            Column(Modifier.fillMaxSize().background(bg).padding(20.dp), verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.CheckCircle, null, tint = green, modifier = Modifier.size(58.dp))
                Spacer(Modifier.height(12.dp))
                Text("Mini test tamamlandı", color = text, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text(topic, color = green, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text("$correct doğru • $wrong yanlış • ${questions.size} soru", color = muted, fontSize = 14.sp)
                Spacer(Modifier.height(18.dp))
                Button(onClick = onExit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Çalışma merkezine dön", fontWeight = FontWeight.Black) }
            }
            return@MaterialTheme
        }
        val q = questions[index]
        val answered = selected >= 0
        LazyColumn(Modifier.fillMaxSize().background(bg), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onExit) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) }
                    Column(Modifier.weight(1f)) { Text("ZAYIF KONU ANTRENMANI", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black); Text(topic, color = text, fontSize = 19.sp, fontWeight = FontWeight.Black) }
                    Text("${index + 1}/${questions.size}", color = muted, fontWeight = FontWeight.Bold)
                }
            }
            item { LinearProgressIndicator(progress = { (index + 1f) / questions.size }, modifier = Modifier.fillMaxWidth().height(7.dp), color = green, trackColor = card) }
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = card)) {
                    Column(Modifier.padding(18.dp)) {
                        Text(q.text, color = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp)
                        Spacer(Modifier.height(14.dp))
                        q.options.forEachIndexed { i, option ->
                            val isCorrect = i == q.correctIndex
                            val isSelected = i == selected
                            val optionColor = when { !answered -> text; isCorrect -> green; isSelected -> red; else -> muted }
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(enabled = !answered) { selected = i; if (i == q.correctIndex) correct++ else wrong++; SmartQuestionSelector.record(prefs, q.topic, i == q.correctIndex) }, shape = RoundedCornerShape(13.dp), colors = CardDefaults.cardColors(containerColor = if (isSelected || (answered && isCorrect)) green.copy(alpha = .12f) else bg)) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("${('A'.code + i).toChar()}", color = optionColor, fontWeight = FontWeight.Black)
                                    Spacer(Modifier.width(10.dp)); Text(option, color = optionColor, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        if (answered) {
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(if (selected == q.correctIndex) Icons.Default.CheckCircle else Icons.Default.Warning, null, tint = if (selected == q.correctIndex) green else red); Spacer(Modifier.width(7.dp)); Text(if (selected == q.correctIndex) "Doğru!" else "Yanlış • Doğru cevap: ${q.options[q.correctIndex]}", color = if (selected == q.correctIndex) green else red, fontWeight = FontWeight.Black) }
                            Spacer(Modifier.height(7.dp)); Text(q.explanation, color = muted, fontSize = 12.sp, lineHeight = 18.sp)
                            Spacer(Modifier.height(12.dp)); Button(onClick = { if (index + 1 >= questions.size) { ProgressTracker.recordQuiz(prefs, mode, questions.size, correct, wrong); finished = true } else { index++; selected = -1 } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp)) { Text(if (index + 1 >= questions.size) "Sonucu Gör" else "Sonraki Soru", fontWeight = FontWeight.Black) }
                        }
                    }
                }
            }
        }
    }
}

private fun intentTopic(context: android.content.Context): String? = (context as? android.app.Activity)?.intent?.getStringExtra("topic")
