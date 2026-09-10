package tr.yurdunubil.app

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.Serializable

@Serializable
private data class AdminGateProfile(val role: String? = null)

@Composable
fun AdminFloatingEntry() {
    val context = LocalContext.current
    val client = remember { SupabaseClientProvider.client }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        runCatching {
            val user = client.auth.currentUserOrNull() ?: return@runCatching
            val profile = client.postgrest.from("profiles").select {
                filter { eq("id", user.id) }
                limit(1)
            }.decodeSingle<AdminGateProfile>()
            isAdmin = profile.role == "admin"
        }
    }

    if (!isAdmin) return

    Box(
        modifier = Modifier.fillMaxSize().padding(end = 16.dp, bottom = 150.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { context.startActivity(Intent(context, AdminCenterActivity::class.java)) },
            containerColor = Color(0xFFFFC857),
            contentColor = Color(0xFF2A2108)
        ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Merkezi")
        }
    }
}
