package tr.yurdunubil.app

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun rememberSaveableStateHolder(): SaveableStateHolder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()

@Composable
fun Button(onClick: () -> Unit, modifier: Modifier, shape: Shape, colors: ButtonColors = ButtonDefaults.buttonColors(), content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    MaterialButton(onClick = onClick, modifier = modifier, shape = shape, colors = colors, content = content)
}

@Composable
fun OutlinedButton(onClick: () -> Unit, modifier: Modifier, shape: Shape, colors: ButtonColors = ButtonDefaults.outlinedButtonColors(), content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = shape, colors = colors, content = content)
}
