package ua.chnu.campustix.domain.auth

import ua.chnu.campustix.core.Result

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        return authRepository.login(username.trim(), password)
    }
}
