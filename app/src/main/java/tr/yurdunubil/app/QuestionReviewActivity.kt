package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    val bg = Color(0xFF06140F)
    val card = Color(0xFF10251E)
    val text = Color(0xFFF3FBF7)
    val muted = Color(0xFF95AEA5)
    val green = Color(0xFF28DE98)
    val red = Color(0xFFFF8178)
    val question = questionId?.let { id -> SharedQuestionPool.all.firstOrNull { it.id == id } }

    MaterialTheme(colorScheme = darkColorScheme(primary = green, background = bg, surface = card)) {
        Scaffold(
            containerColor = bg,
            topBar = {
                TopAppBar(
                    title = { Text("Soru Tekrarı", color = text, fontWeight = FontWeight.Black) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
                )
            }
        ) { pad ->
            if (question == null) {
                Column(Modifier.fillMaxSize().padding(pad).padding(20.dp)) {
                    Icon(Icons.Default.Warning, null, tint = red, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Soru bulunamadı", color = text, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Bu soru artık aktif soru havuzunda olmayabilir.", color = muted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(pad),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("${question.topic} • Soru #${question.id}", color = green, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(7.dp))
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = card)) {
                            Text(question.text, color = text, fontSize = 18.sp, fontWeight = FontWeight.Black, lineHeight = 25.sp, modifier = Modifier.padding(18.dp))
                        }
                    }
                    itemsIndexed(question.options) { index, option ->
                        val correct = index == question.correctIndex
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (correct) Color(0xFF123D2E) else card,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(15.dp)) {
                                Icon(if (correct) Icons.Default.CheckCircle else Icons.Default.Lightbulb, null, tint = if (correct) green else muted, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(option, color = text, fontWeight = if (correct) FontWeight.Black else FontWeight.Medium, fontSize = 14.sp)
                                    if (correct) Text("DOĞRU CEVAP", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 3.dp))
                                }
                            }
                        }
                    }
                    item {
                        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF173128))) {
                            Column(Modifier.padding(17.dp)) {
                                Text("NEDEN?", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                Spacer(Modifier.height(5.dp))
                                Text(question.explanation, color = text, fontSize = 12.sp, lineHeight = 19.sp)
                            }
                        }
                    }
                    item {
                        Text("💡 Cevabı ezberlemek yerine nedenini hatırla; sonra soruyu kapatıp kendi kendine tekrar et.", color = muted, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(bottom = 20.dp))
                    }
                }
            }
        }
    }
}
