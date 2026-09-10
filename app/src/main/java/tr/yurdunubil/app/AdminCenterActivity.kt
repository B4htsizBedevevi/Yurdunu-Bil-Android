package tr.yurdunubil.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.Serializable

class AdminCenterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminCenterScreen(onBack = { finish() }) }
    }
}

@Serializable
data class AdminProfileRow(val role: String? = null, val username: String? = null)

@androidx.compose.runtime.Composable
private fun AdminCenterScreen(onBack: () -> Unit) {
    val client = remember { SupabaseClientProvider.client }
    var allowed by remember { mutableStateOf<Boolean?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            val user = client.auth.currentUserOrNull() ?: error("Oturum bulunamadı")
            val profile = client.postgrest.from("profiles").select {
                filter { eq("id", user.id) }
                limit(1)
            }.decodeSingle<AdminProfileRow>()
            allowed = profile.role == "admin"
        }.onFailure {
            error = it.message ?: "Yetki kontrolü başarısız."
            allowed = false
        }
    }

    MaterialTheme {
        if (allowed == true) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }
                        Column(Modifier.weight(1f)) {
                            Text("Admin Merkezi", fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Black)
                            Text("Yurdunu Bil yönetim paneli", color = Color(0xFF18A878), fontSize = 12.sp)
                        }
                        Icon(Icons.Default.AdminPanelSettings, "Admin")
                    }
                }
                item { AdminCard("📢", "Bildirim Gönder", "Kullanıcılara duyuru ve özel bildirim gönder.", Icons.Default.Campaign) }
                item { AdminCard("👥", "Kullanıcılar", "Kullanıcıları ve sosyal sistemi yönet.", Icons.Default.People) }
                item { AdminCard("📚", "Soru Bankası", "Ortak soru havuzunu ve içerikleri yönet.", Icons.Default.QuestionAnswer) }
                item {
                    Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF06251C))) {
                        Column(Modifier.padding(18.dp)) {
                            Text("GÜVENLİK", color = Color(0xFFFFC857), fontSize = 10.sp)
                            Spacer(Modifier.height(5.dp))
                            Text("Admin yetkisi sunucu tarafında kontrol edilir.", color = Color.White, fontSize = 15.sp)
                            Text("APK içine admin şifresi veya servis anahtarı koyulmaz.", color = Color.White.copy(alpha = .72f), fontSize = 11.sp)
                        }
                    }
                }
                item {
                    Button(onClick = onBack, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(48.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18C98A))) {
                        Text("Panele Dön", color = Color(0xFF052118))
                    }
                }
            }
        } else if (allowed == false) {
            Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Admin erişimi yok", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Black)
                Text(error ?: "Bu hesap yönetici hesabı değil.", modifier = Modifier.padding(top = 8.dp), color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Geri") }
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Admin yetkisi kontrol ediliyor…", color = Color.Gray)
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AdminCard(emoji: String, title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 26.sp)
            Spacer(Modifier.padding(4.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Black)
                Text(body, color = Color.Gray, fontSize = 11.sp)
            }
            Icon(icon, null, tint = Color(0xFF18A878))
        }
    }
}
