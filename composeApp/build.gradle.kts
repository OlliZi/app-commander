import org.jetbrains.compose.desktop.application.dsl.TargetFormat

private val mainPackage = "de.joz.appcommander"
private val mainVersion = "2.0.0"

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
	alias(libs.plugins.koverCodeCoverage)
	alias(libs.plugins.buildConfig)
}

buildConfig {
	packageName(mainPackage)
	buildConfigField(name = "MAIN_VERSION", value = mainVersion)
}

allprojects {
	apply(plugin = "io.gitlab.arturbosch.detekt")

	detekt {
		buildUponDefaultConfig = true // preconfigure defaults
		allRules = false // activate all available (even unstable) rules.
		// point to your custom config defining rules to run, overwriting default behavior
		autoCorrect = false // Enable automatic correction of issues found by detekt
		config.setFrom("$projectDir/../detekt-config.yml")
		parallel = true // Run detekt in parallel mode for better performance
	}
}

kotlin {
	jvm()

	sourceSets {
		commonMain.dependencies {
			implementation(libs.compose.runtime)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.compose.ui)
			implementation(libs.compose.components.resources)
			implementation(libs.androidx.lifecycle.viewmodelCompose)
			implementation(libs.androidx.lifecycle.runtimeCompose)
			implementation(libs.navigation.compose)
			implementation(libs.androidx.datastore.preferences)
			implementation(libs.bundles.koin)
			implementation(libs.compose.icons)
			implementation(libs.kotlinx.serialization.json)
		}
		commonTest.dependencies {
			implementation(libs.compose.ui.test)
			implementation(libs.kotlin.test)
			implementation(libs.mockk)
			implementation(libs.kotlinx.coroutines.test)
		}
		jvmMain.dependencies {
			implementation(compose.desktop.currentOs)
			implementation(libs.kotlinx.coroutinesSwing)
		}
	}
}

compose.resources {
	publicResClass = true
	packageOfResClass = "$mainPackage.resources"
	generateResClass = always
}

dependencies {
	ksp(libs.koin.ksp)
}

ksp {
	arg("KOIN_CONFIG_CHECK", "true")
	arg("KOIN_DEFAULT_MODULE", "false")
}

compose.desktop {
	application {
		mainClass = "$mainPackage.launch.DesktopAppKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg)
			packageName = "App-Commander"
			packageVersion = mainVersion
			modules("jdk.unsupported")
		}
	}
}

tasks.register("runDependencyUpdates") {
	group = "_joz"
	description = "Run dependency udpates."
	dependsOn("dependencyUpdates")
}

tasks.register("runCodeCoverage") {
	group = "_joz"
	description = "Run all kover task and execute tests and create code coverage."
	dependsOn("koverLog")
	dependsOn("koverVerify")
	dependsOn("koverHtmlReport")
}

kover {
	reports {
		filters {
			excludes {
				packages("org.koin.ksp.generated", "$mainPackage.resources", "$mainPackage.launch")
				classes("**ComposableSingletons**", $$"**NavigationScreens$Companion**")
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
