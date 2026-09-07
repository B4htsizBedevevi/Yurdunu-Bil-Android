package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Compatibility overload for the retained library screen call site. */
@Composable
fun RetentionCard(
    RCmod: Modifier,
    accent: Color,
    dark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = RCmod, colors = CardDefaults.cardColors(if (dark) YurdunuBilColors.Deep else YurdunuBilColors.Surface)) {
        Row {
            androidx.compose.foundation.layout.Box(Modifier.width(4.dp).background(accent))
            Column(content = content)
        }
    }
}
