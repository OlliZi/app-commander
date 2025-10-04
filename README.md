# What is the "App-Commander"?

App-Commander: Your programmable multi-device execution helper. Execute scripts for your apps on
multiple devices.
This is a Kotlin Multiplatform project targeting Desktop (JVM). Android and iOS dont make sense but
is still maybe possible in future.

# Features

- Welcome-screen
- Script-screen
- Terminal-screen
- Logging-section
- Settings-screen
- Light and dark mode
- Save user preferences
- Open script file to make changes
- Automatically refresh scripts list all x seconds if file has changed

# TODOs

- Fix detekt
- UI-tests
- Screenshot testing
- Code coverage
- Enter new scripts screen
- Unit-test for appended device id in PreferencesRepositoryImpl + TrackScriptsFileChangesUseCaseTest
- Fix test "should append device id in script ..." in ExecuteScriptUseCaseTest
- Open-Icons für Logging and Terminal unten
  rechts? https://joebirch.co/android/exploring-material-3-for-compose-floating-action-button-menu/

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
- Code quality (UI-tests, Unit-tests, detekt & ktlint)
- Previews for all screens and elements
