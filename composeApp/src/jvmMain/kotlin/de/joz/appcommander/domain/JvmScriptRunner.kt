package de.joz.appcommander.domain

import java.io.File

actual fun getScriptRunner(): ScriptRunner = JvmScriptRunner()

internal class JvmScriptRunner(
    private val workingDir: File = File("."),
) : ScriptRunner {
    override suspend fun executeScript(
        script: String,
        selectedDevice: String
    ): ScriptRunner.Result {
        return runCatching {
            ScriptRunner.Result.Success(
                output = ProcessBuilder(script.split(" "))
                    .directory(workingDir)
                    .start()
                    .inputReader()
                    .readText()
            )
        }.getOrElse {
            ScriptRunner.Result.Error(message = it.message ?: "Unknown error")
        }
    }
}
