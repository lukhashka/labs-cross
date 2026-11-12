package ua.chnu.campustix.data.network

import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.auth.AuthSession
import ua.chnu.campustix.data.auth.service.AuthApiService
import ua.chnu.campustix.data.security.SecureStorage

private fun HttpClientConfig<*>.installCommonPlugins() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Logging) {
        // INFO: only method, url and status. Passwords and tokens never get into the log
        level = LogLevel.INFO
        logger = object : Logger {
            override fun log(message: String) {
                KermitLogger.withTag("Network").d { message }
            }
        }
    }
}

// For login and refresh: these requests must not carry or refresh tokens themselves.
fun createPlainHttpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
    installCommonPlugins()
}

// For everything else: adds the access token and renews it after a 401 response.
fun createAuthorizedHttpClient(
    engine: HttpClientEngine,
    secureStorage: SecureStorage,
    authApiService: AuthApiService,
    authSession: AuthSession,
): HttpClient = HttpClient(engine) {
    installCommonPlugins()
    install(Auth) {
        bearer {
            sendWithoutRequest { true }
            loadTokens {
                val refreshToken = secureStorage.getRefreshToken()
                if (refreshToken.isEmpty()) null else BearerTokens(secureStorage.getAccessToken(), refreshToken)
            }
            refreshTokens {
                refreshSession(secureStorage, authApiService, authSession)
            }
        }
    }
}

private suspend fun refreshSession(
    secureStorage: SecureStorage,
    authApiService: AuthApiService,
    authSession: AuthSession,
): BearerTokens? {
    val log = KermitLogger.withTag("Auth")
    val refreshToken = secureStorage.getRefreshToken()
    if (refreshToken.isEmpty()) return null

    return when (val result = authApiService.refresh(refreshToken)) {
        is Result.Success -> {
            secureStorage.saveTokens(result.data.accessToken, result.data.refreshToken)
            log.i { "Tokens refreshed" }
            BearerTokens(result.data.accessToken, result.data.refreshToken)
        }
        is Result.Failure -> {
            log.w { "Token refresh failed: ${result.error}" }
            // No internet is not a reason to sign out: the tokens are still valid to try later
            if (result.error == DataError.Unauthorized) {
                secureStorage.clearTokens()
                authSession.notifyExpired()
            }
            null
        }
    }
}
