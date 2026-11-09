package ua.chnu.campustix.presentation.auth

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import ua.chnu.campustix.domain.auth.validation.ValidatePasswordUseCase
import ua.chnu.campustix.domain.auth.validation.ValidateUsernameUseCase

class AuthViewModel(
    private val validateUsernameUseCase: ValidateUsernameUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Auth")

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _events = Channel<AuthEvent>(capacity = Channel.BUFFERED)
    val events: Flow<AuthEvent> = _events.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            // The error of a field disappears as soon as the user starts fixing it
            is AuthAction.OnUsernameChange ->
                _state.update { it.copy(username = action.username, usernameError = null) }
            is AuthAction.OnPasswordChange ->
                _state.update { it.copy(password = action.password, passwordError = null) }
            AuthAction.OnPasswordVisibilityToggle ->
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            AuthAction.OnSignInClick -> signIn()
            AuthAction.OnSettingsClick -> _events.trySend(AuthEvent.NavigateToSettings)
        }
    }

    private fun signIn() {
        val current = _state.value
        val usernameError = validateUsernameUseCase(current.username)
        val passwordError = validatePasswordUseCase(current.password)
        _state.update { it.copy(usernameError = usernameError, passwordError = passwordError) }

        if (usernameError != null || passwordError != null) {
            log.d { "Validation failed: username=$usernameError, password=$passwordError" }
            return
        }
        // Network request is added in the next lab
        log.i { "Credentials passed validation" }
        _events.trySend(AuthEvent.ShowCredentialsAccepted)
    }
}
