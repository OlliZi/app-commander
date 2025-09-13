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
                    label = "my script",
                    script = "foo",
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