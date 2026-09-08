package tr.yurdunubil.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder

@Composable
fun rememberSaveableStateHolder(): SaveableStateHolder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()
