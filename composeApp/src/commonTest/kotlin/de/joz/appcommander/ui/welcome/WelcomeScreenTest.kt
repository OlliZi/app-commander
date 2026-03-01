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
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.domain.preference.PreferencesRepository
import de.joz.appcommander.helper.PreferencesRepositoryMock
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import de.joz.appcommander.ui.welcome.bubble.BubblesStrategy
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class WelcomeScreenTest {
	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)
	private lateinit var koin: Koin

	private val preferencesRepositoryMock = PreferencesRepositoryMock()

	@BeforeTest
	fun setup() {
		koin =
			startKoin {
				modules(
					DependencyInjection().module +
						module {
							single<PreferencesRepository> { preferencesRepositoryMock }
						},
				)
			}.koin
	}

	@AfterTest
	fun tearDown() {
		stopKoin()
	}

	@Test
	fun `should display all default labels on screen`() {
		runComposeUiTest {
			val navController: NavController = mockk(relaxed = true)
			setContent {
				setTestContent(
					navController = navController,
				)
			}

			onNodeWithText("Welcome to \n'App-Commander'.").assertIsDisplayed()
			onNodeWithText(
				"Your programmable multi-device execution helper. Execute your custom scripts for your apps on multiple devices.",
			).assertIsDisplayed()
			onNodeWithText("Let's go!").assertIsDisplayed().assertHasClickAction()
			onNodeWithText("Do not show welcome screen again.").assertIsDisplayed()
		}
	}

	@Test
	fun `should render animation bubbles when app is started`() =
		runTest {
			val navController: NavController = mockk(relaxed = true)
			runComposeUiTest {
				setTestContent(
					navController = navController,
					useCustomBubbleStrategy = false,
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
					useCustomBubbleStrategy = true,
				)

				onNodeWithText("Do not show welcome screen again.").performClick()

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

				onNodeWithText("Do not show welcome screen again.").performClick()
				onNodeWithText("Do not show welcome screen again.").performClick()
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

			onNodeWithText("Let's go!").performClick()

			verify { navController.navigate(NavigationScreens.ScriptsScreen) }
		}
	}

	private fun ComposeUiTest.setTestContent(
		navController: NavController,
		useCustomBubbleStrategy: Boolean = false,
	) {
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					WelcomeScreen(
						viewModel =
							WelcomeViewModel(
								navController = navController,
								savePreferenceUseCase = koin.get(),
							),
						bubblesStrategy =
							if (useCustomBubbleStrategy) {
								object : BubblesStrategy {
									override fun drawBubbles(
										drawScope: DrawScope,
										size: Size,
										step: Float,
									) {
										drawScope.drawCircle(Color.LightGray)
									}
								}
							} else {
								koin.get()
							},
						isInTextExecution = true,
					)
				},
			)
		}
	}
}
