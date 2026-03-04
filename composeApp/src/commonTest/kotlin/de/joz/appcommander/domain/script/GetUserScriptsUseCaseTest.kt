package de.joz.appcommander.domain.script

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserScriptsUseCaseTest {
	@Test
	fun `should return scripts from repository`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk()
			coEvery {
				scriptsRepositoryMock.getScripts()
			} returns
				ScriptsRepository.JsonParseResult(
					scripts =
						listOf(
							ScriptsRepository.Script(
								label = "foo",
								multiScripts = listOf("echo"),
								platform = ScriptsRepository.Platform.ANDROID,
							),
						),
					parsingMetaData = null,
				)

			val getUserScriptsUseCase =
				GetUserScriptsUseCase(
					scriptsRepository = scriptsRepositoryMock,
				)

			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "foo",
						multiScripts = listOf("echo"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
				),
				getUserScriptsUseCase().scripts,
			)

			coVerify {
				scriptsRepositoryMock.getScripts()
			}
		}
}
