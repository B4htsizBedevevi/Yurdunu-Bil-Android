package tr.yurdunubil.app

import androidx.compose.ui.Modifier

/**
 * Compatibility overload for legacy helper composables that call weight() outside
 * a ColumnScope receiver. Inside Row/Column scopes Compose's real weight extension
 * still wins; this fallback is intentionally a no-op for standalone helpers.
 */
@Suppress("UNUSED_PARAMETER")
fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier = this
