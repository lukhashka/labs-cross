package ua.chnu.campustix.domain.settings

class ChangeThemeUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(mode: ThemeMode) {
        settingsRepository.setThemeMode(mode)
    }
}
