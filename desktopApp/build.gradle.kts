import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
}

dependencies {
	implementation(projects.composeApp)
	implementation(compose.desktop.currentOs)
	implementation(libs.kotlinx.coroutinesSwing)
}

private val mainPackage = rootProject.ext["mainPackage"].toString()
private val mainVersion = rootProject.ext["mainVersion"].toString()

compose.desktop {
	application {
		mainClass = "$mainPackage.launch.DesktopAppKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Msi)
			packageName = "App-Commander"
			packageVersion = mainVersion
			modules("jdk.unsupported")
		}
	}
}
