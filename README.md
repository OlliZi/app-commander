# app-commander

App-Commander: Execute your custom scripts for your apps on multiple devices.
This is a Kotlin Multiplatform project targeting Desktop (JVM). Android and iOS dont make sense but
is still maybe possible in future.

# Features

- Welcome-screen
- Script-screen
- Settings-screen
- Save user preferences
- Open script file to make changes
- Code quality (UI-tests, Unit-tests, detekt)

# TODOs

- Script screen
- Previews for all screens and elements
- Fix detekt
- integrate ktlint
- Unit-Tests for PreferencesRepositoryImpl
- UI-tests
- Theming (light and dark)
- Code coverage
- Auto refresh script file
- Terminal screen
- Log screen
- Unit-test for appended device id in ExecuteScriptUseCase

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
