package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import org.koin.core.annotation.Factory

@Factory
class SaveSelectedDevicesUseCase(
	private val selectedDevicesRepository: SelectedDevicesRepository,
) {
	suspend operator fun invoke(devices: List<Device>) =
		selectedDevicesRepository.saveSelectedDevices(
			devices.filter {
				it.isSelected
			},
		)
}
