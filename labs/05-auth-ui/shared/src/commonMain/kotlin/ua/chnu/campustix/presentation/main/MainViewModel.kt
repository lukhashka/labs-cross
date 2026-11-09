package ua.chnu.campustix.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ua.chnu.campustix.domain.onboarding.CheckOnboardingCompletedUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase
import ua.chnu.campustix.presentation.navigation.Route

// Root of the app: chooses the first screen and the theme.
class MainViewModel(
    checkOnboardingCompletedUseCase: CheckOnboardingCompletedUseCase,
    observePreferencesUseCase: ObservePreferencesUseCase,
) : ViewModel() {

    // The start route is calculated once, later moves are made by the back stack
    private val startRoute: Route =
        if (checkOnboardingCompletedUseCase()) Route.Auth else Route.Onboarding

    val state: StateFlow<MainState> = observePreferencesUseCase()
        .map { MainState(startRoute = startRoute, themeMode = it.themeMode) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainState(
                startRoute = startRoute,
                themeMode = observePreferencesUseCase().value.themeMode,
            ),
        )

    init {
        Logger.withTag("Main").i { "Start route: $startRoute" }
    }
}
