package ua.chnu.campustix.presentation.onboarding

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.domain.onboarding.OnboardingPage

@Immutable
data class OnboardingState(
    val pages: List<OnboardingPage> = OnboardingPage.entries,
    val currentPage: Int = 0,
) {
    val isLastPage: Boolean get() = currentPage == pages.lastIndex
}

sealed class OnboardingAction {
    data class OnPageChanged(val page: Int) : OnboardingAction()
    data object OnNextClick : OnboardingAction()
    data object OnSkipClick : OnboardingAction()
}

sealed class OnboardingEvent {
    // PagerState lives in Compose, so the ViewModel asks the UI to scroll
    data class ScrollToPage(val page: Int) : OnboardingEvent()
    data object NavigateToAuth : OnboardingEvent()
    data object NavigateToHome : OnboardingEvent()
}
