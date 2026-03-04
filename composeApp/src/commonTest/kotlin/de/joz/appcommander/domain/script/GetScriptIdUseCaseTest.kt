package de.joz.appcommander.domain.script

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GetScriptIdUseCaseTest {
	private val getScriptIdUseCase = GetScriptIdUseCase()

	@Test
	fun `should return script id`() {
		val scriptId =
			getScriptIdUseCase(
				script =
					ScriptsRepository.Script(
						multiScripts = emptyList(),
						label = "",
						platform = ScriptsRepository.Platform.ANDROID,
					),
			)

		assertEquals(
			ScriptsRepository
				.Script(
					multiScripts = emptyList(),
					label = "",
					platform = ScriptsRepository.Platform.ANDROID,
				).hashCode(),
			scriptId,
		)
	}

	@Test
	fun `should return same script id`() {
		val script =
			ScriptsRepository.Script(
				multiScripts = listOf("foo"),
				label = "bar",
				platform = ScriptsRepository.Platform.ANDROID,
			)
		val scriptModified =
			script.copy(
				multiScripts = listOf("bar"),
				platform = ScriptsRepository.Platform.IOS,
			)

		assertNotEquals(
			getScriptIdUseCase(
				script = script,
			),
			getScriptIdUseCase(
				script = scriptModified,
			),
		)

		assertEquals(
			getScriptIdUseCase(
				script = script,
			),
			getScriptIdUseCase(
				script =
					scriptModified.copy(
						multiScripts = listOf("foo"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
			),
		)
	}
}
