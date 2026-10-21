# Tic-Tac-Toe for Android

A single-player tic-tac-toe game for Android, written in Kotlin. You play X against a
computer opponent that plays O, with animated marks, sound effects and a scoreboard that
persists between sessions.

## Features

- **Computer opponent** — wins when it can, blocks your winning move, then prefers the
  center and a random corner.
- **Animated board** — X and O marks are drawn in with a glow and drop shadow; the
  winning line pulses when the game ends.
- **Turn indicator** — shows whose turn it is, colored by mark.
- **Persistent scoreboard** — wins, ties and losses are saved on the device.
- **Sound** — background music with fade-in on the home screen, distinct sounds for each
  move, and an end-of-game cue.
- **Rotation-safe** — a game in progress survives screen rotation; a dedicated landscape
  layout is included.
- **Accessibility** — each cell announces its state (empty, X or O) to screen readers.

## Screens

| Screen | Activity | Purpose |
|---|---|---|
| Home | `WelcomeActivity` | Title, play button, link to the Play Store listing |
| Game | `MainActivity` | 3×3 board, turn indicator, scoreboard |
| Result | `WinActivity` | Winner, running score, play again or exit |

## Project structure

```
app/src/main/java/com/myawesomeappv5/tic_tac_toe/
  TicTacToeGame.kt     Board state, win detection and computer strategy (no Android dependencies)
  CellView.kt          Custom view that draws and animates a single cell
  ScoreStore.kt        Win/tie/loss counters in SharedPreferences
  MainActivity.kt      Game screen: input, turn flow, sounds, state restore
  WelcomeActivity.kt   Home screen and background music
  WinActivity.kt       Result screen
app/src/main/res/      Layouts (portrait and landscape), colors, strings, sounds
app/src/test/          JVM unit tests for the game logic
```

Game rules and the computer's strategy live in `TicTacToeGame`, which has no Android
dependencies, so they are covered by fast JVM unit tests.

## Requirements

| | Version |
|---|---|
| JDK | 17 |
| Android Gradle Plugin | 8.6.0 |
| Gradle (wrapper) | 8.7 |
| Kotlin | 1.9.0 |
| Compile / target SDK | 34 |
| Minimum SDK | 24 (Android 7.0) |

## Build and run

Open the project in Android Studio and run the `app` configuration, or use the Gradle
wrapper. Gradle needs the Android SDK location, either from `ANDROID_HOME` or a
`local.properties` file (`sdk.dir=/path/to/Android/Sdk`), which Android Studio creates
automatically.

```bash
./gradlew assembleDebug          # APK at app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # install on a connected device or emulator
```

## Tests

```bash
./gradlew testDebugUnitTest      # JVM unit tests for the game logic
./gradlew connectedAndroidTest   # instrumented tests, needs a device or emulator
```

The unit tests in `TicTacToeGameTest` cover win detection on rows, columns and
diagonals, ties, illegal moves, turn order, the computer's move priorities and board
serialization.

## Release builds

`./gradlew bundleRelease` produces an Android App Bundle for Google Play. Signing
keystores and their passwords are deliberately excluded from the repository (see
`.gitignore`); configure signing locally in Android Studio under
**Build → Generate Signed Bundle / APK**.
