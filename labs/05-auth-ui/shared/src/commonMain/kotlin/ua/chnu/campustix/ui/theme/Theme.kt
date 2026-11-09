package ua.chnu.campustix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ua.chnu.campustix.domain.settings.ThemeMode

private val LightColors = lightColorScheme(
    primary = Burgundy,
    onPrimary = Color.White,
    primaryContainer = BurgundyContainer,
    onPrimaryContainer = BurgundyDarkContainer,
    secondary = Gold,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldDarkContainer,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
)

private val DarkColors = darkColorScheme(
    primary = BurgundyLight,
    onPrimary = BurgundyDarkContainer,
    primaryContainer = BurgundyDarkContainer,
    onPrimaryContainer = BurgundyContainer,
    secondary = GoldLight,
    onSecondary = GoldDarkContainer,
    secondaryContainer = GoldDarkContainer,
    onSecondaryContainer = GoldContainer,
    background = Night,
    onBackground = Color(0xFFF0DEDE),
    surface = NightSurface,
    onSurface = Color(0xFFF0DEDE),
)

@Composable
fun CampusTixTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = campusTixTypography(),
        content = content,
    )
}
