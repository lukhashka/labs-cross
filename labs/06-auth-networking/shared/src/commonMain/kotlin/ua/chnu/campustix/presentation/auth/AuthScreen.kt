package ua.chnu.campustix.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.domain.auth.validation.PasswordError
import ua.chnu.campustix.domain.auth.validation.UsernameError
import ua.chnu.campustix.presentation.ui.ObserveEvents
import ua.chnu.campustix.presentation.ui.messageRes
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.auth_error_password_empty
import ua.chnu.campustix.resources.auth_error_password_short
import ua.chnu.campustix.resources.auth_error_username_empty
import ua.chnu.campustix.resources.auth_error_username_invalid
import ua.chnu.campustix.resources.auth_error_username_short
import ua.chnu.campustix.resources.auth_hide_password
import ua.chnu.campustix.resources.auth_password
import ua.chnu.campustix.resources.auth_show_password
import ua.chnu.campustix.resources.auth_sign_in
import ua.chnu.campustix.resources.auth_subtitle
import ua.chnu.campustix.resources.auth_title
import ua.chnu.campustix.resources.auth_username
import ua.chnu.campustix.resources.ic_onboarding_ticket
import ua.chnu.campustix.resources.settings_title

@Composable
fun AuthScreen(
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(getString(event.error.messageRes()))
            }
            AuthEvent.NavigateToHome -> onNavigateToHome()
            AuthEvent.NavigateToSettings -> onOpenSettings()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        AuthContent(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun AuthContent(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_onboarding_ticket),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(52.dp),
                )
            }
            Text(
                text = stringResource(Res.string.auth_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.auth_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = { onAction(AuthAction.OnUsernameChange(it)) },
                label = { Text(stringResource(Res.string.auth_username)) },
                isError = state.usernameError != null,
                supportingText = state.usernameError?.let { error -> { Text(stringResource(error.messageRes())) } },
                singleLine = true,
                // "Next" on the keyboard moves the cursor to the password field
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { onAction(AuthAction.OnPasswordChange(it)) },
                label = { Text(stringResource(Res.string.auth_password)) },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { error -> { Text(stringResource(error.messageRes())) } },
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { onAction(AuthAction.OnPasswordVisibilityToggle) }) {
                        Text(
                            stringResource(
                                if (state.isPasswordVisible) Res.string.auth_hide_password else Res.string.auth_show_password
                            )
                        )
                    }
                },
                // "Done" on the keyboard is the same action as the Sign in button
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboard?.hide()
                    onAction(AuthAction.OnSignInClick)
                }),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { onAction(AuthAction.OnSignInClick) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(Res.string.auth_sign_in))
                }
            }
            TextButton(onClick = { onAction(AuthAction.OnSettingsClick) }) {
                Text(stringResource(Res.string.settings_title))
            }
        }
    }
}

private fun UsernameError.messageRes(): StringResource = when (this) {
    UsernameError.EMPTY -> Res.string.auth_error_username_empty
    UsernameError.TOO_SHORT -> Res.string.auth_error_username_short
    UsernameError.INVALID_CHARACTERS -> Res.string.auth_error_username_invalid
}

private fun PasswordError.messageRes(): StringResource = when (this) {
    PasswordError.EMPTY -> Res.string.auth_error_password_empty
    PasswordError.TOO_SHORT -> Res.string.auth_error_password_short
}
