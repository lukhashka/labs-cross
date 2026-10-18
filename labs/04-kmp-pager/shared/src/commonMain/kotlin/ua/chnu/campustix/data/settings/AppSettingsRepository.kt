package ua.chnu.campustix.data.settings

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ua.chnu.campustix.domain.settings.AppPreferences
import ua.chnu.campustix.domain.settings.SettingsRepository
import ua.chnu.campustix.domain.settings.ThemeMode

// multiplatform-settings: SharedPreferences on Android,
// java.util.prefs on Desktop, localStorage in the browser.
class AppSettingsRepository(
    private val settings: Settings
) : SettingsRepository {

    private val log = Logger.withTag("Settings")

    private val _preferences = MutableStateFlow(readPreferences())
    override val preferences: StateFlow<AppPreferences> = _preferences.asStateFlow()

    init {
        log.i { "Loaded preferences: ${_preferences.value}" }
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, completed)
        _preferences.update { it.copy(onboardingCompleted = completed) }
        log.i { "onboardingCompleted = $completed" }
    }

    override fun setThemeMode(mode: ThemeMode) {
        settings.putString(KEY_THEME_MODE, mode.name)
        _preferences.update { it.copy(themeMode = mode) }
        log.i { "themeMode = $mode" }
    }

    private fun readPreferences(): AppPreferences {
        val themeName = settings.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return AppPreferences(
            onboardingCompleted = settings.getBoolean(KEY_ONBOARDING_COMPLETED, false),
            // unknown value (e.g. after renaming enum) falls back to SYSTEM
            themeMode = ThemeMode.entries.firstOrNull { it.name == themeName } ?: ThemeMode.SYSTEM,
        )
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
