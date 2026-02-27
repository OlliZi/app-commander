package de.joz.appcommander.domain

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import org.koin.core.annotation.Factory
import java.io.File
import kotlin.math.max

@Factory
class ExecuteScriptUseCase(
	private val addLoggingUseCase: AddLoggingUseCase,
	private val workingDir: File = File("."),
	private val processBuilder: ProcessBuilder = ProcessBuilder(),
) {
	operator fun invoke(
		script: ScriptsRepository.Script,
		selectedDevice: String = "",
	): Result {
		val allSubScripts = script.script.split("&&").map { it.trim() }

		return runCatching {
			val outputs = mutableListOf<String>()

			allSubScripts.forEach { subScript ->
				val scriptForSelectedDevice =
					injectDeviceId(script = subScript, platform = script.platform, selectedDevice)

				addLoggingUseCase("Execute script: '$scriptForSelectedDevice' on device '$selectedDevice'.")

				val commands = scriptForSelectedDevice.split(" ")
				val loopCount = getLoopCount(commands)
				val plainCommand = removeSpecialCommands(commands)

				(1..loopCount).forEach { _ ->
					outputs.add("- ${innerExecuteScript(plainCommand)}")
				}
			}

			Result.Success(
				output = outputs.joinToString(""),
			)
		}.getOrElse {
			val error = it.message ?: "Unknown error"
			addLoggingUseCase(error)
			Result.Error(error)
		}
	}

	private fun removeSpecialCommands(commands: List<String>) = commands.filterNot { it.contains(LOOP_COMMAND_REGEX) }

	private fun getLoopCount(commands: List<String>) =
		max(
			1,
			LOOP_COMMAND_REGEX
				.find(commands.joinToString(" "))
				?.groupValues
				?.get(1)
				?.toIntOrNull() ?: 1,
		)

	private fun innerExecuteScript(commands: List<String>) =
		processBuilder
			.command(commands)
			.directory(workingDir)
			.start()
			.inputReader()
			.readText()

	private fun injectDeviceId(
		script: String,
		platform: ScriptsRepository.Platform,
		selectedDevice: String,
	): String {
		if (selectedDevice.isEmpty()) {
			return script
		}

		return when (platform) {
			ScriptsRepository.Platform.ANDROID -> {
				script.replace(
					"adb",
					"adb -s $selectedDevice",
				)
			}

			// TODO
			ScriptsRepository.Platform.IOS -> {
				script
			}
		}
	}

	sealed interface Result {
		data class Success(
			val output: String,
		) : Result

		data class Error(
			val message: String,
		) : Result
	}

	companion object {
		private val LOOP_COMMAND_REGEX = """#LOOP_(\d+)""".toRegex()
	}
}
