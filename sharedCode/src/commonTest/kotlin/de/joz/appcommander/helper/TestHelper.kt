@file:OptIn(ExperimentalTestApi::class)

package de.joz.appcommander.helper

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

suspend fun ComposeUiTest.click(string: StringResource) {
	click(getString(string))
}

fun ComposeUiTest.click(string: String) {
	onNode(hasText(string) or hasTestTag(string), useUnmergedTree = true).performClick()
}

suspend fun ComposeUiTest.assertIsDisplayed(string: StringResource) {
	assertIsDisplayed(getString(string))
}

fun ComposeUiTest.assertIsDisplayed(string: String) {
	onNodeWithText(string).assertIsDisplayed()
}
