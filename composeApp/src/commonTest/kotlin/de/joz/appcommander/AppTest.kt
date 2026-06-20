@file:Suppress("WildcardImport")

package de.joz.appcommander

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.ui.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.*
import kotlin.test.Test

class AppTest : TestRuleApplier() {
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	@OptIn(ExperimentalTestApi::class)
	@Test
	fun `should show app when launched`() {
		runComposeUiTest {
			setContent {
				KoinApplication(
					configuration = koinConfiguration(declaration = {
						modules(DependencyInjection().module)
						modules(
							module {
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
									}
								}
							},
						)
					}),
					content = {
						App()
					},
				)
			}

			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "app_launch")
		}
	}
}
