package ua.chnu.campustix.di

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ua.chnu.campustix.data.settings.AppSettingsRepository
import ua.chnu.campustix.domain.auth.validation.ValidatePasswordUseCase
import ua.chnu.campustix.domain.auth.validation.ValidateUsernameUseCase
import ua.chnu.campustix.domain.onboarding.CheckOnboardingCompletedUseCase
import ua.chnu.campustix.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.campustix.domain.onboarding.ResetOnboardingUseCase
import ua.chnu.campustix.domain.settings.ChangeThemeUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase
import ua.chnu.campustix.domain.settings.SettingsRepository
import ua.chnu.campustix.presentation.auth.AuthViewModel
import ua.chnu.campustix.presentation.main.MainViewModel
import ua.chnu.campustix.presentation.onboarding.OnboardingViewModel
import ua.chnu.campustix.presentation.settings.SettingsViewModel

val dataModule = module {
    // no-arg module creates platform storage without passing Context
    single<Settings> { Settings() }
    singleOf(::AppSettingsRepository) { bind<SettingsRepository>() }
}

val domainModule = module {
    factoryOf(::ObservePreferencesUseCase)
    factoryOf(::ChangeThemeUseCase)
    factoryOf(::CheckOnboardingCompletedUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::ResetOnboardingUseCase)
    factoryOf(::ValidateUsernameUseCase)
    factoryOf(::ValidatePasswordUseCase)
}

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SettingsViewModel)
}
