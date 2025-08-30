package de.joz.appcommander.domain

import java.io.BufferedReader
import java.io.InputStreamReader

actual fun getScriptRunner(): ScriptRunner = AndroidScriptRunner()

internal class AndroidScriptRunner() : ScriptRunner {
    override suspend fun executeScript(
        script: String,
        selectedDevice: String
    ): ScriptRunner.Result {
        return runCatching {
            // implement on Android (if possible)
            val process = Runtime.getRuntime().exec("logcat")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            ScriptRunner.Result.Success(output = bufferedReader.readText().take(100))
        }.getOrElse {
            ScriptRunner.Result.Error(message = it.message ?: "Unknown error")
        }
    }
}