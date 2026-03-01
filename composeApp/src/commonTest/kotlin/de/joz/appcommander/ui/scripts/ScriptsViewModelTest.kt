package de.joz.appcommander.ui.scripts

import androidx.navigation.NavController
import de.joz.appcommander.domain.logging.ClearLoggingUseCase
import de.joz.appcommander.domain.logging.GetLoggingUseCase
import de.joz.appcommander.domain.navigation.NavigationScreens
import de.joz.appcommander.domain.preference.ChangedPreference
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptsUseCase
import de.joz.appcommander.domain.script.OpenScriptFileUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.TrackScriptsFileChangesUseCase
import de.joz.appcommander.ui.model.ToolSection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ScriptsViewModelTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val getConnectedDevicesUseCaseMock: GetConnectedDevicesUseCase = mockk()
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val getUserScriptsUseCaseMock: GetUserScriptsUseCase = mockk()
	private val openScriptFileUseCaseMock: OpenScriptFileUseCase = mockk(relaxed = true)
	private val clearLoggingUseCaseMock: ClearLoggingUseCase = mockk(relaxed = true)
	private val getLoggingUseCaseMock: GetLoggingUseCase = mockk(relaxed = true)
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk(relaxed = true)
	private val trackScriptsFileChangesUseCaseMock: TrackScriptsFileChangesUseCase =
		mockk(relaxed = true)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)

	@BeforeTest
	fun setUp() {
		coEvery {
			getConnectedDevicesUseCaseMock()
		} returns
			listOf(
				GetConnectedDevicesUseCase.ConnectedDevice(
					id = "p7",
					label = "pixel 7",
				),
			)

		coEvery {
			getUserScriptsUseCaseMock()
		} returns
			ScriptsRepository.JsonParseResult(
				scripts =
					listOf(
						ScriptsRepository.Script(
							label = "my script",
							script = "foo",
							platform = ScriptsRepository.Platform.ANDROID,
						),
						ScriptsRepository.Script(
							label = "my another script",
							script = "bar",
							platform = ScriptsRepository.Platform.ANDROID,
						),
					),
				throwable = null,
			)
	}

	@Test
	fun `should load devices and scripts when viewmodel is initialized`() =
		runTest {
			val viewModel = createViewModel()
			runCurrent()

			assertEquals(
				listOf(
					ScriptsViewModel.Device(
						label = "pixel 7",
						id = "p7",
						isSelected = true,
					),
				),
				viewModel.uiState.value.connectedDevices,
			)

			assertEquals(
				listOf(
					ScriptsViewModel.Script(
						description = "my script",
						scriptText = "foo",
						originalScript =
							ScriptsRepository.Script(
								label = "my script",
								script = "foo",
								platform = ScriptsRepository.Platform.ANDROID,
							),
					),
					ScriptsViewModel.Script(
						description = "my another script",
						scriptText = "bar",
						originalScript =
							ScriptsRepository.Script(
								label = "my another script",
								script = "bar",
								platform = ScriptsRepository.Platform.ANDROID,
							),
					),
				),
				viewModel.uiState.value.scripts,
			)

			coVerify {
				getConnectedDevicesUseCaseMock()
				getUserScriptsUseCaseMock()
			}
		}

	@Test
	fun `should navigate to settings when event 'OnNavigateToSettings' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = ScriptsViewModel.Event.OnNavigateToSettings)
			runCurrent()

			verify {
				navControllerMock.navigate(NavigationScreens.SettingsScreen)
			}
		}

	@Test
	fun `should filter scripts when event 'OnFilterScripts' is fired`() =
		runTest {
			val viewModel = createViewModel()
			assertEquals(2, viewModel.uiState.value.scripts.size)

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnFilterScripts(
						filter = "bar",
					),
			)
			runCurrent()

			assertEquals(1, viewModel.uiState.value.scripts.size)
			assertTrue(
				viewModel.uiState.value.scripts
					.all {
						it.description.contains("bar") || it.scriptText.contains("bar")
					},
			)
			assertFalse(
				viewModel.uiState.value.scripts
					.any {
						it.description.contains("foo") || it.scriptText.contains("foo")
					},
			)
			verify {
				getUserScriptsUseCaseMock.invoke()
			}
		}

	@Test
	fun `should select device when event 'OnDeviceSelected' is fired`() =
		runTest {
			val viewModel = createViewModel()
			val device =
				viewModel.uiState.value.connectedDevices
					.first()
			val preSelectedState = device.isSelected

			viewModel.onEvent(event = ScriptsViewModel.Event.OnDeviceSelected(device = device))
			runCurrent()

			assertTrue(preSelectedState)
			assertFalse(
				viewModel.uiState.value.connectedDevices
					.first()
					.isSelected,
			)
		}

	@Test
	fun `should navigate to edit a new script when event 'OnNewScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = ScriptsViewModel.Event.OnNewScript)
			runCurrent()

			verify {
				navControllerMock.navigate(
					NavigationScreens.NewScriptScreen(
						scriptKey = null,
					),
				)
			}
		}

	@Test
	fun `should edit a script when event 'OnEditScript' is fired`() =
		runTest {
			val viewModel = createViewModel()
			every { getScriptIdUseCaseMock(any()) } returns 123

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnEditScript(
						script =
							viewModel.uiState.value.scripts
								.first(),
					),
			)
			runCurrent()

			verify {
				navControllerMock.navigate(
					NavigationScreens.NewScriptScreen(
						scriptKey = 123,
					),
				)
			}
		}

	@Test
	fun `should expand script when event 'OnExpandScript' is fired`() =
		runTest {
			val viewModel = createViewModel()
			val script =
				viewModel.uiState.value.scripts
					.first()
			val preExpandedState = script.isExpanded

			viewModel.onEvent(event = ScriptsViewModel.Event.OnExpandScript(script = script))
			runCurrent()

			assertFalse(preExpandedState)
			assertTrue(
				viewModel.uiState.value.scripts
					.first()
					.isExpanded,
			)
		}

	@Test
	fun `should execute script when event 'OnExecuteScript' is fired`() =
		runTest {
			val viewModel = createViewModel()
			val script =
				viewModel.uiState.value.scripts
					.first()

			coEvery {
				executeScriptUseCaseMock(
					script = script.originalScript,
					selectedDevice = "p7",
				)
			} returns ExecuteScriptUseCase.Result.Success(output = "")

			viewModel.onEvent(event = ScriptsViewModel.Event.OnExecuteScript(script = script))
			runCurrent()

			coVerify {
				executeScriptUseCaseMock(
					script = script.originalScript,
					selectedDevice = "p7",
				)
			}
		}

	@Test
	fun `should open script file when event 'OnOpenScriptFile' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = ScriptsViewModel.Event.OnOpenScriptFile)
			runCurrent()

			coVerify {
				openScriptFileUseCaseMock()
			}
		}

	@Test
	fun `should clear log when event 'OnClearLogging' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = ScriptsViewModel.Event.OnClearLogging)
			runCurrent()

			coVerify {
				clearLoggingUseCaseMock()
			}
		}

	@Test
	fun `should execute script when event 'OnExecuteScriptText' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnExecuteScriptText(
						script = "echo",
						platform = ScriptsRepository.Platform.ANDROID,
					),
			)
			runCurrent()

			coVerify {
				executeScriptUseCaseMock(
					script =
						ScriptsRepository.Script(
							label = "entered by terminal script",
							script = "echo",
							platform = ScriptsRepository.Platform.ANDROID,
						),
					selectedDevice = "p7",
				)
			}
		}

	@Test
	fun `should add index to log`() =
		runTest {
			every { getLoggingUseCaseMock() } returns flowOf(listOf("foo", "bar"))

			val viewModel = createViewModel()

			assertEquals(listOf("1. foo", "2. bar"), viewModel.uiState.value.logging)
		}

	@Test
	fun `should run script on devices when 'OnExecuteScript' is fired and multiples devices are selected`() =
		runTest {
			val testScript =
				ScriptsRepository.Script(
					label = "my script",
					script = "foo",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns
				listOf(
					GetConnectedDevicesUseCase.ConnectedDevice(id = "1", label = "P1"),
					GetConnectedDevicesUseCase.ConnectedDevice(id = "2", label = "P2"),
					GetConnectedDevicesUseCase.ConnectedDevice(id = "3", label = "P3"),
				)
			coEvery {
				executeScriptUseCaseMock(
					script = testScript,
					selectedDevice = "1",
				)
			} returns ExecuteScriptUseCase.Result.Success(output = "")
			coEvery {
				executeScriptUseCaseMock(
					script = testScript,
					selectedDevice = "3",
				)
			} returns ExecuteScriptUseCase.Result.Success(output = "")

			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnDeviceSelected(
						device =
							viewModel.uiState.value.connectedDevices
								.first(),
					),
			)
			runCurrent()
			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnDeviceSelected(
						device =
							viewModel.uiState.value.connectedDevices
								.last(),
					),
			)
			runCurrent()

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnExecuteScript(
						script =
							ScriptsViewModel.Script(
								originalScript = testScript,
								description = "",
								scriptText = "",
							),
					),
			)
			runCurrent()

			coVerify {
				executeScriptUseCaseMock(
					script = testScript,
					selectedDevice = "1",
				)
				executeScriptUseCaseMock(
					script = testScript,
					selectedDevice = "3",
				)
			}
		}

	@Test
	fun `should keep device selected when devices are refreshed`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns
				listOf(
					GetConnectedDevicesUseCase.ConnectedDevice(id = "1", label = "P1"),
					GetConnectedDevicesUseCase.ConnectedDevice(id = "2", label = "P2"),
				)
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnDeviceSelected(
						device = viewModel.uiState.value.connectedDevices[1],
					),
			)
			runCurrent()

			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns
				listOf(
					GetConnectedDevicesUseCase.ConnectedDevice(id = "1", label = "P1"),
					GetConnectedDevicesUseCase.ConnectedDevice(id = "2", label = "P2"),
					GetConnectedDevicesUseCase.ConnectedDevice(id = "3", label = "P3"),
				)

			viewModel.onEvent(event = ScriptsViewModel.Event.OnRefreshDevices)
			runCurrent()

			assertFalse(
				viewModel.uiState.value.connectedDevices[0]
					.isSelected,
			)
			assertTrue(
				viewModel.uiState.value.connectedDevices[1]
					.isSelected,
			)
			assertFalse(
				viewModel.uiState.value.connectedDevices[2]
					.isSelected,
			)
		}

	@Test
	fun `should reload scripts automatically when script are changed in the file`() =
		runTest {
			val mutableSharedFlow = MutableSharedFlow<ScriptsRepository.JsonParseResult>()
			coEvery {
				trackScriptsFileChangesUseCaseMock()
			} returns mutableSharedFlow

			coEvery {
				getUserScriptsUseCaseMock()
			} returns
				ScriptsRepository.JsonParseResult(
					scripts =
						listOf(
							ScriptsRepository.Script(
								label = "my script",
								script = "foo",
								platform = ScriptsRepository.Platform.ANDROID,
							),
						),
					throwable = null,
				)

			val viewModel = createViewModel()
			runCurrent()

			viewModel.onEvent(
				event =
					ScriptsViewModel.Event.OnExpandScript(
						viewModel.uiState.value.scripts
							.first(),
					),
			)
			runCurrent()

			mutableSharedFlow.emit(
				ScriptsRepository.JsonParseResult(
					scripts =
						listOf(
							ScriptsRepository.Script(
								label = "my script",
								script = "foo",
								platform = ScriptsRepository.Platform.ANDROID,
							),
							ScriptsRepository.Script(
								label = "abc",
								script = "123",
								platform = ScriptsRepository.Platform.IOS,
							),
						),
					throwable = null,
				),
			)

			assertEquals(
				listOf(
					ScriptsViewModel.Script(
						description = "my script",
						scriptText = "foo",
						isExpanded = true,
						originalScript =
							ScriptsRepository.Script(
								label = "my script",
								script = "foo",
								platform = ScriptsRepository.Platform.ANDROID,
							),
					),
					ScriptsViewModel.Script(
						description = "abc",
						scriptText = "123",
						isExpanded = false,
						originalScript =
							ScriptsRepository.Script(
								label = "abc",
								script = "123",
								platform = ScriptsRepository.Platform.IOS,
							),
					),
				),
				viewModel.uiState.value.scripts,
			)
		}

	@Test
	fun `should return an error when JSON contains invalid scripts`() =
		runTest {
			coEvery {
				getUserScriptsUseCaseMock()
			} returns
				ScriptsRepository.JsonParseResult(
					scripts = emptyList(),
					throwable = Exception("Cannot parse JSON"),
				)

			val viewModel = createViewModel()
			runCurrent()

			assertEquals("Cannot parse JSON", viewModel.uiState.value.jsonParsingError)
		}

	@Test
	fun `should update tool section items when preferences are changed`() =
		runTest {
			val keys = ToolSection.entries.map { it.name }
			val mutableFlow =
				MutableStateFlow(
					listOf(
						ChangedPreference(
							key = ToolSection.TERMINAL.name,
							value = true,
						),
					),
				)

			coEvery {
				getPreferenceUseCaseMock.getAsFlow(keys = keys.toTypedArray())
			} returns mutableFlow

			val viewModel = createViewModel()
			runCurrent()

			assertEquals(
				ToolSection.entries,
				viewModel.uiState.value.toolSections,
			)

			mutableFlow.emit(
				listOf(
					ChangedPreference(
						key = ToolSection.TERMINAL.name,
						value = true,
					),
					ChangedPreference(
						key = ToolSection.FILTER.name,
						value = false,
					),
					ChangedPreference(
						key = ToolSection.LOGGING.name,
						value = false,
					),
				),
			)

			assertEquals(
				listOf(ToolSection.TERMINAL),
				viewModel.uiState.value.toolSections,
			)
		}

	private fun createViewModel(): ScriptsViewModel =
		ScriptsViewModel(
			navController = navControllerMock,
			getConnectedDevicesUseCase = getConnectedDevicesUseCaseMock,
			executeScriptUseCase = executeScriptUseCaseMock,
			getUserScriptsUseCase = getUserScriptsUseCaseMock,
			openScriptFileUseCase = openScriptFileUseCaseMock,
			trackScriptsFileChangesUseCase = trackScriptsFileChangesUseCaseMock,
			clearLoggingUseCase = clearLoggingUseCaseMock,
			getLoggingUseCase = getLoggingUseCaseMock,
			getScriptIdUseCase = getScriptIdUseCaseMock,
			mainDispatcher = Dispatchers.Unconfined,
			getPreferenceUseCase = getPreferenceUseCaseMock,
			ioDispatcher = Dispatchers.Unconfined,
		)
}
