buildscript {
	dependencies {
		classpath(libs.ktlint.gradle)
	}
}

rootProject.ext["mainPackage"] = "de.joz.appcommander".also { println("Package: $it") }
rootProject.ext["mainVersion"] = "3.1.0".also { println("Version: $it") }
rootProject.ext["isRelease"] = gradle.startParameter.taskNames
	.any {
		it.contains("package")
	}.also { println("Is release build: $it") }

plugins {
	alias(libs.plugins.composeMultiplatform) apply false
	alias(libs.plugins.composeCompiler) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.kotlinJvm) apply false
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
	alias(libs.plugins.ktlint)
}
