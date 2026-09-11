package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun HomeModernV5(bg: Color, card: Color, text: Color, muted: Color, green: Color, gold: Color, prefs: SharedPreferences, quick: () -> Unit, arena: () -> Unit, onSocial: () -> Unit, onLibrary: () -> Unit, onSettings: () -> Unit, onMap: () -> Unit) {
    val solved = prefs.getInt("solved", 0); val correct = prefs.getInt("correct", 0); val xp = prefs.getInt("xp", 0); val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Column(Modifier.fillMaxWidth().background(Color(0xFF06231A), RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)).padding(horizontal = 18.dp, vertical = 14.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Image(painterResource(R.drawable.yurdunu_bil_app_icon), "Yurdunu Bil", Modifier.size(45.dp).clip(RoundedCornerShape(13.dp))); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Keşfet • Öğren • Yarış • Geliş", color = Color(0xFF9EEFD0), fontSize = 10.sp) }; IconButton(onClick = onSocial) { Icon(Icons.Default.Notifications, "Bildirimler", tint = Color.White) } }; Spacer(Modifier.height(13.dp)); Text("Bugün Türkiye'yi biraz daha tanı.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(10.dp)); Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { HomeStat("$solved", "SORU"); HomeStat("$xp", "XP"); HomeStat("%$accuracy", "DOĞRULUK") } } }
        item { SectionCardV5(card) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Quiz, null, tint = green, modifier = Modifier.size(29.dp)); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { SmallLabelV5("GÜNÜN SORUSU", green); Text("Hızlı 10", color = text, fontSize = 18.sp, fontWeight = FontWeight.Black); Text("Kısa bir coğrafya turu yap ve ritmini koru.", color = muted, fontSize = 10.sp) } }; Spacer(Modifier.height(10.dp)); CompactActionV5("Başla • 10 Soru", green, quick) } }
        item { Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) { QuickTileV5("Kütüphane", Icons.Default.MenuBook, Color(0xFF29C7E6), text, card, onLibrary, Modifier.weight(1f)); QuickTileV5("Soru Çöz", Icons.Default.Quiz, Color(0xFFE86BD0), text, card, quick, Modifier.weight(1f)); QuickTileV5("Oyunlar", Icons.Default.Gamepad, Color(0xFF7E82F2), text, card, arena, Modifier.weight(1f)) } }
        item { Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) { QuickTileV5("Arena", Icons.Default.SportsEsports, gold, text, card, arena, Modifier.weight(1f)); QuickTileV5("Haritalar", Icons.Default.Map, Color(0xFF39D4A2), text, card, onMap, Modifier.weight(1f)); QuickTileV5("İstatistik", Icons.Default.BarChart, Color(0xFF6B9AF7), text, card, { }, Modifier.weight(1f)) } }
        item { Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = card)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Explore, null, tint = gold, modifier = Modifier.size(27.dp)); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { SmallLabelV5("GÜNÜN ÖNERİSİ", gold); Text("Her gün 10 soru çöz, farkı gör!", color = text, fontSize = 14.sp, fontWeight = FontWeight.Black) } } } }
    }
}
@Composable private fun SectionCardV5(card: Color, content: @Composable ColumnScope.() -> Unit) = Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = card)) { Column(Modifier.padding(14.dp), content = content) }
@Composable private fun CompactActionV5(label: String, color: Color, onClick: () -> Unit) = Box(Modifier.fillMaxWidth().height(43.dp).clip(RoundedCornerShape(12.dp)).background(color).clickable(onClick = onClick), contentAlignment = Alignment.Center) { Text(label, color = Color(0xFF052118), fontSize = 12.sp, fontWeight = FontWeight.Black) }
@Composable private fun HomeStat(value: String, label: String) = Column(Modifier.clip(RoundedCornerShape(11.dp)).background(Color.White.copy(alpha = .09f)).padding(horizontal = 10.dp, vertical = 6.dp)) { Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black); Text(label, color = Color.White.copy(alpha = .55f), fontSize = 7.sp) }
@Composable private fun SmallLabelV5(label: String, color: Color) = Text(label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = .8.sp)
@Composable private fun QuickTileV5(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color, text: Color, card: Color, onClick: () -> Unit, modifier: Modifier) = Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = card)) { Column(Modifier.fillMaxWidth().padding(vertical = 13.dp, horizontal = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, tint = accent, modifier = Modifier.size(27.dp)); Spacer(Modifier.height(6.dp)); Text(title, color = text, fontSize = 10.sp, fontWeight = FontWeight.Black) } }
