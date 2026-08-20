import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.ksp)
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_11
	}
}

dependencies {
	implementation(projects.sharedCode)
	implementation(libs.androidx.activity.compose)
	implementation(libs.compose.uiToolingPreview)
	debugImplementation(libs.compose.uiTooling)
}

android {
	namespace = "de.joz.appcommander"
	compileSdk = libs.versions.android.compileSdk
		.get()
		.toInt()

	defaultConfig {
		applicationId = "de.joz.appcommander"
		minSdk = libs.versions.android.minSdk
			.get()
			.toInt()
		targetSdk = libs.versions.android.targetSdk
			.get()
			.toInt()
		versionCode = rootProject.ext["mainVersion"]
			.toString()
			.replace(".", "")
			.toInt()
		versionName = rootProject.ext["mainVersion"].toString()
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
	}
}
