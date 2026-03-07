@file:Suppress("WildcardImport")

package de.joz.appcommander

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.settings.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.koin.ksp.generated.*
import kotlin.test.Test

class AppTest {
	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	@OptIn(ExperimentalTestApi::class)
	@Test
	fun `should show app when launched`() {
		runComposeUiTest {
			setContent {
				KoinApplication(
					application = {
						modules(DependencyInjection().module)
						modules(
							module {
								single<ScriptsRepository> {
									mockk {
										every { getScripts() } returns
											ScriptsRepository.JsonParseResult(
												scripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS,
												parsingMetaData = null,
											)
									}
								}
							},
						)
					},
				) {
					val savePreferenceUseCase: SavePreferenceUseCase = koinInject()
					runBlocking {
						savePreferenceUseCase(SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY, true)
					}

					App()
				}
			}

			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "app_launch")
		}
	}
}
