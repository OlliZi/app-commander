package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.script.FilterScriptUseCase.Companion.SCRIPT_FILTER_PREF_KEY
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterScriptUseCaseTest {
	private val getPreferenceUseCaseMock = mockk<GetPreferenceUseCase>()
	private val useCase = FilterScriptUseCase(getPreferenceUseCaseMock)

	private val script1 = ScriptsRepository.Script(
		label = "Android Build",
		platform = ScriptsRepository.Platform.ANDROID,
		scripts = listOf(
			ScriptsRepository.ScriptCode.Script("foo bar"),
		),
		comment = "Builds the android app",
	)
	private val script2 = ScriptsRepository.Script(
		label = "iOS Run",
		platform = ScriptsRepository.Platform.IOS,
		scripts = listOf(
			ScriptsRepository.ScriptCode.CommentedScript(
				script = "echo iOS",
				comment = "Runs the iOS app",
			),
		),
	)
	private val scripts = listOf(script1, script2)

	@BeforeTest
	fun setUp() {
		coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns ""
	}

	@Test
	fun `should filter with empty string returns all scripts`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns ""

			val result = useCase(scripts)

			assertEquals(scripts, result.scripts)
			assertEquals("", result.filterText)
		}

	@Test
	fun `should filter with label returns matching script`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "android"

			val result = useCase(scripts)

			assertEquals(listOf(script1), result.scripts)
		}

	@Test
	fun `should filter with platform name returns matching script`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "IOS"

			val result = useCase(scripts)

			assertEquals(listOf(script2), result.scripts)
		}

	@Test
	fun `should filter with script code returns matching script`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "foo bar"

			val result = useCase(scripts)

			assertEquals(listOf(script1), result.scripts)
		}

	@Test
	fun `should filter with script code comment returns matching script`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "runs the ios"

			val result = useCase(scripts)

			assertEquals(listOf(script2), result.scripts)
		}

	@Test
	fun `should filter with overall comment returns matching script`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "builds the android"

			val result = useCase(scripts)

			assertEquals(listOf(script1), result.scripts)
		}

	@Test
	fun `should filter is case insensitive`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "ANDROID"

			val result = useCase(scripts)

			assertEquals(listOf(script1), result.scripts)
		}

	@Test
	fun `should filter with no match returns empty list`() =
		runTest {
			coEvery { getPreferenceUseCaseMock.get(SCRIPT_FILTER_PREF_KEY, "") } returns "no match"

			val result = useCase(scripts)

			assertEquals(emptyList(), result.scripts)
		}
}
