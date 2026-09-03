# AetherMind

> **No cloud. No API. Pure local intelligence.**

AetherMind is a fully offline chatbot that runs entirely on your device — no internet connection, no API keys, no external services. It uses a keyword-based conversational engine with short-term memory, delivered across three platforms with a consistent dark-mode UI.

**Current version: v0.9**

---

## Platforms

| Platform | Language | Entry point |
|----------|----------|-------------|
| Desktop | Java (Swing) | `AetherMindApp.java` |
| Android | Kotlin | `android/` |
| Web | Vanilla JS / HTML / CSS | `web/` |

All three follow the same matching algorithm and design language. Each ships its own copy of the knowledge map — they are close, not identical (see [docs/KNOWLEDGE.md](docs/KNOWLEDGE.md)). No platform requires an internet connection at runtime.

---

## Features

- **Keyword-matching engine** — topic maps of 31 (Desktop), 44 (Web), and 50 (Android) keys covering greetings, programming jokes, philosophy, and more
- **Short-term memory** — remembers your last 8 messages; ask "what did I say earlier?" and it recalls (only if no keyword matched)
- **Status indicator** — ONLINE / THINKING... / OFFLINE
- **Typewriter animation** — Desktop and Web render replies character by character; Android inserts the full line after a short delay
- **Boot sequence** — animated startup copy on every surface
- **Exit command** — type `exit` or `quit` (Web also treats `bye` / `goodbye` as a hard stop)

---

## Documentation

| Doc | What it covers |
|-----|----------------|
| [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) | How to run Desktop, Web, and Android from this repo (including Linux) |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Brain algorithm, per-platform layout, matching caveats |
| [docs/KNOWLEDGE.md](docs/KNOWLEDGE.md) | Keyword inventory and which surfaces have each key |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to add topics without letting the three brains drift further |
| [ROADMAP.md](ROADMAP.md) | Planned work from v1.0 onward |
| [CHANGELOG.md](CHANGELOG.md) | Shipped versions |
| [android/HOW_TO_BUILD_APK.md](android/HOW_TO_BUILD_APK.md) | Debug and signed APK from Android Studio |

---

## Getting started

### Desktop (Java Swing)

**Requirements:** JDK 11+ with `javac` / `java` / `jar` on your `PATH`.

```bash
javac AetherMindApp.java
jar cfm AetherMind.jar MANIFEST.MF AetherMindApp.class AetherMindApp\$*.class
java -jar AetherMind.jar
```

`build.bat` is a Windows helper aimed at a hardcoded IntelliJ JBR path under `C:\AetherMind`. Prefer the commands above from this checkout. Full notes: [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md).

---

### Android

**Requirements:** Android Studio, Android 8.0+ device or emulator (API 26+).

1. Open the `android/` folder in Android Studio (not the repo root)
2. Let Gradle sync (first run takes 1–3 minutes)
3. Click **Run** to install on a connected device or emulator

This tree has no Gradle wrapper; Studio generates one on first sync. For a release APK, see [`android/HOW_TO_BUILD_APK.md`](android/HOW_TO_BUILD_APK.md).

**Minimum SDK:** API 26 (Android 8.0)
**Target SDK:** API 35 (Android 15)

---

### Web

No build step. From the repo root:

```bash
cd web
python3 -m http.server 8080
```

Open `http://127.0.0.1:8080`. On another device on the same Wi-Fi, use this machine's LAN IP. You can also open `web/index.html` directly in a browser.

---

## How the engine works

AetherMind's brain is a deterministic keyword matcher — there is no LLM, no model weights, and no network calls.

1. Your input is lowercased and scanned against a map of keyword → reply-list entries
2. The **first** matching keyword wins (substring, not whole word); a random reply is picked from its list
3. If no keyword matches but the message contains `remember`, `earlier`, or `before`, it echoes your second-to-last message from the rolling 8-item buffer
4. If nothing matches, a random fallback line is returned

This design makes the app instant, fully offline, and constant in resource use — it will never make a network request. Details and false-match examples: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## Project structure

```
aether/
├── AetherMindApp.java          # Desktop (Swing) — UI + brain in one file
├── MANIFEST.MF                 # JAR Main-Class
├── build.bat                   # Windows desktop build (hardcoded paths)
├── uninstall.bat               # Windows uninstaller (deletes C:\AetherMind)
├── CONTRIBUTING.md
├── CHANGELOG.md
├── docs/
│   ├── ARCHITECTURE.md
│   ├── DEVELOPMENT.md
│   └── KNOWLEDGE.md
├── android/                    # Kotlin app — open this folder in Android Studio
│   ├── app/src/main/java/com/aethermind/app/
│   │   ├── AetherBrain.kt      # Knowledge + memory
│   │   ├── MainActivity.kt     # Chat UI + coroutine reply flow
│   │   ├── ChatAdapter.kt
│   │   ├── ChatMessage.kt
│   │   └── AetherApp.kt
│   └── HOW_TO_BUILD_APK.md
└── web/
    ├── index.html
    ├── aether.js               # Brain + typewriter + DOM
    ├── style.css
    └── StartServer.bat         # Windows Python server helper
```

---

## Tech stack

| Layer | Desktop | Android | Web |
|-------|---------|---------|-----|
| Language | Java | Kotlin 2.0.21 | Vanilla JS (ES6) |
| UI | Swing | RecyclerView + ViewBinding | HTML5 + CSS3 |
| Build | javac + jar | Gradle / AGP 8.5.2 | None |
| Min runtime | JDK 11+ | Android 8.0 (API 26) | Any modern browser |
| Dependencies | None | AndroidX, Coroutines, Material | None |

---

## License

No `LICENSE` file is in the repository yet. Contact the author for terms before redistributing.
