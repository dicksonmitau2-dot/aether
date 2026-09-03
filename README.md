# AetherMind

> **No cloud. No API. Pure local intelligence.**

AetherMind is a fully offline chatbot that runs entirely on your device — no internet connection, no API keys, no external services. It uses a keyword-based engine with short-term memory, on Desktop, Android, and Web, with one shared knowledge file.

**Current version: v0.9**

**Start here:** [docs/HOW_TO_RUN.md](docs/HOW_TO_RUN.md) · **Is it working?** [docs/STATUS.md](docs/STATUS.md)

```bash
./run-web.sh
# open http://127.0.0.1:8080/web/
```

---

## Platforms

| Platform | Language | Entry point |
|----------|----------|-------------|
| Desktop | Java (Swing) | `AetherMindApp.java` + `AetherBrain.java` |
| Android | Kotlin | `android/` |
| Web | Vanilla JS / HTML / CSS | `web/` |

All three load [`shared/knowledge.json`](shared/knowledge.json). No platform needs the internet at runtime.

---

## Features

- **Keyword engine** — 51 topics, whole-word match, longest key first
- **Short-term memory** — last 8 user lines; “what did I say earlier?” recalls them
- **Saved chat** — restored on relaunch (file / SharedPreferences / localStorage)
- **Status indicator** — ONLINE / THINKING... / OFFLINE
- **Typewriter animation** — all three surfaces
- **Exit** — type `exit` or `quit`

---

## Documentation

| Doc | What it covers |
|-----|----------------|
| [docs/HOW_TO_RUN.md](docs/HOW_TO_RUN.md) | Run Web, Desktop, and Android |
| [docs/STATUS.md](docs/STATUS.md) | What is ready vs blocked right now |
| [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) | Build details, version bump, layout |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Engine and per-platform wiring |
| [docs/KNOWLEDGE.md](docs/KNOWLEDGE.md) | How to edit the shared topic map |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Rules for changing the brain and UI |
| [ROADMAP.md](ROADMAP.md) | Planned work from v1.0 onward |
| [CHANGELOG.md](CHANGELOG.md) | Shipped versions |
| [android/HOW_TO_BUILD_APK.md](android/HOW_TO_BUILD_APK.md) | Debug and signed APK |

---

## How the engine works

1. Input is lowercased
2. Scan `shared/knowledge.json` for a **whole-word** key, **longest first**
3. If none match, `remember` / `earlier` / `before` echoes the previous user line
4. Otherwise a random fallback line

No LLM, no weights, no network. Details: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## Project structure

```
aether/
├── shared/knowledge.json       # Source of truth for every platform
├── AetherMindApp.java          # Desktop UI
├── AetherBrain.java            # Desktop brain
├── run-web.sh / run-desktop.sh / build.sh
├── test_match.py
├── android/
└── web/
```

---

## Tech stack

| Layer | Desktop | Android | Web |
|-------|---------|---------|-----|
| Language | Java 11+ | Kotlin 2.0.21 | Vanilla JS (ES6) |
| UI | Swing | RecyclerView + ViewBinding | HTML5 + CSS3 |
| Build | `./build.sh` | Gradle / AGP 8.5.2 | None |
| Min runtime | JDK 11+ to compile | Android 8.0 (API 26) | Any modern browser |
| Dependencies | None | AndroidX, Coroutines, Material | None |

---

## License

No `LICENSE` file is in the repository yet. Contact the author for terms before redistributing.
