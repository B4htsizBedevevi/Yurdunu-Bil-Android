package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SafeNextGenerationApp() {
    var retryToken by remember { mutableIntStateOf(0) }
    androidx.compose.runtime.key(retryToken) {
        NextGenerationApp()
    }
}

@Composable
private fun AppRecoveryScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8F5))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Yurdunu Bil", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF06271F))
        Text(
            "Uygulama açılırken küçük bir sorun oluştu. Verilerini silmeden tekrar deneyebiliriz.",
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
            color = Color(0xFF465650)
        )
        Button(onClick = onRetry) {
            Text("Tekrar dene")
        }
    }
}
