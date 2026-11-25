package ua.chnu.campustix.domain.onboarding

import ua.chnu.campustix.domain.settings.SettingsRepository

// Used on app start to choose the first screen.
class CheckOnboardingCompletedUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Boolean = settingsRepository.preferences.value.onboardingCompleted
}
