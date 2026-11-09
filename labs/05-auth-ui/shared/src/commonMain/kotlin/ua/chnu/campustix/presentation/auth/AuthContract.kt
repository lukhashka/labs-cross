package ua.chnu.campustix.presentation.auth

import androidx.compose.runtime.Immutable
import ua.chnu.campustix.domain.auth.validation.PasswordError
import ua.chnu.campustix.domain.auth.validation.UsernameError

@Immutable
data class AuthState(
    val username: String = "",
    val password: String = "",
    val usernameError: UsernameError? = null,
    val passwordError: PasswordError? = null,
    val isPasswordVisible: Boolean = false,
)

sealed class AuthAction {
    data class OnUsernameChange(val username: String) : AuthAction()
    data class OnPasswordChange(val password: String) : AuthAction()
    data object OnPasswordVisibilityToggle : AuthAction()
    data object OnSignInClick : AuthAction()
    data object OnSettingsClick : AuthAction()
}

sealed class AuthEvent {
    data object ShowCredentialsAccepted : AuthEvent()
    data object NavigateToSettings : AuthEvent()
}
