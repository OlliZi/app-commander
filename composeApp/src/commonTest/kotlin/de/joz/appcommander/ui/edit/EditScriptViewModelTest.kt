package de.joz.appcommander.ui.edit

import androidx.navigation.NavController
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.script.GetConnectedDevicesUseCase.ConnectedDevice
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.RunFileBackupUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.ui.misc.model.Device
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditScriptViewModelTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock: SaveUserScriptUseCase = mockk(relaxed = true)
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val removeUserScriptUseCaseMock: RemoveUserScriptUseCase = mockk(relaxed = true)
	private val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = false)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)
	private val getConnectedDevicesUseCaseMock: GetConnectedDevicesUseCase = mockk(relaxed = true)

	@BeforeTest
	fun setUp() {
		every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns null
	}

	@Test
	fun `should at least add at minimum one empty script when there was no script provided for the edit session`() =
		runTest {
			val viewModel = createViewModel()

			assertEquals(1, viewModel.uiState.value.scriptUiState.scripts.size)
			assertTrue(
				viewModel.uiState.value.scriptUiState.scripts[0]
					.isEmpty(),
			)
			assertTrue(
				viewModel.uiState.value.scriptUiState.scriptName
					.isEmpty(),
			)
			assertEquals(ScriptsRepository.Platform.ANDROID, viewModel.uiState.value.scriptUiState.selectedPlatform)
			assertTrue(viewModel.uiState.value.showDeviceSelection)
			assertTrue(
				viewModel.uiState.value.connectedDevices
					.isEmpty(),
			)
			assertFalse(viewModel.uiState.value.scriptChanged)
			assertTrue(
				viewModel.uiState.value.errorMessages
					.isEmpty(),
			)
		}

	@Test
	fun `should navigate back when event 'OnNavigateBack' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = EditScriptViewModel.Event.OnNavigateBack)
			runCurrent()

			verify {
				navControllerMock.navigateUp()
			}
		}

	@Test
	fun `should detect no changes when script was not changed`() =
		runTest {
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns ScriptsRepository.Script(
				label = "label",
				scripts = listOf("script 1"),
				platform = ScriptsRepository.Platform.IOS,
			)

			val viewModel = createViewModel()

			assertFalse(viewModel.uiState.value.scriptChanged)
		}

	@Test
	fun `should detect changes when script was changed`() =
		runTest {
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns ScriptsRepository.Script(
				label = "label",
				scripts = listOf("script 1"),
				platform = ScriptsRepository.Platform.IOS,
			)

			val viewModel = createViewModel()

			viewModel.onEvent(event = EditScriptViewModel.Event.OnChangeScriptName("new name"))
			assertTrue(viewModel.uiState.value.scriptChanged)
			viewModel.onEvent(event = EditScriptViewModel.Event.OnChangeScriptName("label"))
			assertFalse(viewModel.uiState.value.scriptChanged)

			viewModel.onEvent(event = EditScriptViewModel.Event.OnSelectPlatform(ScriptsRepository.Platform.ANDROID))
			assertTrue(viewModel.uiState.value.scriptChanged)
			viewModel.onEvent(event = EditScriptViewModel.Event.OnSelectPlatform(ScriptsRepository.Platform.IOS))
			assertFalse(viewModel.uiState.value.scriptChanged)

			viewModel.onEvent(event = EditScriptViewModel.Event.OnChangeScript(0, "foo"))
			assertTrue(viewModel.uiState.value.scriptChanged)
			viewModel.onEvent(event = EditScriptViewModel.Event.OnChangeScript(0, "script 1"))
			assertFalse(viewModel.uiState.value.scriptChanged)

			viewModel.onEvent(event = EditScriptViewModel.Event.OnAddSubScript(0))
			assertTrue(viewModel.uiState.value.scriptChanged)
			viewModel.onEvent(event = EditScriptViewModel.Event.OnRemoveSubScript(1))
			assertFalse(viewModel.uiState.value.scriptChanged)
		}

	@Test
	fun `should refresh devices when event 'OnRefreshDevices' is fired`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock.invoke()
			} returns listOf(ConnectedDevice(id = "1", label = "label 1"))
			val viewModel = createViewModel()

			viewModel.onEvent(event = EditScriptViewModel.Event.OnRefreshDevices)
			runCurrent()

			assertEquals(
				Device(
					id = "1",
					label = "label 1",
					isSelected = true,
				),
				viewModel.uiState.value.connectedDevices
					.first(),
			)

			coVerify(exactly = 2) {
				getConnectedDevicesUseCaseMock.invoke()
			}
		}

	@Test
	fun `should refresh devices and disable selected devices when event 'OnDeviceSelected' is fired`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock.invoke()
			} returns listOf(ConnectedDevice(id = "1", label = "label 1"), ConnectedDevice(id = "2", label = "label 2"))
			val viewModel = createViewModel()

			viewModel.onEvent(event = EditScriptViewModel.Event.OnRefreshDevices)
			runCurrent()

			assertEquals(
				listOf(
					Device(
						id = "1",
						label = "label 1",
						isSelected = false,
					),
					Device(
						id = "2",
						label = "label 2",
						isSelected = false,
					),
				),
				viewModel.uiState.value.connectedDevices,
			)

			coVerify(exactly = 2) {
				getConnectedDevicesUseCaseMock.invoke()
			}
		}

	@Test
	fun `should select device when event 'OnDeviceSelected' is fired`() =
		runTest {
			coEvery { getConnectedDevicesUseCaseMock() } returnsMany listOf(
				listOf(
					ConnectedDevice(
						id = "id 1",
						label = "device 1",
					),
					ConnectedDevice(
						id = "id 2",
						label = "device 2",
					),
				),
			)
			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnDeviceSelected(
					device = Device(
						id = "id 1",
						label = "device 1",
						isSelected = false,
					),
				),
			)

			assertEquals(
				listOf(
					Device(
						id = "id 1",
						label = "device 1",
						isSelected = true,
					),
					Device(
						id = "id 2",
						label = "device 2",
						isSelected = false,
					),
				),
				viewModel.uiState.value.connectedDevices,
			)
		}

	@Test
	fun `should select platform when event 'OnSelectPlatform' is fired`() =
		runTest {
			val viewModel = createViewModel()
			assertEquals(ScriptsRepository.Platform.ANDROID, viewModel.uiState.value.scriptUiState.selectedPlatform)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnSelectPlatform(
					platform = ScriptsRepository.Platform.IOS,
				),
			)
			runCurrent()

			assertEquals(ScriptsRepository.Platform.IOS, viewModel.uiState.value.scriptUiState.selectedPlatform)
			assertTrue(viewModel.uiState.value.showDeviceSelection)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnSelectPlatform(
					platform = ScriptsRepository.Platform.ANDROID,
				),
			)
			runCurrent()

			assertEquals(ScriptsRepository.Platform.ANDROID, viewModel.uiState.value.scriptUiState.selectedPlatform)
			assertTrue(viewModel.uiState.value.showDeviceSelection)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnSelectPlatform(
					platform = ScriptsRepository.Platform.DESKTOP,
				),
			)
			runCurrent()

			assertEquals(ScriptsRepository.Platform.DESKTOP, viewModel.uiState.value.scriptUiState.selectedPlatform)
			assertFalse(viewModel.uiState.value.showDeviceSelection)
		}

	@Test
	fun `should change script when event 'OnChangeScript' is fired`() =
		runTest {
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns ScriptsRepository.Script(
				label = "label",
				scripts = listOf("script 1", "script 2"),
				platform = ScriptsRepository.Platform.IOS,
			)

			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnChangeScript(
					index = 1,
					script = "new script 2",
				),
			)
			runCurrent()

			assertEquals(listOf("script 1", "new script 2"), viewModel.uiState.value.scriptUiState.scripts)
			assertEquals(ScriptsRepository.Platform.IOS, viewModel.uiState.value.scriptUiState.selectedPlatform)
			assertEquals("label", viewModel.uiState.value.scriptUiState.scriptName)
		}

	@Test
	fun `should change name of script when event 'OnChangeScriptName' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnChangeScriptName(
					scriptName = "new name",
				),
			)
			runCurrent()

			assertEquals("new name", viewModel.uiState.value.scriptUiState.scriptName)
		}

	@Test
	fun `should add a new script under existing script when event 'OnAddSubScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			assertEquals(1, viewModel.uiState.value.scriptUiState.scripts.size)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnAddSubScript(
					index = 0,
				),
			)
			runCurrent()

			assertEquals(2, viewModel.uiState.value.scriptUiState.scripts.size)
			assertEquals("<enter new script>", viewModel.uiState.value.scriptUiState.scripts[1])
		}

	@Test
	fun `should remove script when event 'OnRemoveSubScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			assertEquals(1, viewModel.uiState.value.scriptUiState.scripts.size)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnRemoveSubScript(
					index = 0,
				),
			)
			runCurrent()

			assertEquals(1, viewModel.uiState.value.scriptUiState.scripts.size)
			assertEquals("", viewModel.uiState.value.scriptUiState.scripts[0])
		}

	@Test
	fun `should remove script on correct index when event 'OnRemoveSubScript' is fired`() =
		runTest {
			val testScript = ScriptsRepository.Script(
				label = "label",
				scripts = listOf("script 1", "script 2", "script 3"),
				platform = ScriptsRepository.Platform.IOS,
			)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			val viewModel = createViewModel()

			assertEquals(3, viewModel.uiState.value.scriptUiState.scripts.size)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnRemoveSubScript(
					index = 1,
				),
			)
			runCurrent()

			assertEquals(2, viewModel.uiState.value.scriptUiState.scripts.size)
			assertEquals("script 1", viewModel.uiState.value.scriptUiState.scripts[0])
			assertEquals("script 3", viewModel.uiState.value.scriptUiState.scripts[1])
		}

	@Test
	fun `should save script and close screen when event 'OnSaveScript' is fired and there are no error messages`() =
		runTest {
			val viewModel = createViewModel()

			coEvery { saveUserScriptUseCaseMock.invoke(any(), null) } returns SaveUserScriptUseCase.Result(
				backupMessage = null,
				writeScriptMessage = null,
			)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnSaveScript,
			)
			runCurrent()

			assertTrue(
				viewModel.uiState.value.errorMessages
					.isEmpty(),
			)
			coVerify { saveUserScriptUseCaseMock.invoke(any(), null) }
			verify { navControllerMock.navigateUp() }
		}

	@Test
	fun `should save script and not close screen when event 'OnSaveScript' is fired and there are some error messages`() =
		runTest {
			val viewModel = createViewModel()

			coEvery { saveUserScriptUseCaseMock.invoke(any(), null) } returns SaveUserScriptUseCase.Result(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupFile("foo"),
				writeScriptMessage = null,
			)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnSaveScript,
			)
			runCurrent()

			assertTrue(
				viewModel.uiState.value.errorMessages
					.isNotEmpty(),
			)
			coVerify { saveUserScriptUseCaseMock.invoke(any(), null) }
			verify(exactly = 0) { navControllerMock.navigateUp() }
		}

	@Test
	fun `should save script with script id when event 'OnSaveScript' is fired`() =
		runTest {
			val viewModel = createViewModel(
				scriptKey = 1,
			)

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnSaveScript,
			)
			runCurrent()

			coVerify { saveUserScriptUseCaseMock.invoke(any(), 1) }
			coVerify { getScriptIdUseCaseMock.invoke(any()) }
		}

	@Test
	fun `should apply script when script is loaded over constructor`() =
		runTest {
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns ScriptsRepository.Script(
				scripts = listOf("foo"),
				label = "bar",
				platform = ScriptsRepository.Platform.IOS,
			)

			val viewModel = createViewModel(
				scriptKey = 1,
			)

			assertEquals(listOf("foo"), viewModel.uiState.value.scriptUiState.scripts)
			assertEquals("bar", viewModel.uiState.value.scriptUiState.scriptName)
			assertEquals(ScriptsRepository.Platform.IOS, viewModel.uiState.value.scriptUiState.selectedPlatform)

			coVerify { getUserScriptByKeyUseCaseMock.invoke(any()) }
		}

	@Test
	fun `should remove script when event 'OnRemoveScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnRemoveScript,
			)
			runCurrent()

			coVerify { removeUserScriptUseCaseMock.invoke(any()) }
			verify { navControllerMock.navigateUp() }
		}

	@Test
	fun `should execute script when event 'OnExecuteAllScripts' is fired`() =
		runTest {
			val testScript = ScriptsRepository.Script(
				label = "label",
				scripts = listOf("script 1", "script 2"),
				platform = ScriptsRepository.Platform.IOS,
			)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			coEvery { getConnectedDevicesUseCaseMock() } returnsMany listOf(
				listOf(
					ConnectedDevice(
						id = "id 1",
						label = "device 1",
					),
				),
			)

			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnExecuteAllScripts,
			)
			runCurrent()

			coVerify { executeScriptUseCaseMock.invoke(script = testScript, selectedDevice = "id 1") }
		}

	@Test
	fun `should execute script when event 'OnExecuteSingleScript' is fired`() =
		runTest {
			val testScript = ScriptsRepository.Script(
				label = "",
				scripts = listOf("script 1", "script 2"),
				platform = ScriptsRepository.Platform.IOS,
			)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			coEvery { getConnectedDevicesUseCaseMock() } returnsMany listOf(
				listOf(
					ConnectedDevice(
						id = "id 1",
						label = "device 1",
					),
				),
			)

			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnExecuteSingleScript("script 2"),
			)
			runCurrent()

			coVerify {
				executeScriptUseCaseMock.invoke(
					script = ScriptsRepository.Script(
						label = "",
						scripts = listOf("script 2"),
						platform = ScriptsRepository.Platform.IOS,
					),
					selectedDevice = "id 1",
				)
			}
		}

	@Test
	fun `should execute script and ignore selected device when event 'OnExecuteSingleScript' is fired on DESKTOP`() =
		runTest {
			val testScript = ScriptsRepository.Script(
				label = "",
				scripts = listOf("script 1", "script 2"),
				platform = ScriptsRepository.Platform.DESKTOP,
			)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			coEvery { getConnectedDevicesUseCaseMock() } returnsMany listOf(
				listOf(
					ConnectedDevice(
						id = "id 1",
						label = "device 1",
					),
				),
			)

			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnExecuteSingleScript("script 2"),
			)
			runCurrent()

			coVerify {
				executeScriptUseCaseMock.invoke(
					script = ScriptsRepository.Script(
						label = "",
						scripts = listOf("script 2"),
						platform = ScriptsRepository.Platform.DESKTOP,
					),
					selectedDevice = "",
				)
			}
		}

	@Test
	fun `should execute script and ignore selected device when event 'OnExecuteAllScripts' is fired on DESKTOP`() =
		runTest {
			val testScript = ScriptsRepository.Script(
				label = "",
				scripts = listOf("script 1", "script 2"),
				platform = ScriptsRepository.Platform.DESKTOP,
			)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			coEvery { getConnectedDevicesUseCaseMock() } returnsMany listOf(
				listOf(
					ConnectedDevice(
						id = "id 1",
						label = "device 1",
					),
				),
			)

			val viewModel = createViewModel()

			viewModel.onEvent(
				event = EditScriptViewModel.Event.OnExecuteAllScripts,
			)
			runCurrent()

			coVerify {
				executeScriptUseCaseMock.invoke(
					script = ScriptsRepository.Script(
						label = "",
						scripts = listOf("script 1", "script 2"),
						platform = ScriptsRepository.Platform.DESKTOP,
					),
					selectedDevice = "",
				)
			}
		}

	private fun createViewModel(scriptKey: Int? = null): EditScriptViewModel =
		EditScriptViewModel(
			scriptKey = scriptKey,
			navController = navControllerMock,
			executeScriptUseCase = executeScriptUseCaseMock,
			saveUserScriptUseCase = saveUserScriptUseCaseMock,
			removeUserScriptUseCase = removeUserScriptUseCaseMock,
			getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
			getScriptIdUseCase = getScriptIdUseCaseMock,
			getConnectedDevicesUseCase = getConnectedDevicesUseCaseMock,
			saveUserScriptUseCaseResultMapper = SaveUserScriptUseCaseResultMapper(),
			mainDispatcher = Dispatchers.Unconfined,
			ioDispatcher = Dispatchers.Unconfined,
		)
}
