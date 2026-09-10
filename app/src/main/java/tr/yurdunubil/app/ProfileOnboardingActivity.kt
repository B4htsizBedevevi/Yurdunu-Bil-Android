package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        setContent { ProfileOnboardingScreen(onComplete = ::openMain) }
    }

    private fun openMain() {
        startActivity(Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

@Composable
private fun ProfileOnboardingScreen(onComplete: () -> Unit) {
    val client = remember { SupabaseClientProvider.client }
    val scope = rememberCoroutineScope()
    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(YBAvatars.first().id) }
    var activeCategory by remember { mutableStateOf<AvatarCategory?>(null) }
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

    val ready = username.length in 3..20 && displayName.trim().length in 2..40
    val visibleAvatars = if (activeCategory == null) YBAvatars else YBAvatars.filter { it.category == activeCategory }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = YurdunuBilColors.NaturalGreen,
            background = YurdunuBilColors.Background,
            surface = Color.White
        )
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEAF8F1), YurdunuBilColors.Background, Color.White)
                )
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
                Column(
                    Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp)
                ) {
                    Spacer(Modifier.height(10.dp))
                    Column(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                        Text("Şimdi seni tanıyalım.", color = YurdunuBilColors.Deep, fontSize = 29.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Bir kullanıcı adı ve avatar seç. Bu profil Arena'da ve sosyal alanlarda seni temsil edecek.",
                            color = YurdunuBilColors.Forest,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(Modifier.height(15.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .96f)),
                        border = BorderStroke(1.dp, YurdunuBilColors.Leaf.copy(alpha = .16f))
                    ) {
                        Column(Modifier.fillMaxWidth().padding(17.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(76.dp).clip(CircleShape)
                                        .background(YurdunuBilColors.NaturalGreen.copy(alpha = .11f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        ybAvatar(selectedAvatar).icon,
                                        contentDescription = null,
                                        tint = YurdunuBilColors.Forest,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Box(
                                        Modifier.size(23.dp).align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(YurdunuBilColors.NaturalGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(Modifier.width(13.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Profil avatarın", color = YurdunuBilColors.Deep, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    Text(
                                        "Seçtiğin avatar hesabında sabit kalır ve Arena'da aynı görünür.",
                                        color = YurdunuBilColors.NaturalGreen,
                                        fontSize = 10.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(15.dp))
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it.take(40); error = null },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Görünen ad") },
                                placeholder = { Text("Ömer") },
                                singleLine = true,
                                shape = RoundedCornerShape(15.dp)
                            )
                            Spacer(Modifier.height(9.dp))
                            OutlinedTextField(
                                value = username,
                                onValueChange = { value ->
                                    username = value.lowercase()
                                        .filter { c -> c in 'a'..'z' || c in '0'..'9' || c == '_' }
                                        .take(20)
                                    error = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Kullanıcı adı") },
                                leadingIcon = {
                                    Text("@", color = YurdunuBilColors.NaturalGreen, fontWeight = FontWeight.Black)
                                },
                                placeholder = { Text("yurdunubilci") },
                                supportingText = { Text("3–20 karakter • a-z, 0-9, _") },
                                singleLine = true,
                                shape = RoundedCornerShape(15.dp)
                            )

                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Avatarını seç", color = YurdunuBilColors.Deep, fontSize = 17.sp, fontWeight = FontWeight.Black)
                                Spacer(Modifier.width(8.dp))
                                Text(YBAvatars.size.toString() + " sabit seçenek", color = YurdunuBilColors.NaturalGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(9.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                AvatarFilter("Tümü", activeCategory == null, Modifier.weight(1f)) { activeCategory = null }
                                AvatarCategory.entries.forEach { category ->
                                    AvatarFilter(category.label, activeCategory == category, Modifier.weight(1f)) {
                                        activeCategory = if (activeCategory == category) null else category
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxWidth().height(270.dp),
                                verticalArrangement = Arrangement.spacedBy(9.dp),
                                horizontalArrangement = Arrangement.spacedBy(9.dp)
                            ) {
                                items(visibleAvatars, key = { it.id }) { avatar ->
                                    AvatarTile(
                                        avatar = avatar,
                                        selected = avatar.id == selectedAvatar,
                                        onClick = { selectedAvatar = avatar.id }
                                    )
                                }
                            }

                            if (error != null) {
                                Spacer(Modifier.height(9.dp))
                                Text(error!!, color = Color(0xFFB3261E), fontSize = 11.sp, lineHeight = 16.sp)
                            }

                            Spacer(Modifier.height(12.dp))
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
                                                throwable.message?.contains("username_taken", true) == true ||
                                                    throwable.message?.contains("duplicate", true) == true ||
                                                    throwable.message?.contains("unique", true) == true ->
                                                    "Bu kullanıcı adı zaten alınmış."
                                                else -> "Profil kaydedilemedi. Bilgilerini kontrol edip tekrar dene."
                                            }
                                        }
                                        saving = false
                                    }
                                },
                                enabled = ready && !saving,
                                modifier = Modifier.fillMaxWidth().height(55.dp),
                                shape = RoundedCornerShape(17.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = YurdunuBilColors.NaturalGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                if (saving) {
                                    CircularProgressIndicator(
                                        Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Save, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Profilimi oluştur ve devam et", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(9.dp))
                    Text(
                        "Bu ekran kayıt sonrası zorunludur. Profilini tamamlamadan ana ekrana geçilmez.",
                        color = YurdunuBilColors.Forest,
                        fontSize = 9.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AvatarFilter(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(34.dp),
        onClick = onClick,
        shape = RoundedCornerShape(11.dp),
        color = if (selected) YurdunuBilColors.NaturalGreen else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) YurdunuBilColors.NaturalGreen else YurdunuBilColors.Leaf.copy(alpha = .14f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text,
                color = if (selected) Color.White else YurdunuBilColors.Forest,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AvatarTile(
    avatar: YBAvatar,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) YurdunuBilColors.NaturalGreen.copy(alpha = .13f) else Color.White
        ),
        border = BorderStroke(
            1.5.dp,
            if (selected) YurdunuBilColors.NaturalGreen else YurdunuBilColors.Leaf.copy(alpha = .13f)
        )
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().padding(7.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(
                        if (selected) YurdunuBilColors.NaturalGreen.copy(alpha = .16f)
                        else YurdunuBilColors.SurfaceSoft
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        avatar.icon,
                        contentDescription = avatar.name,
                        tint = if (selected) YurdunuBilColors.Forest else YurdunuBilColors.NaturalGreen,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    avatar.name,
                    color = YurdunuBilColors.Deep,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            if (selected) {
                Box(
                    Modifier.align(Alignment.TopEnd).padding(5.dp).size(18.dp)
                        .clip(CircleShape).background(YurdunuBilColors.NaturalGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
                }
            }
        }
    }
}
