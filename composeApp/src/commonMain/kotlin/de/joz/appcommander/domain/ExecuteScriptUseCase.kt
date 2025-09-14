package de.joz.appcommander.domain

import org.koin.core.annotation.Factory
import java.io.File

@Factory
class ExecuteScriptUseCase(
    private val workingDir: File = File("."),
    private val processBuilder: ProcessBuilder = ProcessBuilder(),
) {
    suspend operator fun invoke(
        script: ScriptsRepository.Script,
        selectedDevice: String = "",
    ): Result {
        val scriptForSelectedDevice = injectDeviceConfig(script, selectedDevice)
        println("Execute script: '$scriptForSelectedDevice' on device '$selectedDevice' ...")

        return runCatching {
            Result.Success(
                output = processBuilder.command(scriptForSelectedDevice.split(" "))
                    .directory(workingDir)
                    .start()
                    .inputReader()
                    .readText()
                    .also {
                        println("Script executed: '$scriptForSelectedDevice'.")
                    }
            )
        }.getOrElse {
            Result.Error(message = it.message ?: "Unknown error")
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
        data class Success(val output: String) : Result
        data class Error(val message: String) : Result
    }
}