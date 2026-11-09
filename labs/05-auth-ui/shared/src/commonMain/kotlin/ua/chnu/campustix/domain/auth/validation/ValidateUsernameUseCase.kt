package ua.chnu.campustix.domain.auth.validation

enum class UsernameError { EMPTY, TOO_SHORT, INVALID_CHARACTERS }

class ValidateUsernameUseCase {
    // Returns null when the username is valid.
    operator fun invoke(username: String): UsernameError? {
        val value = username.trim()
        return when {
            value.isEmpty() -> UsernameError.EMPTY
            value.length < MIN_LENGTH -> UsernameError.TOO_SHORT
            !value.all { it.isLetterOrDigit() || it in ALLOWED_SYMBOLS } -> UsernameError.INVALID_CHARACTERS
            else -> null
        }
    }

    private companion object {
        const val MIN_LENGTH = 3
        const val ALLOWED_SYMBOLS = "._-"
    }
}
