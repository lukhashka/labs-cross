package ua.chnu.campustix

import com.russhwolf.settings.MapSettings
import ua.chnu.campustix.data.settings.AppSettingsRepository
import ua.chnu.campustix.domain.auth.validation.PasswordError
import ua.chnu.campustix.domain.auth.validation.UsernameError
import ua.chnu.campustix.domain.auth.validation.ValidatePasswordUseCase
import ua.chnu.campustix.domain.auth.validation.ValidateUsernameUseCase
import ua.chnu.campustix.domain.onboarding.CheckOnboardingCompletedUseCase
import ua.chnu.campustix.domain.onboarding.CompleteOnboardingUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthValidationTest {
    private val validateUsername = ValidateUsernameUseCase()
    private val validatePassword = ValidatePasswordUseCase()

    @Test
    fun usernameErrors() {
        assertEquals(UsernameError.EMPTY, validateUsername("   "))
        assertEquals(UsernameError.TOO_SHORT, validateUsername("ab"))
        assertEquals(UsernameError.INVALID_CHARACTERS, validateUsername("bad name"))
    }

    @Test
    fun validUsernames() {
        assertNull(validateUsername("emilys"))
        assertNull(validateUsername("  ivan.petrenko_1-2 "))
    }

    @Test
    fun passwordErrors() {
        assertEquals(PasswordError.EMPTY, validatePassword(""))
        assertEquals(PasswordError.TOO_SHORT, validatePassword("12345"))
    }

    @Test
    fun validPassword() {
        assertNull(validatePassword("123456"))
    }

    @Test
    fun checkOnboardingFollowsSavedFlag() {
        val repository = AppSettingsRepository(MapSettings())
        val check = CheckOnboardingCompletedUseCase(repository)

        assertFalse(check())
        CompleteOnboardingUseCase(repository)()
        assertTrue(check())
    }
}
