package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class SocialModernActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SocialModernScreen { finish() } }
    }
}

@Serializable private data class ModernNotification(val id: Long, val type: String, val title: String, val body: String, val read_at: String? = null)
@Serializable private data class ModernFriend(val friend_id: String, val username: String? = null, val display_name: String? = null, val avatar_id: String? = null)
@Serializable private data class ModernUser(val id: String, val username: String? = null, val display_name: String? = null, val avatar_id: String? = null)

private val SocialBg = Color(0xFF06140F)
private val SocialCard = Color(0xFF0E211B)
private val SocialCard2 = Color(0xFF123229)
private val SocialText = Color(0xFFF3FBF7)
private val SocialMuted = Color(0xFF91AAA1)
private val SocialGreen = Color(0xFF28DE98)

@Composable
private fun SocialModernScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val client = remember { SupabaseClientProvider.client }
    val scope = rememberCoroutineScope()
    val myId = client.auth.currentUserOrNull()?.id
    var tab by remember { mutableIntStateOf(0) }
    var notifications by remember { mutableStateOf(emptyList<ModernNotification>()) }
    var friends by remember { mutableStateOf(emptyList<ModernFriend>()) }
    var users by remember { mutableStateOf(emptyList<ModernUser>()) }
    var query by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch {
            runCatching {
                notifications = client.postgrest.from("notifications").select { order("created_at", Order.DESCENDING); limit(50) }.decodeList()
                friends = client.postgrest.rpc("get_my_friends").decodeList()
            }.onFailure { error = it.message ?: "Sosyal veriler alınamadı." }
        }
    }
    LaunchedEffect(Unit) { refresh(); while (true) { delay(10000); refresh() } }
    LaunchedEffect(query) {
        if (query.trim().length >= 2) {
            runCatching { users = client.postgrest.rpc("search_users", buildJsonObject { put("p_query", JsonPrimitive(query.trim())) }).decodeList() }
                .onFailure { error = it.message ?: "Kullanıcılar aranamadı." }
        } else users = emptyList()
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = SocialGreen, background = SocialBg, surface = SocialCard, onSurface = SocialText)) {
        Column(Modifier.fillMaxSize().background(SocialBg).padding(horizontal = 16.dp)) {
            Row(Modifier.padding(top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = SocialText) }
                Text("Sosyal", color = SocialText, fontSize = 27.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                if (notifications.any { it.read_at == null }) Surface(shape = RoundedCornerShape(50), color = SocialGreen) { Text(notifications.count { it.read_at == null }.coerceAtMost(99).toString(), color = Color(0xFF06221B), fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
            }
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SocialTab("Bildirimler", Icons.Default.Notifications, tab == 0) { tab = 0 }
                SocialTab("Arkadaşlar (${friends.size})", Icons.Default.People, tab == 1) { tab = 1 }
            }
            if (error != null) Text(error!!, color = Color(0xFFFF9B91), fontSize = 10.sp, modifier = Modifier.padding(bottom = 6.dp), maxLines = 2, overflow = TextOverflow.Ellipsis)

            if (tab == 0) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)) {
                    items(notifications, key = { it.id }) { item ->
                        NotificationModernCard(item) {
                            scope.launch { runCatching { client.postgrest.from("notifications").update(mapOf("read_at" to java.time.Instant.now().toString())) { filter { eq("id", item.id) } }; refresh() } }
                        }
                    }
                    if (notifications.isEmpty()) item { EmptyModernSocial("Henüz bildirimin yok.", "Duyurular, arkadaşlıklar ve önemli gelişmeler burada.") }
                    if (notifications.isNotEmpty()) item {
                        Text("Bildirimler otomatik yenileniyor.", color = SocialMuted, fontSize = 9.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)) {
                    item {
                        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Kullanıcı ara") }, leadingIcon = { Icon(Icons.Default.Search, null) }, shape = RoundedCornerShape(14.dp))
                    }
                    items(users, key = { it.id }) { user ->
                        UserModernCard(user) {
                            scope.launch { runCatching { client.postgrest.rpc("send_friend_request", buildJsonObject { put("p_receiver_id", JsonPrimitive(user.id)) }); refresh() }.onFailure { error = it.message } }
                        }
                    }
                    item { Text("Arkadaşlarım", color = SocialText, fontWeight = FontWeight.Black, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp)) }
                    if (friends.isEmpty()) item { EmptyModernSocial("Henüz arkadaşın yok", "Kullanıcı ara ve ilk arkadaşlık isteğini gönder.") }
                    items(friends, key = { it.friend_id }) { friend -> FriendModernCard(friend) }
                }
            }
        }
    }
}

@Composable private fun SocialTab(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(14.dp), color = if (selected) SocialGreen else SocialCard2, modifier = Modifier.weight(1f)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (selected) Color(0xFF06221B) else SocialText, modifier = Modifier.size(19.dp)); Spacer(Modifier.width(6.dp)); Text(title, color = if (selected) Color(0xFF06221B) else SocialText, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable private fun NotificationModernCard(item: ModernNotification, onClick: () -> Unit) {
    val accent = when (item.type) { "arena_result" -> Color(0xFFFFC857); "achievement" -> Color(0xFF6B9AF7); "friend_request", "friend_request_accepted" -> Color(0xFFE86BD0); else -> SocialGreen }
    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = if (item.read_at == null) SocialCard2 else SocialCard) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).background(accent.copy(alpha = .14f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Notifications, null, tint = accent, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(item.title, color = SocialText, fontWeight = FontWeight.Black, fontSize = 13.sp); Text(item.body, color = SocialMuted, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis) }
            if (item.read_at == null) Box(Modifier.size(7.dp).background(SocialGreen, RoundedCornerShape(50)))
        }
    }
}

@Composable private fun UserModernCard(user: ModernUser, onAdd: () -> Unit) { Surface(shape = RoundedCornerShape(16.dp), color = SocialCard) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.PersonAdd, null, tint = SocialGreen, modifier = Modifier.size(26.dp)); Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text(user.display_name?.takeIf { it.isNotBlank() } ?: (user.username ?: "Kullanıcı"), color = SocialText, fontWeight = FontWeight.Black, fontSize = 13.sp); Text("@${user.username ?: "kullanici"}", color = SocialMuted, fontSize = 10.sp) }; Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = SocialGreen, contentColor = Color(0xFF06221B)), shape = RoundedCornerShape(10.dp), modifier = Modifier.height(36.dp)) { Text("Ekle", fontSize = 10.sp, fontWeight = FontWeight.Black) } } } }
@Composable private fun FriendModernCard(friend: ModernFriend) { Surface(shape = RoundedCornerShape(16.dp), color = SocialCard) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(YBIcons.AvatarUser, null, tint = SocialGreen, modifier = Modifier.size(26.dp)); Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text(friend.display_name?.takeIf { it.isNotBlank() } ?: (friend.username ?: "Kullanıcı"), color = SocialText, fontWeight = FontWeight.Black, fontSize = 13.sp); Text("@${friend.username ?: "kullanici"}", color = SocialMuted, fontSize = 10.sp) }; Text("Arkadaş", color = SocialGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun EmptyModernSocial(title: String, body: String) { Surface(shape = RoundedCornerShape(18.dp), color = SocialCard) { Column(Modifier.fillMaxWidth().padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(YBIcons.AvatarCompass, null, tint = SocialGreen, modifier = Modifier.size(34.dp)); Spacer(Modifier.height(8.dp)); Text(title, color = SocialText, fontSize = 16.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(3.dp)); Text(body, color = SocialMuted, fontSize = 10.sp) } } }
