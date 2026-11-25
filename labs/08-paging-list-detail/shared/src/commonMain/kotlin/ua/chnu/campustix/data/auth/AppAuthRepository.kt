package ua.chnu.campustix.data.auth

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.flow.Flow
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.core.map
import ua.chnu.campustix.data.auth.service.AuthApiService
import ua.chnu.campustix.data.security.SecureStorage
import ua.chnu.campustix.domain.auth.AuthRepository

class AppAuthRepository(
    private val authApiService: AuthApiService,
    private val secureStorage: SecureStorage,
    private val authSession: AuthSession,
    private val authorizedClient: HttpClient,
) : AuthRepository {

    override val sessionExpired: Flow<Unit> = authSession.expired

    override suspend fun login(username: String, password: String): Result<Unit> {
        return authApiService.login(username, password).map { tokens ->
            secureStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
            forgetCachedTokens()
        }
    }

    override suspend fun isAuthorized(): Boolean = secureStorage.getRefreshToken().isNotEmpty()

    override suspend fun logout() {
        secureStorage.clearTokens()
        forgetCachedTokens()
    }

    // Ktor keeps tokens in memory, without this the previous session would be used
    private fun forgetCachedTokens() {
        authorizedClient.authProvider<BearerAuthProvider>()?.clearToken()
    }
}
