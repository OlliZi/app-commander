package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetConnectedDevicesUseCase(
    private val executeScriptUseCase: ExecuteScriptUseCase,
) {
    suspend operator fun invoke(): List<String> {
        return getConnectedAndroidDevices() + getConnectedIOSDevices()
    }

    private suspend fun getConnectedAndroidDevices(): List<String> {
        return when (val result = executeScriptUseCase(script = "adb devices")) {
            is ExecuteScriptUseCase.Result.Error -> emptyList()
            is ExecuteScriptUseCase.Result.Success -> {
                result.output
                    .split("\n")
                    .drop(1)
                    .filter { it.isNotEmpty() }
            }
        }
    }

    private suspend fun getConnectedIOSDevices(): List<String> {
        // TBD
        return emptyList()
    }
}