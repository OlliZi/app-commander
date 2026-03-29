package de.joz.appcommander.domain.script

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SaveUserScriptsUseCaseTest {
	private val scriptsRepository: ScriptsRepository = mockk(relaxed = true)

	@Test
	fun `should save scripts when executed`() =
		runTest {
			val useCase = SaveUserScriptsUseCase(scriptsRepository = scriptsRepository)
			val scripts = listOf(
				ScriptsRepository.Script(
					label = "foo",
					platform = ScriptsRepository.Platform.DESKTOP,
					scripts = listOf("bar"),
				),
			)

			useCase(scripts = scripts)

			verify {
				scriptsRepository.saveScripts(scripts = scripts)
			}
		}
}
