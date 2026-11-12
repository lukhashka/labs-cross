package ua.chnu.campustix.data.auth.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.auth.model.LoginRequest
import ua.chnu.campustix.data.auth.model.RefreshRequest
import ua.chnu.campustix.data.auth.model.TokensResponse
import ua.chnu.campustix.data.common.safeRequest

class AppAuthApiService(
    private val client: HttpClient,
) : AuthApiService {

    override suspend fun login(username: String, password: String): Result<TokensResponse> {
        return client.safeRequest {
            post("$BASE_URL/$AUTH_API/$LOGIN") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }
        }
    }

    override suspend fun refresh(refreshToken: String): Result<TokensResponse> {
        return client.safeRequest {
            post("$BASE_URL/$AUTH_API/$REFRESH") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken))
            }
        }
    }
}
