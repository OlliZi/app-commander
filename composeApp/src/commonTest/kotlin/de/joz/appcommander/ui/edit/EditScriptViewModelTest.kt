package de.joz.appcommander.ui.edit

import androidx.navigation.NavController
import de.joz.appcommander.domain.ExecuteScriptUseCase
import de.joz.appcommander.domain.RemoveUserScriptUseCase
import de.joz.appcommander.domain.SaveUserScriptUseCase
import de.joz.appcommander.domain.ScriptsRepository
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EditScriptViewModelTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock: SaveUserScriptUseCase = mockk()
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val removeUserScriptUseCaseMock: RemoveUserScriptUseCase = mockk(relaxed = true)

	@Test
	fun `should load devices and scripts when viewmodel is initialized`() =
		runTest {
			val viewModel = createViewModel()
		}

	@Test
	fun `should navigate back when event OnNavigateBack is fired`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = EditScriptViewModel.Event.OnNavigateBack)
			runCurrent()

			verify {
				navControllerMock.navigateUp()
			}
		}

	@Test
	fun `should select platform when event OnSelectPlatform is fired`() =
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
	fun `should change script when event OnChangeScript is fired`() =
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
	fun `should change name of script when event OnChangeScriptName is fired`() =
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

	private fun createViewModel(): EditScriptViewModel =
		EditScriptViewModel(
			navController = navControllerMock,
			executeScriptUseCase = executeScriptUseCaseMock,
			saveUserScriptUseCase = saveUserScriptUseCaseMock,
			removeUserScriptUseCase = removeUserScriptUseCaseMock,
			dispatcher = Dispatchers.Unconfined,
			dispatcherIO = Dispatchers.Unconfined,
		)
}
