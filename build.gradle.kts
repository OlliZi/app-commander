buildscript {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}

	dependencies {
		// https://github.com/ben-manes/gradle-versions-plugin
		classpath(libs.gradle.versions.plugin)
		classpath(libs.ktlint.gradle)
	}
}

plugins {
	alias(libs.plugins.composeMultiplatform) apply false
	alias(libs.plugins.composeCompiler) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
	alias(libs.plugins.ktlint)
}

allprojects {
	apply(plugin = "com.github.ben-manes.versions")
	apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register<Exec>("convertMov2Gif") {
	group = "_joz"
	commandLine(
		"sh",
		"-c",
		"ffmpeg -i preview_overview.mov -pix_fmt rgb8 -r 10 preview_overview.gif && gifsicle -O3 preview_overview.gif -o preview_overview.gif",
	)
}
