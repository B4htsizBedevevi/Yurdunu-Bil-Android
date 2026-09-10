package tr.yurdunubil.app

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip as materialClip
import androidx.compose.ui.graphics.Shape

/** Small compatibility extension for auth UI files that intentionally keep imports minimal. */
fun Modifier.clip(shape: Shape): Modifier = this.materialClip(shape)
