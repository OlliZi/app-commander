package de.joz.appcommander.helper

import de.joz.appcommander.domain.script.ScriptsRepository

fun List<String>.toSubScripts(): List<ScriptsRepository.SubScript> =
	map {
		ScriptsRepository.SubScript(subScript = it, comment = null)
	}
