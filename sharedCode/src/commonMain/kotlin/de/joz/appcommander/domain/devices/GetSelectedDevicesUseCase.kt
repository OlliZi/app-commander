package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import org.koin.core.annotation.Factory

@Factory
class GetSelectedDevicesUseCase(
	private val selectedDevicesRepository: SelectedDevicesRepository,
	private val getConnectedDevicesUseCase: GetConnectedDevicesUseCase,
	private val saveSelectedDevicesUseCase: SaveSelectedDevicesUseCase,
) {
	suspend operator fun invoke(): List<Device> {
		val connectedDevices = getConnectedDevicesUseCase()
		val selectedDevices = selectedDevicesRepository.getSelectedDevices()

		val connected = selectedDevices.filter { device ->
			connectedDevices.any { it.id == device.id }
		}

		// udapte save devices?
		saveSelectedDevicesUseCase(devices = connected)

		return connected
		// filter all not connected devices
	}
}
