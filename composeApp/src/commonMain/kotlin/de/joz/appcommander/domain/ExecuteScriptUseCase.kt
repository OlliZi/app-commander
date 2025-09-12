package de.joz.appcommander.domain

import org.koin.core.annotation.Factory
import java.io.File

@Factory
class ExecuteScriptUseCase(
    private val workingDir: File = File("."),
) {
    suspend operator fun invoke(
        script: String,
        selectedDevice: String = "",
    ): Result {
        return runCatching {
            Result.Success(
                output = ProcessBuilder(script.split(" "))
                    .directory(workingDir)
                    .start()
                    .inputReader()
                    .readText()
            )
        }.getOrElse {
            Result.Error(message = it.message ?: "Unknown error")
        }
    }

    sealed interface Result {
        data class Success(val output: String) : Result
        data class Error(val message: String) : Result
    }
}