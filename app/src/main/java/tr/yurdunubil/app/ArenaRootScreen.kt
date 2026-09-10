package tr.yurdunubil.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*

@Composable
fun ArenaRootScreen(darkMode: Boolean, onExit: () -> Unit) {
    var selectedMode by remember { mutableStateOf<SharedGameMode?>(null) }
    var matchedId by remember { mutableStateOf<String?>(null) }

    BackHandler {
        when {
            matchedId != null -> matchedId = null
            selectedMode != null -> selectedMode = null
            else -> onExit()
        }
    }

    val mode = selectedMode
    val matchId = matchedId
    when {
        mode == null -> ArenaHubScreen(
            darkMode = darkMode,
            onLaunch = { selectedMode = it },
            onBack = onExit
        )
        matchId != null -> ArenaMatchScreen(
            darkMode = darkMode,
            mode = mode,
            matchId = matchId,
            onBack = { matchedId = null }
        )
        else -> OnlineArenaScreen(
            darkMode = darkMode,
            mode = mode,
            onBack = { selectedMode = null },
            onMatched = { matchedId = it }
        )
    }
}
