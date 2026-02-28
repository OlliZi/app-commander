# App-Commander 🚀

**Your programmable multi-device execution helper.**

[![App-Commander - PR-Checks](https://github.com/OlliZi/app-commander/actions/workflows/pr_check.yml/badge.svg)](https://github.com/OlliZi/app-commander/actions/workflows/pr_check.yml)

App-Commander is a powerful desktop application built with Kotlin Multiplatform that simplifies your mobile development workflow. It allows you to define, manage, and execute custom scripts across multiple Android and iOS devices simultaneously. Say goodbye to repetitive manual tasks and hello to streamlined efficiency!

## ✨ Key Features

*   **Multi-Device Control:** Execute commands on any number of connected devices at once.
*   **Customizable Scripts:** Create and save your own scripts to automate frequent tasks.
*   **Simple Script Management:** An intuitive UI to add, edit, and organize your scripts.
*   **Live Terminals:** View real-time command output for each device in a dedicated terminal section.
*   **Centralized Logging:** Keep track of all script executions and device responses in one place.
*   **Powerful Scripting:**
    *   **Looping:** Run a command multiple times with a simple prefix.
    *   **Chaining:** Execute a series of commands in sequence.
*   **User-Friendly Interface:**
    *   Light and Dark modes to suit your preference.
    *   Settings screen to customize the app behavior.
    *   Automatic refresh of scripts when you edit them externally.
*   **External Script Editing:** For advanced users, you can directly edit the `scripts.json` file.

## 🎥 First Impressions I
<kbd><img src="preview_overview.gif" width="500"/></kbd>

## 📸 First Impressions II

**Welcome Screen Animation**
    <kbd>![Welcome-screen](https://raw.githubusercontent.com/OlliZi/app-commander/refs/heads/main/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/welcome/screenshots/animation.png)</kbd>

**Scripts Screen**
    <kbd>![Scripts-screen](https://raw.githubusercontent.com/OlliZi/app-commander/refs/heads/main/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/scripts/screenshots/show_all.png)</kbd>
    <kbd>![Scripts-screen with default label](https://raw.githubusercontent.com/OlliZi/app-commander/refs/heads/main/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/scripts/screenshots/default_label.png)</kbd>

**Settings Screen**
    <kbd>![Settings-screen with default label](https://raw.githubusercontent.com/OlliZi/app-commander/refs/heads/main/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/settings/screenshots/default_label.png)</kbd>
    <kbd>![Settings-screen with changed label](https://raw.githubusercontent.com/OlliZi/app-commander/refs/heads/main/composeApp/src/commonTest/kotlin/de/joz/appcommander/ui/settings/screenshots/changed_label.png)</kbd>

## 🤖 How It Works

App-Commander lets you create a library of scripts. Each script consists of:

*   **A Label:** A friendly name for your script (e.g., "Take Screenshot", "Enable Dark Mode").
*   **The Script:** The actual shell command to be executed (e.g., `adb shell cmd uimode night yes`).
*   **Platform:** The target operating system (`ANDROID` or `IOS`).

Your scripts are stored in a `scripts.json` file located in the `.app_commander` directory in your user's home folder.

**Example Script:**
```json
{
  "label": "Toggle Dark Mode On",
  "script": "adb shell cmd uimode night yes",
  "platform": "ANDROID"
}
```

### Advanced Scripting

Unleash the full potential of App-Commander with these special commands in your scripts:

*   **Looping:** To run a script multiple times, prefix it with `#LOOP_N`, where `N` is the number of repetitions.
    ```
    #LOOP_10 adb shell input swipe 500 500 1000 500
    ```
*   **Command Chaining:** Use `&&` to link multiple commands together for sequential execution.
    ```
    adb shell input swipe 500 500 1000 500 && adb shell input tap 500 500
    ```

## 🛠️ Installation and Running

1.  Clone the repository.
2.  Open the project in your IDE (e.g., IntelliJ IDEA, Android Studio).
3.  Navigate to `composeApp/src/jvmMain/kotlin/de/joz/appcommander/DesktopApp.kt`.
4.  Run the `main` function to start the desktop application.

To create a distributable package, run the Gradle task `package` or `package<Platform>` (e.g., `packageDmg`, `packageMsi`).

## 👨‍💻 For Developers: Technical Insights

App-Commander is built with modern technologies and best practices:

*   **Kotlin Multiplatform:** Targeting the JVM for a native desktop experience.
*   **Clean Architecture:** A well-structured codebase separating UI, business logic, and data layers.
*   **Software Layer:** UI ↔ ViewModel ↔ UseCases ↔ Repository ↔ DB/API
*   **Dependency Injection:** Using Koin for managing dependencies.
*   **Data Persistence:** Jetpack Datastore for storing user preferences.
*   **Code Quality:**
    *   Static analysis with Detekt and Ktlint.
    *   Comprehensive testing including UI tests, unit tests, and screenshot tests.
    *   Code Coverage.
    *   Execute code quality locally (on my jenkins) and in github-cloud
*   **Composable Preview:**
    *   Provide previews for all screens and composables.

## 👷TODOs
- Refactoring: Collapse UI
- Create a video showing the UI with emulator and a real device
- Settings: Checkboxes to show sections (Terminal, Logging, Filter)
- Solve selectedDevice = "TODO" (EditScriptVM) + Make device section in edit script-screen workable 
- UI-tests (EditScriptContent, ...)
- Unit-test for PreferencesRepositoryImpl 
- Open-Icons for Logging and Terminal https://joebirch.co/android/exploring-material-3-for-compose-floating-action-button-menu/

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features, bug fixes, or improvements, feel free to open an issue or submit a pull request.
