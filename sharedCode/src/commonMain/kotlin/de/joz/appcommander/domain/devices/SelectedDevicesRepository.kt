package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device

interface SelectedDevicesRepository {
	suspend fun getSelectedDevices(): List<Device>

	suspend fun saveSelectedDevices(devices: List<Device>)
}
