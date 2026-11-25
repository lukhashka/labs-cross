package ua.chnu.campustix.domain.user

import ua.chnu.campustix.core.Result

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
}
