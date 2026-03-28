package de.joz.appcommander.ui.jsoneditor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class JsonVisualTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText =
		TransformedText(
			text = highlightJson(text.text),
			offsetMapping = OffsetMapping.Identity,
		)

	private fun highlightJson(text: String): AnnotatedString {
		val builder = AnnotatedString.Builder()
		val stringColor = Color(0xFF6A8759) // Green
		val keywordColor = Color(0xFFCC7832) // Orange
		val numberColor = Color(0xFF6897BB) // Blue
		val bracketColor = Color(0xFFFFC66D) // Yellow-ish

		var i = 0
		while (i < text.length) {
			when (val char = text[i]) {
				'"' -> {
					val start = i
					i++
					while (i < text.length && text[i] != '"') {
						if (text[i] == '\\' && i + 1 < text.length) i++
						i++
					}
					if (i < text.length) i++
					builder.withStyle(SpanStyle(color = stringColor)) {
						append(text.substring(start, i))
					}
				}

				'{', '}', '[', ']' -> {
					builder.withStyle(SpanStyle(color = bracketColor)) {
						append(char)
					}
					i++
				}

				':', ',' -> {
					builder.withStyle(SpanStyle(color = keywordColor)) {
						append(char)
					}
					i++
				}

				else -> {
					if (char.isDigit() || char == '-') {
						val start = i
						while (i < text.length &&
							(text[i].isDigit() || text[i] == '.' || text[i] == 'e' || text[i] == 'E' || text[i] == '+' || text[i] == '-')
						) {
							i++
						}
						builder.withStyle(SpanStyle(color = numberColor)) {
							append(text.substring(start, i))
						}
					} else if (text.startsWith("true", i)) {
						builder.withStyle(SpanStyle(color = keywordColor)) { append("true") }
						i += 4
					} else if (text.startsWith("false", i)) {
						builder.withStyle(SpanStyle(color = keywordColor)) { append("false") }
						i += 5
					} else if (text.startsWith("null", i)) {
						builder.withStyle(SpanStyle(color = keywordColor)) { append("null") }
						i += 4
					} else {
						builder.append(char)
						i++
					}
				}
			}
		}
		return builder.toAnnotatedString()
	}
}
