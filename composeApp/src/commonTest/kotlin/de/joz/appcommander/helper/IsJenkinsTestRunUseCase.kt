package de.joz.appcommander.helper

class IsJenkinsTestRunUseCase {
	operator fun invoke(): Boolean =
		System.getenv().any {
			it.key.lowercase().contains("jenkins") || it.value.lowercase().contains("jenkins")
		}
}
