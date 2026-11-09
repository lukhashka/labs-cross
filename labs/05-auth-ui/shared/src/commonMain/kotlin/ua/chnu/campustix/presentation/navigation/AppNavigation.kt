package ua.chnu.campustix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import ua.chnu.campustix.presentation.auth.AuthScreen
import ua.chnu.campustix.presentation.onboarding.OnboardingScreen
import ua.chnu.campustix.presentation.settings.SettingsScreen

@Composable
fun AppNavigation(startRoute: Route) {
    // In Navigation 3 the back stack is just a list that we change ourselves
    val backStack = rememberNavBackStack(navigationConfig, startRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            // every screen gets its own ViewModelStore, cleared when it leaves the stack
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Route.Onboarding> {
                OnboardingScreen(onFinished = { backStack.replaceAllWith(Route.Auth) })
            }
            entry<Route.Auth> {
                AuthScreen(onOpenSettings = { backStack.add(Route.Settings) })
            }
            entry<Route.Settings> {
                SettingsScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onShowOnboarding = { backStack.replaceAllWith(Route.Onboarding) },
                )
            }
        },
    )
}

// System "back" must not return to a screen that is already finished
private fun NavBackStack<NavKey>.replaceAllWith(route: Route) {
    clear()
    add(route)
}
