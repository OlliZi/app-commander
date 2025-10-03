package de.joz.appcommander.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserScriptsUseCaseTest {
    @Test
    fun `should return scripts from repository`() = runTest {
        val scriptsRepositoryMock: ScriptsRepository = mockk()
        coEvery {
            scriptsRepositoryMock.getScripts()
        } returns listOf(
            ScriptsRepository.Script(
                label = "foo",
                script = "echo",
                platform = ScriptsRepository.Platform.ANDROID,
            )
        )

        val getUserScriptsUseCase = GetUserScriptsUseCase(
            scriptsRepository = scriptsRepositoryMock,
        )

        assertEquals(
            listOf(
                ScriptsRepository.Script(
                    label = "foo",
                    script = "echo",
                    platform = ScriptsRepository.Platform.ANDROID,
                )
            ), getUserScriptsUseCase()
        )

        coVerify {
            scriptsRepositoryMock.getScripts()
        }
    }
}