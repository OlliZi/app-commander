package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import kotlinx.coroutines.delay
import org.koin.core.annotation.Factory
import java.io.File
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

@Factory
class ExecuteScriptUseCase(
	private val addLoggingUseCase: AddLoggingUseCase,
	private val workingDir: File,
	private val processRunner: ProcessRunner,
) {
	suspend operator fun invoke(
		script: ScriptsRepository.Script,
		selectedDevice: String = "",
		log: Boolean = true,
	): Result {
		val scripts = script.scripts.map { it.subScript.trim() }

		return runCatching {
			val outputs = mutableListOf<String>()

			scripts.forEach { subScript ->
				val scriptForSelectedDevice = injectDeviceId(
					script = subScript,
					platform = script.platform,
					selectedDevice,
				)

				val commands = scriptForSelectedDevice.split(" ")
				val loopCount = getLoopCount(commands)
				val plainCommand = removeSpecialCommands(commands)

				(1..loopCount).forEach { _ ->
					delay((if (loopCount > 1) 200 else 0).milliseconds)
					if (log) {
						addLoggingUseCase(
							"Execute script: '${plainCommand.joinToString(" ")}'" +
								(if (selectedDevice.isNotEmpty()) " on device '$selectedDevice'." else "."),
						)
					}
					val result = processRunner.runProcess(commands = plainCommand, workingDir = workingDir)
					outputs.add("- $result")
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
				replaceAdbWithSelectedDevice(
					script = script,
					selectedDevice = selectedDevice,
				)
			}

			ScriptsRepository.Platform.IOS -> {
				replaceIdbWithSelectedDevice(
					script = script,
					selectedDevice = selectedDevice,
				)
			}

			ScriptsRepository.Platform.DESKTOP -> {
				val desktopScript = replaceAdbWithSelectedDevice(
					script = script,
					selectedDevice = selectedDevice,
				)

				replaceIdbWithSelectedDevice(
					script = desktopScript,
					selectedDevice = selectedDevice,
				)
			}
		}
	}

	private fun replaceAdbWithSelectedDevice(
		script: String,
		selectedDevice: String,
	): String =
		script.replace(
			"adb",
			"adb -s $selectedDevice",
		)

	// TODO
	private fun replaceIdbWithSelectedDevice(
		script: String,
		selectedDevice: String,
	): String =
		script.replace(
			"idb",
			"idb -s $selectedDevice",
		)

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
