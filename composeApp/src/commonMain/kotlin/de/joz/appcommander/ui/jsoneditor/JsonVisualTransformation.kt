package de.joz.appcommander.ui.jsoneditor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import de.joz.appcommander.domain.script.ScriptsRepository

class JsonVisualTransformation(
	private val jsonMenuItems: List<JsonEditorViewModel.JsonObjectItem>,
) : VisualTransformation {
	private val stringColor = Color(0xFF6A8759)
	private val keywordColor = Color(0xFFCC7832)
	private val scriptFieldColor = Color(0xFF6897BB)
	private val bracketColor = Color(0xFFFFC66D)

	override fun filter(text: AnnotatedString) =
		TransformedText(
			text = highlightJson(text.text),
			offsetMapping = OffsetMapping.Identity,
		)

	private fun highlightJson(text: String): AnnotatedString {
		val builder = AnnotatedString.Builder()

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
					val text = text.substring(start, i)
					if (KNOWN_JSON_FIELDS.contains(text)) {
						builder.withAnnotation(tag = text, text) {
							withStyle(SpanStyle(color = scriptFieldColor)) {
								append(text)
							}
						}
					} else {
						builder.withStyle(SpanStyle(color = stringColor)) {
							append(text)
						}
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
					builder.append(char)
					i++
				}
			}
		}

		var annotString = builder.toAnnotatedString()
		jsonMenuItems.forEach { item ->
			if (item.isExpanded.not() && item.type == JsonEditorViewModel.JsonType.ARRAY) {
				val startIndex = item.currentVisitedJsonStringCount + 1
				val endIndex = jsonMenuItems
					.first {
						it.index > item.index && it.type == JsonEditorViewModel.JsonType.CONTENT && it.content.trim() == "]"
					}.currentVisitedJsonStringCount

				val string = annotString.subSequence(startIndex, endIndex)
				println(string)
			}
		}

		return builder.toAnnotatedString()
	}

	companion object {
		private val KNOWN_JSON_FIELDS = ScriptsRepository.Script::class.java.declaredFields.map { "\"${it.name}\"" }
	}
}
