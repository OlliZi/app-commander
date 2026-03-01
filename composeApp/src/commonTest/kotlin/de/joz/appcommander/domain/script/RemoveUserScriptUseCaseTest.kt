package de.joz.appcommander.domain.script

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoveUserScriptUseCaseTest {
	@Test
	fun `should execute repository when use case is executed`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val removeUserScriptUseCase =
				RemoveUserScriptUseCase(
					scriptsRepository = scriptsRepositoryMock,
				)

			removeUserScriptUseCase(
				script =
					ScriptsRepository.Script(
						label = "key",
						script = "foo",
						platform = ScriptsRepository.Platform.ANDROID,
					),
			)

			verify {
				scriptsRepositoryMock.removeScript(
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
