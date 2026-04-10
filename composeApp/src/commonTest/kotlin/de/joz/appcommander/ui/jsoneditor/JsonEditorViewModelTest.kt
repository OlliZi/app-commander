package de.joz.appcommander.ui.jsoneditor

import androidx.navigation.NavController
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.domain.script.GetUserScriptsUseCase
import de.joz.appcommander.domain.script.OpenScriptFileUseCase
import de.joz.appcommander.domain.script.SaveUserScriptsUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class JsonEditorViewModelTest {
	private val jsonParser = DependencyInjection().provideJson()
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val getUserScriptsUseCaseMock: GetUserScriptsUseCase = mockk(relaxed = true)
	private val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
	private val saveUserScriptsUseCaseMock: SaveUserScriptsUseCase =
		SaveUserScriptsUseCase(scriptsRepository = scriptsRepositoryMock)
	private val openScriptFileUseCaseMock: OpenScriptFileUseCase =
		OpenScriptFileUseCase(scriptsRepository = scriptsRepositoryMock)

	@BeforeTest
	fun setUp() {
		every { getUserScriptsUseCaseMock() } returns ScriptsRepository.JsonParseResult(
			scripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS,
			parsingMetaData = null,
		)
	}

	@Test
	fun `should load default state when viewmodel is initialized`() =
		runTest {
			val viewModel = createViewModel()

			assertTrue(viewModel.uiState.value.isJsonValid)
			assertTrue(
				viewModel.uiState.value.json
					.isNotEmpty(),
			)
			assertEquals(3, viewModel.uiState.value.jsonScriptForUi.size)

			viewModel.uiState.value.jsonScriptForUi.forEach {
				assertTrue(it.isWholeObjectExpanded)
				assertEquals(it.originalScript, it.collapseScript)
			}

			assertTrue(
				viewModel.uiState.value.jsonValidMessage
					.isEmpty(),
			)
		}

	@Test
	fun `should convert script list to expected json menu`() =
		runTest {
			val scripts = listOf(
				ScriptsRepository.Script(
					label = "foo",
					platform = ScriptsRepository.Platform.ANDROID,
					scripts = listOf("foo", "bar"),
				),
				ScriptsRepository.Script(
					label = "bar",
					platform = ScriptsRepository.Platform.DESKTOP,
					scripts = listOf("foo"),
				),
			)

			assertEquals(
				listOf(
					JsonEditorViewModel.JsonItem(
						isWholeObjectExpanded = true,
						originalScript = ScriptsRepository.Script(
							label = "foo",
							platform = ScriptsRepository.Platform.ANDROID,
							scripts = listOf("foo", "bar"),
						),
						collapseScript = ScriptsRepository.Script(
							label = "foo",
							platform = ScriptsRepository.Platform.ANDROID,
							scripts = listOf("foo", "bar"),
						),
					),
					JsonEditorViewModel.JsonItem(
						isWholeObjectExpanded = true,
						originalScript = ScriptsRepository.Script(
							label = "bar",
							platform = ScriptsRepository.Platform.DESKTOP,
							scripts = listOf("foo"),
						),
						collapseScript = ScriptsRepository.Script(
							label = "bar",
							platform = ScriptsRepository.Platform.DESKTOP,
							scripts = listOf("foo"),
						),
					),
				),
				JsonEditorViewModel.fromScriptsToJsonMenu(
					scripts = scripts,
				),
			)
		}

	@Test
	fun `should convert script list to expected json`() =
		runTest {
			val json = JsonEditorViewModel.convertScriptsToUi(
				scripts = listOf(
					ScriptsRepository.Script(
						label = "foo",
						platform = ScriptsRepository.Platform.ANDROID,
						scripts = listOf("foo", "bar"),
					),
					ScriptsRepository.Script(
						label = "bar",
						platform = ScriptsRepository.Platform.DESKTOP,
						scripts = listOf("foo"),
					),
				),
				jsonParser = jsonParser,
			)

			assertEquals(
				"{\n" + "    \"label\": \"foo\",\n" + "    \"platform\": \"ANDROID\",\n" + "    \"scripts\": [\n" +
					"        \"foo\",\n" +
					"        \"bar\"\n" +
					"    ]\n" +
					"},\n" +
					"{\n" +
					"    \"label\": \"bar\",\n" +
					"    \"platform\": \"DESKTOP\",\n" +
					"    \"scripts\": [\n" +
					"        \"foo\"\n" +
					"    ]\n" +
					"}",
				json,
			)
		}

	@Test
	fun `should navigate back when back button is clicked`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = JsonEditorViewModel.Event.OnNavigateBack)

			verify { navControllerMock.navigateUp() }
		}

	@Test
	fun `should open script file externally when open button is clicked`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = JsonEditorViewModel.Event.OnOpenScriptFile)
			runCurrent()

			coVerify {
				scriptsRepositoryMock.openScriptFile()
			}
		}

	@Test
	fun `should save script when save button is clicked`() =
		runTest {
			val viewModel = createViewModel()

			viewModel.onEvent(event = JsonEditorViewModel.Event.OnSaveScript)
			runCurrent()

			coVerify {
				scriptsRepositoryMock.saveScripts(ScriptsRepositoryImpl.DEFAULT_SCRIPTS)
			}
		}

	private fun createViewModel(): JsonEditorViewModel =
		JsonEditorViewModel(
			navController = navControllerMock,
			saveUserScriptsUseCase = saveUserScriptsUseCaseMock,
			getUserScriptsUseCase = getUserScriptsUseCaseMock,
			openScriptFileUseCase = openScriptFileUseCaseMock,
			jsonParser = jsonParser,
			mainDispatcher = Dispatchers.Unconfined,
			ioDispatcher = Dispatchers.Unconfined,
		)
}
