# How to build the AetherMind APK

## What you need

| Tool | Where | Size |
|------|-------|------|
| Android Studio | https://developer.android.com/studio | ~1 GB |
| JDK 17 | Bundled with Android Studio | — |
| Android SDK API 35 | Installed from the SDK Manager | ~500 MB |

Open the **`android/`** directory of this repository in Android Studio — not the repo root.

---

## Step-by-step

### 1. Install Android Studio

1. Download from https://developer.android.com/studio and install with the defaults.
2. On first launch, let it download the Android SDK.
3. In the SDK Manager, enable **Android 15 (API 35)**.

### 2. Open the project

1. Launch Android Studio → **Open**.
2. Select the `android/` folder inside this checkout.
3. Wait for Gradle to sync (1–3 minutes the first time). There is no `gradlew` in git; Studio generates the wrapper.

### 3. Build a debug APK (testing)

1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. When the popup says the build succeeded, click **locate**.
3. Output path (relative to `android/`):

```
app/build/outputs/apk/debug/app-debug.apk
```

The debug application id is `com.aethermind.app.debug` (see `applicationIdSuffix` in `app/build.gradle.kts`).

### 4. Install on a phone

#### USB (easiest)

1. On the phone: **Settings → About phone** → tap **Build number** seven times.
2. **Settings → Developer options → USB debugging** → on.
3. Plug in the phone and click the green **Run** button in Android Studio.

#### Sideload the APK

1. Copy `app-debug.apk` to the phone.
2. Allow your file manager under **Install unknown apps**.
3. Open the APK and tap **Install**.

### 5. Build a signed release APK (sharing)

1. **Build → Generate Signed Bundle / APK** → **APK**.
2. Create a new keystore **outside** the repo (do not commit `*.jks`).
3. **Next → Release → Finish**.
4. Typical output:

```
app/release/app-release.apk
```

---

## Project layout (under `android/`)

```
android/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/aethermind/app/
│   │   ├── AetherApp.kt
│   │   ├── AetherBrain.kt
│   │   ├── ChatAdapter.kt
│   │   ├── ChatMessage.kt
│   │   └── MainActivity.kt
│   └── res/
├── gradle/libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Minimum device

- Android 8.0 (API 26) or higher
- Compile / target: Android 15 (API 35)

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Gradle sync fails | File → Invalidate Caches → Restart |
| SDK not found | SDK Manager → install API 35 |
| Phone not detected | Enable USB debugging |
| "App not installed" | Enable Install unknown apps; uninstall any previous debug build first (`com.aethermind.app.debug`) |
| First build is slow | Normal — later builds are incremental |
