package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.time.Instant

class SocialCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SocialCenterScreen(onBack = { finish() }) }
    }
}

@Serializable
data class SocialNotificationRow(val id: Long, val type: String, val title: String, val body: String, val read_at: String? = null)
@Serializable
data class SocialUserRow(val id: String, val username: String? = null, val display_name: String? = null, val avatar_id: String? = null)
@Serializable
data class IncomingFriendRequestRow(val id: Long, val sender_id: String, val status: String, val created_at: String? = null)
@Serializable
data class FriendRow(val friend_id: String, val username: String? = null, val display_name: String? = null, val avatar_id: String? = null, val friends_since: String? = null)
@Serializable
data class ProgressRow(val streak_days: Int = 0, val best_streak: Int = 0, val xp: Int = 0, val level: Int = 1, val daily_completed: Boolean = false)

private object SocialRepository {
    private val client get() = SupabaseClientProvider.client

    suspend fun notifications(): List<SocialNotificationRow> = client.postgrest.from("notifications").select {
        order("created_at", Order.DESCENDING)
        limit(50)
    }.decodeList()

    suspend fun markRead(id: Long) {
        client.postgrest.from("notifications").update(mapOf("read_at" to Instant.now().toString())) { filter { eq("id", id) } }
    }

    suspend fun markAllRead() { client.postgrest.rpc("mark_all_notifications_read") }
    suspend fun unreadCount(): Int = client.postgrest.rpc("get_unread_notification_count").decodeSingle()

    suspend fun searchUsers(query: String): List<SocialUserRow> = client.postgrest.rpc(
        "search_users", buildJsonObject { put("p_query", JsonPrimitive(query)) }
    ).decodeList()

    suspend fun sendRequest(userId: String) = client.postgrest.rpc(
        "send_friend_request", buildJsonObject { put("p_receiver_id", JsonPrimitive(userId)) }
    )

    suspend fun incomingRequests(myId: String): List<IncomingFriendRequestRow> = client.postgrest.from("friend_requests").select {
        filter { eq("receiver_id", myId); eq("status", "pending") }
        order("created_at", Order.DESCENDING)
        limit(30)
    }.decodeList()

    suspend fun respond(id: Long, accept: Boolean) = client.postgrest.rpc(
        "respond_friend_request", buildJsonObject {
            put("p_request_id", JsonPrimitive(id))
            put("p_accept", JsonPrimitive(accept))
        }
    )

    suspend fun friends(): List<FriendRow> = client.postgrest.rpc("get_my_friends").decodeList()

    suspend fun progress(myId: String): ProgressRow = client.postgrest.from("user_progress").select {
        filter { eq("user_id", myId) }
        limit(1)
    }.decodeSingleOrNull() ?: ProgressRow()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SocialCenterScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val scope = rememberCoroutineScope()
    val client = remember { SupabaseClientProvider.client }
    val myId = client.auth.currentUserOrNull()?.id
    val bg = Color(0xFF06140F)
    val card = Color(0xFF10251E)
    val card2 = Color(0xFF12352A)
    val text = Color(0xFFF3FBF7)
    val muted = Color(0xFF91AAA1)
    val green = Color(0xFF28DE98)
    var tab by remember { mutableIntStateOf(0) }
    var notifications by remember { mutableStateOf(emptyList<SocialNotificationRow>()) }
    var users by remember { mutableStateOf(emptyList<SocialUserRow>()) }
    var requests by remember { mutableStateOf(emptyList<IncomingFriendRequestRow>()) }
    var friends by remember { mutableStateOf(emptyList<FriendRow>()) }
    var progress by remember { mutableStateOf(ProgressRow()) }
    var unread by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun refresh(showBusy: Boolean = false) {
        scope.launch {
            if (showBusy) busy = true
            error = null
            runCatching {
                notifications = SocialRepository.notifications()
                unread = SocialRepository.unreadCount()
                if (myId != null) {
                    requests = SocialRepository.incomingRequests(myId)
                    friends = SocialRepository.friends()
                    progress = SocialRepository.progress(myId)
                }
            }.onFailure { throwable -> error = throwable.message ?: "Sosyal veriler alınamadı." }
            if (showBusy) busy = false
        }
    }

    LaunchedEffect(Unit) {
        refresh(true)
        while (true) {
            delay(8000)
            refresh(false)
        }
    }

    LaunchedEffect(query) {
        val q = query.trim()
        if (q.length >= 2) {
            runCatching { users = SocialRepository.searchUsers(q) }
                .onFailure { throwable -> error = throwable.message ?: "Kullanıcılar aranamadı." }
        } else {
            users = emptyList()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = green, background = bg, surface = card, onSurface = text)) {
        Scaffold(
            containerColor = bg,
            topBar = {
                TopAppBar(
                    title = { Text("Sosyal", fontWeight = FontWeight.Black) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") } },
                    actions = { if (unread > 0) BadgeBox(unread) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bg, titleContentColor = text, navigationIconContentColor = text)
                )
            }
        ) { pad ->
            Column(Modifier.fillMaxSize().padding(pad)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { tab = 0 }, colors = ButtonDefaults.buttonColors(containerColor = if (tab == 0) green else card2, contentColor = if (tab == 0) Color(0xFF06221B) else text), shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(6.dp)); Text(if (unread > 0) "Bildirimler ($unread)" else "Bildirimler")
                    }
                    Button(onClick = { tab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = if (tab == 1) green else card2, contentColor = if (tab == 1) Color(0xFF06221B) else text), shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.People, null); Spacer(Modifier.width(6.dp)); Text("Arkadaşlar (${friends.size})")
                    }
                }
                if (error != null) Text(error ?: "", color = Color(0xFFFF8E83), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp))
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth(), color = green)

                if (tab == 0) {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item { StreakCard(progress, green, card2, text, muted) }
                        if (notifications.isNotEmpty()) item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Bildirim geçmişi", color = text, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                TextButton(onClick = { scope.launch { runCatching { SocialRepository.markAllRead() }; refresh(false) } }) { Text("Tümünü oku", color = green) }
                            }
                        }
                        if (notifications.isEmpty()) item { EmptySocial("Henüz bildirimin yok.", "Arkadaşlık, seri ve önemli gelişmeler burada görünecek.") }
                        items(notifications, key = { notification -> notification.id }) { notification ->
                            Card(Modifier.fillMaxWidth().clickable { scope.launch { runCatching { SocialRepository.markRead(notification.id) }; refresh(false) } }, shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = if (notification.read_at == null) card2 else card)) {
                                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(notificationEmoji(notification.type), fontSize = 26.sp)
                                    Spacer(Modifier.width(11.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(notification.title, color = text, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        Text(notification.body, color = muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                    if (notification.read_at == null) Box(Modifier.size(8.dp).background(green, RoundedCornerShape(50)))
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Kullanıcı veya görünen ad ara") }, leadingIcon = { Icon(Icons.Default.PersonAdd, null) }, shape = RoundedCornerShape(15.dp))
                        }
                        if (requests.isNotEmpty()) item { Text("Gelen istekler", color = green, fontWeight = FontWeight.Black, fontSize = 13.sp) }
                        items(requests, key = { request -> "r${request.id}" }) { request ->
                            FriendRequestCard(request, card, text, muted, green, onRespond = { accept -> scope.launch { runCatching { SocialRepository.respond(request.id, accept) }.onFailure { throwable -> error = throwable.message }; refresh(false) } })
                        }
                        if (users.isNotEmpty()) item { Text("Kullanıcılar", color = green, fontWeight = FontWeight.Black, fontSize = 13.sp) }
                        items(users, key = { user -> user.id }) { user ->
                            UserCard(user, card, text, muted, green, onAdd = { scope.launch { runCatching { SocialRepository.sendRequest(user.id) }.onFailure { throwable -> error = throwable.message }; refresh(false) } })
                        }
                        item { Text("Arkadaşlarım", color = green, fontWeight = FontWeight.Black, fontSize = 13.sp) }
                        if (friends.isEmpty()) item { EmptySocial("Henüz arkadaşın yok", "Yukarıdan bir kullanıcı ara ve ilk arkadaşlık isteğini gönder.") }
                        items(friends, key = { friend -> friend.friend_id }) { friend -> FriendCard(friend, card, text, muted, green) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendRequestCard(request: IncomingFriendRequestRow, card: Color, text: Color, muted: Color, green: Color, onRespond: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = card)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Yeni arkadaşlık isteği", color = text, fontWeight = FontWeight.Black); Text(request.sender_id.take(8) + "…", color = muted, fontSize = 11.sp) }
            TextButton(onClick = { onRespond(false) }) { Text("Reddet", color = muted) }
            Button(onClick = { onRespond(true) }, colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color(0xFF06221B)), shape = RoundedCornerShape(12.dp)) { Text("Kabul", fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
private fun UserCard(user: SocialUserRow, card: Color, text: Color, muted: Color, green: Color, onAdd: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = card)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("👤", fontSize = 25.sp); Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(user.display_name?.takeIf { value -> value.isNotBlank() } ?: (user.username ?: "Kullanıcı"), color = text, fontWeight = FontWeight.Black)
                Text("@${user.username ?: "kullanici"}", color = muted, fontSize = 11.sp)
            }
            Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color(0xFF06221B)), shape = RoundedCornerShape(12.dp)) { Text("Ekle", fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
private fun FriendCard(friend: FriendRow, card: Color, text: Color, muted: Color, green: Color) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = card)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("🧭", fontSize = 25.sp); Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(friend.display_name?.takeIf { value -> value.isNotBlank() } ?: (friend.username ?: "Kullanıcı"), color = text, fontWeight = FontWeight.Black)
                Text("@${friend.username ?: "kullanici"}", color = muted, fontSize = 11.sp)
            }
            Text("Arkadaş", color = green, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
private fun BadgeBox(count: Int) {
    Surface(shape = RoundedCornerShape(50), color = Color(0xFF28DE98), modifier = Modifier.padding(end = 12.dp)) {
        Text(count.coerceAtMost(99).toString(), color = Color(0xFF06221B), fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
private fun StreakCard(progress: ProgressRow, green: Color, card: Color, text: Color, muted: Color) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = card)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 30.sp); Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Çalışma serin", color = text, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    Text(if (progress.daily_completed) "Bugünün görevi tamamlandı!" else "Bugün çalışarak serini koru.", color = muted, fontSize = 11.sp)
                }
                Text("${progress.streak_days} gün", color = green, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniStat("En iyi", "${progress.best_streak} gün", text, muted)
                MiniStat("Seviye", progress.level.toString(), text, muted)
                MiniStat("XP", progress.xp.toString(), text, muted)
            }
        }
    }
}

@Composable
private fun RowScope.MiniStat(label: String, value: String, text: Color, muted: Color) {
    Column(Modifier.weight(1f)) { Text(value, color = text, fontWeight = FontWeight.Black, fontSize = 14.sp); Text(label, color = muted, fontSize = 10.sp) }
}

@Composable
private fun EmptySocial(title: String, body: String) {
    Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF10251E))) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🧭", fontSize = 28.sp); Spacer(Modifier.height(8.dp)); Text(title, color = Color.White, fontWeight = FontWeight.Black); Text(body, color = Color(0xFF91AAA1), fontSize = 11.sp)
        }
    }
}

private fun notificationEmoji(type: String): String = when (type) {
    "friend_request" -> "👋"
    "friend_request_accepted" -> "🤝"
    "streak_warning" -> "🔥"
    "arena_result" -> "⚔️"
    "achievement" -> "🏆"
    else -> "🔔"
}
