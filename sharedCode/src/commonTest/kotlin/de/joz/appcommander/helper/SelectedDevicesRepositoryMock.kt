package de.joz.appcommander.helper

import de.joz.appcommander.domain.devices.SelectedDevicesRepository
import de.joz.appcommander.domain.model.Device

class SelectedDevicesRepositoryMock(
	var testDevices: List<Device> = emptyList(),
) : SelectedDevicesRepository {
	private var callSaveCounter = 0

	fun getCounterSaveAndReset(): Int {
		val counter = callSaveCounter
		callSaveCounter = 0
		return counter
	}

	override suspend fun getSelectedDevices(): List<Device> {
		TODO("Not yet implemented")
	}

	override suspend fun saveSelectedDevices(devices: List<Device>) {
		callSaveCounter++
		testDevices = devices
	}
}
