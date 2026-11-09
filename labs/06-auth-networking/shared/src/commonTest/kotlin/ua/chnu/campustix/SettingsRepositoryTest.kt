package ua.chnu.campustix

import com.russhwolf.settings.MapSettings
import ua.chnu.campustix.data.settings.AppSettingsRepository
import ua.chnu.campustix.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.campustix.domain.onboarding.ResetOnboardingUseCase
import ua.chnu.campustix.domain.settings.ChangeThemeUseCase
import ua.chnu.campustix.domain.settings.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// MapSettings keeps values in memory, so we can test without a device.
class SettingsRepositoryTest {

    @Test
    fun onboardingIsNotCompletedOnFirstLaunch() {
        val repository = AppSettingsRepository(MapSettings())

        assertFalse(repository.preferences.value.onboardingCompleted)
        assertEquals(ThemeMode.SYSTEM, repository.preferences.value.themeMode)
    }

    @Test
    fun completedOnboardingSurvivesRestart() {
        val storage = MapSettings()
        CompleteOnboardingUseCase(AppSettingsRepository(storage))()

        // new repository = app started again with the same storage
        val afterRestart = AppSettingsRepository(storage)

        assertTrue(afterRestart.preferences.value.onboardingCompleted)
    }

    @Test
    fun resetShowsOnboardingAgain() {
        val repository = AppSettingsRepository(MapSettings())
        CompleteOnboardingUseCase(repository)()

        ResetOnboardingUseCase(repository)()

        assertFalse(repository.preferences.value.onboardingCompleted)
    }

    @Test
    fun themeModeIsSaved() {
        val storage = MapSettings()
        ChangeThemeUseCase(AppSettingsRepository(storage))(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, AppSettingsRepository(storage).preferences.value.themeMode)
    }

    @Test
    fun unknownThemeValueFallsBackToSystem() {
        val storage = MapSettings("theme_mode" to "PURPLE")

        assertEquals(ThemeMode.SYSTEM, AppSettingsRepository(storage).preferences.value.themeMode)
    }
}
