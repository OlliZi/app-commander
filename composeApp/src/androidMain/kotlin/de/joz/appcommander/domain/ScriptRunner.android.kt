package de.joz.appcommander.domain

actual fun getScriptRunner(): ScriptRunner = AndroidScriptRunner()

internal class AndroidScriptRunner() : ScriptRunner {
    override suspend fun executeScript(
        script: String,
        selectedDevice: String
    ): ScriptRunner.Result {
        return ScriptRunner.Result.Error(message = "Not yet implemented")
    }
}