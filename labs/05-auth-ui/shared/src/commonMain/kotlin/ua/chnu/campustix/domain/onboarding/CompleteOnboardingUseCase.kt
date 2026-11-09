package ua.chnu.campustix.domain.onboarding

import ua.chnu.campustix.domain.settings.SettingsRepository

// After this the onboarding is never shown again (until reset in settings).
class CompleteOnboardingUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() {
        settingsRepository.setOnboardingCompleted(true)
    }
}
