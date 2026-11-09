package ua.chnu.campustix.domain.onboarding

import ua.chnu.campustix.domain.settings.SettingsRepository

class ResetOnboardingUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() {
        settingsRepository.setOnboardingCompleted(false)
    }
}
