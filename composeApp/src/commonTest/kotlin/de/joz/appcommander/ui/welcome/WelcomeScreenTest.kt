package de.joz.appcommander.ui.welcome

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.domain.PreferencesRepository
import de.joz.appcommander.helper.PreferencesRepositoryMock
import de.joz.appcommander.helper.screenshot.ScreenshotResult
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import de.joz.appcommander.ui.welcome.bubble.BubblesStrategy
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.*
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Arrays
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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

	//private val testClass: Class = this
	private val storeDirectory: File = File("./build/reports/tests/screenshots/")
	private val goldenImageDirectory: File = File("./src/commonTest/kotlin/")

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

			verifyScreenshot(source = this, screenshotName = "all_labels")
		}
	}

	@Test
	fun `should save flag when toggle is clicked`() =
		runTest {
			val navController: NavController = mockk(relaxed = true)
			runComposeUiTest {
				setTestContent(
					navController = navController,
				)

				onNodeWithText("Do not show welcome screen again.").performClick()

				// screenshotVerifier.verifyScreenshot(source = this, screenshotName = "toggle_click")
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

	private fun ComposeUiTest.setTestContent(navController: NavController) {
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
							object : BubblesStrategy {
								override fun drawBubbles(
									drawScope: DrawScope,
									size: Size,
									step: Float,
								) {
									drawScope.drawCircle(Color.LightGray)
								}
							},
					)
				},
			)
		}
	}

	fun verifyScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
	) {
		println("1")
		val screenshotResult =
			takeScreenshot(
				source = source,
				screenshotName = screenshotName,
			)
		println("Result is: $screenshotResult")
		when (screenshotResult) {
			is ScreenshotResult.Success,
			->
				verifyAgainstGoldenImage(
					screenshotFile = screenshotResult.screenshot,
				)

			is ScreenshotResult.Failure -> {
				screenshotResult.error
			}
		}
	}

	private fun takeScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
	): ScreenshotResult =
		runCatching {
			println("2")
			val image = source.onNode(isRoot()).captureToImage()
			val bytearray = convertToPng(image.asSkiaBitmap())

			if (bytearray == null || bytearray.isEmpty()) {
				println("3")
				throw Exception("Screenshot is empty.")
			}

			val file = File(storeDirectory, "$screenshotName.png")
			file.writeBytes(bytearray)
			println("4")
			ScreenshotResult.Success(screenshot = file)
		}.getOrElse { throwable ->
			ScreenshotResult.Failure(error = throwable)
		}

	private fun verifyAgainstGoldenImage(screenshotFile: File) {
		val goldenImage =
			readGoldenImageFromSrcDir(
				screenshotFileName = screenshotFile.name,
			)

		val compareResult =
			if (goldenImage.exists()) {
				Arrays.compare(screenshotFile.readBytes(), goldenImage.readBytes())
			} else {
				IMAGE_DOES_NOT_EXIST
			}

		if (compareResult == IMAGE_DOES_NOT_EXIST) {
			Files.copy(screenshotFile.toPath(), goldenImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
		} else if (compareResult != IDENTICAL_IMAGES) {
			makeDiff(goldenImage, screenshotFile)
			val currentScreenshot = File(goldenImage.parentFile, "${goldenImage.nameWithoutExtension}_current.png")
			Files.copy(screenshotFile.toPath(), currentScreenshot.toPath(), StandardCopyOption.REPLACE_EXISTING)
		}

		val failMessage =
			when (compareResult) {
				IDENTICAL_IMAGES -> "WILL NOT PRINT IN TEST RESULT."
				IMAGE_DOES_NOT_EXIST -> "Fail: Golden image does not exist. Copied screenshot for you:)."
				else -> "Fail: Screenshots are not identical. Created screenshot diff to directory."
			}

		assertEquals(
			IDENTICAL_IMAGES,
			compareResult,
			"$failMessage\n" +
				"Current: ${screenshotFile.absolutePath}\n" +
				"Golden: ${goldenImage.absolutePath}\n" +
				"Verify your screenshots in your VCS.",
		)
	}

	private fun makeDiff(
		goldenImage: File,
		screenshotFile: File,
	) {
		val image1Bitmap = createBitmapFromScreenshot(screenshotFile = goldenImage)
		val image2Bitmap = createBitmapFromScreenshot(screenshotFile = screenshotFile)

		if (image1Bitmap.width != image2Bitmap.width || image1Bitmap.height != image2Bitmap.height) {
			throw IllegalStateException("Images are not the same size.")
		}

		val destination = Bitmap()
		destination.allocPixels(image1Bitmap.imageInfo)
		val canvas = Canvas(destination)
		val paint =
			Paint().apply {
				color = 0xFFFF0000.toInt()
			}
		val normalPaint = Paint()
		for (x in 0 until image1Bitmap.width) {
			for (y in 0 until image1Bitmap.height) {
				val pixelColor1 = image1Bitmap.getColor(x = x, y = y)
				val pixelColor2 = image2Bitmap.getColor(x = x, y = y)
				canvas.drawPoint(
					x.toFloat(),
					y.toFloat(),
					if (pixelColor1 == pixelColor2) normalPaint.apply { color = pixelColor1 } else paint,
				)
			}
		}

		val diffFile = File(goldenImage.parentFile, "${goldenImage.nameWithoutExtension}_diff.png")
		diffFile.writeBytes(convertToPng(destination)!!)
	}

	private fun readGoldenImageFromSrcDir(screenshotFileName: String): File {
		val sourceDirectory =
			this.javaClass.name
				.split(".") // split class name into parts
				.dropLast(1) // remove class name
				.joinToString("/") // convert to directory path

		val parentScreenshotDir = File(goldenImageDirectory.absolutePath.plus("/$sourceDirectory/screenshots/"))
		parentScreenshotDir.mkdir()

		return File(parentScreenshotDir, screenshotFileName)
	}

	private fun createBitmapFromScreenshot(screenshotFile: File): Bitmap {
		val imageBitmap = Bitmap()
		val image = Image.makeFromEncoded(screenshotFile.readBytes())

		imageBitmap.allocPixels(image.imageInfo)
		image.readPixels(imageBitmap)

		return imageBitmap
	}

	private fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

	companion object Companion {
		private const val IMAGE_QUALITY = 100
		private const val IDENTICAL_IMAGES = 0
		private const val IMAGE_DOES_NOT_EXIST = -1
	}
}
