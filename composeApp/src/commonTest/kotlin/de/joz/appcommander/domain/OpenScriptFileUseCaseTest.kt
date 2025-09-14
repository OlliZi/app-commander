package de.joz.appcommander.domain

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class OpenScriptFileUseCaseTest {

    @Test
    fun `should call repository when use case is executed`() = runTest {
        val scriptsRepository: ScriptsRepository = mockk(relaxed = true)

        OpenScriptFileUseCase(
            scriptsRepository = scriptsRepository,
        ).invoke()

        coVerify {
            scriptsRepository.openScriptFile()
        }
    }
}