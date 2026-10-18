package ua.chnu.campustix.di

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ua.chnu.campustix.data.settings.AppSettingsRepository
import ua.chnu.campustix.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.campustix.domain.onboarding.ResetOnboardingUseCase
import ua.chnu.campustix.domain.settings.ChangeThemeUseCase
import ua.chnu.campustix.domain.settings.ObservePreferencesUseCase
import ua.chnu.campustix.domain.settings.SettingsRepository
import ua.chnu.campustix.presentation.home.HomeViewModel
import ua.chnu.campustix.presentation.onboarding.OnboardingViewModel
import ua.chnu.campustix.presentation.root.RootViewModel

val dataModule = module {
    // no-arg module creates platform storage without passing Context
    single<Settings> { Settings() }
    singleOf(::AppSettingsRepository) { bind<SettingsRepository>() }
}

val domainModule = module {
    factoryOf(::ObservePreferencesUseCase)
    factoryOf(::ChangeThemeUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::ResetOnboardingUseCase)
}

val presentationModule = module {
    viewModelOf(::RootViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
}
