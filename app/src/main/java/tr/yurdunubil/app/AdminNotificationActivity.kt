package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AdminNotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminNotificationScreen { finish() } }
    }
}

@Serializable
private data class AutomationRow(
    val id: Long,
    val key: String,
    val title: String,
    val category: String,
    val time_local: String,
    val active: Boolean = true
)

@Serializable
private data class TemplateCountRow(val id: Long)

@Composable
private fun AdminNotificationScreen(onBack: () -> Unit) {
    val client = remember { SupabaseClientProvider.client }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var templateTitle by remember { mutableStateOf("") }
    var templateBody by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("motivation") }
    var categoryOpen by remember { mutableStateOf(false) }

    var automations by remember { mutableStateOf(emptyList<AutomationRow>()) }
    var templateCount by remember { mutableIntStateOf(0) }
    var status by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    val categories = listOf(
        "study" to "Çalışma",
        "streak" to "Seri",
        "quiz" to "Mini Test",
        "geography" to "Coğrafya",
        "arena" to "Arena",
        "content" to "İçerik",
        "motivation" to "Motivasyon"
    )

    fun refresh() {
        scope.launch {
            runCatching {
                automations = client.postgrest.from("notification_automations").select {
                    order("time_local")
                }.decodeList<AutomationRow>()

                templateCount = client.postgrest.from("notification_templates").select {
                    filter { eq("active", true) }
                }.decodeList<TemplateCountRow>().size
            }.onFailure {
                status = "❌ Bildirim ayarları yüklenemedi."
            }
        }
    }

    LaunchedEffect(Unit) { refresh() }

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
            }.onSuccess { response ->
                val raw = response.data?.toString() ?: ""
                val sent = Regex("\"push_sent\"\\s*:\\s*(\\d+)").find(raw)?.groupValues?.get(1)
                val configured = raw.contains("\"push_configured\":true")
                status = when {
                    configured && sent != null -> "✅ FCM gönderimi tamamlandı: $sent aktif cihaza ulaştırma denemesi yapıldı."
                    configured -> "✅ FCM isteği işlendi. Kullanıcı cihazları teslimat raporunu sağlayacaktır."
                    else -> "⚠️ Bildirim uygulama içine kaydedildi ancak Firebase/FCM yapılandırması hazır görünmüyor."
                }
                title = ""
                body = ""
            }.onFailure {
                status = "❌ Gönderilemedi: " + (it.message ?: "Sunucu hatası")
            }
            sending = false
        }
    }

    fun saveAutomation(row: AutomationRow) {
        val normalized = row.time_local.trim()
        if (!Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(normalized)) {
            status = "⚠️ Saat HH:mm biçiminde olmalı."
            return
        }

        scope.launch {
            runCatching {
                client.postgrest.from("notification_automations").update(
                    mapOf(
                        "time_local" to normalized,
                        "active" to row.active,
                        "updated_at" to java.time.Instant.now().toString()
                    )
                ) {
                    filter { eq("id", row.id) }
                }
                refresh()
            }.onSuccess {
                status = "✅ Otomatik bildirim planı güncellendi."
            }.onFailure {
                status = "❌ Plan kaydedilemedi: " + (it.message ?: "Sunucu hatası")
            }
        }
    }

    fun addTemplate() {
        if (templateTitle.isBlank() || templateBody.isBlank()) return

        scope.launch {
            runCatching {
                client.postgrest.from("notification_templates").insert(
                    mapOf(
                        "category" to selectedCategory,
                        "title" to templateTitle.trim().take(80),
                        "body" to templateBody.trim().take(220),
                        "action" to "home",
                        "active" to true,
                        "weight" to 1
                    )
                )
            }.onSuccess {
                status = "✅ Yeni şablon eklendi. Otomatik bildirim havuzunda kullanılacak."
                templateTitle = ""
                templateBody = ""
                refresh()
            }.onFailure {
                status = "❌ Şablon eklenemedi: " + (it.message ?: "Sunucu hatası")
            }
        }
    }

    MaterialTheme {
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }
                    Column(Modifier.weight(1f)) {
                        Text("Bildirim Merkezi", fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Text("Anlık + otomatik + şablon", color = Color(0xFF18A878), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.Campaign, "Bildirim")
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7F1))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("GERÇEK TELEFON BİLDİRİMİ", color = Color(0xFF18A878), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Kullanıcı uygulamayı açık tutmak zorunda değil. FCM bildirimi Android üst bildirim panelinde gösterilir.",
                            color = Color(0xFF06221B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Aynı duyuru uygulama içindeki Bildirim Merkezi'ne de kaydedilir.",
                            color = Color(0xFF71847D),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            item { Text("ŞİMDİ GÖNDER", color = Color(0xFF18A878), fontSize = 10.sp, fontWeight = FontWeight.Black) }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 80) title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Başlık") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = body,
                    onValueChange = { if (it.length <= 220) body = it },
                    modifier = Modifier.fillMaxWidth().height(145.dp),
                    label = { Text("Mesaj") },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                Button(
                    enabled = !sending && title.isNotBlank() && body.isNotBlank(),
                    onClick = ::sendAnnouncement,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(7.dp))
                    Text(if (sending) "Gönderiliyor…" else "Tüm Kullanıcılara Gönder", fontWeight = FontWeight.Black)
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, null, tint = Color(0xFF18A878))
                    Spacer(Modifier.width(8.dp))
                    Text("OTOMATİK BİLDİRİMLER", color = Color(0xFF18A878), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }

            item {
                Text(
                    "Saatleri değiştirip aç/kapat. Her saat için ilgili kategoriden farklı bir şablon seçilir.",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            items(automations, key = { it.id }) { row ->
                var draft by remember(row.id, row.time_local, row.active) { mutableStateOf(row) }
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(draft.title, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                Text(
                                    categories.firstOrNull { it.first == draft.category }?.second ?: draft.category,
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                            Switch(
                                checked = draft.active,
                                onCheckedChange = { draft = draft.copy(active = it) }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = draft.time_local,
                                onValueChange = { if (it.length <= 5) draft = draft.copy(time_local = it) },
                                modifier = Modifier.weight(1f),
                                label = { Text("Saat HH:mm") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            FilledTonalButton(
                                onClick = { saveAutomation(draft) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Save, null)
                                Spacer(Modifier.width(5.dp))
                                Text("Kaydet")
                            }
                        }
                    }
                }
            }

            item { Text("ŞABLON EKLE", color = Color(0xFF18A878), fontSize = 10.sp, fontWeight = FontWeight.Black) }

            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Aktif şablon: " + templateCount, fontWeight = FontWeight.Black)

                        Box {
                            OutlinedButton(
                                onClick = { categoryOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Kategori: " + (categories.first { it.first == selectedCategory }.second))
                            }

                            DropdownMenu(
                                expanded = categoryOpen,
                                onDismissRequest = { categoryOpen = false }
                            ) {
                                categories.forEach { (id, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            selectedCategory = id
                                            categoryOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = templateTitle,
                            onValueChange = { if (it.length <= 80) templateTitle = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Şablon başlığı") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = templateBody,
                            onValueChange = { if (it.length <= 220) templateBody = it },
                            modifier = Modifier.fillMaxWidth().height(130.dp),
                            label = { Text("Şablon mesajı") },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            enabled = templateTitle.isNotBlank() && templateBody.isNotBlank(),
                            onClick = ::addTemplate,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Şablonu Havuza Ekle", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            item {
                status?.let {
                    Text(it, color = Color(0xFF5F716A), fontSize = 11.sp, lineHeight = 17.sp)
                }
            }
        }
    }
}
