package ua.chnu.campustix.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.presentation.ui.ObserveEvents
import ua.chnu.campustix.presentation.ui.messageRes
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.home_greeting
import ua.chnu.campustix.resources.home_logout
import ua.chnu.campustix.resources.home_title
import ua.chnu.campustix.resources.settings_title

@Composable
fun HomeScreen(
    onNavigateToAuth: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(getString(event.error.messageRes()))
            }
            HomeEvent.NavigateToSettings -> onOpenSettings()
            HomeEvent.NavigateToAuth -> onNavigateToAuth()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        HomeContent(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.home_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.user?.let { user ->
                Text(
                    text = stringResource(Res.string.home_greeting, user.fullName),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            OutlinedButton(onClick = { onAction(HomeAction.OnLogoutClick) }) {
                Text(stringResource(Res.string.home_logout))
            }
            TextButton(onClick = { onAction(HomeAction.OnSettingsClick) }) {
                Text(stringResource(Res.string.settings_title))
            }
        }
    }
}
