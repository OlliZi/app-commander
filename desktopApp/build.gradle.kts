import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.io.gitlab.arturbosch.detekt)
}

dependencies {
	implementation(projects.sharedCode)
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

			macOS {
				iconFile.set(project.file("app_logo_mac.icns"))
			}
			windows {
				// TODO Create PR if needed:) iconFile.set(project.file("icon.ico"))
			}
			linux {
				iconFile.set(project.file("../sharedCode/src/commonMain/composeResources/drawable/app_logo.png"))
			}
		}
	}
}
