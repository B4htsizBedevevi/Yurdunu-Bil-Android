package tr.yurdunubil.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent as activitySetContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch as coroutineLaunch

@Composable
fun rememberSaveableStateHolder(): SaveableStateHolder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()

fun ComponentActivity.setContent(content: @Composable () -> Unit) = activitySetContent { content() }

fun CoroutineScope.launch(block: suspend CoroutineScope.() -> Unit): Job = coroutineLaunch(block = block)

/** Compatibility overload for the legacy event card that supplied lineHeight as Dp. */
@Composable
fun Text(
    text: String,
    color: Color,
    fontSize: TextUnit,
    lineHeight: Dp,
    fontWeight: FontWeight? = null
) {
    androidx.compose.material3.Text(
        text = text,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight.value.sp,
        fontWeight = fontWeight
    )
}
