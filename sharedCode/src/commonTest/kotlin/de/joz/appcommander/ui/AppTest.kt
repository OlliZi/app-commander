package de.joz.appcommander.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import de.joz.appcommander.App
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.domain.misc.ManageUiAppearanceUseCase
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.PreferencesRepository
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.ui.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.Test

class AppTest :
	TestRuleApplier(),
	KoinTest {
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	@get:Rule
	val koinTestRule = KoinTestRule.create {
		modules(DependencyInjection().module)
		modules(
			module {
				single<SavePreferenceUseCase> {
					mockk(relaxed = true)
				}
				single<PreferencesRepository> { mockk(relaxed = true) }
				single<ScriptsRepository> {
					mockk {
						every { getScripts() } returns ScriptsRepository.JsonParseResult(
							scripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS,
							parsingMetaData = null,
						)
					}
				}
				single<GetPreferenceUseCase> {
					mockk {
						coEvery {
							get(
								SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
								any<Boolean>(),
							)
						} returns false

						coEvery {
							get(
								ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
								any<Int>(),
							)
						} returns ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex
					}
				}
			},
		)
	}

	@OptIn(ExperimentalTestApi::class)
	@Test
	fun `should show app when launched`() {
		runComposeUiTest {
			setContent {
				App()
			}

			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "app_launch")
		}
	}
}
