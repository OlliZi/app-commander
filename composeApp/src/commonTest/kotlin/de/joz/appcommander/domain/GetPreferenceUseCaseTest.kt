package de.joz.appcommander.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPreferenceUseCaseTest {

    @Test
    fun `should execute repository for integer when use case is executed`() = runTest {
        val preferencesRepository: PreferencesRepository = mockk()
        coEvery {
            preferencesRepository.get("key", defaultValue = -1)
        } returns 123

        val getPreferenceUseCase = GetPreferenceUseCase(preferencesRepository)
        assertEquals(123, getPreferenceUseCase.get(key = "key", defaultValue = -1))

        coVerify {
            preferencesRepository.get(key = "key", defaultValue = -1)
        }
    }

    @Test
    fun `should execute repository for boolean when use case is executed`() = runTest {
        val preferencesRepository: PreferencesRepository = mockk()
        coEvery {
            preferencesRepository.get("key", defaultValue = false)
        } returns true

        val getPreferenceUseCase = GetPreferenceUseCase(preferencesRepository)
        assertTrue(getPreferenceUseCase.get(key = "key", defaultValue = false))

        coVerify {
            preferencesRepository.get(key = "key", defaultValue = false)
        }
    }
}