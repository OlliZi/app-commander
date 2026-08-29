package de.joz.appcommander.data.json

import de.joz.appcommander.domain.script.ScriptsRepository
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

object ScriptSerializer : KSerializer<ScriptsRepository.Script> {
	@Serializable
	private data class ScriptSurrogate(
		val label: String,
		val platform: ScriptsRepository.Platform,
		val scripts: List<ScriptsRepository.ScriptCode>,
		val comment: String? = null,
	)

	override val descriptor: SerialDescriptor = ScriptSurrogate.serializer().descriptor

	override fun serialize(
		encoder: Encoder,
		value: ScriptsRepository.Script,
	) {
		val surrogate = ScriptSurrogate(
			label = value.label,
			platform = value.platform,
			scripts = value.scripts,
			comment = value.comment,
		)
		encoder.encodeSerializableValue(ScriptSurrogate.serializer(), surrogate)
	}

	override fun deserialize(decoder: Decoder): ScriptsRepository.Script {
		val jsonDecoder = decoder as? JsonDecoder
		if (jsonDecoder != null) {
			val element = jsonDecoder.decodeJsonElement()
			if (element is JsonObject) {
				val surrogate = jsonDecoder.json.decodeFromJsonElement<ScriptSurrogate>(element)
				return ScriptsRepository.Script(
					label = surrogate.label,
					platform = surrogate.platform,
					scripts = surrogate.scripts,
					comment = surrogate.comment,
				)
			}
		}

		val surrogate = decoder.decodeSerializableValue(ScriptSurrogate.serializer())
		return ScriptsRepository.Script(
			label = surrogate.label,
			platform = surrogate.platform,
			scripts = surrogate.scripts,
			comment = surrogate.comment,
		)
	}
}
