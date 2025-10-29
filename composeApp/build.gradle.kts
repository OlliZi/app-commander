import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.composeHotReload)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
	alias(libs.plugins.koverCodeCoverage)
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
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.ui)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.androidx.lifecycle.viewmodelCompose)
			implementation(libs.androidx.lifecycle.runtimeCompose)
			implementation(libs.navigation.compose)
			implementation(libs.androidx.datastore.preferences)
			implementation(libs.bundles.koin)
			implementation(libs.compose.icons)
			implementation(libs.kotlinx.serialization.json)
		}
		commonTest.dependencies {
			@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
			implementation(compose.uiTest)
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
	packageOfResClass = "de.joz.appcommander.resources"
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
		mainClass = "de.joz.appcommander.DesktopAppKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "App-Commander"
			packageVersion = "1.0.0"
		}
	}
}

tasks.register("runDependencyUpdates") {
	group = "_joz"
	dependsOn("dependencyUpdates")
}

tasks.register("runCodeCoverage") {
	group = "_joz"
	dependsOn("koverLog")
	dependsOn("koverVerify")
	dependsOn("koverHtmlReport")
}

kover {
	reports {
		filters {
			excludes {
				packages("org.koin.ksp.generated", "de.joz.appcommander.resources")
				classes("**ComposableSingletons**")
			}
		}
		verify {
			rule("Minimal line coverage rate in percent.") {
				minBound(69)
			}
			rule("Maximum line coverage rate in percent. Indicator to adjust when coverage was increased.") {
				maxBound(70)
			}
		}
	}
}
