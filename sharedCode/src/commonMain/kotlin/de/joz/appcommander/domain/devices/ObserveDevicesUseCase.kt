package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds

@Single
class ObserveDevicesUseCase(
	private val getDevicesUseCase: GetDevicesUseCase,
) {
	private val flowOfDevices by lazy {
		MutableSharedFlow<List<Device>>().onStart {
			runCatching {
				while (true) {
					emit(getDevicesUseCase())
					delay(WAIT_DELAY)
				}
			}.onFailure {
				println(it.message)
			}
		}
	}

	operator fun invoke(): Flow<List<Device>> = flowOfDevices

	companion object {
		private val WAIT_DELAY = 3000.milliseconds
	}
}
