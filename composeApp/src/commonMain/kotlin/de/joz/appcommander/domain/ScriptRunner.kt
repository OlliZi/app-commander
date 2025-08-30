package de.joz.appcommander.domain

interface ScriptRunner {

    suspend fun executeScript(
        script: String,
        selectedDevice: String,
    ): Result

    sealed interface Result {
        data class Success(val output: String) : Result
        data class Error(val message: String) : Result
    }
}

expect fun getScriptRunner(): ScriptRunner