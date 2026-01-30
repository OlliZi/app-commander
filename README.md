# App-Commander
[![App-Commander - PR-Checks](https://github.com/OlliZi/app-commander/actions/workflows/pr_check.yml/badge.svg)](https://github.com/OlliZi/app-commander/actions/workflows/pr_check.yml)

# What is "App-Commander"?

App-Commander: Your programmable multi-device execution helper. Execute scripts for your apps on
multiple devices.
This is a Kotlin Multiplatform project targeting Desktop (JVM). Android and iOS dont make sense but
is still maybe possible in future.

# Description
Your scripts are saved in home directory in ".app_commander". The file name is "scripts.json".
You can simple edit your custom script here, for example:
```json
{
    "label": "Dark mode",
    "script": "adb shell cmd uimode night yes",
    "platform": "ANDROID"
}
```

- "label": The name of your script.
- "script": The script itself.
- "platform": The target platform ("ANDROID" or "IOS")

# Special commands
- The device identifier is automatically injected to run your script on multiple devices.
- To run a script in a loop, add "#LOOP_10" before your script, for example: 
```
"#LOOP_10 adb shell input swipe 500 500 1000 500"
```
This runs the script ten times.

# Features

- Welcome-screen
- Scripts-screen
- Script-screen for new scripts + edit existing scripts
- Terminal-screen
- Logging-section
- Settings-screen
- Light and dark mode
- Save user preferences
- Open script file to make changes
- Automatically refresh scripts list all x seconds if file has changed

# Screenshots

- Scripts-screen
  ![Scripts-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/scripts/screenshots/default_label.png)
- Settings-screen
  ![Scripts-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/settings/screenshots/default_label.png)
  ![Scripts-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/settings/screenshots/changed_label.png)

# TODOs

- Solve selectedDevice = "TODO" (EditScriptVM) + Make device section in edit script-screen workable
- UI-tests (EditScriptContent, ...)
- build artefacts in CI
- Unit-test for PreferencesRepositoryImpl
- Fix Unit-test "should append device id in script ..." in ExecuteScriptUseCaseTest
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
- Code coverage
- Screenshot testing
- Execute code quality locally (on my jenkins) and in github-cloud
- Previews for all screens and elements
