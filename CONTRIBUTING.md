# Contributing

AetherMind is a small, offline-first project. Keep changes local, deterministic, and in sync across Desktop, Android, and Web unless a platform genuinely cannot support them.

## Before you start

1. Read [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) so you know why there are three brains.
2. Check [ROADMAP.md](ROADMAP.md) so work lands on an existing milestone when it fits.
3. Match the style of the file you are in: section banners (`── Name ──`), existing color tokens, no new dependencies unless the roadmap item requires them.

## Adding a topic to the brain

The knowledge map is copied in three places. A topic that exists on only one platform is a bug.

| Platform | File | Shape |
|----------|------|-------|
| Android | `android/.../AetherBrain.kt` | `"key" to listOf("reply", ...)` |
| Web | `web/aether.js` | `"key": ["reply", ...]` |
| Desktop | `AetherMindApp.java` | `add("key", "reply", ...);` |

Rules:

- Add the **same key** and the **same replies** to all three, unless the line is platform-specific (`kotlin`, `android`, `mobile`).
- Put **longer, more specific keys before short ones** (e.g. `goodbye` before `good`, `thank you` before `you` if you ever add `you`). Android and Web honor insertion order; Desktop currently does not (`HashMap`) — still keep the Java `add(...)` calls in the same order so a future `LinkedHashMap` switch is trivial.
- Avoid keys shorter than three letters when you can. `hi`, `ai`, `no`, `ok` already false-match inside ordinary English (`this`, `said`, `know`).
- Keep replies in Aether's voice: short, slightly dry, no cloud/API claims.

## UI changes

Visual language is cyan-on-dark (`#00e6ff` on `#0d0d14`), monospace, status pill ONLINE / THINKING / OFFLINE. If you change a color or the boot copy, change it on every platform that shows it.

Behavior that should stay aligned:

- Rolling memory of 8 user messages
- Status → THINKING while a reply is in flight, then ONLINE
- `exit` and `quit` end the session (Web also ends on `bye` / `goodbye`; changing that should be deliberate and documented)

## What not to add

- Network calls, analytics, crash reporters, or API keys
- A cloud LLM "upgrade" path that breaks the offline promise
- Copy-pasted secrets, keystores, or `local.properties`

## Tests

There is no test suite yet (see ROADMAP → Ongoing). If you add one, start with the brain: given an input, assert which key fired. That is the highest-value coverage because matching is substring-based and easy to regress.

## License

No `LICENSE` file is in the tree yet. Do not assume MIT or public-domain until the author adds terms.
