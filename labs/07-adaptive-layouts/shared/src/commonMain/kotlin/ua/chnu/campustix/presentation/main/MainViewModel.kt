package ua.chnu.campustix.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.chnu.campustix.domain.auth.CheckAuthorizedUseCase
import ua.chnu.campustix.domain.auth.ObserveSessionExpiredUseCase
import ua.chnu.campustix.domain.onboarding.CheckOnboardingCompletedUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase
import ua.chnu.campustix.presentation.navigation.Route

// Root of the app: chooses the first screen and the theme, reacts to an expired session.
class MainViewModel(
    private val checkOnboardingCompletedUseCase: CheckOnboardingCompletedUseCase,
    private val checkAuthorizedUseCase: CheckAuthorizedUseCase,
    observePreferencesUseCase: ObservePreferencesUseCase,
    observeSessionExpiredUseCase: ObserveSessionExpiredUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Main")
    private val startRoute = MutableStateFlow<Route?>(null)

    val state: StateFlow<MainState> = combine(startRoute, observePreferencesUseCase()) { route, preferences ->
        MainState(startRoute = route, themeMode = preferences.themeMode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainState(themeMode = observePreferencesUseCase().value.themeMode),
    )

    private val _events = Channel<MainEvent>(capacity = Channel.BUFFERED)
    val events: Flow<MainEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            startRoute.value = chooseStartRoute()
            log.i { "Start route: ${startRoute.value}" }
        }
        viewModelScope.launch {
            observeSessionExpiredUseCase().collect {
                log.w { "Session expired, back to sign in" }
                _events.send(MainEvent.NavigateToAuth)
            }
        }
    }

    private suspend fun chooseStartRoute(): Route = when {
        !checkOnboardingCompletedUseCase() -> Route.Onboarding
        checkAuthorizedUseCase() -> Route.Home
        else -> Route.Auth
    }
}
