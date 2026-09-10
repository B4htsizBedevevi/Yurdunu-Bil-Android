package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePulseCard(prefs: SharedPreferences, darkMode: Boolean, onQuiz: () -> Unit, onArena: () -> Unit, onLibrary: () -> Unit) {
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF70847B)
    val solved = prefs.getInt("solved", 0)
    val streak = prefs.getInt("streak", 0)
    val today = java.time.LocalDate.now().toString()
    val dailyDone = prefs.getString("daily_completed_date", "") == today

    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(Modifier.padding(15.dp)) {
            Text("BUGÜNÜN ROTASI", color = Color(0xFF18C986), fontSize = 9.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Text("Sıkılmadan ilerle: 3 küçük hamle.", color = text, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(10.dp))
            HomePulseRow("⚡", "10 soru", if (solved > 0) "Toplam $solved soru çözdün." else "Hızlı 10 ile ritmi başlat.", onQuiz, text, muted, Color(0xFF18C986))
            HomePulseRow("📚", "1 konu kartı", "Bir konuyu hızlıca tekrar et.", onLibrary, text, muted, Color(0xFF5AA8FF))
            HomePulseRow("⚔️", "Arena", if (streak > 0) "$streak günlük serin var. Şimdi düelloya gir." else "Rakip bul ve ilk Arena maçını dene.", onArena, text, muted, Color(0xFFFFC857))
            if (dailyDone) {
                Spacer(Modifier.height(4.dp))
                Text("✓ Bugünün görevi tamamlandı. Yarın yeni rota açılacak.", color = Color(0xFF18C986), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomePulseRow(icon: String, title: String, subtitle: String, onClick: () -> Unit, text: Color, muted: Color, accent: Color) {
    Row(Modifier.fillMaxWidth().background(accent.copy(alpha = .08f), RoundedCornerShape(13.dp))) {
        Column(Modifier.padding(horizontal = 11.dp, vertical = 9.dp).weight(1f)) {
            Row { Text(icon, fontSize = 17.sp); Spacer(Modifier.width(8.dp)); Text(title, color = text, fontSize = 12.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = muted, fontSize = 9.sp, maxLines = 1)
        }
        Text("Aç →", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(end = 11.dp, top = 16.dp))
    }
    Spacer(Modifier.height(6.dp))
}
