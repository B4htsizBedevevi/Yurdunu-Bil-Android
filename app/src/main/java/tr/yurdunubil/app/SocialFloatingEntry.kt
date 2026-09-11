package tr.yurdunubil.app

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SocialFloatingEntry() {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize().padding(end = 16.dp, bottom = 82.dp), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = { context.startActivity(Intent(context, SocialModernActivity::class.java)) },
            containerColor = Color(0xFF28DE98),
            contentColor = Color(0xFF06221B)
        ) { Icon(Icons.Default.People, contentDescription = "Sosyal ve arkadaşlar") }
    }
}
