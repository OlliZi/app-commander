package de.joz.appcommander.data.json

import de.joz.appcommander.domain.script.ScriptsRepository
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object ScriptCodeSerializer : KSerializer<ScriptsRepository.ScriptCode> {
	private val simpleScriptSerializer = ScriptsRepository.ScriptCode.Script.serializer()
	private val commentedScriptSerializer = ScriptsRepository.ScriptCode.CommentedScript.serializer()

	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ScriptCode")

	override fun serialize(
		encoder: Encoder,
		value: ScriptsRepository.ScriptCode,
	) {
		when (value) {
			is ScriptsRepository.ScriptCode.Script -> simpleScriptSerializer.serialize(encoder, value)
			is ScriptsRepository.ScriptCode.CommentedScript -> commentedScriptSerializer.serialize(encoder, value)
		}
	}

	override fun deserialize(decoder: Decoder): ScriptsRepository.ScriptCode {
		val jsonDecoder = decoder as? JsonDecoder
			?: throw UnsupportedOperationException("This serializer is only for JSON")

		val element = jsonDecoder.decodeJsonElement()
		if (element is JsonPrimitive && element.isString) {
			return ScriptsRepository.ScriptCode.Script(element.content)
		}

		if (element is JsonObject) {
			return if (element.containsKey("comment")) {
				jsonDecoder.json.decodeFromJsonElement(commentedScriptSerializer, element)
			} else {
				jsonDecoder.json.decodeFromJsonElement(simpleScriptSerializer, element)
			}
		}
		throw UnsupportedOperationException("This serializer is only for JSON")
	}
}
