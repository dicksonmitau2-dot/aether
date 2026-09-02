# ⬡ AetherMind

> **No cloud. No API. Pure local intelligence.**

AetherMind is a fully offline AI chatbot that runs entirely on your device — no internet connection, no API keys, no external services. It features a keyword-based conversational engine with short-term memory, delivered across three platforms with a consistent dark-mode UI.

**Current version: v0.9**

---

## Platforms

| Platform | Language | Entry Point |
|----------|----------|-------------|
| 🖥️ Desktop | Java (Swing) | `AetherMindApp.java` |
| 📱 Android | Kotlin | `android/` |
| 🌐 Web | Vanilla JS / HTML / CSS | `web/` |

All three share the same AI logic and design language. No platform requires an internet connection at runtime.

---

## Features

- **Keyword-matching AI** — ~50 topic categories including greetings, programming jokes, philosophy, emotions, and more
- **Short-term memory** — remembers your last 8 messages; ask "what did I say earlier?" and it recalls
- **Status indicator** — live ONLINE / THINKING... / OFFLINE states
- **Typewriter animation** — Aether's replies render character by character on all platforms
- **Boot sequence** — animated startup: "Initializing neural pathways... Consciousness online."
- **Exit command** — type `exit` or `quit` for a graceful shutdown

---

## Getting Started

### 🖥️ Desktop (Java Swing)

**Requirements:** Java (JDK 11+). The included `build.bat` defaults to the JBR bundled with IntelliJ IDEA — update the paths inside if you use a different Java installation.

```bat
# Build
build.bat

# Run
java -jar AetherMind.jar
```

To uninstall, run `uninstall.bat`.

---

### 📱 Android

**Requirements:** Android Studio (latest stable), Android 8.0+ device or emulator (API 26+).

1. Open the `android/` folder in Android Studio
2. Let Gradle sync (first run takes 1–3 minutes)
3. Click **Run ▶** to install on a connected device or emulator

For a release APK, see [`android/HOW_TO_BUILD_APK.md`](android/HOW_TO_BUILD_APK.md).

**Minimum SDK:** API 26 (Android 8.0)  
**Target SDK:** API 35 (Android 15)

---

### 🌐 Web

No build step needed. Two options:

**Option A — Local network (recommended for mobile):**
```bat
# Starts a Python HTTP server on port 8080
web\StartServer.bat
```
Then open `http://<YOUR_LOCAL_IP>:8080` on any device on the same Wi-Fi. The web app is installable as a PWA from your browser's "Add to Home Screen" option.

**Option B — Direct file:**  
Open `web/index.html` directly in any modern browser.

---

## Project Structure

```
AetherMind/
├── AetherMindApp.java          # Desktop app (Java Swing) — full AI + UI in one file
├── MANIFEST.MF                 # JAR manifest for the desktop build
├── build.bat                   # Desktop compile + package script
├── uninstall.bat               # Desktop uninstaller
│
├── android/                    # Android app (Kotlin)
│   ├── app/src/main/
│   │   ├── java/com/aethermind/app/
│   │   │   ├── AetherBrain.kt  # AI engine (knowledge base + memory)
│   │   │   ├── MainActivity.kt # Chat UI + coroutine reply flow
│   │   │   ├── ChatAdapter.kt  # RecyclerView adapter (user/aether bubbles)
│   │   │   ├── ChatMessage.kt  # Data model
│   │   │   └── AetherApp.kt    # Application class
│   │   ├── res/layout/         # activity_main, item_msg_user, item_msg_aether
│   │   └── AndroidManifest.xml
│   ├── HOW_TO_BUILD_APK.md
│   └── build.gradle.kts
│
└── web/                        # Web app (Vanilla JS)
    ├── index.html
    ├── aether.js               # AI logic + typewriter animation + DOM
    ├── style.css               # Dark theme, chat bubbles, mobile layout
    └── StartServer.bat         # Python HTTP server launcher
```

---

## How the AI Works

AetherMind's brain is a deterministic keyword matcher — there is no LLM, no model weights, and no network calls.

1. Your input is lowercased and scanned against a map of ~50 keyword → response-list entries
2. The first matching keyword wins; a random response is picked from its list
3. If no keyword matches but your message contains "remember", "earlier", or "before", it echoes your second-to-last message from the rolling 8-item memory buffer
4. If nothing matches, a random fallback response is returned

This design makes the app instant, fully offline, and completely deterministic in its resource usage — it will never make a network request.

---

## Tech Stack

| Layer | Desktop | Android | Web |
|-------|---------|---------|-----|
| Language | Java | Kotlin 2.0.21 | Vanilla JS (ES6) |
| UI | Swing | RecyclerView + ViewBinding | HTML5 + CSS3 |
| Build | javac + jar | Gradle / AGP 8.5.2 | None |
| Min runtime | JDK 11+ | Android 8.0 (API 26) | Any modern browser |
| Dependencies | None | AndroidX, Coroutines, Material | None |

---

## License

This project is open source. See [LICENSE](LICENSE) if present, or contact the author for terms.
