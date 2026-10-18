package ua.chnu.campustix.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ua.chnu.campustix.domain.onboarding.ResetOnboardingUseCase
import ua.chnu.campustix.domain.settings.ChangeThemeUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase

class HomeViewModel(
    observePreferencesUseCase: ObservePreferencesUseCase,
    private val changeThemeUseCase: ChangeThemeUseCase,
    private val resetOnboardingUseCase: ResetOnboardingUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Home")

    // State is derived from saved preferences
    val state: StateFlow<HomeState> = observePreferencesUseCase()
        .map { HomeState(themeMode = it.themeMode) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeState(themeMode = observePreferencesUseCase().value.themeMode),
        )

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnThemeSelected -> {
                log.i { "Theme selected: ${action.mode}" }
                changeThemeUseCase(action.mode)
            }
            HomeAction.OnShowOnboardingClick -> {
                log.i { "Onboarding requested again" }
                resetOnboardingUseCase()
            }
        }
    }
}
