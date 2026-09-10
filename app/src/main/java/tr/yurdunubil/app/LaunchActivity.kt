package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private val LC = YurdunuBilColors
private val Ink = Color(0xFFF5FAF7)
private val Muted = Color(0xFFB8C9C2)
private val Glass = Color(0xFF0B211B).copy(alpha = 0.92f)
private val GlassSoft = Color(0xFF17372D).copy(alpha = .62f)
private val Accent = Color(0xFF35E7A1)
private val AccentSoft = Color(0xFFBDF7DF)

@Serializable
data class ProfileGate(
    val id: String,
    val display_name: String = "Öğrenci",
    val username: String? = null,
    val avatar_id: String = "atlas-01",
    @SerialName("onboarding_complete") val onboarding_completed: Boolean = false
)

class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
        setContent { LaunchGate(onReady = ::openApp, initialMode = intent.getBooleanExtra("register", false)) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        runCatching { SupabaseClientProvider.client.handleDeeplinks(intent) }
    }

    private fun openApp() {
        startActivity(Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

@Composable
private fun LaunchGate(onReady: () -> Unit, initialMode: Boolean) {
    var loading by remember { mutableStateOf(false) }
    var session by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ProfileGate?>(null) }
    var authMode by remember { mutableStateOf(initialMode) }

    LaunchedEffect(Unit) {
        val current = SupabaseClientProvider.client.auth.currentSessionOrNull()
        session = current != null
        if (current != null) {
            profile = loadProfile()
            if (profile?.onboarding_completed == true) onReady()
        }
        loading = false
    }

    Box(Modifier.fillMaxSize()) {
        LoginAtmosphere()
        when {
            loading -> LoadingGate()
            !session -> AuthGate(
                mode = authMode,
                onModeChange = { authMode = it },
                onAuthenticated = onReady
            )
            profile?.onboarding_completed != true -> ProfileOnboarding(existing = profile, onComplete = onReady)
            else -> onReady()
        }
    }
}

@Composable
private fun LoginAtmosphere() {
    Canvas(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF041712), Color(0xFF0A3029), Color(0xFF03100C))
                )
            )
    ) {
        val w = size.width
        val h = size.height
        drawCircle(Color(0xFF19D995).copy(alpha = .13f), w * .68f, androidx.compose.ui.geometry.Offset(w * .84f, h * .06f))
        drawCircle(Color(0xFF3E8FA3).copy(alpha = .10f), w * .54f, androidx.compose.ui.geometry.Offset(w * .02f, h * .39f))
        drawCircle(Color(0xFFD7B45A).copy(alpha = .05f), w * .48f, androidx.compose.ui.geometry.Offset(w * .98f, h * .86f))

        val map = Path().apply {
            moveTo(w * .10f, h * .27f)
            lineTo(w * .20f, h * .21f)
            lineTo(w * .31f, h * .24f)
            lineTo(w * .40f, h * .20f)
            lineTo(w * .51f, h * .23f)
            lineTo(w * .63f, h * .19f)
            lineTo(w * .75f, h * .24f)
            lineTo(w * .87f, h * .22f)
            lineTo(w * .91f, h * .31f)
            lineTo(w * .82f, h * .36f)
            lineTo(w * .71f, h * .34f)
            lineTo(w * .64f, h * .39f)
            lineTo(w * .51f, h * .35f)
            lineTo(w * .39f, h * .40f)
            lineTo(w * .27f, h * .36f)
            lineTo(w * .17f, h * .39f)
            close()
        }
        drawPath(map, Accent.copy(alpha = .07f))
        drawPath(map, Accent.copy(alpha = .25f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f, cap = StrokeCap.Round))

        val horizon = Path().apply {
            moveTo(0f, h * .70f)
            lineTo(w * .14f, h * .59f)
            lineTo(w * .23f, h * .67f)
            lineTo(w * .38f, h * .51f)
            lineTo(w * .51f, h * .68f)
            lineTo(w * .68f, h * .55f)
            lineTo(w * .82f, h * .67f)
            lineTo(w, h * .58f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(horizon, Color(0xFF05120E).copy(alpha = .84f))
        drawPath(horizon, Color(0xFF4B9B76).copy(alpha = .17f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))

        listOf(.18f to .25f, .33f to .29f, .49f to .24f, .66f to .30f, .81f to .27f).forEach { (x, y) ->
            drawCircle(Accent.copy(alpha = .48f), 4f, androidx.compose.ui.geometry.Offset(w * x, h * y))
        }
    }
}

@Composable
private fun LoadingGate() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LogoMark()
            Spacer(Modifier.height(12.dp))
            Text("Yurdunu Bil", color = Ink, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator(color = Accent, strokeWidth = 2.dp, modifier = Modifier.size(25.dp))
        }
    }
}

@Composable
private fun AuthGate(mode: Boolean, onModeChange: (Boolean) -> Unit, onAuthenticated: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val ready = email.contains("@") && password.length >= 6

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { -35 })) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LogoMark()
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Yurdunu Bil", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.Black)
                            Text("Türkiye'yi öğren. Hedefini büyüt.", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(13.dp))
                    Surface(color = Accent.copy(alpha = .09f), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, Accent.copy(alpha = .16f))) {
                        Row(Modifier.padding(horizontal = 11.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, null, tint = Accent, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("KPSS • TÜRKİYE COĞRAFYASI • YENİ NESİL", color = AccentSoft, fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = .65.sp)
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        if (mode) "KPSS yolculuğuna katıl." else "Kaldığın yerden devam et.",
                        color = Ink,
                        fontSize = 28.sp,
                        lineHeight = 33.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Konuları öğren, testlerle pekiştir ve ilerlemeni adım adım takip et.",
                        color = Muted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FeatureChip(Icons.Default.Map, "Türkiye") }
                item { FeatureChip(Icons.Default.MenuBook, "Öğren") }
                item { FeatureChip(Icons.Default.Quiz, "Test") }
                item { FeatureChip(Icons.Default.EmojiEvents, "Yarış") }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Glass),
                border = BorderStroke(1.dp, Color.White.copy(alpha = .12f))
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(GlassSoft).padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AuthTab("Giriş Yap", selected = !mode, modifier = Modifier.weight(1f)) { onModeChange(false); error = null; notice = null }
                        AuthTab("Yeni Hesap", selected = mode, modifier = Modifier.weight(1f)) { onModeChange(true); error = null; notice = null }
                    }
                    Spacer(Modifier.height(17.dp))

                    Text("E-POSTA", color = Accent.copy(alpha = .84f), fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 1.1.sp)
                    Spacer(Modifier.height(5.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trimStart(); error = null; notice = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ornek@mail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = loginFieldColors()
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("ŞİFRE", color = Accent.copy(alpha = .84f), fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 1.1.sp)
                    Spacer(Modifier.height(5.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null; notice = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("En az 6 karakter") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (!busy && ready) {
                                scope.launch { performAuth(mode, email, password, onAuthenticated, { notice = it }, { error = it }, { busy = it }) }
                            }
                        }),
                        colors = loginFieldColors()
                    )

                    AnimatedVisibility(visible = password.isNotEmpty() && password.length < 6, enter = fadeIn()) {
                        Text("Şifre en az 6 karakter olmalı.", color = Color(0xFFFFC2BA), fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                    AnimatedContent(targetState = error ?: notice, label = "auth-message") { message ->
                        if (message != null) {
                            val good = notice != null && error == null
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = if (good) Accent else Color(0xFFFF9B91), modifier = Modifier.size(17.dp))
                                Spacer(Modifier.width(7.dp))
                                Text(message, color = if (good) AccentSoft else Color(0xFFFFB5AD), fontSize = 11.sp, lineHeight = 17.sp)
                            }
                        } else {
                            Text(
                                if (mode) "Kayıt sonrası e-posta doğrulaması istenebilir." else "Bilgilerin cihazında değil, hesabında saklanır.",
                                color = Muted,
                                fontSize = 10.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { scope.launch { performAuth(mode, email, password, onAuthenticated, { notice = it }, { error = it }, { busy = it }) } },
                        enabled = !busy && ready,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color(0xFF06251B), disabledContainerColor = Color(0xFF2A4A40))
                    ) {
                        if (busy) CircularProgressIndicator(color = Color(0xFF06251B), strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                        else {
                            Text(if (mode) "Haritaya Katıl" else "Devam Et", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }
                }
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Türkiye'yi öğren • Test çöz • Arena'da yarış", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(7.dp))
                Text("KPSS • COĞRAFYA • ÖĞRENME • YARIŞ", color = Accent.copy(alpha = .88f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = .4.sp)
            }
        }
    }
}

// AUTH PRESENTATION V2

private suspend fun performAuth(
    mode: Boolean,
    email: String,
    password: String,
    onAuthenticated: () -> Unit,
    onNotice: (String) -> Unit,
    onError: (String) -> Unit,
    onBusy: (Boolean) -> Unit
) {
    onBusy(true)
    onError("")
    onNotice("")
    try {
        if (mode) {
            SupabaseClientProvider.client.auth.signUpWith(Email) {
                this.email = email.trim()
                this.password = password
            }
            if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onAuthenticated()
            else onNotice("Hesabın oluşturuldu. E-posta kutundaki doğrulama bağlantısını kontrol et.")
        } else {
            SupabaseClientProvider.client.auth.signInWith(Email) {
                this.email = email.trim()
                this.password = password
            }
            if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onAuthenticated()
            else onError("Giriş tamamlanamadı. Lütfen bilgilerini kontrol et.")
        }
    } catch (e: Exception) {
        onError(friendlyAuthError(e))
    } finally {
        onBusy(false)
    }
}

private fun friendlyAuthError(error: Throwable): String {
    val raw = (error.message ?: "").lowercase()
    return when {
        "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta veya şifre hatalı."
        "email not confirmed" in raw -> "E-posta adresini doğruladıktan sonra tekrar giriş yap."
        "user already registered" in raw || "already registered" in raw -> "Bu e-posta zaten kayıtlı. Giriş Yap sekmesine geç."
        "network" in raw || "timeout" in raw || "unable to resolve host" in raw -> "Bağlantı kurulamadı. İnternetini kontrol edip tekrar dene."
        else -> "İşlem tamamlanamadı. Lütfen tekrar dene."
    }
}

@Composable
private fun FeatureChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = .07f)).border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Accent, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AuthTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(15.dp)).background(if (selected) Color.White.copy(alpha = .12f) else Color.Transparent).clickable(onClick = onClick).padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) Ink else Muted, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Accent,
    unfocusedBorderColor = Color.White.copy(alpha = .15f),
    focusedLabelColor = Accent,
    unfocusedLabelColor = Muted,
    cursorColor = Accent,
    focusedTextColor = Ink,
    unfocusedTextColor = Ink,
    focusedLeadingIconColor = Accent,
    unfocusedLeadingIconColor = Muted,
    focusedTrailingIconColor = Accent,
    unfocusedTrailingIconColor = Muted,
    focusedContainerColor = Color.White.copy(alpha = .045f),
    unfocusedContainerColor = Color.White.copy(alpha = .025f)
)

@Composable
private fun LogoMark() {
    Box(
        modifier = Modifier.size(42.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF36E9A2), Color(0xFF197A5B)))),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Map, null, tint = Color(0xFF06251B), modifier = Modifier.size(23.dp))
    }
}

private suspend fun loadProfile(): ProfileGate? = withContext(Dispatchers.IO) {
    runCatching {
        val user = SupabaseClientProvider.client.auth.currentSessionOrNull()?.user ?: return@runCatching null
        SupabaseClientProvider.client.postgrest["profiles"].select {
            filter { eq("id", user.id) }
        }.decodeList<ProfileGate>().firstOrNull()
    }.getOrNull()
}

private val AVATARS = listOf(
    "atlas-01" to "🧭", "atlas-02" to "🌍", "atlas-03" to "🏔️", "atlas-04" to "🌊",
    "atlas-05" to "🌲", "atlas-06" to "☀️", "atlas-07" to "🗺️", "atlas-08" to "🦅",
    "atlas-09" to "🌿", "atlas-10" to "⛰️", "atlas-11" to "🏕️", "atlas-12" to "⭐"
)

@Composable
private fun ProfileOnboarding(existing: ProfileGate?, onComplete: () -> Unit) {
    var displayName by remember { mutableStateOf(existing?.display_name.orEmpty()) }
    var username by remember { mutableStateOf(existing?.username.orEmpty()) }
    var avatar by remember { mutableStateOf(existing?.avatar_id ?: AVATARS.first().first) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(Modifier.fillMaxSize(), color = LC.Background) {
        LazyColumn(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("Şimdi seni tanıyalım", color = LC.Forest, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("Arena'da kullanacağın ad ve avatarı seç.", color = LC.NaturalGreen)
            }
            item { OutlinedTextField(displayName, { displayName = it.take(40) }, Modifier.fillMaxWidth(), label = { Text("Görünen ad") }, leadingIcon = { Icon(Icons.Default.Person, null) }, singleLine = true) }
            item { OutlinedTextField(username, { username = it.lowercase().filter { c -> c in 'a'..'z' || c in '0'..'9' || c == '_' }.take(20) }, Modifier.fillMaxWidth(), label = { Text("Kullanıcı adı") }, leadingIcon = { Text("@") }, singleLine = true, supportingText = { Text("3–20 karakter • a-z, 0-9, _") }) }
            item { Text("Avatarını seç", color = LC.Deep, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(AVATARS) { (id, icon) ->
                        Card(onClick = { avatar = id }, shape = CircleShape, colors = CardDefaults.cardColors(containerColor = if (avatar == id) LC.Sky else LC.SurfaceSoft), modifier = Modifier.size(58.dp)) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(icon, fontSize = 28.sp) }
                        }
                    }
                }
            }
            item {
                if (error != null) Text(error!!, color = Color(0xFFB3261E), fontSize = 12.sp)
                Button(
                    onClick = {
                        busy = true; error = null
                        scope.launch {
                            try {
                                val params = buildJsonObject {
                                    put("p_username", username.trim())
                                    put("p_display_name", displayName.trim())
                                    put("p_avatar_id", avatar)
                                }
                                SupabaseClientProvider.client.postgrest.rpc("complete_onboarding", params)
                                onComplete()
                            } catch (e: Exception) {
                                error = if (e.message?.contains("username_taken") == true) "Bu kullanıcı adı zaten alınmış." else "Profil kaydedilemedi. Lütfen tekrar dene."
                            } finally { busy = false }
                        }
                    },
                    enabled = !busy && username.length >= 3 && displayName.trim().length >= 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LC.NaturalGreen)
                ) { Text(if (busy) "Kaydediliyor…" else "Profilimi Oluştur") }
            }
        }
    }
}
