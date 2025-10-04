package de.joz.appcommander.ui.scripts

import androidx.navigation.NavController
import de.joz.appcommander.domain.ExecuteScriptUseCase
import de.joz.appcommander.domain.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.GetUserScriptsUseCase
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.domain.OpenScriptFileUseCase
import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.domain.TrackScriptsFileChangesUseCase
import de.joz.appcommander.domain.logging.ClearLoggingUseCase
import de.joz.appcommander.domain.logging.GetLoggingUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
    private val trackScriptsFileChangesUseCaseMock: TrackScriptsFileChangesUseCase =
        mockk(relaxed = true)

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
            listOf(
                ScriptsRepository.Script(
                    label = "my script",
                    script = "foo",
                    platform = ScriptsRepository.Platform.ANDROID,
                ),
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
            } returns ExecuteScriptUseCase.Result.Success(output = "", emptyList())

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
            } returns ExecuteScriptUseCase.Result.Success(output = "", emptyList())
            coEvery {
                executeScriptUseCaseMock(
                    script = testScript,
                    selectedDevice = "3",
                )
            } returns ExecuteScriptUseCase.Result.Success(output = "", emptyList())

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
            val mutableSharedFlow = MutableSharedFlow<List<ScriptsRepository.Script>>()
            coEvery {
                trackScriptsFileChangesUseCaseMock()
            } returns mutableSharedFlow

            coEvery {
                getUserScriptsUseCaseMock()
            } returns
                listOf(
                    ScriptsRepository.Script(
                        label = "my script",
                        script = "foo",
                        platform = ScriptsRepository.Platform.ANDROID,
                    ),
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
            dispatcher = Dispatchers.Unconfined,
            dispatcherIO = Dispatchers.Unconfined,
        )
}
