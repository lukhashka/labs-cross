package ua.chnu.campustix.presentation.home

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.domain.user.User

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val user: User? = null,
)

sealed class HomeAction {
    data object OnSettingsClick : HomeAction()
    data object OnLogoutClick : HomeAction()
}

sealed class HomeEvent {
    data class ShowError(val error: DataError) : HomeEvent()
    data object NavigateToSettings : HomeEvent()
    data object NavigateToAuth : HomeEvent()
}
