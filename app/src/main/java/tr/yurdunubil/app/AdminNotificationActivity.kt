package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AdminNotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminNotificationScreen { finish() } }
    }
}

@Composable
private fun AdminNotificationScreen(onBack: () -> Unit) {
    val client = remember { SupabaseClientProvider.client }
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    fun sendAnnouncement() {
        if (title.isBlank() || body.isBlank() || sending) return
        sending = true
        status = null
        scope.launch {
            runCatching {
                client.functions.invoke(
                    function = "admin-broadcast-notification",
                    body = buildJsonObject {
                        put("title", JsonPrimitive(title.trim()))
                        put("body", JsonPrimitive(body.trim()))
                        put("type", JsonPrimitive("announcement"))
                    }
                )
            }.onSuccess {
                status = "✅ Duyuru tüm kullanıcıların uygulama içi bildirim merkezine gönderildi. Firebase yapılandırılmışsa telefon bildirimi de gönderilir."
                title = ""
                body = ""
            }.onFailure {
                status = "❌ Gönderilemedi: " + (it.message ?: "Sunucu hatası")
            }
            sending = false
        }
    }

    MaterialTheme {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }
                    Column(Modifier.weight(1f)) {
                        Text("Bildirim Gönder", fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Text("Yeni duyuru oluştur", color = Color(0xFF18A878), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.Campaign, "Bildirim")
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7F1))) {
                    Column(Modifier.padding(16.dp)) {
                        Text("DUYURU MERKEZİ", color = Color(0xFF18A878), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(5.dp))
                        Text("Önce uygulama içinde yayınla; telefon bildirimi ayrıca çalışır.", color = Color(0xFF06221B), fontSize = 15.sp, fontWeight = FontWeight.Black)
                        Text("Gönderme işlemi admin yetkisiyle güvenli Edge Function üzerinden yapılır.", color = Color(0xFF71847D), fontSize = 11.sp)
                    }
                }
            }
            item { OutlinedTextField(value = title, onValueChange = { if (it.length <= 80) title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Başlık") }, singleLine = true, shape = RoundedCornerShape(14.dp)) }
            item { OutlinedTextField(value = body, onValueChange = { if (it.length <= 220) body = it }, modifier = Modifier.fillMaxWidth().height(150.dp), label = { Text("Mesaj") }, shape = RoundedCornerShape(14.dp)) }
            item { Text("Hedef: Tüm kullanıcılar • Tür: Duyuru • Hazır", color = Color.Gray, fontSize = 11.sp) }
            item {
                Button(enabled = !sending && title.isNotBlank() && body.isNotBlank(), onClick = ::sendAnnouncement, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18C98A), contentColor = Color(0xFF052118))) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, null)
                        Spacer(Modifier.width(7.dp))
                        Text(if (sending) "Gönderiliyor…" else "Tüm Kullanıcılara Gönder", fontWeight = FontWeight.Black)
                    }
                }
            }
            item { status?.let { Text(it, color = Color(0xFF5F716A), fontSize = 11.sp, lineHeight = 17.sp) } }
        }
    }
}
