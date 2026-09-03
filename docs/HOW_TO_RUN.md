# How to run AetherMind

Step-by-step for using the app, not for changing its code. For architecture and contributing, see the other files under `docs/`.

**Current version:** v0.9  
**Best path on this repo:** the **web** client. It needs only Python 3 and a browser.

---

## 1. Web (works without a compiler)

From the repository root:

```bash
chmod +x run-web.sh
./run-web.sh
```

Then open **http://127.0.0.1:8080/web/**

What you should see: dark chat UI, “ONLINE”, boot lines, clickable hints (`hello`, `joke`, `java`, …). Type a message and press Enter.

| Try | Expected |
|-----|----------|
| `hello` | A greeting from Aether |
| `this is fine` | A fallback or other topic — **not** a “hi” reply |
| `joke` | A programming joke |
| `what did I say earlier?` | Recalls your previous line (after two messages) |
| `exit` or `quit` | Goodbye, status OFFLINE, input disabled |
| `bye` | A bye **reply**; chat stays open |

Refresh the page: the conversation comes back (browser `localStorage`).

### Manual equivalent

```bash
cp shared/knowledge.json web/knowledge.json
python3 -m http.server 8080
```

Then open `http://127.0.0.1:8080/web/`  
or, if you started the server **inside** `web/`, open `http://127.0.0.1:8080/`.

Opening `web/index.html` as a `file://` URL often **cannot** load `knowledge.json`. Use the HTTP server.

### Stop

In the terminal: `Ctrl+C`.

---

## 2. Desktop (Java window)

**Requires a JDK** (not only a JRE). You need `javac` and `jar` on `PATH`, or `JAVA_HOME` pointing at a JDK 11+.

### Check

```bash
javac -version
java -version
```

If `java` works but `javac` is missing:

```bash
# Debian / Kali / Ubuntu
sudo apt install openjdk-21-jdk
```

### Build and run

```bash
chmod +x build.sh run-desktop.sh
./build.sh
./run-desktop.sh
```

Or: `java -jar AetherMind.jar` from the repo root.

Windows: `build.bat`, then `java -jar AetherMind.jar`.

Chat history is stored in `~/.aethermind/` (`history.txt` and `memory.txt`). Delete that folder to start clean.

---

## 3. Android (phone / emulator)

**Requires:** Android Studio, SDK API 35, a device or emulator on API 26+.

1. Open the **`android/`** folder in Android Studio (not the repo root).
2. Wait for Gradle sync. There is no `gradlew` in git; Studio creates the wrapper.
3. Click **Run**.

The APK reads `shared/knowledge.json` as an asset (configured in `app/build.gradle.kts`). Chat history is stored in app SharedPreferences.

Signed APK: [android/HOW_TO_BUILD_APK.md](../android/HOW_TO_BUILD_APK.md).

---

## Commands cheat sheet

| Want | Command |
|------|---------|
| Chat in the browser | `./run-web.sh` → http://127.0.0.1:8080/web/ |
| Chat in a desktop window | `./build.sh && ./run-desktop.sh` |
| Check keyword matching | `python3 test_match.py` |
| After editing topics | `./sync-knowledge.sh` then refresh / rebuild |
| Phone app | Open `android/` in Android Studio → Run |

---

## If it does not start

| Symptom | Fix |
|---------|-----|
| Browser is blank / “Could not load knowledge.json” | Use `./run-web.sh`, not `file://` |
| `./build.sh` says javac not found | Install a JDK (`openjdk-21-jdk`) |
| Desktop window never appears | Display/`DISPLAY` not set (SSH without X11); use the web client |
| Android Gradle sync fails | File → Invalidate Caches; install SDK API 35 |
| Port 8080 in use | `PORT=8081 ./run-web.sh` |

Live readiness of each surface: [STATUS.md](STATUS.md).
