# Knowledge map

All platforms load **`shared/knowledge.json`**. Do not add topics in Java, Kotlin, or JS.

After editing the JSON:

```bash
./sync-knowledge.sh     # copies into web/knowledge.json
python3 test_match.py   # whole-word regression checks
```

Rebuild the desktop JAR (`./build.sh`) and reinstall the Android app so they pick up the file.

## Shape

```json
{
  "version": "0.9",
  "memorySize": 8,
  "fallbacks": ["…"],
  "topics": [
    {"key": "hello", "replies": ["Greetings, human.", "…"]}
  ]
}
```

Keys are matched as whole words/phrases (`\bkey\b`), longest first. `exit` and `quit` still exist as topics but the UI intercepts the exact commands before `respond()`.

## Counts (v0.9 shared file)

51 topic keys, 7 fallbacks. Inventory is the `topics` array in the JSON — there is no longer a per-platform table.

## Adding a key

1. Append a `{ "key", "replies" }` object in `shared/knowledge.json`.
2. Prefer phrases over 1–2 letter keys (`hi`, `ai`, `no`, `ok` are already in the map; whole-word matching limits the damage).
3. Run `python3 test_match.py` and add a case if the new key could collide.
4. `./sync-knowledge.sh` and rebuild desktop/Android.

Voice: short, dry, no cloud/API claims.
