package ua.chnu.campustix.presentation.home

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.domain.settings.ThemeMode

@Immutable
data class HomeState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

sealed class HomeAction {
    data class OnThemeSelected(val mode: ThemeMode) : HomeAction()
    data object OnShowOnboardingClick : HomeAction()
}
