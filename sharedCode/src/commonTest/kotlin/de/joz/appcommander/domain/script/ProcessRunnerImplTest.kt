package de.joz.appcommander.domain.script

import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessRunnerImplTest {
	@Test
	fun `should run simple process`() =
		runTest {
			val processRunner = ProcessRunnerImpl(processBuilder = ProcessBuilder())

			val result = processRunner.runProcess(commands = listOf("echo", "foo"), workingDir = File("."))

			assertEquals("foo\n", result)
		}
}
