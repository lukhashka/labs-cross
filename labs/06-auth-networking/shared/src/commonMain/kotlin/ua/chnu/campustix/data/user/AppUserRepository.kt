package ua.chnu.campustix.data.user

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.core.map
import ua.chnu.campustix.data.auth.service.AUTH_API
import ua.chnu.campustix.data.auth.service.BASE_URL
import ua.chnu.campustix.data.common.safeRequest
import ua.chnu.campustix.domain.user.User
import ua.chnu.campustix.domain.user.UserRepository

// The first request that needs the access token, so it also exercises the automatic refresh.
class AppUserRepository(
    private val authorizedClient: HttpClient,
) : UserRepository {

    override suspend fun getCurrentUser(): Result<User> {
        return authorizedClient
            .safeRequest<UserResponse> { get("$BASE_URL/$AUTH_API/me") }
            .map { it.toDomain() }
    }
}
