package ua.chnu.campustix.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import ua.chnu.campustix.domain.onboarding.ResetOnboardingUseCase
import ua.chnu.campustix.domain.settings.ChangeThemeUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase

class SettingsViewModel(
    observePreferencesUseCase: ObservePreferencesUseCase,
    private val changeThemeUseCase: ChangeThemeUseCase,
    private val resetOnboardingUseCase: ResetOnboardingUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Settings")

    // State is derived from saved preferences
    val state: StateFlow<SettingsState> = observePreferencesUseCase()
        .map { SettingsState(themeMode = it.themeMode) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsState(themeMode = observePreferencesUseCase().value.themeMode),
        )

    private val _events = Channel<SettingsEvent>(capacity = Channel.BUFFERED)
    val events: Flow<SettingsEvent> = _events.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnThemeSelected -> {
                log.i { "Theme selected: ${action.mode}" }
                changeThemeUseCase(action.mode)
            }
            SettingsAction.OnShowOnboardingClick -> {
                log.i { "Onboarding requested again" }
                resetOnboardingUseCase()
                _events.trySend(SettingsEvent.NavigateToOnboarding)
            }
            SettingsAction.OnBackClick -> _events.trySend(SettingsEvent.NavigateBack)
        }
    }
}
