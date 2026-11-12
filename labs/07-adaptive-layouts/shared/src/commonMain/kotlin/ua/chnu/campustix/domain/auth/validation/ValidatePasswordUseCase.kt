package ua.chnu.campustix.domain.auth.validation

enum class PasswordError { EMPTY, TOO_SHORT }

class ValidatePasswordUseCase {
    // Password is not trimmed: spaces can be a part of it.
    operator fun invoke(password: String): PasswordError? {
        return when {
            password.isEmpty() -> PasswordError.EMPTY
            password.length < MIN_LENGTH -> PasswordError.TOO_SHORT
            else -> null
        }
    }

    private companion object {
        const val MIN_LENGTH = 6
    }
}
