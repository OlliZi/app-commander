package de.joz.appcommander.domain

import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.ui.settings.SettingsViewModel.Companion.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrackScriptsFileChangesUseCaseTest {
	private val getUserScriptsUseCaseMock: GetUserScriptsUseCase = mockk()
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk()

	@Test
	fun `invoke emits a new script list when a change is detected`() =
		runTest {
			coEvery { getUserScriptsUseCaseMock() } returns
				createDummyScripts(1) andThen
				createDummyScripts(2)
			val trackScriptsFileChangesUseCase = createUseCase()

			val emittedScripts = trackScriptsFileChangesUseCase().first()

			assertEquals(2, emittedScripts.scripts.size)
			assertEquals(
				createDummyScripts(2).scripts[0],
				emittedScripts.scripts[0],
			)
			assertEquals(
				createDummyScripts(2).scripts[1],
				emittedScripts.scripts[1],
			)
		}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `invoke does not emit when script list has not changed`() =
		runTest {
			coEvery { getUserScriptsUseCaseMock() } returns createDummyScripts(1)
			val trackScriptsFileChangesUseCase = createUseCase()
			val collectedScripts = mutableListOf<List<ScriptsRepository.Script>>()
			val job =
				launch {
					trackScriptsFileChangesUseCase().collect {
						collectedScripts.add(it.scripts)
					}
				}

			advanceTimeBy(2500)

			assertTrue(collectedScripts.isEmpty(), "Should not emit anything if the scripts do not change.")

			job.cancel()
		}

	private fun createDummyScripts(count: Int) =
		ScriptsRepository.JsonParseResult(
			scripts =
				(1..count).map {
					ScriptsRepository.Script(
						label = "foo $it",
						script = "echo $it",
						platform = ScriptsRepository.Platform.ANDROID,
					)
				},
			throwable = null,
		)

	private fun createUseCase(): TrackScriptsFileChangesUseCase {
		coEvery {
			getPreferenceUseCaseMock.get(TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY, any<Int>())
		} returns 1

		return TrackScriptsFileChangesUseCase(
			getUserScriptsUseCase = getUserScriptsUseCaseMock,
			getPreferenceUseCase = getPreferenceUseCaseMock,
		)
	}
}
