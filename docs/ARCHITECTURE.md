# Architecture

AetherMind is a fully offline chatbot. There is no server, no model file, and no network call at runtime. All three platforms load the same map: `shared/knowledge.json`.

```
User message
    │
    ├─ trim / lowercase
    ├─ exact command: exit | quit  → goodbye, OFFLINE, input off
    ├─ push into rolling memory (max 8)
    └─ AetherBrain.respond / think
           │
           ├─ whole-word match, longest key first
           │     └─ random reply from that key
           ├─ else if remember | earlier | before (whole words)
           │     └─ echo the second-to-last memory entry
           └─ else random fallback line
```

How to run: [HOW_TO_RUN.md](HOW_TO_RUN.md). Live readiness: [STATUS.md](STATUS.md).

## Shared knowledge

| File | Role |
|------|------|
| `shared/knowledge.json` | **Source of truth** — topics + fallbacks |
| `web/knowledge.json` | Copy for the web server (`./sync-knowledge.sh`) |
| JAR `/knowledge.json` | Packaged by `./build.sh` |
| Android asset `knowledge.json` | Gradle `assets.srcDir` → `../../shared` |

Edit only `shared/knowledge.json`, then sync/rebuild. Matching is `\bkey\b` (word boundaries), keys sorted by length descending so `goodbye` wins over `good`. Spec: `python3 test_match.py`.

## Platforms

| Surface | UI | Brain loader |
|---------|----|----------------|
| Desktop | `AetherMindApp.java` (Swing) | `AetherBrain.java` reads packaged or `shared/knowledge.json` |
| Android | `MainActivity.kt` + RecyclerView | `AetherBrain.kt` reads `assets/knowledge.json` |
| Web | `web/index.html` + `aether.js` | `fetch('knowledge.json')` |

### Desktop

- Typewriter: Swing `Timer` at 16 ms/character
- History: `~/.aethermind/history.txt` and `memory.txt`
- Build: `./build.sh` (PATH / `JAVA_HOME`), not a hardcoded IntelliJ path

### Android

- Typewriter: coroutine updates the last bubble, 16 ms/character
- History: SharedPreferences `aether`
- Exit: OFFLINE + input disabled (does not kill the activity)

### Web

- Typewriter: 18 ms/character
- History: `localStorage` key `aethermind.session.v1`
- Serve from repo root (`./run-web.sh`) so `/web/` and `/shared/knowledge.json` both resolve; the run script also copies the JSON into `web/`

## What is intentionally not here

- No HTTP APIs, API keys, or analytics
- No LLM / model weights
- No PWA service worker yet (see ROADMAP)
- No centralized version constant yet
