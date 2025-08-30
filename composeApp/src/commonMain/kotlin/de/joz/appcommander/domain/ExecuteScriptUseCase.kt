package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
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