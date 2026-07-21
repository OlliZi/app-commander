package de.joz.appcommander.helper

import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.model.Device

class GetDevicesUseCaseMock(
	private val devicesLambda: () -> List<Device>,
) : GetDevicesUseCase {
	private var callCounter = 0

	fun getCounterAndReset(): Int {
		val counter = callCounter
		callCounter = 0
		return counter
	}

	override suspend fun invoke(): List<Device> {
		callCounter++
		return devicesLambda()
	}
}
