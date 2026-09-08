package tr.yurdunubil.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent as activitySetContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch as coroutineLaunch

@Composable
fun rememberSaveableStateHolder(): SaveableStateHolder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()

fun ComponentActivity.setContent(content: @Composable () -> Unit) = activitySetContent { content() }

fun CoroutineScope.launch(block: suspend CoroutineScope.() -> Unit): Job = coroutineLaunch(block = block)
