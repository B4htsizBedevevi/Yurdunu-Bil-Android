package tr.yurdunubil.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Compact replacement used by the production V4 UI. Keeps the Android gesture
 * inset while reducing the visual navigation bar footprint.
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = NavigationBarDefaults.containerColor,
    contentColor: androidx.compose.ui.graphics.Color = NavigationBarDefaults.contentColor,
    tonalElevation: androidx.compose.ui.unit.Dp = NavigationBarDefaults.Elevation,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    androidx.compose.material3.NavigationBar(
        modifier = modifier.navigationBarsPadding().height(66.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        windowInsets = WindowInsets(0, 0, 0, 0),
        content = content
    )
}
