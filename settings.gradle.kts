rootProject.name = "App-Commander"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
	repositories {
		google {
			mavenContent {
				includeGroup("androidx")
				includeGroupByRegex("androidx\\..*")
				includeGroup("com.android")
				includeGroupByRegex("com\\.android\\..*")
				includeGroup("com.google")
				includeGroupByRegex("com\\.google\\..*")
			}
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositories {
		google {
			mavenContent {
				includeGroup("androidx")
				includeGroupByRegex("androidx\\..*")
				includeGroup("com.android")
				includeGroupByRegex("com\\.android\\..*")
				includeGroup("com.google")
				includeGroupByRegex("com\\.google\\..*")
			}
		}
		mavenCentral()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":sharedCode")
include(":desktopApp")
include(":androidApp")
