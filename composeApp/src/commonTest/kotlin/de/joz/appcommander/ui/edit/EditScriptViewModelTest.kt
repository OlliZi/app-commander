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
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnChangeScript(
						script = "foo",
					),
			)
			runCurrent()

			assertEquals("foo", viewModel.uiState.value.script)
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
					script = "foo",
					label = "bar",
					platform = ScriptsRepository.Platform.IOS,
				)

			val viewModel =
				createViewModel(
					scriptKey = 1,
				)

			assertEquals("foo", viewModel.uiState.value.script)
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
	fun `should execute script when event 'OnExecuteScript' is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(
				event =
					EditScriptViewModel.Event.OnExecuteScript,
			)
			runCurrent()

			coVerify { executeScriptUseCaseMock.invoke(any(), any()) }
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
