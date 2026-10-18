package ua.chnu.campustix.presentation.root

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import ua.chnu.campustix.domain.settings.AppPreferences
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase

// Decides which screen to show (onboarding or home) and which theme to use.
class RootViewModel(
    observePreferencesUseCase: ObservePreferencesUseCase,
) : ViewModel() {
    val state: StateFlow<AppPreferences> = observePreferencesUseCase()
}
