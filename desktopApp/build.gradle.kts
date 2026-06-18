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

compose.desktop {
	application {
		mainClass = "${rootProject.ext["mainPackage"]}.launch.DesktopAppKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Msi)
			packageName = "App-Commander"
			packageVersion = rootProject.ext["mainVersion"].toString()
			modules("jdk.unsupported")
		}
	}
}
