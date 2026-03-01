package de.joz.appcommander.domain

import de.joz.appcommander.domain.preference.PreferencesRepository
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SavePreferenceUseCaseTest {
	@Test
	fun `should execute repository for integer when use case is executed`() =
		runTest {
			val preferencesRepositoryMock: PreferencesRepository = mockk(relaxed = true)
			val savePreferenceUseCase =
				SavePreferenceUseCase(
					preferencesRepository = preferencesRepositoryMock,
				)

			savePreferenceUseCase(key = "key", value = 123)

			coVerify {
				preferencesRepositoryMock.store(key = "key", value = 123)
			}
		}

	@Test
	fun `should execute repository for boolean when use case is executed`() =
		runTest {
			val preferencesRepositoryMock: PreferencesRepository = mockk(relaxed = true)
			val savePreferenceUseCase =
				SavePreferenceUseCase(
					preferencesRepository = preferencesRepositoryMock,
				)

			savePreferenceUseCase(key = "key", value = true)

			coVerify {
				preferencesRepositoryMock.store(key = "key", value = true)
			}
		}
}
