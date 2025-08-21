package de.joz.app_commander.domain

class ExecuteScriptUseCase(
    private val scriptRunner: ScriptRunner = getScriptRunner(),
) {
    suspend operator fun invoke(
        script: String,
        selectedDevice: String,
    ): ScriptRunner.Result {
        return scriptRunner.executeScript(script = script, selectedDevice = selectedDevice)
    }
}