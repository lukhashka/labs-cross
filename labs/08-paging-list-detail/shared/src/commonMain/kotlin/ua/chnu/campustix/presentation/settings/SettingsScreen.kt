package ua.chnu.campustix.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.domain.settings.ThemeMode
import ua.chnu.campustix.presentation.ui.ObserveEvents
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.settings_back
import ua.chnu.campustix.resources.settings_language_hint
import ua.chnu.campustix.resources.settings_show_onboarding
import ua.chnu.campustix.resources.settings_theme
import ua.chnu.campustix.resources.settings_title
import ua.chnu.campustix.resources.theme_dark
import ua.chnu.campustix.resources.theme_light
import ua.chnu.campustix.resources.theme_system

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onShowOnboarding: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.NavigateBack -> onBack()
            SettingsEvent.NavigateToOnboarding -> onShowOnboarding()
        }
    }

    SettingsContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun SettingsContent(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
            TextButton(onClick = { onAction(SettingsAction.OnBackClick) }) {
                Text(stringResource(Res.string.settings_back))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.settings_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(24.dp))
            Text(stringResource(Res.string.settings_theme), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.themeMode == mode,
                        onClick = { onAction(SettingsAction.OnThemeSelected(mode)) },
                        label = { Text(stringResource(mode.titleRes())) },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.settings_language_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = { onAction(SettingsAction.OnShowOnboardingClick) }) {
                Text(stringResource(Res.string.settings_show_onboarding))
            }
        }
    }
}

private fun ThemeMode.titleRes() = when (this) {
    ThemeMode.SYSTEM -> Res.string.theme_system
    ThemeMode.LIGHT -> Res.string.theme_light
    ThemeMode.DARK -> Res.string.theme_dark
}
