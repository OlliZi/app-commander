package de.joz.appcommander.domain

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SaveUserScriptUseCaseTest {
	@Test
	fun `should execute repository when use case is executed`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val savePreferenceUseCase =
				SaveUserScriptUseCase(
					scriptsRepository = scriptsRepositoryMock,
				)

			savePreferenceUseCase(
				script =
					ScriptsRepository.Script(
						label = "key",
						script = "foo",
						platform = ScriptsRepository.Platform.ANDROID,
					),
			)

			verify {
				scriptsRepositoryMock.saveScript(
					script =
						ScriptsRepository.Script(
							label = "key",
							script = "foo",
							platform = ScriptsRepository.Platform.ANDROID,
						),
				)
			}
		}
}
