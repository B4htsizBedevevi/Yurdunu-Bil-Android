package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminModernActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { AdminModernScreen { finish() } } }
}

@Serializable private data class AdminModernProfile(val role: String? = null)
@Serializable private data class AdminModernCampaign(val id: Long, val title: String, val body: String, val status: String? = null)

private val AdminBg = Color(0xFF06140F)
private val AdminCard = Color(0xFF0E211B)
private val AdminCard2 = Color(0xFF123229)
private val AdminText = Color(0xFFF3FBF7)
private val AdminMuted = Color(0xFF91AAA1)
private val AdminGreen = Color(0xFF28DE98)
private val AdminGold = Color(0xFFFFC857)

@Composable
private fun AdminModernScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val client = remember { SupabaseClientProvider.client }
    var allowed by remember { mutableStateOf<Boolean?>(null) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }
    var campaigns by remember { mutableStateOf(emptyList<AdminModernCampaign>()) }

    LaunchedEffect(Unit) {
        runCatching {
            val user = client.auth.currentUserOrNull() ?: error("Oturum bulunamadı")
            val profile = client.postgrest.from("profiles").select { filter { eq("id", user.id) }; limit(1) }.decodeSingle<AdminModernProfile>()
            allowed = profile.role == "admin"
            if (allowed == true) campaigns = client.postgrest.from("notification_campaigns").select { order("created_at", Order.DESCENDING); limit(5) }.decodeList()
        }.onFailure { allowed = false; status = it.message }
    }

    if (allowed != true) {
        Column(Modifier.fillMaxSize().background(AdminBg).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AdminPanelSettings, null, tint = AdminGold, modifier = Modifier.size(48.dp)); Spacer(Modifier.height(12.dp)); Text(if (allowed == false) "Admin erişimi yok" else "Admin kontrol ediliyor…", color = AdminText, fontSize = 22.sp, fontWeight = FontWeight.Black); Text(status ?: "", color = AdminMuted, fontSize = 10.sp); Spacer(Modifier.height(16.dp)); OutlinedButton(onClick = onBack) { Text("Geri") }
        }
        return
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = AdminGreen, background = AdminBg, surface = AdminCard, onSurface = AdminText)) {
        Column(Modifier.fillMaxSize().background(AdminBg)) {
            Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = AdminText) }
                Column(Modifier.weight(1f)) { Text("Admin Panel", color = AdminText, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Sadece yetkili kullanıcılar", color = AdminGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                Icon(Icons.Default.AdminPanelSettings, null, tint = AdminGreen)
            }
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Surface(shape = RoundedCornerShape(18.dp), color = AdminCard) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null, tint = AdminGreen, modifier = Modifier.size(22.dp)); Spacer(Modifier.size(8.dp)); Text("Bildirim Gönder", color = AdminText, fontSize = 17.sp, fontWeight = FontWeight.Black) }
                            Text("Duyurunu önce cihazında test et, sonra aktif Android cihazlarına gönder.", color = AdminMuted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                            OutlinedTextField(value = title, onValueChange = { if (it.length <= 80) title = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Başlık") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = body, onValueChange = { if (it.length <= 220) body = it }, modifier = Modifier.fillMaxWidth().height(105.dp), label = { Text("Mesaj") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(enabled = title.isNotBlank() && body.isNotBlank(), onClick = { if (NotificationHelper.canNotify(context)) { NotificationHelper.sendNow(context, title.trim(), body.trim(), openSocial = true); status = "Test bildirimi bu cihazda gösterildi." } else status = "Bildirim izni kapalı." }, modifier = Modifier.weight(1f).height(42.dp), shape = RoundedCornerShape(11.dp)) { Icon(Icons.Default.Notifications, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.size(4.dp)); Text("Test", fontSize = 10.sp, fontWeight = FontWeight.Black) }
                                Button(enabled = !sending && title.isNotBlank() && body.isNotBlank(), onClick = { sendBroadcast(client, title, body, { sending = it }, { status = it; if (it.startsWith("Gönderim başarılı")) { title = ""; body = "" } }) }, modifier = Modifier.weight(1.2f).height(42.dp), shape = RoundedCornerShape(11.dp), colors = ButtonDefaults.buttonColors(containerColor = AdminGreen, contentColor = Color(0xFF052118))) { Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.size(4.dp)); Text(if (sending) "Gönderiliyor" else "Gönder", fontSize = 10.sp, fontWeight = FontWeight.Black) }
                            }
                            if (status != null) Text(status!!, color = if (status!!.contains("başarısız")) Color(0xFFFF9B91) else AdminMuted, fontSize = 9.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
                item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { AdminMini("Aktif Android", Icons.Default.People, AdminGreen); AdminMini("Son duyurular", Icons.Default.Notifications, AdminGold); AdminMini("Ayarlar", Icons.Default.Settings, Color(0xFF6B9AF7)) } }
                item { Text("Son Gönderimler", color = AdminText, fontSize = 15.sp, fontWeight = FontWeight.Black) }
                items(campaigns, key = { it.id }) { c -> Surface(shape = RoundedCornerShape(14.dp), color = AdminCard2) { Column(Modifier.padding(12.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(c.title, color = AdminText, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.weight(1f)); Text(c.status ?: "", color = AdminGreen, fontSize = 9.sp) }; Text(c.body, color = AdminMuted, fontSize = 10.sp, maxLines = 2) } } }
                if (campaigns.isEmpty()) item { Text("Henüz gönderim yok.", color = AdminMuted, fontSize = 10.sp) }
            }
        }
    }
}

@Composable private fun RowScope.AdminMini(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) { Surface(shape = RoundedCornerShape(13.dp), color = AdminCard, modifier = Modifier.weight(1f)) { Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, tint = tint, modifier = Modifier.size(21.dp)); Spacer(Modifier.height(4.dp)); Text(title, color = AdminMuted, fontSize = 8.sp) } } }

private fun sendBroadcast(client: io.github.jan.supabase.SupabaseClient, title: String, body: String, setSending: (Boolean) -> Unit, setStatus: (String) -> Unit) {
    setSending(true)
    CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val user = client.auth.currentUserOrNull() ?: error("Oturum bulunamadı")
            client.postgrest.from("notification_campaigns").insert(mapOf("title" to title.trim(), "body" to body.trim(), "type" to "announcement", "audience" to "all", "status" to "sending", "created_by" to user.id))
            client.functions.invoke("send-push-broadcast", body = buildJsonObject { put("title", title.trim()); put("body", body.trim()); put("data", buildJsonObject { put("type", "announcement") }) })
        }.onSuccess { setStatus("Gönderim başarılı • aktif cihazlara iletiliyor.") }.onFailure { setStatus("Gönderim başarısız • ${it.message ?: "Bilinmeyen hata"}") }
        setSending(false)
    }
}
