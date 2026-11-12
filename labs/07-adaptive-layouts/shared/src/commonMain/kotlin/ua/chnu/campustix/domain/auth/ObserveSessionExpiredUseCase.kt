package ua.chnu.campustix.domain.auth

import kotlinx.coroutines.flow.Flow

class ObserveSessionExpiredUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Unit> = authRepository.sessionExpired
}
