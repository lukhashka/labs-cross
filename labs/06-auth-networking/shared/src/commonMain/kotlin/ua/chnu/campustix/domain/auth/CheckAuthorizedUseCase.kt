package ua.chnu.campustix.domain.auth

class CheckAuthorizedUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = authRepository.isAuthorized()
}
