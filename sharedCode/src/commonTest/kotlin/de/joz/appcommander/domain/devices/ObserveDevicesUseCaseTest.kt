package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveDevicesUseCaseTest {
	private val getDevicesUseCaseMock: GetDevicesUseCase = mockk()

	@Test
	fun `should return a flow of devices`() =
		runTest {
			coEvery {
				getDevicesUseCaseMock.invoke()
			} returns listOf(
				Device(id = "id 1", label = "label 1", isSelected = true),
			)

			val useCase = ObserveDevicesUseCase(
				getDevicesUseCase = getDevicesUseCaseMock,
			)

			val collectedDevices = mutableListOf<List<Device>>()
			val job = launch {
				useCase().collect {
					collectedDevices.add(it)
				}
			}

			advanceTimeBy(3000.milliseconds)

			assertTrue(collectedDevices.isNotEmpty())

			job.cancel()
		}

	@Test
	fun `should return no devices when get devices crashes`() =
		runTest {
			coEvery {
				getDevicesUseCaseMock.invoke()
			} throws Exception()

			val useCase = ObserveDevicesUseCase(
				getDevicesUseCase = getDevicesUseCaseMock,
			)

			var collectedDevices: List<Device>? = listOf()
			val job = launch {
				collectedDevices = useCase().firstOrNull()
			}

			advanceTimeBy(3000.milliseconds)

			assertEquals(true, collectedDevices?.isEmpty())

			job.cancel()
		}
}
