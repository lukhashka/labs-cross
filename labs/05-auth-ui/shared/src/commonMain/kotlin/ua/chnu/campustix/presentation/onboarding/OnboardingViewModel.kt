package ua.chnu.campustix.presentation.onboarding

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import ua.chnu.campustix.domain.onboarding.CompleteOnboardingUseCase

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Onboarding")

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _events = Channel<OnboardingEvent>(capacity = Channel.BUFFERED)
    val events: Flow<OnboardingEvent> = _events.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnPageChanged -> {
                _state.update { it.copy(currentPage = action.page) }
                log.d { "Page shown: ${action.page + 1}/${_state.value.pages.size}" }
            }
            OnboardingAction.OnNextClick -> {
                val current = _state.value
                if (current.isLastPage) {
                    finish(skipped = false)
                } else {
                    _events.trySend(OnboardingEvent.ScrollToPage(current.currentPage + 1))
                }
            }
            OnboardingAction.OnSkipClick -> finish(skipped = true)
        }
    }

    private fun finish(skipped: Boolean) {
        val page = _state.value.currentPage + 1
        log.i { if (skipped) "Onboarding skipped on page $page" else "Onboarding completed" }
        completeOnboardingUseCase()
        _events.trySend(OnboardingEvent.NavigateToAuth)
    }
}
