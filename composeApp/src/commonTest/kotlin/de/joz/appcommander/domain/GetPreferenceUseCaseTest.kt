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
    fun `should execute repository for integer when use case is executed`() =
        runTest {
            val preferencesRepositoryMock: PreferencesRepository = mockk()
            coEvery {
                preferencesRepositoryMock.get("key", defaultValue = -1)
            } returns 123

            val getPreferenceUseCase =
                GetPreferenceUseCase(
                    preferencesRepository = preferencesRepositoryMock,
                )

            assertEquals(123, getPreferenceUseCase.get(key = "key", defaultValue = -1))

            coVerify {
                preferencesRepositoryMock.get(key = "key", defaultValue = -1)
            }
        }

    @Test
    fun `should execute repository for boolean when use case is executed`() =
        runTest {
            val preferencesRepositoryMock: PreferencesRepository = mockk()
            coEvery {
                preferencesRepositoryMock.get("key", defaultValue = false)
            } returns true

            val getPreferenceUseCase =
                GetPreferenceUseCase(
                    preferencesRepository = preferencesRepositoryMock,
                )
            assertTrue(getPreferenceUseCase.get(key = "key", defaultValue = false))

            coVerify {
                preferencesRepositoryMock.get(key = "key", defaultValue = false)
            }
        }
}
