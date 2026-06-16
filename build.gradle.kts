buildscript {
	dependencies {
		classpath(libs.gradle.versions.plugin)
		classpath(libs.ktlint.gradle)
	}
}

subprojects {
	ext["mainPackage"] = "de.joz.appcommander"
	ext["mainVersion"] = "2.0.1"
}

plugins {
	alias(libs.plugins.composeMultiplatform) apply false
	alias(libs.plugins.composeCompiler) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
	alias(libs.plugins.ktlint)
}
