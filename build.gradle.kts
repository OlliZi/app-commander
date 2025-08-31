buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        // https://github.com/ben-manes/gradle-versions-plugin
        classpath(libs.gradle.versions.plugin)
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    apply(plugin = "com.github.ben-manes.versions")
}