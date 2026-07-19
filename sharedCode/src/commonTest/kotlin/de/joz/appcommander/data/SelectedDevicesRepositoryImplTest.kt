package de.joz.appcommander.data

import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectedDevicesRepositoryImplTest {
	private val savePreferenceUseCaseMock: SavePreferenceUseCase = mockk(relaxed = true)
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk(relaxed = true)
	private val jsonHandlerMock = DependencyInjection().provideJson()

	@Test
	fun `should save selected devices when repository is executed`() =
		runTest {
			val devices = listOf(
				Device(id = "id 1", label = "label 1", isSelected = true),
				Device(id = "id 2", label = "label 2", isSelected = false),
			)
			val repository = createRepository()

			repository.saveSelectedDevices(devices = devices)

			coVerify {
				savePreferenceUseCaseMock.invoke(
					key = "SELECTED_DEVICES",
					value = jsonHandlerMock.encodeToString(devices),
				)
			}
			coVerify {
				getPreferenceUseCaseMock wasNot called
			}
		}

	@Test
	fun `should save selected devices when repository is executed and list is empty`() =
		runTest {
			val repository = createRepository()

			repository.saveSelectedDevices(devices = emptyList())

			coVerify {
				savePreferenceUseCaseMock.invoke(
					key = "SELECTED_DEVICES",
					value = jsonHandlerMock.encodeToString(emptyList<Device>()),
				)
			}
			coVerify {
				getPreferenceUseCaseMock wasNot called
			}
		}

	@Test
	fun `should get selected devices when repository is executed`() =
		runTest {
			val devices = listOf(
				Device(id = "id 1", label = "label 1", isSelected = true),
				Device(id = "id 2", label = "label 2", isSelected = false),
			)
			coEvery {
				getPreferenceUseCaseMock.get("SELECTED_DEVICES", "")
			} returns jsonHandlerMock.encodeToString(devices)

			val repository = createRepository()

			assertEquals(devices, repository.getSelectedDevices())

			coVerify {
				savePreferenceUseCaseMock wasNot called
			}
			coVerify {
				getPreferenceUseCaseMock.get(
					key = "SELECTED_DEVICES",
					defaultValue = "",
				)
			}
		}

	@Test
	fun `should return empty list when storage contains no devices`() =
		runTest {
			coEvery {
				getPreferenceUseCaseMock.get("SELECTED_DEVICES", "")
			} throws Exception()
			val repository = createRepository()

			assertTrue(repository.getSelectedDevices().isEmpty())

			coVerify {
				savePreferenceUseCaseMock wasNot called
			}
			coVerify {
				getPreferenceUseCaseMock.get(
					key = "SELECTED_DEVICES",
					defaultValue = "",
				)
			}
		}

	@Test
	fun `should return empty list when JSON is not parsable`() =
		runTest {
			coEvery {
				getPreferenceUseCaseMock.get("SELECTED_DEVICES", "")
			} returns "not valid JSON"

			coEvery {
				getPreferenceUseCaseMock.get("SELECTED_DEVICES", "")
			} throws Exception()
			val repository = createRepository()

			assertTrue(repository.getSelectedDevices().isEmpty())

			coVerify {
				savePreferenceUseCaseMock wasNot called
			}
			coVerify {
				getPreferenceUseCaseMock.get(
					key = "SELECTED_DEVICES",
					defaultValue = "",
				)
			}
		}

	private fun createRepository() =
		SelectedDevicesRepositoryImpl(
			savePreferenceUseCase = savePreferenceUseCaseMock,
			getPreferenceUseCase = getPreferenceUseCaseMock,
			jsonHandler = jsonHandlerMock,
		)
}
