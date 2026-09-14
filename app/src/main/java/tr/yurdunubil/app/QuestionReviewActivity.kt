package tr.yurdunubil.app


import android.os.Bundle
import tr.yurdunubil.app.YurdunuBilCopy
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class QuestionReviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val questionId = intent.getStringExtra(EXTRA_QUESTION_ID)?.toIntOrNull()
        setContent { QuestionReviewScreen(questionId = questionId, onBack = { finish() }) }
    }
    companion object { const val EXTRA_QUESTION_ID = "question_id" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionReviewScreen(questionId: Int?, onBack: () -> Unit) {
    BackHandler { onBack() }
    val bg = Color(0xFF06140F); val card = Color(0xFF10251E)
    val text = Color(0xFFF3FBF7); val muted = Color(0xFF95AEA5)
    val green = Color(0xFF28DE98); val red = Color(0xFFFF8178)
    val question = questionId?.let { id -> SharedQuestionPool.all.firstOrNull { it.id == id } }
    var selectedIndex by remember(questionId) { mutableStateOf<Int?>(null) }
    var completed by remember(questionId) { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(primary = green, background = bg, surface = card)) {
        Scaffold(containerColor = bg, topBar = {
            TopAppBar(
                title = { Text(if (completed) "Tekrar Sonucu" else "Hadi Bir Daha Deneyelim", color = text, fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
            )
        }) { pad ->
            if (question == null) {
                Column(Modifier.fillMaxSize().padding(pad).padding(20.dp)) {
                    Icon(Icons.Default.Warning, null, tint = red, modifier = Modifier.size(42.dp)); Spacer(Modifier.height(12.dp))
                    Text("Soru bulunamadı", color = text, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Bu soru artık aktif soru havuzunda olmayabilir.", color = muted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Text("${question.topic} • Soru #${question.id}", color = green, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(7.dp))
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = card)) {
                            Text(question.text, color = text, fontSize = 18.sp, fontWeight = FontWeight.Black, lineHeight = 25.sp, modifier = Modifier.padding(18.dp))
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(if (selectedIndex == null) "Cevabı görmeden yeniden çöz. Hazırsan bir seçeneğe dokun." else "Cevabın kaydedildi. Sonucu ve nedenini incele.", color = muted, fontSize = 11.sp)
                    }
                    itemsIndexed(question.options) { index, option ->
                        val answered = selectedIndex != null; val isCorrect = index == question.correctIndex; val isSelected = index == selectedIndex
                        val surfaceColor = when { !answered -> card; isCorrect -> Color(0xFF123D2E); isSelected -> Color(0xFF432421); else -> card }
                        Surface(shape = RoundedCornerShape(16.dp), color = surfaceColor, modifier = Modifier.fillMaxWidth().clickable(enabled = !answered) { selectedIndex = index; completed = true }) {
                            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(when { answered && isCorrect -> Icons.Default.CheckCircle; answered && isSelected -> Icons.Default.Warning; else -> Icons.Default.Lightbulb }, null, tint = when { answered && isCorrect -> green; answered && isSelected -> red; else -> muted }, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) {
                                    Text(option, color = text, fontWeight = if (answered && isCorrect) FontWeight.Black else FontWeight.Medium, fontSize = 14.sp)
                                    when { answered && isCorrect -> Text("DOĞRU CEVAP", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 3.dp)); answered && isSelected -> Text("SENİN CEVABIN", color = red, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 3.dp)) }
                                }
                            }
                        }
                    }
                    if (completed) {
                        item {
                            val success = selectedIndex == question.correctIndex
                            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (success) Color(0xFF123D2E) else Color(0xFF432421))) {
                                Column(Modifier.padding(17.dp)) {
                                    Text(if (success) "🎯 Doğru! Bu kez yakaladın." else "🔁 Tekrar et. Bu soru henüz oturmadı.", color = if (success) green else red, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    Spacer(Modifier.height(7.dp)); Text("NEDEN?", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(5.dp))
                                    Text(question.explanation, color = text, fontSize = 12.sp, lineHeight = 19.sp)
                                }
                            }
                        }
                        item { OutlinedButton(onClick = { selectedIndex = null; completed = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(7.dp)); Text("Bir daha deneyelim") } }
                    } else {
                        item { Text("💡 İpucu: Cevabı aramadan önce sorunun kilit kelimelerini düşün.", color = muted, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 3.dp, bottom = 20.dp)) }
                    }
                }
            }
        }
    }
}
