package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class WrongAnswersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WrongAnswersScreen(onBack = { finish() }) }
    }
}

@Serializable
data class WrongAnswerRow(
    val question_id: String,
    val topic: String,
    val selected_index: Int? = null,
    val correct: Boolean = false,
    val created_at: String? = null
)

private object WrongAnswersRepository {
    private val client get() = SupabaseClientProvider.client

    suspend fun latestWrong(limit: Int = 30): List<WrongAnswerRow> {
        val user = client.auth.currentUserOrNull() ?: return emptyList()
        return client.postgrest.from("question_attempts").select {
            filter {
                eq("user_id", user.id)
                eq("correct", false)
            }
            order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit(limit.toLong())
        }.decodeList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WrongAnswersScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val scope = rememberCoroutineScope()
    val bg = Color(0xFFF4F8F6)
    val text = Color(0xFF07251C)
    val muted = Color(0xFF71867E)
    val green = Color(0xFF19BF82)
    val soft = Color(0xFFE9F5F0)

    var rows by remember { mutableStateOf<List<WrongAnswerRow>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        loading = true
        error = null
        scope.launch {
            runCatching { WrongAnswersRepository.latestWrong() }
                .onSuccess { rows = it }
                .onFailure { error = it.message ?: "Yanlışların alınamadı." }
            loading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = { Text("Yanlışlarım", color = text, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri", tint = text)
                    }
                },
                actions = {
                    IconButton(onClick = ::load) {
                        Icon(Icons.Default.Refresh, "Yenile", tint = green)
                    }
                }
            )
        }
    ) { pad ->
        when {
            loading -> Box(
                Modifier.fillMaxSize().padding(pad),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = green) }

            error != null -> Column(
                Modifier.fillMaxSize().padding(pad).padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Warning, null, tint = Color(0xFFE56A61), modifier = Modifier.size(42.dp))
                Spacer(Modifier.height(8.dp))
                Text("Yanlışlar yüklenemedi", color = text, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(4.dp))
                Text(error.orEmpty(), color = muted, fontSize = 11.sp)
            }

            rows.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(pad).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, tint = green, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Harika gidiyorsun! 🎉", color = text, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(4.dp))
                    Text("Kayıtlı yanlışın yok. Yeni sorularla kendini yokla.", color = muted, fontSize = 11.sp)
                }
            }

            else -> LazyColumn(
                Modifier.fillMaxSize().padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = soft),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text("TEKRAR MERKEZİ", color = green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(5.dp))
                            Text("En son kaçırdıkların burada.", color = text, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${rows.size} yanlış soru • Önce bunları kapat.",
                                color = muted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                items(rows.size) { index ->
                    val row = rows[index]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFEDEC)) {
                                Icon(
                                    Icons.Default.Warning,
                                    null,
                                    tint = Color(0xFFE56A61),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(row.topic, color = text, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                Text("Soru #${row.question_id}", color = muted, fontSize = 10.sp)
                            }
                            Icon(Icons.Default.Replay, null, tint = green)
                        }
                    }
                }
            }
        }
    }
}
