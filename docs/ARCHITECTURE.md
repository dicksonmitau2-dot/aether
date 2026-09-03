# Architecture

AetherMind is a fully offline chatbot. There is no server, no model file, and no network call at runtime. Each platform embeds its own copy of the same idea: lowercase the user message, scan a keyword map, pick a random reply, and keep a short rolling memory.

```
User message
    │
    ├─ trim / lowercase
    ├─ special commands (exit / quit; web also treats bye / goodbye)
    ├─ push into rolling memory (max 8)
    └─ AetherBrain.think / respond
           │
           ├─ first keyword whose key is a substring of the input
           │     └─ random choice from that key's reply list
           ├─ else if input contains remember / earlier / before
           │     └─ echo the second-to-last memory entry
           └─ else random fallback line
```

## Platforms

| Surface | Code | UI | Brain |
|---------|------|----|-------|
| Desktop | `AetherMindApp.java` | Java Swing (`JTextPane`) | `think()` + `knowledge` HashMap in the same file |
| Android | `android/app/src/main/java/com/aethermind/app/` | RecyclerView + ViewBinding | `AetherBrain` object |
| Web | `web/` | HTML + CSS | `think()` in `aether.js` |

The three brains are **not generated from a shared source**. They implement the same algorithm by hand and have already drifted (topic counts at v0.9: Desktop 31, Web 44, Android 50). Treat `AetherBrain.kt` as the richest reference when adding topics, and copy into the other two.

## Desktop (`AetherMindApp.java`)

Single class that owns colors, Swing layout, typewriter animation, memory, and the knowledge map.

- **UI thread:** all Swing work stays on the Event Dispatch Thread. The typewriter uses `javax.swing.Timer` (16 ms/character).
- **Matching order:** `knowledge` is a `HashMap`, so iteration order is not insertion order. The "first match" can change between runs. Android (`mapOf`) and Web (object literals) preserve insertion order.
- **Exit:** `exit` / `quit` skip `think()`, print a goodbye line, set status to OFFLINE, and leave input disabled.

## Android (`com.aethermind.app`)

| Class | Role |
|-------|------|
| `AetherApp` | `Application` subclass; currently a placeholder for future init |
| `MainActivity` | Chat screen, boot sequence, send/reply coroutine flow, status pill |
| `AetherBrain` | Knowledge map, 8-slot `ArrayDeque` memory, `respond()` |
| `ChatAdapter` | Two view types: user bubble vs Aether bubble |
| `ChatMessage` | `text` + `Sender` enum |

Reply delay is a coroutine `delay(350..650 ms)` then a full-string insert. There is **no** typewriter animation on Android (unlike Desktop and Web).

`exit` / `quit` call `finishAndRemoveTask()` after the goodbye line — the activity is removed from recents, not merely disabled.

Chat is held in an in-memory `mutableListOf` on the activity. Rotation or process death drops the conversation. `AetherBrain` is a process-wide `object`, so memory survives activity recreation but not process death.

## Web (`web/`)

| File | Role |
|------|------|
| `index.html` | Shell: header, `#chatWindow`, input, PWA-ish meta tags |
| `aether.js` | Knowledge, memory, DOM bubbles, typewriter (18 ms/character) |
| `style.css` | Dark theme, `100dvh` layout, mobile keyboard / safe-area padding |
| `StartServer.bat` | Windows helper for `python -m http.server 8080` |

There is no `manifest.json` and no service worker, so "Add to Home Screen" works as a shortcut, not as a cached offline PWA.

Web treats `bye` and `goodbye` as hard exit (input stays disabled). Desktop and Android only do that for `exit` / `quit`; `bye` is a normal keyword reply.

## Matching caveats

Keys are raw substrings, not tokens. Short keys fire inside unrelated words:

| Input | Likely key | Why |
|-------|------------|-----|
| `this is fine` | `hi` | `"this"` contains `"hi"` |
| `I said hello` | `ai` | `"said"` contains `"ai"` |
| `goodbye` | `good` | `"goodbye"` contains `"good"` (if `good` is scanned before `goodbye`) |
| `I know` | `no` | `"know"` contains `"no"` |

Longer, more specific keys should be listed **before** short ones in Android and Web. Desktop cannot rely on list order until the map is a `LinkedHashMap`.

## What is intentionally not here

- No HTTP client, no API keys, no analytics.
- No persistent transcript (see ROADMAP v1.0).
- No shared knowledge JSON (see ROADMAP v1.0 "Sync knowledge bases").
- No unit tests (see ROADMAP Ongoing).
