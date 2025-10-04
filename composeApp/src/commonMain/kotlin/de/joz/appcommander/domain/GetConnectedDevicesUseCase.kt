package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetConnectedDevicesUseCase(
    private val executeScriptUseCase: ExecuteScriptUseCase,
) {
    suspend operator fun invoke(): List<ConnectedDevice> = getConnectedAndroidDevices() + getConnectedIOSDevices()

    private suspend fun getConnectedAndroidDevices(): List<ConnectedDevice> =
        when (val result = executeScriptUseCase(script = ANDROID_GET_DEVICES_SCRIPT)) {
            is ExecuteScriptUseCase.Result.Error -> emptyList()
            is ExecuteScriptUseCase.Result.Success -> {
                result.output
                    .split("\n")
                    .drop(1)
                    .filter { it.isNotEmpty() }
                    .map {
                        ConnectedDevice(
                            id = it.split("\t").first(),
                            label = it,
                        )
                    }
            }
        }

    private suspend fun getConnectedIOSDevices(): List<ConnectedDevice> {
        // TODO
        return when (val result = executeScriptUseCase(script = IOS_GET_DEVICES_SCRIPT)) {
            is ExecuteScriptUseCase.Result.Error -> emptyList()
            is ExecuteScriptUseCase.Result.Success -> {
                result.output
                    .split("\n")
                    .drop(1)
                    .filter { it.isNotEmpty() }
                    .map {
                        ConnectedDevice(
                            id = it, // TODO parse device id
                            label = it,
                        )
                    }
            }
        }
    }

    data class ConnectedDevice(
        val id: String,
        val label: String,
    )

    companion object {
        val ANDROID_GET_DEVICES_SCRIPT =
            ScriptsRepository.Script(
                label = "Get connected Android devices",
                script = "adb devices",
                platform = ScriptsRepository.Platform.ANDROID,
            )
        val IOS_GET_DEVICES_SCRIPT =
            ScriptsRepository.Script(
                label = "Get connected iOS devices",
                script = "TODO for iOS",
                platform = ScriptsRepository.Platform.IOS,
            )
    }
}
