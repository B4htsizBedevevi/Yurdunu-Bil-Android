package tr.yurdunubil.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private val BrandBg = Color(0xFF061914)
private val BrandSurface = Color(0xFF10271F).copy(alpha = .94f)
private val BrandSurface2 = Color(0xFF17352B)
private val BrandInk = Color(0xFFF5FAF7)
private val BrandMuted = Color(0xFFABC1B8)
private val BrandAccent = Color(0xFF35E7A1)
private val BrandGold = Color(0xFFE4C968)

@Serializable
data class ProfileGate(
    val id: String,
    val display_name: String = "Öğrenci",
    val username: String? = null,
    val avatar_id: String = "atlas-01",
    @SerialName("onboarding_complete") val onboarding_completed: Boolean = false
)

@Composable
fun BrandedAuthLoading() {
    Box(Modifier.fillMaxSize()) {
        BrandBackdrop()
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandLogo(78.dp)
            Spacer(Modifier.height(16.dp))
            Text("Yurdunu Bil", color = BrandInk, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator(color = BrandAccent, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun BrandedAuthScreen(onAuthenticated: () -> Unit) {
    var createAccount by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageGood by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (busy || !email.contains("@") || password.length < 6) return
        scope.launch {
            busy = true
            message = null
            try {
                if (createAccount) {
                    SupabaseClientProvider.client.auth.signUpWith(Email) {
                        this.email = email.trim()
                        this.password = password
                    }
                    if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) {
                        onAuthenticated()
                    } else {
                        messageGood = true
                        message = "Harika! Hesabını oluşturduk. Gelen kutundaki doğrulama bağlantısına dokun, sonra giriş yap."
                    }
                } else {
                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        this.email = email.trim()
                        this.password = password
                    }
                    if (SupabaseClientProvider.client.auth.currentSessionOrNull() != null) onAuthenticated()
                    else {
                        messageGood = false
                        message = "Giriş tamamlanmadı. E-posta ya da şifreyi bir kez daha kontrol edelim."
                    }
                }
            } catch (e: Exception) {
                messageGood = false
                message = friendlyAuthMessage(e)
            } finally {
                busy = false
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        BrandBackdrop()
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { -30 })) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BrandLogo(58.dp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Yurdunu Bil", color = BrandInk, fontSize = 25.sp, fontWeight = FontWeight.Black)
                                Text("Geleceğini Bil.", color = BrandAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(BrandAccent.copy(alpha = .11f)).border(1.dp, BrandAccent.copy(alpha = .20f), RoundedCornerShape(50)).padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Text("KPSS COĞRAFYA  •  2026", color = BrandAccent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            if (createAccount) "Türkiye'yi birlikte keşfedelim." else "Hoş geldin. Hadi coğrafyayı halledelim.",
                            color = BrandInk,
                            fontSize = 28.sp,
                            lineHeight = 33.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            if (createAccount) "Kendi hesabını oluştur, ilerlemeni kaydet ve her gün biraz daha güçlen."
                            else "Kaldığın yerden devam et, yanlışlarını kapat ve KPSS netlerini adım adım yükselt.",
                            color = BrandMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { BrandFeature(Icons.Default.Map, "Atlas") }
                    item { BrandFeature(Icons.Default.MenuBook, "Kütüphane") }
                    item { BrandFeature(Icons.Default.Quiz, "Test") }
                    item { BrandFeature(Icons.Default.EmojiEvents, "Arena") }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp)).background(BrandSurface).border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(30.dp)).padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = .045f)).padding(4.dp)
                    ) {
                        BrandTab("Giriş yap", selected = !createAccount, Modifier.weight(1f)) {
                            createAccount = false
                            message = null
                        }
                        BrandTab("Hesap oluştur", selected = createAccount, Modifier.weight(1f)) {
                            createAccount = true
                            message = null
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    Text(if (createAccount) "Yeni bir başlangıç yap" else "Kaldığın yerden devam et", color = BrandInk, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(if (createAccount) "Sadece e-posta ve güçlü bir şifre yeterli." else "Hesabına gir, ilerlemen kaldığı yerden devam etsin.", color = BrandMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trimStart(); message = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("E-posta adresin") },
                        placeholder = { Text("ornek@mail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = brandFieldColors()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; message = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Şifre") },
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
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        colors = brandFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))
                    AnimatedContent(targetState = message, label = "friendly-auth-message") { text ->
                        if (text == null) {
                            Spacer(Modifier.height(2.dp))
                        } else {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.CheckCircle, null, tint = if (messageGood) BrandAccent else Color(0xFFFFA59C), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(7.dp))
                                Text(text, color = if (messageGood) Color(0xFFBDF7DF) else Color(0xFFFFC1BA), fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(7.dp))
                    Button(
                        onClick = { submit() },
                        enabled = !busy && email.contains("@") && password.length >= 6,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent, contentColor = Color(0xFF06251B), disabledContainerColor = Color(0xFF29473D), disabledContentColor = Color(0xFF779188))
                    ) {
                        if (busy) CircularProgressIndicator(color = Color(0xFF06251B), strokeWidth = 2.dp, modifier = Modifier.size(21.dp))
                        else {
                            Text(if (createAccount) "Hesabımı oluştur" else "Devam et", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (createAccount) "E-posta doğrulaması istenebilir. Bu, hesabını güvende tutmak için."
                        else "Bilgilerin cihazında değil, güvenli hesabında kullanılır.",
                        color = BrandMuted,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Öğren", color = BrandMuted, fontSize = 11.sp)
                    Text("  •  ", color = BrandAccent.copy(alpha = .65f), fontSize = 11.sp)
                    Text("Çöz", color = BrandMuted, fontSize = 11.sp)
                    Text("  •  ", color = BrandAccent.copy(alpha = .65f), fontSize = 11.sp)
                    Text("Geliş", color = BrandMuted, fontSize = 11.sp)
                    Text("  •  ", color = BrandAccent.copy(alpha = .65f), fontSize = 11.sp)
                    Text("Kazan", color = BrandAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun friendlyAuthMessage(error: Throwable): String {
    val raw = (error.message ?: "").lowercase()
    return when {
        "invalid_credentials" in raw || "invalid login credentials" in raw -> "E-posta ya da şifre uyuşmuyor. Bir kez daha deneyelim."
        "email not confirmed" in raw -> "E-postanı doğruladıktan sonra buraya dönelim."
        "user already registered" in raw || "already registered" in raw -> "Bu e-posta zaten kayıtlı. Giriş yapmayı deneyelim."
        "too many" in raw || "rate limit" in raw -> "Biraz yavaşlayalım 🙂 Çok fazla deneme yapıldı. Birkaç dakika sonra tekrar dene."
        "network" in raw || "timeout" in raw || "unable to resolve host" in raw -> "Bağlantıyı kuramadım. İnternetini kontrol edip tekrar deneyelim."
        else -> "Bir şey ters gitti. Merak etme, tekrar deneyebiliriz."
    }
}

@Composable
private fun BrandFeature(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = .055f)).border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(50)).padding(horizontal = 11.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, color = BrandInk, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun BrandTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(15.dp)).background(if (selected) BrandSurface2 else Color.Transparent).clickable(onClick = onClick).padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) BrandInk else BrandMuted, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun brandFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BrandAccent,
    unfocusedBorderColor = Color.White.copy(alpha = .12f),
    focusedLabelColor = BrandAccent,
    unfocusedLabelColor = BrandMuted,
    cursorColor = BrandAccent,
    focusedTextColor = BrandInk,
    unfocusedTextColor = BrandInk,
    focusedLeadingIconColor = BrandAccent,
    unfocusedLeadingIconColor = BrandMuted,
    focusedTrailingIconColor = BrandAccent,
    unfocusedTrailingIconColor = BrandMuted,
)

@Composable
private fun BrandLogo(size: androidx.compose.ui.unit.Dp) {
    androidx.compose.foundation.Image(
        painter = painterResource(id = R.drawable.ic_yurdunu_bil),
        contentDescription = "Yurdunu Bil",
        modifier = Modifier.size(size).clip(CircleShape)
    )
}

@Composable
private fun BrandBackdrop() {
    Canvas(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandBg, Color(0xFF0A2B22), Color(0xFF04110E))))
    ) {
        val w = size.width
        val h = size.height
        drawCircle(BrandAccent.copy(alpha = .08f), w * .62f, androidx.compose.ui.geometry.Offset(w * .92f, h * .04f))
        drawCircle(Color(0xFF2F8FA0).copy(alpha = .07f), w * .55f, androidx.compose.ui.geometry.Offset(w * .02f, h * .40f))

        val contour = listOf(.22f, .29f, .36f, .43f, .50f, .57f)
        contour.forEachIndexed { index, yRatio ->
            val path = Path().apply {
                moveTo(-20f, h * yRatio)
                cubicTo(w * .18f, h * (yRatio - .07f), w * .31f, h * (yRatio + .06f), w * .49f, h * yRatio)
                cubicTo(w * .67f, h * (yRatio - .06f), w * .82f, h * (yRatio + .06f), w + 20f, h * yRatio)
            }
            drawPath(path, BrandAccent.copy(alpha = .045f + index * .006f), style = Stroke(width = 2f, cap = StrokeCap.Round))
        }

        val region = Path().apply {
            moveTo(w * .10f, h * .22f)
            lineTo(w * .26f, h * .17f)
            lineTo(w * .40f, h * .20f)
            lineTo(w * .53f, h * .14f)
            lineTo(w * .69f, h * .20f)
            lineTo(w * .84f, h * .16f)
            lineTo(w * .91f, h * .29f)
            lineTo(w * .76f, h * .35f)
            lineTo(w * .60f, h * .31f)
            lineTo(w * .47f, h * .37f)
            lineTo(w * .31f, h * .33f)
            lineTo(w * .17f, h * .37f)
            close()
        }
        drawPath(region, BrandAccent.copy(alpha = .035f))
        drawPath(region, BrandAccent.copy(alpha = .16f), style = Stroke(width = 2f))

        listOf(.15f to .24f, .30f to .28f, .48f to .22f, .67f to .27f, .83f to .23f).forEach { (x, y) ->
            drawCircle(BrandGold.copy(alpha = .30f), 3.5f, androidx.compose.ui.geometry.Offset(w * x, h * y))
        }
    }
}

private suspend fun loadAuthProfile(): ProfileGate? = withContext(Dispatchers.IO) {
    runCatching {
        val user = SupabaseClientProvider.client.auth.currentSessionOrNull()?.user ?: return@runCatching null
        SupabaseClientProvider.client.postgrest["profiles"].select {
            filter { eq("id", user.id) }
        }.decodeList<ProfileGate>().firstOrNull()
    }.getOrNull()
}

private val BRANDED_AVATARS = listOf(
    "atlas-01" to "🧭", "atlas-02" to "🌍", "atlas-03" to "🏔️", "atlas-04" to "🌊",
    "atlas-05" to "🌲", "atlas-06" to "☀️", "atlas-07" to "🗺️", "atlas-08" to "🦅"
)

@Composable
fun BrandedProfileOnboarding(existing: ProfileGate?, onComplete: () -> Unit) {
    var displayName by remember { mutableStateOf(existing?.display_name.orEmpty()) }
    var username by remember { mutableStateOf(existing?.username.orEmpty()) }
    var avatar by remember { mutableStateOf(existing?.avatar_id ?: BRANDED_AVATARS.first().first) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        BrandBackdrop()
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BrandLogo(64.dp)
                Spacer(Modifier.height(16.dp))
                Text("Şimdi seni tanıyalım 👋", color = BrandInk, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("Arena'da kullanacağın adı seç. Gerisini birlikte hallederiz.", color = BrandMuted, fontSize = 13.sp)
            }
            item {
                OutlinedTextField(displayName, { displayName = it.take(40) }, Modifier.fillMaxWidth(), label = { Text("Görünen adın") }, singleLine = true, colors = brandFieldColors())
            }
            item {
                OutlinedTextField(username, { username = it.lowercase().filter { c -> c in 'a'..'z' || c in '0'..'9' || c == '_' }.take(20) }, Modifier.fillMaxWidth(), label = { Text("Kullanıcı adın") }, prefix = { Text("@", color = BrandAccent) }, singleLine = true, supportingText = { Text("3–20 karakter • a-z, 0-9, _") }, colors = brandFieldColors())
            }
            item { Text("Avatarını seç", color = BrandInk, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(BRANDED_AVATARS) { (id, icon) ->
                        Box(
                            Modifier.size(56.dp).clip(CircleShape).background(if (avatar == id) BrandAccent.copy(alpha = .20f) else Color.White.copy(alpha = .07f)).border(1.dp, if (avatar == id) BrandAccent else Color.White.copy(alpha = .10f), CircleShape).clickable { avatar = id },
                            contentAlignment = Alignment.Center
                        ) { androidx.compose.material3.Text(icon, fontSize = 27.sp) }
                    }
                }
            }
            item {
                if (error != null) Text(error!!, color = Color(0xFFFFB5AD), fontSize = 12.sp)
                Button(
                    onClick = {
                        busy = true
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
                                error = if (e.message?.contains("username_taken") == true) "Bu kullanıcı adı alınmış. Başka bir tane seçelim." else "Profilini kaydederken takıldık. Bir kez daha deneyelim."
                            } finally { busy = false }
                        }
                    },
                    enabled = !busy && username.length >= 3 && displayName.trim().length >= 2,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent, contentColor = Color(0xFF06251B))
                ) { Text(if (busy) "Kaydediyoruz…" else "Devam edelim", fontWeight = FontWeight.ExtraBold) }
            }
        }
    }
}
