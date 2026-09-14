package tr.yurdunubil.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LearningExplanationCard(
    question: Question,
    selectedIndex: Int,
    darkMode: Boolean = false
) {
    val correct = selectedIndex == question.correctIndex
    val text = if (darkMode) Color(0xFFF3FBF7) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF61766D)
    val surface = if (darkMode) Color(0xFF102820) else Color.White
    val soft = if (correct) Color(0xFF18C986).copy(alpha = if (darkMode) .14f else .08f) else Color(0xFFE65353).copy(alpha = if (darkMode) .14f else .07f)
    val accent = if (correct) Color(0xFF18C986) else Color(0xFFE65353)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (correct) Icons.Default.CheckCircle else Icons.Default.Psychology, null, tint = accent, modifier = Modifier.size(21.dp))
                Spacer(Modifier.width(7.dp))
                Text(if (correct) "DOĞRU • ÖĞREN" else "YANLIŞ • ÖĞREN", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = .7.sp)
            }

            Surface(color = soft, shape = RoundedCornerShape(13.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("DOĞRU CEVAP", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(4.dp))
                    Text(question.options[question.correctIndex], color = text, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    if (!correct) {
                        Spacer(Modifier.height(7.dp))
                        Text("SENİN CEVABIN", color = Color(0xFFE65353), fontSize = 9.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(3.dp))
                        Text(question.options.getOrElse(selectedIndex) { "Seçim yok" }, color = text, fontSize = 12.sp)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFFB52E), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("NEDEN?", color = Color(0xFF9B6A00), fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
            Text(question.explanation, color = text, fontSize = 13.sp, lineHeight = 19.sp)

            Surface(color = if (darkMode) Color.White.copy(alpha = .045f) else Color(0xFFF3F7F5), shape = RoundedCornerShape(13.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RemoveCircleOutline, null, tint = muted, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("ŞIK ELEME İPUCU", color = muted, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Önce sorunun istediği kavramı belirle. Doğru cevabı bu ana ilişkiye bağla; diğer seçenekleri bu ilişkiyi sağlamadığı için ele.",
                        color = muted, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }

            Surface(color = if (darkMode) Color(0xFF17362B) else Color(0xFFFFF7E3), shape = RoundedCornerShape(13.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Text("KPSS PÜF", color = Color(0xFF9B6A00), fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Bu soruyu sadece cevabıyla değil, “neden?” bağlantısıyla hatırla: "+question.topic+".",
                        color = text, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}
