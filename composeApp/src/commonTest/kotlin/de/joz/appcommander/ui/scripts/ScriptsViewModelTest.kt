package de.joz.appcommander.ui.scripts

import androidx.navigation.NavController
import de.joz.appcommander.domain.ExecuteScriptUseCase
import de.joz.appcommander.domain.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.GetUserScriptsUseCase
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.domain.ScriptsRepository
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
class ScriptsViewModelTest {

    private val navControllerMock: NavController = mockk()
    private val getConnectedDevicesUseCaseMock: GetConnectedDevicesUseCase = mockk()
    private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk()
    private val getUserScriptsUseCaseMock: GetUserScriptsUseCase = mockk()

    @BeforeTest
    fun setUp() {
        coEvery {
            getConnectedDevicesUseCaseMock()
        } returns listOf(
            GetConnectedDevicesUseCase.ConnectedDevice(
                id = "p7",
                label = "pixel 7",
            )
        )

        coEvery {
            getUserScriptsUseCaseMock()
        } returns listOf(
            ScriptsRepository.Script(
                label = "my script",
                script = "foo",
                platform = ScriptsRepository.Platform.ANDROID,
            ),
        )
    }

    @Test
    fun `should load devices and scripts when viewmodel is initialized`() = runTest {
        val viewModel = createViewModel()
        runCurrent()

        assertEquals(
            listOf(
                ScriptsViewModel.Device(
                    label = "pixel 7",
                    id = "p7",
                    isSelected = true,
                )
            ), viewModel.uiState.value.connectedDevices
        )

        assertEquals(
            listOf(
                ScriptsViewModel.Script(
                    description = "my script",
                    scriptText = "foo",
                    originalScript = ScriptsRepository.Script(
                        label = "my script",
                        script = "foo",
                        platform = ScriptsRepository.Platform.ANDROID,
                    ),
                )
            ),
            viewModel.uiState.value.scripts,
        )

        coVerify {
            getConnectedDevicesUseCaseMock()
            getUserScriptsUseCaseMock()
        }
    }

    @Test
    fun `should navigate to settings when event 'OnNavigateToSettings' is fired`() = runTest {
        val viewModel = createViewModel()

        every {
            navControllerMock.navigate(NavigationScreens.SettingsScreen)
        } returns Unit

        viewModel.onEvent(event = ScriptsViewModel.Event.OnNavigateToSettings)
        runCurrent()

        verify {
            navControllerMock.navigate(NavigationScreens.SettingsScreen)
        }
    }

    @Test
    fun `should select device when event 'OnDeviceSelected' is fired`() = runTest {
        val viewModel = createViewModel()
        val device = viewModel.uiState.value.connectedDevices.first()
        val preSelectedState = device.isSelected

        viewModel.onEvent(event = ScriptsViewModel.Event.OnDeviceSelected(device = device))
        runCurrent()

        assertTrue(preSelectedState)
        assertFalse(viewModel.uiState.value.connectedDevices.first().isSelected)
    }

    @Test
    fun `should expand script when event 'OnExpandScript' is fired`() = runTest {
        val viewModel = createViewModel()
        val script = viewModel.uiState.value.scripts.first()
        val preExpandedState = script.isExpanded

        viewModel.onEvent(event = ScriptsViewModel.Event.OnExpandScript(script = script))
        runCurrent()

        assertFalse(preExpandedState)
        assertTrue(viewModel.uiState.value.scripts.first().isExpanded)
    }

    @Test
    fun `should execute script when event 'OnExecuteScript' is fired`() = runTest {
        val viewModel = createViewModel()
        val script = viewModel.uiState.value.scripts.first()

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
    fun `should execute script on multiple devices when event 'OnExecuteScript' is fired and multiples devices are selected`() =
        runTest {
            val testScript = ScriptsRepository.Script(
                label = "my script",
                script = "foo",
                platform = ScriptsRepository.Platform.ANDROID,
            )

            coEvery {
                getConnectedDevicesUseCaseMock()
            } returns listOf(
                GetConnectedDevicesUseCase.ConnectedDevice(id = "1", label = "P1"),
                GetConnectedDevicesUseCase.ConnectedDevice(id = "2", label = "P2"),
                GetConnectedDevicesUseCase.ConnectedDevice(id = "3", label = "P3"),
            )
            coEvery {
                executeScriptUseCaseMock(
                    script = testScript,
                    selectedDevice = "P1",
                )
            } returns ExecuteScriptUseCase.Result.Success(output = "")
            coEvery {
                executeScriptUseCaseMock(
                    script = testScript,
                    selectedDevice = "P3",
                )
            } returns ExecuteScriptUseCase.Result.Success(output = "")

            val viewModel = createViewModel()

            viewModel.onEvent(event = ScriptsViewModel.Event.OnDeviceSelected(device = viewModel.uiState.value.connectedDevices.first()))
            runCurrent()
            viewModel.onEvent(event = ScriptsViewModel.Event.OnDeviceSelected(device = viewModel.uiState.value.connectedDevices.last()))
            runCurrent()

            viewModel.onEvent(
                event = ScriptsViewModel.Event.OnExecuteScript(
                    script = ScriptsViewModel.Script(
                        originalScript = testScript,
                        description = "",
                        scriptText = "",
                    )
                )
            )
            runCurrent()

            coVerify {
                executeScriptUseCaseMock(
                    script = testScript,
                    selectedDevice = "P1",
                )
            }
        }

    private fun createViewModel(): ScriptsViewModel {
        return ScriptsViewModel(
            navController = navControllerMock,
            getConnectedDevicesUseCase = getConnectedDevicesUseCaseMock,
            executeScriptUseCase = executeScriptUseCaseMock,
            getUserScriptsUseCase = getUserScriptsUseCaseMock,
            dispatcher = Dispatchers.Unconfined,
        )
    }
}