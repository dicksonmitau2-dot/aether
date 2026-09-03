# Development

How to run each surface from this repository. Paths below are relative to the repo root.

## Desktop (Java Swing)

**Requires:** JDK 11 or newer (`java` and `javac` on `PATH`).

The Windows `build.bat` is hardcoded to an IntelliJ JBR under `C:\AetherMind` and will not work as-is on Linux, macOS, or a stock JDK. Use the JDK on your `PATH` instead:

```bash
javac AetherMindApp.java
jar cfm AetherMind.jar MANIFEST.MF AetherMindApp.class AetherMindApp\$*.class
java -jar AetherMind.jar
```

On Windows cmd.exe, escape inner-class globbing differently:

```bat
javac AetherMindApp.java
jar cfm AetherMind.jar MANIFEST.MF AetherMindApp.class AetherMindApp$*.class
java -jar AetherMind.jar
```

`uninstall.bat` deletes `C:\AetherMind` and a Desktop shortcut. Do not run it against this git checkout.

## Web

**Requires:** any modern browser. A local HTTP server is optional but recommended (some browsers restrict `file://` behavior).

```bash
cd web
python3 -m http.server 8080
```

Then open `http://127.0.0.1:8080`. On another device on the same network, use this machine's LAN address instead of localhost.

Windows users can run `web/StartServer.bat` if Python is installed; it still assumes the old `C:\AetherMind\web` path, so prefer the command above from the real repo location.

There is no build step and no bundler. Edit `web/aether.js` / `web/style.css` and refresh.

## Android

**Requires:** Android Studio (or a JDK 17 + Android SDK), min API 26, compile/target API 35.

This tree does **not** include the Gradle wrapper (`gradlew`). Opening `android/` in Android Studio is the supported path — Studio generates the wrapper on first sync.

1. Open the `android/` directory (not the repo root) in Android Studio.
2. Let Gradle sync.
3. Run on an API 26+ emulator or device.

Release / APK steps: [android/HOW_TO_BUILD_APK.md](../android/HOW_TO_BUILD_APK.md).

Do not commit `local.properties`, keystores (`*.jks`), or `android/app/build/`.

## Version string

`0.9` is duplicated in UI copy and Gradle. When bumping, update all of:

- `AetherMindApp.java` (window title + boot banner)
- `web/index.html` (subtitle + boot banner)
- `android/app/build.gradle.kts` (`versionName`)
- `android/app/src/main/res/values/strings.xml` (`app_subtitle`)
- `android/.../MainActivity.kt` (boot text)
- `README.md`, `CHANGELOG.md`, `ROADMAP.md`

## Layout of this repo

```
aether/
├── AetherMindApp.java     Desktop app (UI + brain)
├── MANIFEST.MF            JAR Main-Class
├── build.bat              Windows-only desktop build (hardcoded paths)
├── uninstall.bat          Windows-only; deletes C:\AetherMind
├── android/               Kotlin app (open this folder in Android Studio)
├── web/                   Static HTML/JS/CSS
├── docs/                  Architecture and contributor guides
├── README.md
├── ROADMAP.md
└── CHANGELOG.md
```
