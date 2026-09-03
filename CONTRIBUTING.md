# Contributing

AetherMind is a small, offline-first project. Keep changes local and deterministic. All three platforms must keep the same brain behavior.

## Before you start

1. [docs/HOW_TO_RUN.md](docs/HOW_TO_RUN.md) — run what you changed
2. [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — engine + file layout
3. [docs/STATUS.md](docs/STATUS.md) — what is currently blocked
4. [ROADMAP.md](ROADMAP.md) — pick an existing milestone when it fits

Match the style of the file you are in: section banners (`── Name ──`), existing color tokens, no new runtime dependencies.

## Adding a topic

Edit **only** [`shared/knowledge.json`](shared/knowledge.json). Then:

```bash
./sync-knowledge.sh
python3 test_match.py
```

Rebuild `./build.sh` and the Android app. Do not add keys in `AetherBrain.java`, `AetherBrain.kt`, or `aether.js`.

## Session rules (keep in sync)

- Rolling memory of 8 user messages
- Status THINKING while a reply is in flight, then ONLINE
- Exact `exit` / `quit` → goodbye, OFFLINE, input disabled
- `bye` / `goodbye` are normal keyword replies, not hard exit
- Chat history restored on relaunch

## What not to add

- Network calls, analytics, crash reporters, or API keys
- A cloud LLM path that breaks the offline promise
- Secrets, keystores, or `local.properties`

## Tests

`python3 test_match.py` covers whole-word collisions (`this` must not match `hi`). Add a case when you introduce a short or overlapping key.

## License

No `LICENSE` file is in the tree yet. Do not assume MIT or public-domain until the author adds terms.
