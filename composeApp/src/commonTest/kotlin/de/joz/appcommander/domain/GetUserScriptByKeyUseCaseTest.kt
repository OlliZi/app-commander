package de.joz.appcommander.domain

import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetUserScriptByKeyUseCaseTest {
	private val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)

	@BeforeTest
	fun setUp() {
		every { getScriptIdUseCaseMock.invoke(any()) } returns 1
		every { scriptsRepositoryMock.getScripts() } returns
			listOf(
				ScriptsRepository.Script(
					script = "",
					label = "",
					platform = ScriptsRepository.Platform.ANDROID,
				),
			)
	}

	@Test
	fun `should return null if input is null`() {
		val useCase = createUseCase()

		assertNull(useCase(scriptKey = null))
	}

	@Test
	fun `should return script if input is found`() {
		val useCase = createUseCase()

		assertNotNull(useCase(scriptKey = 1))
	}

	@Test
	fun `should return null if input is not found`() {
		val useCase = createUseCase()

		assertNull(useCase(scriptKey = 2))
	}

	@Test
	fun `should return nothing if repository has no scripts`() {
		every { scriptsRepositoryMock.getScripts() } returns emptyList()

		val useCase = createUseCase()

		assertNull(useCase(scriptKey = 1))
		assertNull(useCase(scriptKey = null))
	}

	private fun createUseCase() =
		GetUserScriptByKeyUseCase(
			scriptsRepository = scriptsRepositoryMock,
			getScriptIdUseCase = getScriptIdUseCaseMock,
		)
}
