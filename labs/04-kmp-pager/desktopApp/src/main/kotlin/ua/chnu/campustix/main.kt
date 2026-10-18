package ua.chnu.campustix

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ua.chnu.campustix.di.initApp

fun main() {
    initApp()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CampusTix",
            state = rememberWindowState(width = 480.dp, height = 860.dp),
        ) {
            App()
        }
    }
}
