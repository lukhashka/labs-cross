package ua.chnu.campustix.di

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.chnu.campustix.data.auth.AppAuthRepository
import ua.chnu.campustix.data.auth.AuthSession
import ua.chnu.campustix.data.auth.service.AppAuthApiService
import ua.chnu.campustix.data.auth.service.AuthApiService
import ua.chnu.campustix.data.network.createAuthorizedHttpClient
import ua.chnu.campustix.data.network.createPlainHttpClient
import ua.chnu.campustix.data.security.AppSecureStorage
import ua.chnu.campustix.data.security.SecureStorage
import ua.chnu.campustix.data.settings.AppSettingsRepository
import ua.chnu.campustix.data.user.AppUserRepository
import ua.chnu.campustix.domain.auth.AuthRepository
import ua.chnu.campustix.domain.auth.CheckAuthorizedUseCase
import ua.chnu.campustix.domain.auth.LoginUseCase
import ua.chnu.campustix.domain.auth.LogoutUseCase
import ua.chnu.campustix.domain.auth.ObserveSessionExpiredUseCase
import ua.chnu.campustix.domain.auth.validation.ValidatePasswordUseCase
import ua.chnu.campustix.domain.auth.validation.ValidateUsernameUseCase
import ua.chnu.campustix.domain.onboarding.CheckOnboardingCompletedUseCase
import ua.chnu.campustix.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.campustix.domain.onboarding.ResetOnboardingUseCase
import ua.chnu.campustix.domain.settings.ChangeThemeUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase
import ua.chnu.campustix.domain.settings.SettingsRepository
import ua.chnu.campustix.domain.user.GetCurrentUserUseCase
import ua.chnu.campustix.domain.user.UserRepository
import ua.chnu.campustix.presentation.auth.AuthViewModel
import ua.chnu.campustix.presentation.home.HomeViewModel
import ua.chnu.campustix.presentation.main.MainViewModel
import ua.chnu.campustix.presentation.onboarding.OnboardingViewModel
import ua.chnu.campustix.presentation.settings.SettingsViewModel

private val PLAIN_CLIENT = named("plain")
private val AUTHORIZED_CLIENT = named("authorized")

val dataModule = module {
    // no-arg module creates platform storage without passing Context
    single<Settings> { Settings() }
    singleOf(::AppSettingsRepository) { bind<SettingsRepository>() }

    singleOf(::AppSecureStorage) { bind<SecureStorage>() }
    singleOf(::AuthSession)

    // Two clients: the plain one refreshes tokens for the authorized one, so there is no cycle
    single(PLAIN_CLIENT) { createPlainHttpClient(get()) }
    single<AuthApiService> { AppAuthApiService(get(PLAIN_CLIENT)) }
    single<HttpClient>(AUTHORIZED_CLIENT) { createAuthorizedHttpClient(get(), get(), get(), get()) }

    single<AuthRepository> { AppAuthRepository(get(), get(), get(), get(AUTHORIZED_CLIENT)) }
    single<UserRepository> { AppUserRepository(get(AUTHORIZED_CLIENT)) }
}

val domainModule = module {
    factoryOf(::ObservePreferencesUseCase)
    factoryOf(::ChangeThemeUseCase)
    factoryOf(::CheckOnboardingCompletedUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::ResetOnboardingUseCase)
    factoryOf(::ValidateUsernameUseCase)
    factoryOf(::ValidatePasswordUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::CheckAuthorizedUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveSessionExpiredUseCase)
    factoryOf(::GetCurrentUserUseCase)
}

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}
