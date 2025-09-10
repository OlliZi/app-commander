# app-commander

App-Commander: Execute your custom scripts for your apps on multiple devices.
This is a Kotlin Multiplatform project targeting Desktop (JVM). Android and iOS dont make sense but
is still maybe possible in future.

# Features

- Welcome-screen
- Settings-screen
- Save user preferences
- Code quality (UI-tests, Unit-tests, detekt)

# TODOs

- integrate ktlint
- script screen
- unit-tests for PreferencesRepositoryImpl
- ui-test
- Theming (light and dark)
- Code coverage

# Installation/Run

- Run desktop app:
    - Navigate to DesktopApp.kt in ../composeApp/src/jvmMain/kotlin/de/joz/appcommander and start
      main function.

    - Installation: Run gradle task "package" or "package<platform>".

# Technical background

- Clean architecture (UI -> ViewModel -> UseCases -> Repository -> DB/API)
- Dependency injection by koin
- Preferences by datastore
- Integrate library update tooling
- Integrate detekt plugin + baseline
