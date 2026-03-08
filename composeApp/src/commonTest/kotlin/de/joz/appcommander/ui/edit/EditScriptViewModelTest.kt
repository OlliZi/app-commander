package de.joz.appcommander.ui.edit

import androidx.navigation.NavController
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
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
class EditScriptViewModelTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock: SaveUserScriptUseCase = mockk(relaxed = true)
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val removeUserScriptUseCaseMock: RemoveUserScriptUseCase = mockk(relaxed = true)
	private val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = false)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)

	@BeforeTest
	fun setUp() {
		every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns null
	}

	@Test
	fun `should at least add at minimum one empty script when there was no script provided for the edit session`() =
		runTest {
			val viewModel = createViewModel()

			assertEquals(1, viewModel.uiState.value.scripts.size)
			assertEquals("", viewModel.uiState.value.scripts[0])
			assertEquals("", viewModel.uiState.value.scriptName)
			assertEquals(ScriptsRepository.Platform.ANDROID, viewModel.uiState.value.selectedPlatform)
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
	fun `should select platform when event 'OnSelectPlatform' is fired`() =
		runTest {
			val viewModel = createViewModel()
			assertEquals(ScriptsRepository.Platform.ANDROID, viewModel.uiState.value.selectedPlatform)

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnSelectPlatform(
						platform = ScriptsRepository.Platform.IOS,
					),
			)
			runCurrent()

			assertEquals(ScriptsRepository.Platform.IOS, viewModel.uiState.value.selectedPlatform)
		}

	@Test
	fun `should change script when event 'OnChangeScript' is fired`() =
		runTest {
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns
				ScriptsRepository.Script(
					label = "label",
					scripts = listOf("script 1", "script 2"),
					platform = ScriptsRepository.Platform.IOS,
				)

			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnChangeScript(
						index = 1,
						script = "new script 2",
					),
			)
			runCurrent()

			assertEquals(listOf("script 1", "new script 2"), viewModel.uiState.value.scripts)
			assertEquals(ScriptsRepository.Platform.IOS, viewModel.uiState.value.selectedPlatform)
			assertEquals("label", viewModel.uiState.value.scriptName)
		}

	@Test
	fun `should change name of script when event 'OnChangeScriptName' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnChangeScriptName(
						scriptName = "new name",
					),
			)
			runCurrent()

			assertEquals("new name", viewModel.uiState.value.scriptName)
		}

	@Test
	fun `should add a new script under existing script when event 'OnAddSubScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			assertEquals(1, viewModel.uiState.value.scripts.size)

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnAddSubScript(
						index = 0,
					),
			)
			runCurrent()

			assertEquals(2, viewModel.uiState.value.scripts.size)
			assertEquals("<enter new script>", viewModel.uiState.value.scripts[1])
		}

	@Test
	fun `should remove script when event 'OnRemoveSubScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			assertEquals(1, viewModel.uiState.value.scripts.size)

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnRemoveSubScript(
						index = 0,
					),
			)
			runCurrent()

			assertEquals(1, viewModel.uiState.value.scripts.size)
			assertEquals("", viewModel.uiState.value.scripts[0])
		}

	@Test
	fun `should remove script on correct index when event 'OnRemoveSubScript' is fired`() =
		runTest {
			val testScript =
				ScriptsRepository.Script(
					label = "label",
					scripts = listOf("script 1", "script 2", "script 3"),
					platform = ScriptsRepository.Platform.IOS,
				)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			val viewModel = createViewModel()

			assertEquals(3, viewModel.uiState.value.scripts.size)

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnRemoveSubScript(
						index = 1,
					),
			)
			runCurrent()

			assertEquals(2, viewModel.uiState.value.scripts.size)
			assertEquals("script 1", viewModel.uiState.value.scripts[0])
			assertEquals("script 3", viewModel.uiState.value.scripts[1])
		}

	@Test
	fun `should save script when event 'OnSaveScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnSaveScript,
			)
			runCurrent()

			coVerify { saveUserScriptUseCaseMock.invoke(any(), null) }
			verify { navControllerMock.navigateUp() }
		}

	@Test
	fun `should save script with script id when event 'OnSaveScript' is fired`() =
		runTest {
			val viewModel =
				createViewModel(
					scriptKey = 1,
				)

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnSaveScript,
			)
			runCurrent()

			coVerify { saveUserScriptUseCaseMock.invoke(any(), 1) }
			coVerify { getScriptIdUseCaseMock.invoke(any()) }
		}

	@Test
	fun `should apply script when script is loaded over constructor`() =
		runTest {
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns
				ScriptsRepository.Script(
					scripts = listOf("foo"),
					label = "bar",
					platform = ScriptsRepository.Platform.IOS,
				)

			val viewModel =
				createViewModel(
					scriptKey = 1,
				)

			assertEquals(listOf("foo"), viewModel.uiState.value.scripts)
			assertEquals("bar", viewModel.uiState.value.scriptName)
			assertEquals(ScriptsRepository.Platform.IOS, viewModel.uiState.value.selectedPlatform)

			coVerify { getUserScriptByKeyUseCaseMock.invoke(any()) }
		}

	@Test
	fun `should remove script when event 'OnRemoveScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnRemoveScript,
			)
			runCurrent()

			coVerify { removeUserScriptUseCaseMock.invoke(any()) }
			verify { navControllerMock.navigateUp() }
		}

	@Test
	fun `should execute script when event 'OnExecuteAllScripts' is fired`() =
		runTest {
			val testScript =
				ScriptsRepository.Script(
					label = "label",
					scripts = listOf("script 1", "script 2"),
					platform = ScriptsRepository.Platform.IOS,
				)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript

			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnExecuteAllScripts,
			)
			runCurrent()

			coVerify { executeScriptUseCaseMock.invoke(testScript, eq("TODO")) }
		}

	@Test
	fun `should execute script when event 'OnExecuteSingleScript' is fired`() =
		runTest {
			val testScript =
				ScriptsRepository.Script(
					label = "label",
					scripts = listOf("script 1", "script 2"),
					platform = ScriptsRepository.Platform.IOS,
				)
			every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns testScript
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnExecuteSingleScript("script 2"),
			)
			runCurrent()

			coVerify {
				executeScriptUseCaseMock.invoke(
					ScriptsRepository.Script(
						label = "label",
						scripts = listOf("script 2"),
						platform = ScriptsRepository.Platform.IOS,
					),
					eq("TODO"),
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
			mainDispatcher = Dispatchers.Unconfined,
			ioDispatcher = Dispatchers.Unconfined,
		)
}
