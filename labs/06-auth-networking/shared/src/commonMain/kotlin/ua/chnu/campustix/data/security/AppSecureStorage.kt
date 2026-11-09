package ua.chnu.campustix.data.security

import eu.anifantakis.lib.ksafe.KSafe

// KSafe encrypts values: Android Keystore, encrypted store on Desktop and in the browser.
class AppSecureStorage(
    private val ksafe: KSafe,
) : SecureStorage {

    override suspend fun getAccessToken(): String {
        ksafe.awaitReady()
        return ksafe.get(ACCESS_TOKEN_KEY, "")
    }

    override suspend fun getRefreshToken(): String {
        ksafe.awaitReady()
        return ksafe.get(REFRESH_TOKEN_KEY, "")
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        ksafe.awaitReady()
        ksafe.put(ACCESS_TOKEN_KEY, accessToken)
        ksafe.put(REFRESH_TOKEN_KEY, refreshToken)
    }

    override suspend fun clearTokens() {
        ksafe.awaitReady()
        ksafe.delete(ACCESS_TOKEN_KEY)
        ksafe.delete(REFRESH_TOKEN_KEY)
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}

// In the browser KSafe loads its cache asynchronously, other platforms are ready at once.
expect suspend fun KSafe.awaitReady()
