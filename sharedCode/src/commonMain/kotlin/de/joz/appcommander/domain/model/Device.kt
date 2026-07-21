package de.joz.appcommander.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
	val id: String,
	val label: String,
	val isSelected: Boolean,
)
