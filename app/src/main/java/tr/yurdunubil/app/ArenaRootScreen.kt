package tr.yurdunubil.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*

@Composable
fun ArenaRootScreen(darkMode: Boolean, onExit: () -> Unit) {
    var selectedMode by remember { mutableStateOf<SharedGameMode?>(null) }

    BackHandler {
        if (selectedMode != null) selectedMode = null else onExit()
    }

    val mode = selectedMode
    if (mode == null) {
        ArenaHubScreen(darkMode = darkMode, onLaunch = { selectedMode = it })
    } else {
        OnlineArenaScreen(
            darkMode = darkMode,
            mode = mode,
            onBack = { selectedMode = null },
            onMatched = { }
        )
    }
}
