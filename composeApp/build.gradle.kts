import org.jetbrains.kotlin.gradle.dsl.JvmTarget

private val mainPackage = rootProject.ext["mainPackage"].toString()
private val mainVersion = rootProject.ext["mainVersion"].toString()
private val isRelease = rootProject.ext["isRelease"] == "true"

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidMultiplatformLibrary)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
	alias(libs.plugins.koverCodeCoverage)
	alias(libs.plugins.buildConfig)
}

kotlin {
	jvm()

	// move android to androidApp-dir
	androidLibrary {
		namespace = "de.joz.myapplication.shared"
		compileSdk = libs.versions.android.compileSdk
			.get()
			.toInt()
		minSdk = libs.versions.android.minSdk
			.get()
			.toInt()

		compilerOptions {
			jvmTarget = JvmTarget.JVM_11
		}
		androidResources {
			enable = true
		}
		withHostTest {
			isIncludeAndroidResources = true
		}
	}

	sourceSets {
		androidMain.dependencies {
			implementation(libs.androidx.datastore.preferences)
		}
		commonMain.dependencies {
			implementation(libs.compose.runtime)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.compose.ui)
			api(libs.compose.components.resources)
			implementation(libs.androidx.lifecycle.viewmodelCompose)
			implementation(libs.androidx.lifecycle.runtimeCompose)
			implementation(libs.navigation.compose)
			api(libs.androidx.datastore.preferences.core)
			api(libs.bundles.koin)
			implementation(libs.compose.icons)
			implementation(libs.kotlinx.serialization.json)
			api(libs.compose.uiToolingPreview)
		}
		commonTest.dependencies {
			implementation(libs.compose.ui.test)
			implementation(libs.kotlin.test)
			implementation(libs.mockk)
			implementation(libs.kotlinx.coroutines.test)
		}
		jvmTest.dependencies {
			implementation(compose.desktop.currentOs)
		}
	}
}

dependencies {
	ksp(libs.koin.ksp)
	androidRuntimeClasspath(libs.compose.uiTooling)
}

ksp {
	arg("KOIN_CONFIG_CHECK", "true")
	arg("KOIN_DEFAULT_MODULE", "false")
}

buildConfig {
	packageName(mainPackage)
	buildConfigField(
		name = "MAIN_VERSION",
		value = if (isRelease) mainVersion else "Debug 6.7.8",
	)
}

detekt {
	buildUponDefaultConfig = true // preconfigure defaults
	allRules = false // activate all available (even unstable) rules.
	autoCorrect = false // Enable automatic correction of issues found by detekt
	config.setFrom("$projectDir/../detekt-config.yml")
	parallel = true // Run detekt in parallel mode for better performance
}

kover {
	reports {
		filters {
			excludes {
				packages("org.koin.ksp.generated", "$mainPackage.resources", "$mainPackage.launch")
				classes("**ComposableSingletons**", "**NavigationScreens\$Companion**")
			}
		}
		verify {
			// also edit in README.md
			val lineCoverage = 95
			rule("Minimal line coverage rate in percent.") {
				minBound(lineCoverage)
			}
			rule("Maximum line coverage rate in percent. Indicator to adjust when coverage was increased.") {
				maxBound(lineCoverage + 1)
			}
		}
	}
}

compose.resources {
	publicResClass = true
	packageOfResClass = "$mainPackage.resources"
	generateResClass = always
}

tasks.register("runDependencyUpdates") {
	group = "_joz"
	description = "Run dependency updates."
	dependsOn("dependencyUpdates")
}

tasks.register("runCodeCoverage") {
	group = "_joz"
	description = "Run all kover tasks: Execute all tests and create code coverage."
	dependsOn("koverLog")
	dependsOn("koverVerify")
	dependsOn("koverHtmlReport")
}
