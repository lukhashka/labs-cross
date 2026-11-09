package ua.chnu.campustix.domain.settings

import kotlinx.coroutines.flow.StateFlow

class ObservePreferencesUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): StateFlow<AppPreferences> = settingsRepository.preferences
}
