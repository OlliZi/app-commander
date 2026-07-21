package de.joz.appcommander.ui.edit

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.devices.SelectedDevicesRepository
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.RunFileBackupUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.GetDevicesUseCaseMock
import de.joz.appcommander.helper.SelectedDevicesRepositoryMock
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.koin.dsl.module
import org.koin.ksp.generated.*
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class EditScriptScreenComplex2Test :
	TestRuleApplier(),
	KoinTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)
	private val getUserScriptByKeyUseCaseMock = GetUserScriptByKeyUseCase(
		scriptsRepository = scriptsRepositoryMock,
		getScriptIdUseCase = getScriptIdUseCaseMock,
	)
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock = SaveUserScriptUseCase(
		scriptsRepository = scriptsRepositoryMock,
		getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
		runFileBackupUseCase = runFileBackupUseCaseMock,
	)
	private val removeUserScriptUseCaseMock: RemoveUserScriptUseCase = mockk(relaxed = true)
	private val initialDevices = listOf(
		Device(
			id = "id 1",
			label = "device 1",
			isSelected = false,
		),
		Device(
			id = "id 2",
			label = "device 2",
			isSelected = false,
		),
	)

	private val selectedDevicesRepositoryMock = SelectedDevicesRepositoryMock(
		testDevices = initialDevices,
	)

	private val getDevicesUseCaseMock = GetDevicesUseCaseMock {
		selectedDevicesRepositoryMock.testDevices
	}

	@get:Rule
	val koinTestRule = KoinTestRule.create {
		modules(DependencyInjection().module)
		modules(
			module {
				single {
					mockk<ObserveDevicesUseCase>(relaxed = false) {
						every { this@mockk.invoke() } returns flowOf(selectedDevicesRepositoryMock.testDevices)
					}
				}
				single<GetDevicesUseCase> { getDevicesUseCaseMock }
				single<SelectedDevicesRepository> { selectedDevicesRepositoryMock }
			},
		)
	}

	@Test
	fun `should use selected device when script is executed`() {
		runComposeUiTest {
			val script = ScriptsRepository.Script(
				label = "",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf("echo"),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			every { getScriptIdUseCaseMock.invoke(any()) } returns (script.hashCode())
			every { scriptsRepositoryMock.getScripts() } returns ScriptsRepository.JsonParseResult(
				scripts = listOf(script),
				parsingMetaData = null,
			)

			setTestContent(scriptKey = script.hashCode())

			onNodeWithText("device 1").performClick()
			onNodeWithContentDescription(label = "Execute all scripts").performClick()

			onNodeWithText("device 2").performClick()
			onNodeWithContentDescription(label = "Execute all scripts").performClick()

			onNodeWithText("device 1").performClick()
			onNodeWithText("device 2").performClick()
			onNodeWithContentDescription(label = "Execute all scripts").performClick()

			waitForIdle()
			coVerify(exactly = 2) {
				executeScriptUseCaseMock(script = script, selectedDevice = "id 1")
			}

			coVerify(exactly = 1) {
				executeScriptUseCaseMock(script = script, selectedDevice = "id 2")
			}

			assertEquals(4, selectedDevicesRepositoryMock.getSaveCounterAndReset())
		}
	}

	@OptIn(ExperimentalTestApi::class)
	private fun ComposeUiTest.setTestContent(scriptKey: Int? = null) {
		val viewModel = EditScriptViewModel(
			navController = navControllerMock,
			getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
			getScriptIdUseCase = getScriptIdUseCaseMock,
			executeScriptUseCase = executeScriptUseCaseMock,
			saveUserScriptUseCase = saveUserScriptUseCaseMock,
			removeUserScriptUseCase = removeUserScriptUseCaseMock,
			saveUserScriptUseCaseResultMapper = SaveUserScriptUseCaseResultMapper(),
			getDevicesUseCase = getDevicesUseCaseMock,
			mainDispatcher = Dispatchers.Unconfined,
			ioDispatcher = Dispatchers.Unconfined,
			scriptKey = scriptKey,
		)
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					EditScriptScreen(
						viewModel = viewModel,
					)
				},
			)
		}
	}
}
