package ua.chnu.campustix.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// Called once from every platform entry point (Android Application, desktop main, web main).
fun initApp(config: KoinAppDeclaration? = null) {
    Logger.setTag("CampusTix")
    Logger.setMinSeverity(Severity.Debug)
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, domainModule, presentationModule)
    }
    Logger.i { "App started" }
}
