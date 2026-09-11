package tr.yurdunubil.app

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** Always-visible shortcut to the personal study dashboard. */
@Composable
fun SmartStudyFloatingEntry() {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, bottom = 82.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        FloatingActionButton(
            onClick = { context.startActivity(Intent(context, SmartStudyCenterActivity::class.java)) },
            containerColor = Color(0xFFFFC857),
            contentColor = Color(0xFF162118)
        ) {
            Icon(Icons.Default.Psychology, contentDescription = "Akıllı çalışma merkezi")
        }
    }
}
