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
		val scriptForSelectedDevice = injectDeviceId(script, selectedDevice)
		addLoggingUseCase("Execute script: '$scriptForSelectedDevice' on device '$selectedDevice'.")

		return runCatching {
			val commands = scriptForSelectedDevice.split(" ")
			val loopCount = getLoopCount(commands)
			val plainCommand = removeSpecialCommand(commands)
			val output =
				(1..loopCount).joinToString(",") { index ->
					"$index. ${innerExecuteScript(plainCommand)}"
				}

			Result.Success(
				output = output,
				commands = commands,
			)
		}.getOrElse {
			val error = it.message ?: "Unknown error"
			addLoggingUseCase(error)
			Result.Error(error)
		}
	}

	private fun removeSpecialCommand(commands: List<String>) =
		commands.filter {
			!it.contains(LOOP_COMMAND_REGEX)
		}

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
		script: ScriptsRepository.Script,
		selectedDevice: String,
	): String {
		if (selectedDevice.isEmpty()) {
			return script.script
		}

		return when (script.platform) {
			ScriptsRepository.Platform.ANDROID -> {
				script.script.replace(
					"adb",
					"adb -s $selectedDevice",
				)
			}

			// TODO
			ScriptsRepository.Platform.IOS -> {
				script.script
			}
		}
	}

	sealed interface Result {
		data class Success(
			val output: String,
			val commands: List<String>,
		) : Result

		data class Error(
			val message: String,
		) : Result
	}

	companion object {
		private val LOOP_COMMAND_REGEX = """#LOOP_(\d+)""".toRegex()
	}
}
