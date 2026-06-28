package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import org.koin.core.annotation.Factory

@Factory
class GetSelectedDevicesUseCase(
	private val selectedDevicesRepository: SelectedDevicesRepository,
) {
	// // check if there are connectect?
	suspend operator fun invoke(): List<Device> = selectedDevicesRepository.getSelectedDevices()
}
