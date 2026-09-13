package tr.yurdunubil.app

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
private data class QuickAdminProfile(val role: String? = null)

@Composable
fun QuickActionsFloatingEntry() {
    val context = LocalContext.current
    val client = remember { SupabaseClientProvider.client }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        runCatching {
            val user = client.auth.currentUserOrNull() ?: return@runCatching
            val profile = client.postgrest.from("profiles").select {
                filter { eq("id", user.id) }
                limit(1)
            }.decodeSingle<QuickAdminProfile>()
            isAdmin = profile.role == "admin"
        }
    }

    FloatingActionButton(
        onClick = { expanded = !expanded },
        containerColor = Color(0xFF18B982),
        contentColor = Color(0xFF06221A),
        modifier = Modifier.padding(end = 16.dp, bottom = 84.dp)
    ) {
        Icon(if (expanded) Icons.Default.Close else Icons.Default.Speed, contentDescription = "Hızlı erişim")
    }

    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            title = { Text("Hızlı Erişim") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            expanded = false
                            context.startActivity(Intent(context, SmartStudyCenterActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, null)
                        Text("Akıllı Çalışma Merkezi", modifier = Modifier.padding(start = 8.dp))
                    }
                    TextButton(
                        onClick = {
                            expanded = false
                            context.startActivity(Intent(context, SocialCenterActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.People, null)
                        Text("Sosyal & Arkadaşlar", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (isAdmin) {
                        TextButton(
                            onClick = {
                                expanded = false
                                context.startActivity(Intent(context, AdminCenterActivity::class.java))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, null)
                            Text("Admin Merkezi", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { expanded = false }) { Text("Kapat") } }
        )
    }
}
