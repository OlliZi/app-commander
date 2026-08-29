package de.joz.appcommander.data.json

import de.joz.appcommander.domain.script.ScriptsRepository
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScriptCodeSerializerTest {
	private val json = Json

	@Test
	fun `should serialize ScriptCode Script as string`() {
		val script = ScriptsRepository.ScriptCode.Script("adb shell ls")
		val serialized = json.encodeToString(ScriptCodeSerializer, script)
		assertEquals("\"adb shell ls\"", serialized)
	}

	@Test
	fun `should serialize ScriptCode CommentedScript as object`() {
		val script = ScriptsRepository.ScriptCode.CommentedScript("adb shell ls", "list files")
		val serialized = json.encodeToString(ScriptCodeSerializer, script)
		assertEquals("{\"script\":\"adb shell ls\",\"comment\":\"list files\"}", serialized)
	}

	@Test
	fun `should deserialize ScriptCode Script from string`() {
		val jsonString = "\"adb shell ls\""
		val deserialized = json.decodeFromString(ScriptCodeSerializer, jsonString)

		assertIs<ScriptsRepository.ScriptCode.Script>(deserialized)
		assertEquals("adb shell ls", deserialized.script)
	}

	@Test
	fun `should deserialize ScriptCode Script from object without comment`() {
		val jsonString = "{\"script\":\"adb shell ls\"}"
		val deserialized = json.decodeFromString(ScriptCodeSerializer, jsonString)

		assertIs<ScriptsRepository.ScriptCode.Script>(deserialized)
		assertEquals("adb shell ls", deserialized.script)
	}

	@Test
	fun `should deserialize ScriptCode CommentedScript from object with comment`() {
		val jsonString = "{\"script\":\"adb shell ls\",\"comment\":\"list files\"}"
		val deserialized = json.decodeFromString(ScriptCodeSerializer, jsonString)

		assertIs<ScriptsRepository.ScriptCode.CommentedScript>(deserialized)
		assertEquals("adb shell ls", deserialized.script)
		assertEquals("list files", deserialized.comment)
	}
}
