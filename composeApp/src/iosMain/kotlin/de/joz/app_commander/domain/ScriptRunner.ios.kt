package de.joz.app_commander.domain

actual fun getScriptRunner(): ScriptRunner = IOSScriptRunner()

internal class IOSScriptRunner() : ScriptRunner {
    override suspend fun executeScript(
        script: String,
        selectedDevice: String
    ): ScriptRunner.Result {
        return ScriptRunner.Result.Error(message = "Not yet implemented")
    }
}