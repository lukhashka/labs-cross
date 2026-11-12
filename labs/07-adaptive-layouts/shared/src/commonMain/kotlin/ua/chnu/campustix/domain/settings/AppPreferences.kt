package ua.chnu.campustix.domain.settings

// User preferences that survive app restart.
data class AppPreferences(
    val onboardingCompleted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
