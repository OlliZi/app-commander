package de.joz.appcommander.ui.misc

import androidx.compose.ui.graphics.Color

fun Color.lighter(factor: Float = 1.2f): Color =
	Color(
		red = this.red * factor,
		green = this.green * factor,
		blue = this.blue * factor,
	)
