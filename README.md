# App-Commander

[![App-Commander - PR-Checks](https://github.com/OlliZi/app-commander/actions/workflows/pr_check.yml/badge.svg)](https://github.com/OlliZi/app-commander/actions/workflows/pr_check.yml)

# What is "App-Commander"?

App-Commander: Your programmable multi-device execution helper. Execute scripts for your apps on
multiple devices.
This is a Kotlin Multiplatform project targeting Desktop (JVM). Android and iOS don't make sense but
is still maybe possible in the future.

# Scripts

Your scripts are saved in home directory in ".app_commander". The file name is "scripts.json".
You can simply edit your custom script here, for example:

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
- To run a script in a loop, add "#LOOP_10" before your script, for example to run a script ten
  times:

```
"#LOOP_10 adb shell input swipe 500 500 1000 500"
```

- You can chain multiple commands by inserting "&&" between your commands, for example:

```
"adb shell input swipe 500 500 1000 500 && adb shell input tab 500 500"
```

# Features

- Welcome-screen
- Scripts-screen
- Script-screen for new scripts + edit existing scripts
- Terminal-section
- Logging-section
- Settings-screen
- Light and dark mode
- Save user preferences
- Open script file externally to make more custom changes
- Automatically refresh scripts list all x seconds if file has changed

# Screenshots

- Welcome-screen
    - <kbd>![Scripts-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/welcome/screenshots/animation.png)</kbd>
- Scripts-screen
    - <kbd>![Scripts-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/scripts/screenshots/show_all.png)</kbd>
    - <kbd>![Scripts-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/scripts/screenshots/default_label.png)</kbd>
- Settings-screen
    - <kbd>![Settings-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/settings/screenshots/default_label.png)</kbd>
    - <kbd>![Settings-screen](/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/settings/screenshots/changed_label.png)</kbd>

# TODOs

- AI to write readme
- Create a video
- Solve selectedDevice = "TODO" (EditScriptVM) + Make device section in edit script-screen workable
- UI-tests (EditScriptContent, ...)
- Unit-test for PreferencesRepositoryImpl
- Open-Icons for Logging and
  Terminal https://joebirch.co/android/exploring-material-3-for-compose-floating-action-button-menu/

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
