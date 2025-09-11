package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetConnectedDevivesUseCase {

    suspend operator fun invoke(): List<String> {
        return listOf(
            "Device ",
            "Device ",
            "Device ",
            "Device ",
            "Device ",
            "Device ",
            "Device ",
        )
    }
}