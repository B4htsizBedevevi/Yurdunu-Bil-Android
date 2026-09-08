package tr.yurdunubil.app

import androidx.compose.material3.Icon as Material3Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Compatibility wrappers for Compose Icon calls used by the native UI.
 * Both common argument orders are supported so existing call sites remain
 * source-compatible while Kotlin's named/positional argument rules are met.
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Material3Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier
) {
    Material3Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
