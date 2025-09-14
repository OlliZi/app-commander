package de.joz.appcommander.ui.misc

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsTest {

    @Test
    fun `should light color`() {
        val testColor = Color(
            red = 100,
            green = 100,
            blue = 100,
        ).lighter(factor = 1.25f)

        val expectedColor = Color(
            red = 125,
            green = 125,
            blue = 125,
        )

        assertEquals(expectedColor, testColor)
    }
}