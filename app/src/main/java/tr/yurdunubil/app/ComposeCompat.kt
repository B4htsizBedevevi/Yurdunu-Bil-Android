package tr.yurdunubil.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent as activitySetContent
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton as MaterialOutlinedButton
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch as coroutineLaunch

@Composable
fun rememberSaveableStateHolder(): SaveableStateHolder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()

fun ComponentActivity.setContent(content: @Composable () -> Unit) = activitySetContent { content() }

fun CoroutineScope.launch(block: suspend CoroutineScope.() -> Unit): Job = coroutineLaunch(block = block)

@Composable
fun Button(onClick: () -> Unit, modifier: Modifier, shape: Shape, colors: ButtonColors = ButtonDefaults.buttonColors(), content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    MaterialButton(onClick = onClick, modifier = modifier, shape = shape, colors = colors, content = content)
}

@Composable
fun OutlinedButton(onClick: () -> Unit, modifier: Modifier, shape: Shape, colors: ButtonColors = ButtonDefaults.outlinedButtonColors(), content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    MaterialOutlinedButton(onClick = onClick, modifier = modifier, shape = shape, colors = colors, content = content)
}
