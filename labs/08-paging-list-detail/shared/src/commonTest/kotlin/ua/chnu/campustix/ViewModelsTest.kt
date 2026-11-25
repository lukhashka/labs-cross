package ua.chnu.campustix

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.settings.AppSettingsRepository
import ua.chnu.campustix.domain.auth.AuthRepository
import ua.chnu.campustix.domain.auth.CheckAuthorizedUseCase
import ua.chnu.campustix.domain.auth.LoginUseCase
import ua.chnu.campustix.domain.auth.ObserveSessionExpiredUseCase
import ua.chnu.campustix.domain.auth.validation.UsernameError
import ua.chnu.campustix.domain.auth.validation.ValidatePasswordUseCase
import ua.chnu.campustix.domain.auth.validation.ValidateUsernameUseCase
import ua.chnu.campustix.domain.onboarding.CheckOnboardingCompletedUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase
import ua.chnu.campustix.presentation.auth.AuthAction
import ua.chnu.campustix.presentation.auth.AuthEvent
import ua.chnu.campustix.presentation.auth.AuthViewModel
import ua.chnu.campustix.presentation.main.MainEvent
import ua.chnu.campustix.presentation.main.MainViewModel
import ua.chnu.campustix.presentation.navigation.Route
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private class FakeAuthRepository(
    private val authorized: Boolean = false,
    private val loginResult: Result<Unit> = Result.Success(Unit),
) : AuthRepository {
    val expiredChannel = Channel<Unit>(Channel.CONFLATED)
    var loginCalls = 0

    override val sessionExpired: Flow<Unit> = expiredChannel.receiveAsFlow()
    override suspend fun login(username: String, password: String): Result<Unit> {
        loginCalls++
        return loginResult
    }
    override suspend fun isAuthorized() = authorized
    override suspend fun logout() = Unit
}

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mainViewModel(onboarded: Boolean, auth: FakeAuthRepository): MainViewModel {
        val settings = AppSettingsRepository(MapSettings())
        settings.setOnboardingCompleted(onboarded)
        return MainViewModel(
            CheckOnboardingCompletedUseCase(settings),
            CheckAuthorizedUseCase(auth),
            ObservePreferencesUseCase(settings),
            ObserveSessionExpiredUseCase(auth),
        )
    }

    private fun authViewModel(auth: FakeAuthRepository) =
        AuthViewModel(ValidateUsernameUseCase(), ValidatePasswordUseCase(), LoginUseCase(auth))

    private fun AuthViewModel.enter(username: String, password: String) {
        onAction(AuthAction.OnUsernameChange(username))
        onAction(AuthAction.OnPasswordChange(password))
    }

    @Test
    fun firstLaunchStartsWithOnboarding() {
        val viewModel = mainViewModel(onboarded = false, auth = FakeAuthRepository())
        assertEquals(Route.Onboarding, viewModel.state.value.startRoute)
    }

    @Test
    fun guestStartsWithSignIn() {
        val viewModel = mainViewModel(onboarded = true, auth = FakeAuthRepository(authorized = false))
        assertEquals(Route.Auth, viewModel.state.value.startRoute)
    }

    @Test
    fun signedInUserStartsAtHome() {
        val viewModel = mainViewModel(onboarded = true, auth = FakeAuthRepository(authorized = true))
        assertEquals(Route.Home, viewModel.state.value.startRoute)
    }

    @Test
    fun expiredSessionNavigatesToSignIn() = runTest {
        val auth = FakeAuthRepository(authorized = true)
        val viewModel = mainViewModel(onboarded = true, auth = auth)

        auth.expiredChannel.trySend(Unit)

        assertEquals(MainEvent.NavigateToAuth, viewModel.events.first())
    }

    @Test
    fun invalidFieldsDoNotCallLogin() {
        val auth = FakeAuthRepository()
        val viewModel = authViewModel(auth)

        viewModel.onAction(AuthAction.OnSignInClick)

        assertEquals(UsernameError.EMPTY, viewModel.state.value.usernameError)
        assertEquals(0, auth.loginCalls)
    }

    @Test
    fun successfulLoginNavigatesHome() = runTest {
        val viewModel = authViewModel(FakeAuthRepository())
        viewModel.enter("emilys", "emilyspass")

        viewModel.onAction(AuthAction.OnSignInClick)

        assertEquals(AuthEvent.NavigateToHome, viewModel.events.first())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun failedLoginShowsErrorEvent() = runTest {
        val auth = FakeAuthRepository(loginResult = Result.Failure(DataError.Unauthorized))
        val viewModel = authViewModel(auth)
        viewModel.enter("nobody", "wrongpass")

        viewModel.onAction(AuthAction.OnSignInClick)

        assertEquals(AuthEvent.ShowError(DataError.Unauthorized), viewModel.events.first())
    }
}
