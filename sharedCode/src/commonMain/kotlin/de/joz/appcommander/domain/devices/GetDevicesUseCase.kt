package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import org.koin.core.annotation.Factory

@Factory
class GetDevicesUseCase(
	private val getSelectedDevicesUseCase: GetSelectedDevicesUseCase,
	private val getConnectedDevicesUseCase: GetConnectedDevicesUseCase,
) {
	suspend operator fun invoke(): List<Device> =
		runCatching {
			val selectedDevices = getSelectedDevicesUseCase()
			val devices = getConnectedDevicesUseCase().map {
				it.toDomainDevice(selectedDevices)
			}

			devices.sortedBy { device -> device.isSelected }
		}.getOrDefault(emptyList())

	private fun GetConnectedDevicesUseCase.ConnectedDevice.toDomainDevice(selectedDevices: List<Device>) =
		Device(
			id = id,
			label = label,
			isSelected = selectedDevices.any { selectedDevice ->
				selectedDevice.id == id
			},
		)
}
