package tr.yurdunubil.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ArenaRootScreen(darkMode: Boolean, onExit: () -> Unit) {
    var selectedMode by remember { mutableStateOf<SharedGameMode?>(null) }
    var matchedId by remember { mutableStateOf<String?>(null) }

    // Keep exactly one enabled back handler for each nested Arena state.
    BackHandler(enabled = matchedId != null) {
        matchedId = null
    }
    BackHandler(enabled = matchedId == null && selectedMode != null) {
        selectedMode = null
    }
    BackHandler(enabled = matchedId == null && selectedMode == null) {
        onExit()
    }

    val mode = selectedMode
    val matchId = matchedId
    Box(
        Modifier
            .fillMaxSize()
            .background(if (darkMode) YBColors.DarkBg else YBColors.LightBg)
    ) {
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
}
