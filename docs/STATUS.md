# Status

Last updated: 3 September 2026  
Version in UI: **v0.9**

This is the live readiness doc. How to start each surface: [HOW_TO_RUN.md](HOW_TO_RUN.md).

---

## Summary

| Surface | Ready to use? | Blocker on a typical Kali box |
|---------|---------------|-------------------------------|
| Web | **Yes** | None (`python3` + browser) |
| Desktop | After JDK install | `java` is present, **`javac` is not** (JRE only) |
| Android | After Android Studio | No Android SDK / Studio in this environment |

The keyword engine is now **one file** (`shared/knowledge.json`), **whole-word** match, **longest key first**, with **saved chat** on all three surfaces.

---

## Web — ready

- Start: `./run-web.sh` → http://127.0.0.1:8080/web/
- Knowledge: `shared/knowledge.json` copied to `web/knowledge.json` by the run script
- Session: `exit` / `quit` only (not `bye`)
- History: `localStorage` key `aethermind.session.v1`
- Verified here: Python 3.13 present; matching tests via `python3 test_match.py`

---

## Desktop — needs JDK to compile

- Start: `./build.sh` then `./run-desktop.sh`
- Brain: `AetherBrain.java` loads `/knowledge.json` from the JAR, or `shared/knowledge.json` from the working directory
- History: `~/.aethermind/history.txt` and `memory.txt`
- Scripts: `build.sh` / `run-desktop.sh` use `PATH` or `JAVA_HOME` (no `C:\AetherMind`)
- **This checkout:** OpenJDK 21 JRE is installed; install `openjdk-21-jdk` to compile

---

## Android — needs Android Studio

- Start: open `android/` in Android Studio, Run
- Brain: `AetherBrain.kt` loads asset `knowledge.json` from `shared/` (Gradle `assets.srcDir`)
- Typewriter: replies stream character-by-character (same idea as Desktop/Web)
- Exit: goodbye + OFFLINE; does **not** kill the task
- History: SharedPreferences `aether`
- **This checkout:** no Android SDK; cannot produce an APK here

---

## Engine (all platforms)

| Rule | Behavior |
|------|----------|
| Source of topics | `shared/knowledge.json` (51 keys) |
| Match | Whole word / whole phrase (`\bkey\b`), not substring |
| Order | Longer keys win (`goodbye` before `good`) |
| Memory | Last 8 user lines; `remember` / `earlier` / `before` as whole words |
| Hard exit | Exact `exit` or `quit` |
| Offline promise | No HTTP, no API keys, no model download |

Known remaining gaps (not required to chat):

- No PWA service worker
- Version string still duplicated in UI files
- No Gradle wrapper in git
- No CI
- No `LICENSE`

---

## Checks to re-run after changes

```bash
python3 test_match.py
./sync-knowledge.sh
# Web: ./run-web.sh
# Desktop (if JDK installed): ./build.sh && ./run-desktop.sh
```

If you add or rename a topic, edit **only** `shared/knowledge.json`, then run `./sync-knowledge.sh` and rebuild the JAR / Android app.
