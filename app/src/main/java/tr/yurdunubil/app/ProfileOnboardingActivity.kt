package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class ProfileGate(
    val id: String,
    val display_name: String = "Öğrenci",
    val username: String? = null,
    val avatar_id: String? = null,
    val onboarding_complete: Boolean = false
)

class ProfileOnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ProfileOnboardingScreen(onComplete = ::openMain, onBack = { finish() }) }
    }

    private fun openMain() {
        startActivity(Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

@Composable
private fun ProfileOnboardingScreen(onComplete: () -> Unit, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val client = remember { SupabaseClientProvider.client }
    val scope = rememberCoroutineScope()
    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(YBAvatars.first().id) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            val user = client.auth.currentUserOrNull() ?: error("Oturum bulunamadı.")
            val existing = client.postgrest.from("profiles").select {
                filter { eq("id", user.id) }
                limit(1)
            }.decodeSingleOrNull<ProfileGate>()
            displayName = existing?.display_name?.takeIf { it.isNotBlank() }
                ?: user.userMetadata?.get("full_name")?.toString()?.trim('"').orEmpty()
            username = existing?.username.orEmpty()
            selectedAvatar = ybAvatar(existing?.avatar_id).id
        }.onFailure {
            error = "Profil bilgileri yüklenemedi. Tekrar dene."
        }
        loading = false
    }

    val ready = username.length in 3..20 && displayName.trim().length >= 2

    MaterialTheme(colorScheme = lightColorScheme(
        primary = YurdunuBilColors.NaturalGreen,
        background = YurdunuBilColors.Background,
        surface = Color.White
    )) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(YurdunuBilColors.Background, Color.White, YurdunuBilColors.SurfaceSoft))
            )
        ) {
            if (loading) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = YurdunuBilColors.NaturalGreen)
                    Spacer(Modifier.height(12.dp))
                    Text("Profilin hazırlanıyor…", color = YurdunuBilColors.Deep, fontWeight = FontWeight.Bold)
                }
            } else {
                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(YBIcons.Back, contentDescription = "Geri", tint = YurdunuBilColors.Deep) }
                        Column(Modifier.weight(1f)) {
                            Text("Seni tanıyalım", color = YurdunuBilColors.Deep, fontSize = 27.sp, fontWeight = FontWeight.Black)
                            Text("Profilini bir kez seç, hesabında sabit kalsın.", color = YurdunuBilColors.NaturalGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(YBIcons.AvatarCompass, contentDescription = null, tint = YurdunuBilColors.NaturalGreen, modifier = Modifier.size(26.dp))
                    }

                    Spacer(Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .94f)),
                        border = BorderStroke(1.dp, YurdunuBilColors.Leaf.copy(alpha = .18f))
                    ) {
                        Column(Modifier.fillMaxWidth().padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(76.dp), shape = CircleShape, color = YurdunuBilColors.Sky.copy(alpha = .34f)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(ybAvatar(selectedAvatar).icon, contentDescription = null, tint = YurdunuBilColors.Forest, modifier = Modifier.size(41.dp))
                                    }
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Profil görünümün", color = YurdunuBilColors.Deep, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("Seçtiğin avatar uygulamada hep aynı kalır.", color = YurdunuBilColors.NaturalGreen, fontSize = 11.sp, lineHeight = 16.sp)
                                }
                            }

                            Spacer(Modifier.height(18.dp))
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it.take(40); error = null },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Görünen ad") },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                singleLine = true,
                                shape = RoundedCornerShape(15.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = username,
                                onValueChange = { value ->
                                    username = value.lowercase().filter { c -> c in 'a'..'z' || c in '0'..'9' || c == '_' }.take(20)
                                    error = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Kullanıcı adı") },
                                leadingIcon = { Text("@", color = YurdunuBilColors.NaturalGreen, fontWeight = FontWeight.Black) },
                                singleLine = true,
                                supportingText = { Text("3–20 karakter • a-z, 0-9, _") },
                                shape = RoundedCornerShape(15.dp)
                            )

                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Avatarını seç", color = YurdunuBilColors.Deep, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("${YBAvatars.size} seçenek", color = YurdunuBilColors.NaturalGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(9.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxWidth().height(245.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(YBAvatars, key = { it.id }) { avatar ->
                                    AvatarTile(avatar, selected = avatar.id == selectedAvatar) {
                                        selectedAvatar = avatar.id
                                    }
                                }
                            }

                            if (error != null) {
                                Spacer(Modifier.height(10.dp))
                                Text(error!!, color = Color(0xFFB3261E), fontSize = 11.sp)
                            }

                            Spacer(Modifier.height(13.dp))
                            Button(
                                onClick = {
                                    saving = true
                                    error = null
                                    scope.launch {
                                        runCatching {
                                            val params = buildJsonObject {
                                                put("p_username", username.trim())
                                                put("p_display_name", displayName.trim())
                                                put("p_avatar_id", selectedAvatar)
                                            }
                                            client.postgrest.rpc("complete_onboarding", params)
                                        }.onSuccess {
                                            onComplete()
                                        }.onFailure { throwable ->
                                            error = when {
                                                throwable.message?.contains("username_taken", true) == true -> "Bu kullanıcı adı zaten alınmış."
                                                throwable.message?.contains("unique", true) == true -> "Bu kullanıcı adı zaten alınmış."
                                                else -> "Profil kaydedilemedi. Bilgilerini kontrol edip tekrar dene."
                                            }
                                        }
                                        saving = false
                                    }
                                },
                                enabled = ready && !saving,
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(17.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = YurdunuBilColors.NaturalGreen, contentColor = Color.White)
                            ) {
                                if (saving) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                else {
                                    Icon(Icons.Default.Save, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Profilimi Oluştur", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = YurdunuBilColors.NaturalGreen, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Bu seçimler hesabına kaydedilir.", color = YurdunuBilColors.Forest, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarTile(avatar: YBAvatar, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) YurdunuBilColors.Sky.copy(alpha = .42f) else YurdunuBilColors.SurfaceSoft),
        border = BorderStroke(1.5.dp, if (selected) YurdunuBilColors.NaturalGreen else YurdunuBilColors.Leaf.copy(alpha = .16f))
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(avatar.icon, contentDescription = avatar.name, tint = if (selected) YurdunuBilColors.Forest else YurdunuBilColors.NaturalGreen, modifier = Modifier.size(29.dp))
            Spacer(Modifier.height(5.dp))
            Text(avatar.name, color = YurdunuBilColors.Deep, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}
