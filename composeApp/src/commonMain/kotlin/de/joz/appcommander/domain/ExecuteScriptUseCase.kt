package de.joz.appcommander.domain

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import org.koin.core.annotation.Factory
import java.io.File

@Factory
class ExecuteScriptUseCase(
    private val addLoggingUseCase: AddLoggingUseCase,
    private val workingDir: File = File("."),
    private val processBuilder: ProcessBuilder = ProcessBuilder(),
) {
    suspend operator fun invoke(
        script: ScriptsRepository.Script,
        selectedDevice: String = "",
    ): Result {
        val scriptForSelectedDevice = injectDeviceConfig(script, selectedDevice)
        val commands = scriptForSelectedDevice.split(" ")
        addLoggingUseCase("Execute script: '$scriptForSelectedDevice' on device '$selectedDevice'.")

        return runCatching {
            val output =
                processBuilder.command(commands).directory(workingDir).start().inputReader()
                    .readText()

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

    private fun injectDeviceConfig(
        script: ScriptsRepository.Script,
        selectedDevice: String,
    ): String {
        if (selectedDevice.isEmpty()) {
            return script.script
        }

        return when (script.platform) {
            ScriptsRepository.Platform.ANDROID -> script.script.replace(
                "adb",
                "adb -s $selectedDevice",
            )

            // TODO
            ScriptsRepository.Platform.IOS -> script.script
        }
    }

    sealed interface Result {
        data class Success(
            val output: String,
            val commands: List<String>,
        ) : Result

        data class Error(val message: String) : Result
    }
}