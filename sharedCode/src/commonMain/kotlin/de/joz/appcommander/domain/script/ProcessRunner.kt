package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory
import java.io.File

interface ProcessRunner {
	fun runProcess(
		commands: List<String>,
		workingDir: File,
	): String
}

@Factory
class ProcessRunnerImpl(
	private val processBuilder: ProcessBuilder,
) : ProcessRunner {
	override fun runProcess(
		commands: List<String>,
		workingDir: File,
	): String =
		processBuilder
			.command(commands)
			.directory(workingDir)
			.start()
			.inputStream
			.bufferedReader()
			.readText()
}
