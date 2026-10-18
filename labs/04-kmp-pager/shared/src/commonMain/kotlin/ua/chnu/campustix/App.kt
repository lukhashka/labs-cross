package ua.chnu.campustix

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.presentation.home.HomeScreen
import ua.chnu.campustix.presentation.onboarding.OnboardingScreen
import ua.chnu.campustix.presentation.root.RootViewModel
import ua.chnu.campustix.ui.theme.CampusTixTheme

@Composable
fun App() {
    val viewModel = koinViewModel<RootViewModel>()
    val preferences by viewModel.state.collectAsStateWithLifecycle()

    CampusTixTheme(themeMode = preferences.themeMode) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (preferences.onboardingCompleted) {
                HomeScreen()
            } else {
                OnboardingScreen()
            }
        }
    }
}
