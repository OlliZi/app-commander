package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.milliseconds

@Factory
class ObserveDevicesUseCase(
	private val getDevicesUseCase: GetDevicesUseCase,
) {
	operator fun invoke(): Flow<List<Device>> =
		flow {
			runCatching {
				while (true) {
					emit(getDevicesUseCase())
					delay(WAIT_DELAY)
				}
			}.onFailure {
				println(it.message)
			}
		}

	companion object {
		private val WAIT_DELAY = 5000.milliseconds
	}
}
