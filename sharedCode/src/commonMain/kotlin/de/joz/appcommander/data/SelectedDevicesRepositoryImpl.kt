package de.joz.appcommander.data

import de.joz.appcommander.domain.devices.SelectedDevicesRepository
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class SelectedDevicesRepositoryImpl(
	private val savePreferenceUseCase: SavePreferenceUseCase,
	private val getPreferenceUseCase: GetPreferenceUseCase,
	private val jsonHandler: Json,
) : SelectedDevicesRepository {
	override suspend fun getSelectedDevices(): List<Device> =
		runCatching {
			val devicesJson = getPreferenceUseCase.get(SELECTED_DEVICES_PREF_KEY, "")
			jsonHandler.decodeFromString<List<Device>>(devicesJson)
		}.getOrDefault(emptyList())

	override suspend fun saveSelectedDevices(devices: List<Device>) {
		val devicesJson = jsonHandler.encodeToString(devices)
		savePreferenceUseCase(SELECTED_DEVICES_PREF_KEY, devicesJson)
	}

	companion object {
		private const val SELECTED_DEVICES_PREF_KEY = "SELECTED_DEVICES"
	}
}
