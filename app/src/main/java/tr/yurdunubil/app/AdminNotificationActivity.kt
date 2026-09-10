package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminNotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminNotificationScreen { finish() } }
    }
}

@Composable
private fun AdminNotificationScreen(onBack: () -> Unit) {
    val client = remember { SupabaseClientProvider.client }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    MaterialTheme {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }
                    Column(Modifier.weight(1f)) {
                        Text("Bildirim Gönder", fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Text("Yeni kampanya oluştur", color = Color(0xFF18A878), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.Campaign, "Bildirim")
                }
            }
            item { OutlinedTextField(value = title, onValueChange = { if (it.length <= 80) title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Başlık") }, singleLine = true, shape = RoundedCornerShape(14.dp)) }
            item { OutlinedTextField(value = body, onValueChange = { if (it.length <= 220) body = it }, modifier = Modifier.fillMaxWidth().height(150.dp), label = { Text("Mesaj") }, shape = RoundedCornerShape(14.dp)) }
            item { Text("Hedef: Tüm kullanıcılar • Durum: Taslak", color = Color.Gray, fontSize = 11.sp) }
            item {
                Button(
                    enabled = !sending && title.isNotBlank() && body.isNotBlank(),
                    onClick = {
                        sending = true
                        status = null
                        CoroutineScope(Dispatchers.Main).launch {
                            runCatching {
                                val user = client.auth.currentUserOrNull() ?: error("Oturum bulunamadı")
                                client.postgrest.from("notification_campaigns").insert(mapOf("title" to title.trim(), "body" to body.trim(), "type" to "announcement", "audience" to "all", "status" to "draft", "created_by" to user.id))
                            }.onSuccess {
                                status = "Kampanya oluşturuldu. FCM gönderimi için sunucu yapılandırması gerekir."
                                title = ""; body = ""
                            }.onFailure { status = "Hata: ${it.message ?: "Kampanya oluşturulamadı."}" }
                            sending = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18C98A), contentColor = Color(0xFF052118))
                ) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Campaign, null); Spacer(Modifier.width(7.dp)); Text(if (sending) "Oluşturuluyor…" else "Kampanyayı Oluştur", fontWeight = FontWeight.Black) } }
            }
            item { if (status != null) Text(status!!, color = Color.Gray, fontSize = 11.sp) }
        }
    }
}
