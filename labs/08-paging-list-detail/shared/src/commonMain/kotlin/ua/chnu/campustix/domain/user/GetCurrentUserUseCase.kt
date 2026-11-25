package ua.chnu.campustix.domain.user

import ua.chnu.campustix.core.Result

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = userRepository.getCurrentUser()
}
