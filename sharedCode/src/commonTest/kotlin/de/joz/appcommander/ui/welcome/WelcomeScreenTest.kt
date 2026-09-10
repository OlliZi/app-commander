package de.joz.appcommander.ui.welcome

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.domain.navigation.NavigationScreens
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.helper.PreferencesRepositoryMock
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import de.joz.appcommander.ui.welcome.animation.AnimationStrategy
import de.joz.appcommander.ui.welcome.animation.FadingInAnimationStrategy
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class WelcomeScreenTest {
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	private val preferencesRepositoryMock = PreferencesRepositoryMock()

	@Test
	fun `should display all default labels on screen`() {
		runComposeUiTest {
			val navController: NavController = mockk(relaxed = true)
			setContent {
				setTestContent(
					navController = navController,
				)
			}

			onNodeWithText("Welcome to \nApp-Commander.").assertIsDisplayed()
			onNodeWithText("Your programmable multi-device execution helper for your apps.").assertIsDisplayed()
			onNodeWithText("Start").assertIsDisplayed().assertHasClickAction()
			onNodeWithText("Hide welcome screen on next start").assertIsDisplayed()
		}
	}

	@Test
	fun `should render animation tokens when app is started`() =
		runTest {
			val navController: NavController = mockk(relaxed = true)
			runComposeUiTest {
				setTestContent(
					navController = navController,
					useCustomAnimationStrategy = false,
				)

				screenshotVerifier.verifyScreenshot(source = this, screenshotName = "animation")
			}
		}

	@Test
	fun `should save flag when toggle is clicked`() =
		runTest {
			val navController: NavController = mockk(relaxed = true)
			runComposeUiTest {
				setTestContent(
					navController = navController,
					useCustomAnimationStrategy = true,
				)

				onNodeWithText("Hide welcome screen on next start").performClick()

				screenshotVerifier.verifyScreenshot(source = this, screenshotName = "toggle_click")
			}

			assertTrue(
				preferencesRepositoryMock.get(
					"HIDE_WELCOME_SCREEN",
					false,
				),
			)
		}

	@Test
	fun `should revert toggle value when toggle is clicked twice`() =
		runTest {
			val navController: NavController = mockk(relaxed = true)
			runComposeUiTest {
				setTestContent(
					navController = navController,
				)

				onNodeWithText("Hide welcome screen on next start").performClick()
				onNodeWithText("Hide welcome screen on next start").performClick()
			}

			assertFalse(
				preferencesRepositoryMock.get(
					"HIDE_WELCOME_SCREEN",
					true,
				),
			)
		}

	@Test
	fun `should navigate to next screen when next button is clicked`() {
		val navController: NavController = mockk(relaxed = true)
		runComposeUiTest {
			setTestContent(
				navController = navController,
			)

			onNodeWithText("Start").performClick()

			verify { navController.navigate(NavigationScreens.ScriptsScreen) }
		}
	}

	private fun ComposeUiTest.setTestContent(
		navController: NavController,
		useCustomAnimationStrategy: Boolean = false,
	) {
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					WelcomeScreen(
						viewModel = WelcomeViewModel(
							navController = navController,
							savePreferenceUseCase = SavePreferenceUseCase(preferencesRepository = preferencesRepositoryMock),
						),
						animationStrategy = if (useCustomAnimationStrategy) {
							object : AnimationStrategy {
								override fun render(
									drawScope: DrawScope,
									size: Size,
									step: Float,
								) {
									drawScope.drawCircle(Color.LightGray)
								}
							}
						} else {
							FadingInAnimationStrategy()
						},
						isInTextExecution = true,
					)
				},
			)
		}
	}
}
