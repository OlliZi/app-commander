package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import org.koin.core.annotation.Factory

interface GetDevicesUseCase {
	suspend operator fun invoke(): List<Device>
}

@Factory
class GetDevicesUseCaseImpl(
	private val getSelectedDevicesUseCase: GetSelectedDevicesUseCase,
	private val getConnectedDevicesUseCase: GetConnectedDevicesUseCase,
) : GetDevicesUseCase {
	override suspend operator fun invoke(): List<Device> =
		runCatching {
			val selectedDevices = getSelectedDevicesUseCase()
			getConnectedDevicesUseCase().map {
				it.toDomainDevice(selectedDevices)
			}
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
