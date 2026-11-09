package ua.chnu.campustix.di

import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.dsl.module

actual val platformModule = module {
    single { KSafe() }
    single<HttpClientEngine> { Js.create() }
}
