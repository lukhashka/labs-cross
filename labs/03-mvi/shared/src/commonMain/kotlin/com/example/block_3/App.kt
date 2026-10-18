package com.example.block_3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

// Імпорти твоїх створених файлів (перевірь, щоб назви папок збігалися)
import com.example.block_3.di.appModule
import com.example.block_3.presentation.MainScreen

private val CustomDarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),      // М'який синій
    secondary = Color(0xFF81C784),    // Зелений акцент
    background = Color(0xFF1E1E1E),   // Темний фон екрану
    surface = Color(0xFF2D2D2D)       // Фон карток/компонентів
)

private val CustomLightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),      // Насичений синій
    secondary = Color(0xFF4CAF50),    // Зелений акцент
    background = Color(0xFFF5F5F5),   // Світлий фон екрану
    surface = Color(0xFFFFFFFF)       // Білий фон компонентів
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) CustomDarkColorScheme else CustomLightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

@Composable
fun App() {
    KoinApplication(koinConfiguration { modules(appModule) }) {
        AppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainScreen()
            }
        }
    }
}