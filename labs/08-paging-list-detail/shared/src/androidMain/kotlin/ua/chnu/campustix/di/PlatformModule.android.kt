package ua.chnu.campustix.di

import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val platformModule = module {
    single { KSafe(androidApplication()) }
    single<HttpClientEngine> { Android.create() }
}
