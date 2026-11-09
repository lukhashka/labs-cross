package ua.chnu.campustix.domain.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val preferences: StateFlow<AppPreferences>
    fun setOnboardingCompleted(completed: Boolean)
    fun setThemeMode(mode: ThemeMode)
}
