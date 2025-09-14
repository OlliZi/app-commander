package de.joz.appcommander.ui.scripts

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
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
            setContent {
                ScriptsContent(
                    uiState = ScriptsViewModel.UiState(),
                    onDeviceSelect = {},
                    onExecuteScript = {},
                    onRefreshDevices = {},
                    onExpand = {},
                    onNavigateToSettings = {},
                    onOpenScriptFile = {},
                )
            }

            onNodeWithText("Your scripts").assertIsDisplayed()
            onNodeWithText("Connect your devices over USB and click refresh.").assertIsDisplayed()
            onNodeWithText("Refresh").assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun `should show connected devices`() {
        runComposeUiTest {
            setContent {
                ScriptsContent(
                    uiState = ScriptsViewModel.UiState(
                        connectedDevices = listOf(
                            ScriptsViewModel.Device(
                                label = "Device A",
                                id = "1",
                                isSelected = true,
                            ),
                        )
                    ),
                    onDeviceSelect = {},
                    onExecuteScript = {},
                    onRefreshDevices = {},
                    onExpand = {},
                    onNavigateToSettings = {},
                    onOpenScriptFile = {},
                )
            }

            onNodeWithText("Your connected devices:").assertIsDisplayed()
            onNodeWithText("Device A").assertIsDisplayed()
            onNodeWithText("Refresh").performClick()
        }
    }

    @Test
    fun `should refresh devices when refresh button is clicked`() {
        runComposeUiTest {
            var isRefreshClicked = 0
            setContent {
                ScriptsContent(
                    uiState = ScriptsViewModel.UiState(),
                    onDeviceSelect = {},
                    onExecuteScript = {},
                    onRefreshDevices = {
                        isRefreshClicked++
                    },
                    onExpand = {},
                    onNavigateToSettings = {},
                    onOpenScriptFile = {},
                )
            }

            onNodeWithText("Refresh").performClick()

            assertEquals(1, isRefreshClicked)
        }
    }
}