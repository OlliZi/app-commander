package de.joz.appcommander.helper

import de.joz.appcommander.domain.script.ScriptsRepository

fun List<String>.toSubScripts(): List<ScriptsRepository.ScriptCode> =
	map {
		ScriptsRepository.ScriptCode.Script(script = it)
	}
