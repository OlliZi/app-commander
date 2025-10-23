package de.joz.appcommander.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SaveUserScriptUseCaseTest {
	@Test
	fun `should call save script in repository when use case is executed`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)

			every {
				getUserScriptByKeyUseCaseMock(any())
			} returns null

			val savePreferenceUseCase =
				SaveUserScriptUseCase(
					getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
					scriptsRepository = scriptsRepositoryMock,
				)

			savePreferenceUseCase(
				script =
					ScriptsRepository.Script(
						label = "key",
						script = "foo",
						platform = ScriptsRepository.Platform.ANDROID,
					),
				scriptKey = null,
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

	@Test
	fun `should call update script in repository when use case is executed`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)

			val newScript =
				ScriptsRepository.Script(
					label = "key",
					script = "foo",
					platform = ScriptsRepository.Platform.ANDROID,
				)
			val oldScript =
				ScriptsRepository.Script(
					label = "key",
					script = "bar",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			every {
				getUserScriptByKeyUseCaseMock(oldScript.hashCode())
			} returns oldScript

			val savePreferenceUseCase =
				SaveUserScriptUseCase(
					getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
					scriptsRepository = scriptsRepositoryMock,
				)

			savePreferenceUseCase(
				script = newScript,
				scriptKey = oldScript.hashCode(),
			)

			verify {
				scriptsRepositoryMock.updateScript(
					script =
						ScriptsRepository.Script(
							label = "key",
							script = "foo",
							platform = ScriptsRepository.Platform.ANDROID,
						),
					oldScript = oldScript,
				)
			}
		}
}
