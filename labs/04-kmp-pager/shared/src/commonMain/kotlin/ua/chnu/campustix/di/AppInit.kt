package ua.chnu.campustix.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import org.koin.core.context.startKoin

// Called once from every platform entry point (Android Application, desktop main, web main).
fun initApp() {
    Logger.setTag("CampusTix")
    Logger.setMinSeverity(Severity.Debug)
    startKoin {
        modules(dataModule, domainModule, presentationModule)
    }
    Logger.i { "App started" }
}
