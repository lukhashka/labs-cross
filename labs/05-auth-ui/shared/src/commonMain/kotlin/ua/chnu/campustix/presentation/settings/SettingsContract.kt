package ua.chnu.campustix.presentation.settings

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.domain.settings.ThemeMode

@Immutable
data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

sealed class SettingsAction {
    data class OnThemeSelected(val mode: ThemeMode) : SettingsAction()
    data object OnShowOnboardingClick : SettingsAction()
    data object OnBackClick : SettingsAction()
}

sealed class SettingsEvent {
    data object NavigateBack : SettingsEvent()
    data object NavigateToOnboarding : SettingsEvent()
}
