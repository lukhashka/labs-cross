package ua.chnu.campustix

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.auth.AppAuthRepository
import ua.chnu.campustix.data.auth.AuthSession
import ua.chnu.campustix.data.auth.service.AppAuthApiService
import ua.chnu.campustix.data.network.createAuthorizedHttpClient
import ua.chnu.campustix.data.network.createPlainHttpClient
import ua.chnu.campustix.data.security.SecureStorage
import ua.chnu.campustix.data.user.AppUserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeSecureStorage(var access: String = "", var refresh: String = "") : SecureStorage {
    override suspend fun getAccessToken() = access
    override suspend fun getRefreshToken() = refresh
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        access = accessToken
        refresh = refreshToken
    }
    override suspend fun clearTokens() {
        access = ""
        refresh = ""
    }
}

class AuthNetworkingTest {
    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val tokens = """{"accessToken":"new-access","refreshToken":"new-refresh"}"""
    private val user = """{"id":1,"username":"emilys","firstName":"Emily","lastName":"Johnson"}"""
    private val storage = FakeSecureStorage()
    private val session = AuthSession()

    private fun HttpRequestData.isPath(path: String) = url.encodedPath.endsWith(path)

    private fun repositories(handler: MockRequestHandler): Pair<AppAuthRepository, AppUserRepository> {
        val engine = MockEngine(handler)
        val authApi = AppAuthApiService(createPlainHttpClient(engine))
        val authorized = createAuthorizedHttpClient(engine, storage, authApi, session)
        return AppAuthRepository(authApi, storage, session, authorized) to AppUserRepository(authorized)
    }

    @Test
    fun expiredAccessTokenIsRefreshedAndRequestRepeated() = runTest {
        storage.saveTokens("old-access", "old-refresh")
        var refreshCalls = 0
        val (_, users) = repositories { request ->
            when {
                request.isPath("/auth/refresh") -> {
                    refreshCalls++
                    assertEquals(HttpMethod.Post, request.method)
                    assertTrue("old-refresh" in (request.body as TextContent).text)
                    respond(tokens, HttpStatusCode.OK, json)
                }
                request.headers[HttpHeaders.Authorization] == "Bearer new-access" ->
                    respond(user, HttpStatusCode.OK, json)
                else -> respondError(HttpStatusCode.Unauthorized)
            }
        }

        val result = users.getCurrentUser()

        assertEquals("Emily Johnson", assertIs<Result.Success<*>>(result).let { (it.data as ua.chnu.campustix.domain.user.User).fullName })
        assertEquals(1, refreshCalls)
        assertEquals("new-access", storage.access)
        assertEquals("new-refresh", storage.refresh)
    }

    @Test
    fun rejectedRefreshTokenEndsTheSession() = runTest {
        storage.saveTokens("old-access", "old-refresh")
        val (_, users) = repositories { request ->
            if (request.isPath("/auth/refresh")) respondError(HttpStatusCode.Forbidden)
            else respondError(HttpStatusCode.Unauthorized)
        }

        val result = users.getCurrentUser()

        assertEquals(Result.Failure(DataError.Unauthorized), result)
        assertEquals("", storage.refresh)
        withTimeout(1_000) { session.expired.first() }
    }

    @Test
    fun networkErrorDuringRefreshKeepsTokens() = runTest {
        storage.saveTokens("old-access", "old-refresh")
        val (_, users) = repositories { request ->
            if (request.isPath("/auth/refresh")) error("no network")
            else respondError(HttpStatusCode.Unauthorized)
        }

        users.getCurrentUser()

        assertEquals("old-refresh", storage.refresh)
    }

    @Test
    fun loginSavesTokens() = runTest {
        val (auth, _) = repositories { respond(tokens, HttpStatusCode.OK, json) }

        assertEquals(Result.Success(Unit), auth.login("emilys", "emilyspass"))

        assertEquals("new-access", storage.access)
        assertTrue(auth.isAuthorized())
    }

    @Test
    fun wrongCredentialsAreReportedAndNothingIsSaved() = runTest {
        val (auth, _) = repositories { respondError(HttpStatusCode.BadRequest) }

        assertEquals(Result.Failure(DataError.Unauthorized), auth.login("nobody", "wrongpass"))

        assertEquals("", storage.access)
    }

    @Test
    fun noConnectionDuringLoginIsReported() = runTest {
        val (auth, _) = repositories { error("no network") }

        assertEquals(Result.Failure(DataError.NoInternet), auth.login("emilys", "emilyspass"))
    }

    @Test
    fun failedFetchInBrowserIsReportedAsNoConnection() = runTest {
        // the Web engine of Ktor throws kotlin.Error instead of an Exception
        val (auth, _) = repositories { throw Error("Fail to fetch") }

        assertEquals(Result.Failure(DataError.NoInternet), auth.login("emilys", "emilyspass"))
    }

    @Test
    fun logoutRemovesTokens() = runTest {
        storage.saveTokens("access", "refresh")
        val (auth, _) = repositories { respond(tokens, HttpStatusCode.OK, json) }

        auth.logout()

        assertEquals(false, auth.isAuthorized())
    }
}
