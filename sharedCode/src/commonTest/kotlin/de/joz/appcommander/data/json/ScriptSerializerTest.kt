package de.joz.appcommander.data.json

import de.joz.appcommander.domain.script.ScriptsRepository
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ScriptSerializerTest {
	private val json = Json {
		prettyPrint = false
	}

	@Test
	fun `should serialize Script to JSON`() {
		val script = ScriptsRepository.Script(
			label = "Test Label",
			platform = ScriptsRepository.Platform.ANDROID,
			scripts = listOf(
				ScriptsRepository.ScriptCode.Script("command 1"),
				ScriptsRepository.ScriptCode.CommentedScript("command 2", "comment 2"),
			),
			comment = "Main Comment",
		)

		val serialized = json.encodeToString(ScriptSerializer, script)
		val expected = "{\"label\":\"Test Label\",\"platform\":\"ANDROID\",\"scripts\":" +
			"[\"command 1\",{\"script\":\"command 2\",\"comment\":\"comment 2\"}]," +
			"\"comment\":\"Main Comment\"}"
		assertEquals(expected, serialized)
	}

	@Test
	fun `should deserialize Script from JSON`() {
		val jsonString = "{\"label\":\"Test Label\",\"platform\":\"ANDROID\",\"scripts\":" + "" +
			"[\"command 1\",{\"script\":\"command 2\",\"comment\":\"comment 2\"}],\"comment\":\"Main Comment\"}"
		val deserialized = json.decodeFromString(ScriptSerializer, jsonString)

		val expected = ScriptsRepository.Script(
			label = "Test Label",
			platform = ScriptsRepository.Platform.ANDROID,
			scripts = listOf(
				ScriptsRepository.ScriptCode.Script("command 1"),
				ScriptsRepository.ScriptCode.CommentedScript("command 2", "comment 2"),
			),
			comment = "Main Comment",
		)
		assertEquals(expected, deserialized)
	}

	@Test
	fun `should serialize Script without comment to JSON`() {
		val script = ScriptsRepository.Script(
			label = "Minimal",
			platform = ScriptsRepository.Platform.DESKTOP,
			scripts = listOf(ScriptsRepository.ScriptCode.Script("ls")),
			comment = null,
		)

		val serialized = json.encodeToString(ScriptSerializer, script)
		val expected = "{\"label\":\"Minimal\",\"platform\":\"DESKTOP\",\"scripts\":[\"ls\"]}"
		assertEquals(expected, serialized)
	}

	@Test
	fun `should deserialize Script without comment from JSON`() {
		val jsonString = "{\"label\":\"Minimal\",\"platform\":\"DESKTOP\",\"scripts\":[\"ls\"]}"
		val deserialized = json.decodeFromString(ScriptSerializer, jsonString)

		val expected = ScriptsRepository.Script(
			label = "Minimal",
			platform = ScriptsRepository.Platform.DESKTOP,
			scripts = listOf(ScriptsRepository.ScriptCode.Script("ls")),
			comment = null,
		)
		assertEquals(expected, deserialized)
	}
}
