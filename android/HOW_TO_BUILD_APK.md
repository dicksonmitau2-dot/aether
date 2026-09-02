# How to Build AetherMind APK for Android 15

## What You Need (Free Downloads)

| Tool | Download | Size |
|------|----------|------|
| Android Studio | https://developer.android.com/studio | ~1 GB |
| JDK 17 (included in Android Studio) | bundled | - |
| Android SDK API 35 | installed inside Android Studio | ~500 MB |

---

## Step-by-Step Guide

### 1. Install Android Studio
- Go to https://developer.android.com/studio
- Download and install it (keep all default options)
- On first launch, let it download the Android SDK automatically
- When the SDK Manager asks, make sure **Android 15 (API 35)** is checked

---

### 2. Open the Project
1. Launch Android Studio
2. Click **"Open"**
3. Navigate to `C:\AetherMind\android`
4. Click **OK**
5. Wait for Gradle to sync (bottom bar shows progress — takes 1-3 minutes first time)

---

### 3. Build the Debug APK (for testing)
1. In the top menu click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for the build to finish
3. A popup says **"Build successful"** — click **"locate"**
4. The APK is at:
   ```
   C:\AetherMind\android\app\build\outputs\apk\debug\app-debug.apk
   ```

---

### 4. Install on Your Android 15 Phone

#### Option A — USB Cable (easiest)
1. On your phone go to **Settings → About Phone**
2. Tap **Build Number** 7 times to unlock Developer Options
3. Go to **Settings → Developer Options → USB Debugging** → turn ON
4. Plug phone into PC with USB cable
5. In Android Studio click the **▶ Run** button (green play button)
6. Select your phone from the list
7. App installs and opens automatically!

#### Option B — Copy APK file
1. Copy `app-debug.apk` to your phone (via USB, Google Drive, or email)
2. On your phone go to **Settings → Apps → Special Access → Install Unknown Apps**
3. Allow your file manager to install APKs
4. Open the APK file on your phone and tap **Install**

---

### 5. Build a Release APK (for sharing)
1. In Android Studio go to **Build → Generate Signed Bundle / APK**
2. Choose **APK**
3. Create a new keystore (this signs your app):
   - Key store path: `C:\AetherMind\android\aethermind.jks`
   - Set a password you'll remember
   - Fill in alias and your name
4. Click **Next → Release → Finish**
5. Release APK is at:
   ```
   C:\AetherMind\android\app\release\app-release.apk
   ```

---

## Project Structure Reference

```
C:\AetherMind\android\
├── app\
│   └── src\main\
│       ├── AndroidManifest.xml
│       ├── java\com\aethermind\app\
│       │   ├── AetherApp.kt          ← App class
│       │   ├── AetherBrain.kt        ← AI logic & knowledge base
│       │   ├── ChatAdapter.kt        ← RecyclerView adapter
│       │   ├── ChatMessage.kt        ← Message data class
│       │   └── MainActivity.kt       ← Main screen logic
│       └── res\
│           ├── drawable\             ← Icons, bubbles, shapes
│           ├── layout\               ← XML screen layouts
│           ├── mipmap-hdpi\          ← App icon
│           └── values\               ← Colors, strings, themes
├── gradle\
│   └── libs.versions.toml            ← Dependency versions
├── build.gradle.kts                  ← Root build config
└── settings.gradle.kts               ← Project settings
```

---

## Minimum Phone Requirements
- Android 8.0 (API 26) or higher
- Tested target: **Android 15 (API 35)**
- Works on: Samsung, Pixel, OnePlus, Xiaomi, and all major Android brands

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Gradle sync fails | File → Invalidate Caches → Restart |
| SDK not found | SDK Manager → install API 35 |
| Phone not detected | Enable USB Debugging in Developer Options |
| "App not installed" error | Enable "Install Unknown Apps" in Settings |
| Build takes too long | Normal on first build — subsequent builds are faster |
