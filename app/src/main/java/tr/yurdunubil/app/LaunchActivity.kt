package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.auth.providers.Email
import io.github.jan.supabase.auth.user.UserInfo

private val LC = YurdunuBilColors

@Serializable
data class OnboardingRpc(
    val p_username: String,
    val p_display_name: String,
    val p_avatar_id: String
)

@Serializable
data class ProfileGate(
    val id: String,
    val display_name: String = "Öğrenci",
    val username: String? = null,
    val avatar_id: String = "atlas-01",
    val onboarding_completed: Boolean = false
)

class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LaunchGate(onReady = ::openApp) }
    }

    private fun openApp() {
        startActivity(Intent(this, RetentionMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

@Composable
private fun LaunchGate(onReady: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var session by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ProfileGate?>(null) }
    var authMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val client = SupabaseClientProvider.client
        val current = client.auth.currentSessionOrNull()
        session = current != null
        if (current != null) {
            profile = loadProfile()
            if (profile?.onboarding_completed == true) onReady()
        }
        loading = false
    }

    Surface(Modifier.fillMaxSize(), color = LC.Background) {
        when {
            loading -> LoadingGate()
            !session -> AuthGate(
                mode = authMode,
                onModeChange = { authMode = it },
                onSignedIn = {
                    session = true
                    profile = null
                }
            )
            profile?.onboarding_completed != true -> ProfileOnboarding(
                existing = profile,
                onComplete = onReady
            )
            else -> onReady()
        }
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

@Composable
private fun LoadingGate() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Yurdunu Bil", color = LC.Forest, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text("Güvenli bağlantı kuruluyor…", color = LC.NaturalGreen)
        }
    }
}

@Composable
private fun AuthGate(mode: Boolean, onModeChange: (Boolean) -> Unit, onSignedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(22.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Yurdunu Bil", color = LC.Forest, fontSize = 34.sp, fontWeight = FontWeight.Black)
        Text(if (mode) "Hesabını oluştur" else "Türkiye coğrafyasına kaldığın yerden devam et", color = LC.NaturalGreen, fontSize = 15.sp)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("E-posta") }, leadingIcon = { Icon(Icons.Default.Email, null) }, singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Şifre") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
        Spacer(Modifier.height(14.dp))
        if (error != null) Text(error!!, color = Color(0xFFB3261E), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                busy = true; error = null
                kotlinx.coroutines.MainScope().launch {
                    try {
                        if (mode) {
                            SupabaseClientProvider.client.auth.signUpWith(Email) { this.email = email.trim(); this.password = password }
                        } else {
                            SupabaseClientProvider.client.auth.signInWith(Email) { this.email = email.trim(); this.password = password }
                        }
                        if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onSignedIn()
                        else if (mode) error = "E-postanı doğrula. Ardından giriş yapabilirsin."
                    } catch (e: Exception) {
                        error = e.message ?: "İşlem tamamlanamadı."
                    } finally { busy = false }
                }
            },
            enabled = !busy && email.contains("@") && password.length >= 8,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = LC.NaturalGreen)
        ) { Text(if (busy) "Bekleyin…" else if (mode) "Hesap Oluştur" else "Giriş Yap") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onModeChange(!mode); error = null }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = LC.SurfaceSoft, contentColor = LC.Forest)) {
            Text(if (mode) "Zaten hesabım var" else "Yeni hesap oluştur")
        }
        Spacer(Modifier.height(12.dp))
        Text("Şifre en az 8 karakter olmalı. Hesabın ve ilerlemen Supabase Auth + RLS ile korunur.", color = Color(0xFF687B71), fontSize = 11.sp)
    }
}

private val AVATARS = listOf(
    "atlas-01" to "🧭", "atlas-02" to "🏔️", "atlas-03" to "🌊", "atlas-04" to "🌲",
    "atlas-05" to "🗺️", "atlas-06" to "⛰️", "atlas-07" to "☀️", "atlas-08" to "🌿",
    "atlas-09" to "🦅", "atlas-10" to "🧿", "atlas-11" to "🚀", "atlas-12" to "🎯"
)

@Composable
private fun ProfileOnboarding(existing: ProfileGate?, onComplete: () -> Unit) {
    var username by remember { mutableStateOf(existing?.username ?: "") }
    var displayName by remember { mutableStateOf(existing?.display_name ?: "") }
    var avatar by remember { mutableStateOf(existing?.avatar_id ?: AVATARS.first().first) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    LazyColumnLike {
        Text("Şimdi seni tanıyalım", color = LC.Forest, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Text("Arena'da kullanacağın ad ve avatarı seç.", color = LC.NaturalGreen)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(displayName, { displayName = it.take(40) }, Modifier.fillMaxWidth(), label = { Text("Görünen ad") }, leadingIcon = { Icon(Icons.Default.Person, null) }, singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(username, { username = it.lowercase().filter { c -> c.isLetterOrDigit() || c == '_' }.take(20) }, Modifier.fillMaxWidth(), label = { Text("Kullanıcı adı") }, leadingIcon = { Text("@") }, singleLine = true, supportingText = { Text("3–20 karakter • a-z, 0-9, _") })
        Spacer(Modifier.height(14.dp))
        Text("Avatarını seç", color = LC.Deep, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(AVATARS) { (id, icon) ->
                Card(
                    onClick = { avatar = id },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = if (avatar == id) LC.Sky else LC.SurfaceSoft),
                    modifier = Modifier.size(58.dp)
                ) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(icon, fontSize = 28.sp) } }
            }
        }
        Spacer(Modifier.height(18.dp))
        if (error != null) Text(error!!, color = Color(0xFFB3261E), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                busy = true; error = null
                kotlinx.coroutines.MainScope().launch {
                    try {
                        SupabaseClientProvider.client.postgrest.rpc("complete_onboarding", OnboardingRpc(username.trim(), displayName.trim(), avatar)).decodeSingle<ProfileGate>()
                        onComplete()
                    } catch (e: Exception) { error = when { e.message?.contains("username_taken") == true -> "Bu kullanıcı adı zaten alınmış."; else -> e.message ?: "Profil kaydedilemedi." } }
                    finally { busy = false }
                }
            },
            enabled = !busy && username.length >= 3 && displayName.trim().length >= 2,
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = LC.NaturalGreen)
        ) { Text(if (busy) "Kaydediliyor…" else "Profilimi Oluştur") }
    }
}

@Composable
private fun LazyColumnLike(content: @Composable Column.() -> Unit) {
    androidx.compose.foundation.lazy.LazyColumn(
        Modifier.fillMaxSize().padding(22.dp),
        verticalArrangement = Arrangement.Center,
        content = { item { Column(content = content) } }
    )
}
