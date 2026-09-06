package de.joz.appcommander.domain.script

import de.joz.appcommander.helper.toSubScripts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GetScriptIdUseCaseTest {
	private val getScriptIdUseCase = GetScriptIdUseCase()

	@Test
	fun `should return script id`() {
		val scriptId = getScriptIdUseCase(
			script = ScriptsRepository.Script(
				scripts = emptyList(),
				label = "",
				platform = ScriptsRepository.Platform.ANDROID,
			),
		)

		assertEquals(
			ScriptsRepository
				.Script(
					scripts = emptyList(),
					label = "",
					platform = ScriptsRepository.Platform.ANDROID,
				).hashCode(),
			scriptId,
		)
	}

	@Test
	fun `should return same script id`() {
		val script = ScriptsRepository.Script(
			scripts = listOf("foo").toSubScripts(),
			label = "bar",
			platform = ScriptsRepository.Platform.ANDROID,
		)
		val scriptModified = script.copy(
			scripts = listOf("bar").toSubScripts(),
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
				script = scriptModified.copy(
					scripts = listOf("foo").toSubScripts(),
					platform = ScriptsRepository.Platform.ANDROID,
				),
			),
		)
	}
}
