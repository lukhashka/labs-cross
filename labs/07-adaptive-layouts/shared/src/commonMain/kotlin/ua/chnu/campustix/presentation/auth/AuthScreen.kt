package ua.chnu.campustix.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.domain.auth.validation.PasswordError
import ua.chnu.campustix.domain.auth.validation.UsernameError
import ua.chnu.campustix.presentation.ui.ObserveEvents
import ua.chnu.campustix.presentation.ui.ScreenLayout
import ua.chnu.campustix.presentation.ui.currentScreenLayout
import ua.chnu.campustix.presentation.ui.isWindowHeightAtLeastMedium
import ua.chnu.campustix.presentation.ui.messageRes
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.app_name
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
import ua.chnu.campustix.resources.ic_onboarding_events
import ua.chnu.campustix.resources.ic_onboarding_qr
import ua.chnu.campustix.resources.ic_onboarding_ticket
import ua.chnu.campustix.resources.onboarding_events_title
import ua.chnu.campustix.resources.onboarding_qr_title
import ua.chnu.campustix.resources.onboarding_ticket_title
import ua.chnu.campustix.resources.settings_title

private val FormMaxWidth = 420.dp
private const val BrandPaneWeight = 0.45f

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

    // The screen handles window insets itself: in two panes the left pane color
    // has to go under the status bar, so Scaffold must not add its own padding
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (currentScreenLayout()) {
            ScreenLayout.SinglePane -> AuthSinglePane(state, viewModel::onAction, modifier)
            ScreenLayout.TwoPane -> AuthTwoPane(state, viewModel::onAction, modifier)
        }
    }
}

// Phone in portrait: logo, title and form in one scrolling column
@Composable
private fun AuthSinglePane(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // safeDrawing includes the keyboard, so the column shrinks when it opens
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = FormMaxWidth).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(32.dp))
            Logo(size = 96.dp)
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
            AuthForm(state, onAction)
        }
    }
}

// Wide window: brand pane on the left, form on the right
@Composable
private fun AuthTwoPane(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize()) {
        BrandPane(
            modifier = Modifier
                .weight(BrandPaneWeight)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)),
        )
        Box(
            modifier = Modifier
                .weight(1f - BrandPaneWeight)
                .fillMaxHeight()
                // the right pane also gets the keyboard inset (it is part of safeDrawing.Bottom)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End + WindowInsetsSides.Vertical))
                .verticalScroll(rememberScrollState())
                .padding(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.widthIn(max = FormMaxWidth).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(Res.string.auth_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(8.dp))
                AuthForm(state, onAction)
            }
        }
    }
}

// Left pane of the wide layout: what the app is about
@Composable
private fun BrandPane(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Logo(size = 72.dp, background = MaterialTheme.colorScheme.background)
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = stringResource(Res.string.auth_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.widthIn(max = 360.dp),
        )
        // A phone in landscape is wide but low: the feature list is shown only when there is height for it
        if (isWindowHeightAtLeastMedium()) {
            Spacer(Modifier.height(8.dp))
            Feature(Res.drawable.ic_onboarding_events, Res.string.onboarding_events_title)
            Feature(Res.drawable.ic_onboarding_ticket, Res.string.onboarding_ticket_title)
            Feature(Res.drawable.ic_onboarding_qr, Res.string.onboarding_qr_title)
        }
    }
}

@Composable
private fun Feature(icon: DrawableResource, title: StringResource) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun Logo(size: Dp, background: Color = MaterialTheme.colorScheme.primaryContainer) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_onboarding_ticket),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxSize(0.54f),
        )
    }
}

// The same fields and buttons in both layouts: the logic is written once
@Composable
private fun AuthForm(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        TextButton(
            onClick = { onAction(AuthAction.OnSettingsClick) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(stringResource(Res.string.settings_title))
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
