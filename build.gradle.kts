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
    alias(libs.plugins.composeHotReload) apply false
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
