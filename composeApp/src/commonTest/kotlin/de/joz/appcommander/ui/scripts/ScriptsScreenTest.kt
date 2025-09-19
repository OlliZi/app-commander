package de.joz.appcommander.ui.scripts

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ScriptsScreenTest {

    @Test
    fun `should show default label when no devices are connected`() {
        runComposeUiTest {
            setTestContent(
                uiState = ScriptsViewModel.UiState(),
            )

            onNodeWithText("Your scripts").assertIsDisplayed()
            onNodeWithText("Hint: Activate the 'Developer options' on your device.").assertIsDisplayed()
            onNodeWithText("Connect your devices over USB and click refresh.").assertIsDisplayed()
            onNodeWithText("Refresh").assertIsDisplayed().assertHasClickAction()
            onNodeWithText("Logging").assertIsDisplayed()
        }
    }

    @Test
    fun `should show log`() {
        runComposeUiTest {
            setTestContent(
                uiState = ScriptsViewModel.UiState(
                    logging = listOf("Log abc", "Log 123"),
                )
            )

            onNodeWithContentDescription(
                label = "Expand button",
            ).assertIsDisplayed()
                .performClick()

            onNodeWithText("Log abc").assertIsDisplayed()
            onNodeWithText("Log 123").assertIsDisplayed()
        }
    }

    @Test
    fun `should clear log when clear button is executed`() {

    }

    @Test
    fun `should expand log when expand button is executed`() {

    }

    @Test
    fun `should collapse log when collapse button is executed`() {

    }

    @Test
    fun `should show connected devices`() {
        runComposeUiTest {
            setTestContent(
                uiState = ScriptsViewModel.UiState(
                    connectedDevices = listOf(
                        ScriptsViewModel.Device(
                            label = "Device A",
                            id = "1",
                            isSelected = true,
                        ),
                    )
                )
            )

            onNodeWithText("Hint: Activate the 'Developer options' on your device.").assertIsDisplayed()
            onNodeWithText("Your connected devices:").assertIsDisplayed()
            onNodeWithText("Device A").assertIsDisplayed()
            onNodeWithText("Refresh").performClick()
        }
    }

    @Test
    fun `should refresh devices when refresh button is clicked`() {
        runComposeUiTest {
            var isRefreshClicked = 0
            setTestContent(
                uiState = ScriptsViewModel.UiState(),
                onRefreshDevices = {
                    isRefreshClicked++
                }
            )

            onNodeWithText("Refresh").performClick()

            assertEquals(1, isRefreshClicked)
        }
    }

    @Test
    fun `should open script file when open button is clicked`() {
        runComposeUiTest {
            var isOpenClicked = 0
            setTestContent(
                uiState = ScriptsViewModel.UiState(),
                onOpenScriptFile = {
                    isOpenClicked++
                },
            )

            onNodeWithText("Open script file").performClick()

            assertEquals(1, isOpenClicked)
        }
    }

    private fun ComposeUiTest.setTestContent(
        uiState: ScriptsViewModel.UiState,
        onDeviceSelect: (ScriptsViewModel.Device) -> Unit = {},
        onExecuteScript: (ScriptsViewModel.Script) -> Unit = {},
        onRefreshDevices: () -> Unit = {},
        onExpand: (ScriptsViewModel.Script) -> Unit = {},
        onNavigateToSettings: () -> Unit = {},
        onOpenScriptFile: () -> Unit = {},
        onClearLogging: () -> Unit = {},
    ) {
        setContent {
            ScriptsContent(
                uiState = uiState,
                onDeviceSelect = onDeviceSelect,
                onExecuteScript = onExecuteScript,
                onRefreshDevices = onRefreshDevices,
                onExpand = onExpand,
                onNavigateToSettings = onNavigateToSettings,
                onOpenScriptFile = onOpenScriptFile,
                onClearLogging = onClearLogging,
            )
        }
    }
}