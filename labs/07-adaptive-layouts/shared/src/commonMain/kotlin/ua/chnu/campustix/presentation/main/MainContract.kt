package ua.chnu.campustix.presentation.main

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.domain.settings.ThemeMode
import ua.chnu.campustix.presentation.navigation.Route

@Immutable
data class MainState(
    // null while the secure storage is being read
    val startRoute: Route? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

sealed class MainEvent {
    data object NavigateToAuth : MainEvent()
}
