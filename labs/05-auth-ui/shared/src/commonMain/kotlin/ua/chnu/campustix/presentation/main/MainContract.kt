package ua.chnu.campustix.presentation.main

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.domain.settings.ThemeMode
import ua.chnu.campustix.presentation.navigation.Route

@Immutable
data class MainState(
    val startRoute: Route,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
