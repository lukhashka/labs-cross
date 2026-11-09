package ua.chnu.campustix.di

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single {
        KSafe(
            baseDir = File(System.getProperty("user.home"), ".campustix"),
            config = KSafeConfig(appNamespace = "ua.chnu.campustix"),
        )
    }
    single<HttpClientEngine> { CIO.create() }
}
