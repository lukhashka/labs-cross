package ua.chnu.campustix.domain.auth

import kotlinx.coroutines.flow.Flow
import ua.chnu.campustix.core.Result

interface AuthRepository {
    val sessionExpired: Flow<Unit>
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun isAuthorized(): Boolean
    suspend fun logout()
}
