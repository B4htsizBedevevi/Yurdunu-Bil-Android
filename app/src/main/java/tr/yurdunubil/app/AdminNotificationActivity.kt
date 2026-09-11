package tr.yurdunubil.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    val context = LocalContext.current
    val client = remember { SupabaseClientProvider.client }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }
    val bg = YurdunuBilColors.Background
    val text = YurdunuBilColors.Deep
    val muted = Color(0xFF70847B)
    val green = Color(0xFF18C98A)
    val border = Color(0xFFD8E7E0)

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = green,
            background = bg,
            surface = Color.White,
            onBackground = text,
            onSurface = text,
            onPrimary = Color(0xFF052118)
        )
    ) {
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) }
                    Column(Modifier.weight(1f)) {
                        Text("Bildirim Gönder", fontSize = 26.sp, fontWeight = FontWeight.Black, color = text)
                        Text("Yeni duyuru oluştur", color = green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.NotificationsActive, "Bildirim", tint = green)
                }
            }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, border)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("DUYURU MERKEZİ", color = green, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Önce cihazında dene, sonra duyuruyu kaydet.", color = text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Anlık önizleme yalnızca bu cihazda bildirim gösterir. Tüm kullanıcılara FCM gönderimi için sunucu tarafı yapılandırması gerekir.", color = muted, fontSize = 10.sp, lineHeight = 15.sp)
                    }
                }
            }
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
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    label = { Text("Mesaj") },
                    shape = RoundedCornerShape(14.dp)
                )
            }
            item { Text("Hedef: Tüm kullanıcılar • Tür: Duyuru • Durum: Taslak", color = muted, fontSize = 11.sp) }
            item {
                OutlinedButton(
                    enabled = title.isNotBlank() && body.isNotBlank(),
                    onClick = {
                        if (NotificationHelper.canNotify(context)) {
                            NotificationHelper.sendNow(context, title.trim(), body.trim(), openSocial = false)
                            status = "Anlık bildirim bu cihazda gösterildi."
                        } else {
                            status = "Bu cihazda bildirim izni kapalı. Ayarlar → Bildirimler bölümünden izin ver."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = green)
                ) {
                    Icon(Icons.Default.NotificationsActive, null)
                    Spacer(Modifier.padding(horizontal = 3.dp))
                    Text("Şimdi Bu Cihazda Göster", fontWeight = FontWeight.Black)
                }
            }
            item {
                Button(
                    enabled = !sending && title.isNotBlank() && body.isNotBlank(),
                    onClick = {
                        sending = true
                        status = null
                        CoroutineScope(Dispatchers.Main).launch {
                            runCatching {
                                val user = client.auth.currentUserOrNull() ?: error("Oturum bulunamadı")
                                client.postgrest.from("notification_campaigns").insert(
                                    mapOf(
                                        "title" to title.trim(),
                                        "body" to body.trim(),
                                        "type" to "announcement",
                                        "audience" to "all",
                                        "status" to "draft",
                                        "created_by" to user.id
                                    )
                                )
                            }.onSuccess {
                                status = "Duyuru taslak olarak kaydedildi. FCM sunucusu hazır olduğunda tüm kullanıcılara gönderilebilir."
                                title = ""
                                body = ""
                            }.onFailure { status = "Hata: ${it.message ?: "Duyuru kaydedilemedi."}" }
                            sending = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color(0xFF052118))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null)
                        Spacer(Modifier.padding(horizontal = 3.dp))
                        Text(if (sending) "Kaydediliyor…" else "Duyuruyu Kaydet", fontWeight = FontWeight.Black)
                    }
                }
            }
            item { if (status != null) Text(status!!, color = muted, fontSize = 11.sp, lineHeight = 16.sp) }
        }
    }
}
