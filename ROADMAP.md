# AetherMind — Roadmap

This document tracks the planned evolution of AetherMind across all three platforms (Desktop, Android, Web). Items are grouped by milestone. Priorities and timelines may shift as the project grows.

---

## v1.0 — Stable Foundation

The goal of v1.0 is to close the gaps between platforms, harden the build process, and ship a clean, consistent 1.0 across all three targets.

- [ ] **Sync knowledge bases** — Desktop, Android, and Web have diverged slightly. Establish a single source-of-truth knowledge map shared (or mirrored) across all platforms
- [ ] **Fix `build.bat` portability** — Replace the hardcoded IntelliJ JBR path with `JAVA_HOME` or a PATH lookup so the Desktop build works on any machine
- [ ] **Web PWA completion** — Add `manifest.json` and a Service Worker so the web app is a proper installable PWA with offline caching
- [ ] **Persistent chat history** — Optionally save and restore the conversation on relaunch (local file on Desktop, SharedPreferences/Room on Android, localStorage on Web)
- [ ] **Cross-platform version badge** — Centralize the version string so bumping `0.9 → 1.0` in one place propagates everywhere

---

## v1.1 — Smarter Responses

Improve the quality and variety of AetherMind's replies without abandoning the offline-first constraint.

- [ ] **Expanded knowledge base** — Add more topic categories: math, history, space, food, sports, movies, technology trends
- [ ] **Multi-keyword matching** — Score multiple matched keywords instead of stopping at the first hit, allowing more contextually relevant responses
- [ ] **Longer memory window** — Increase short-term memory from 8 → 20+ entries with a "review conversation" command
- [ ] **Contextual follow-ups** — Detect when the user is continuing a topic from the previous message and respond accordingly
- [ ] **"Teach me" mode** — Let users add their own keyword → response pairs at runtime (stored locally, not hardcoded)

---

## v1.2 — UI & UX Polish

Quality-of-life improvements across all platforms.

- [ ] **Message timestamps** — Show the time each message was sent
- [ ] **Copy message** — Long-press (Android) or right-click (Desktop/Web) to copy any bubble's text
- [ ] **Clear conversation** — Button or command to wipe the current chat and memory buffer
- [ ] **Hint keyword clicks** — Web already supports clickable hints; bring this to Android and Desktop
- [ ] **Accessibility** — Screen reader labels on Android (`contentDescription`), keyboard navigation on Desktop, ARIA roles on Web
- [ ] **Haptic feedback** — Subtle vibration on Android when Aether replies

---

## v1.3 — Settings & Customization

Give users control over the experience.

- [ ] **Settings screen** (Android + Desktop) — Configurable options: reply speed, memory size, font size
- [ ] **Theme variants** — Offer a light theme and at least one alternative accent color alongside the default cyan-on-dark
- [ ] **Response speed slider** — Control the typewriter animation speed (or disable it entirely)
- [ ] **Language groundwork** — Externalize all UI strings for future i18n support; Android already uses `strings.xml`, Desktop and Web need the same

---

## v2.0 — Expanded Intelligence

Larger architectural changes that move AetherMind beyond pure keyword matching while keeping the offline-first promise.

- [ ] **On-device NLP** — Explore lightweight, offline-capable models (e.g., ONNX Runtime, TensorFlow Lite on Android) for intent classification on top of the keyword engine
- [ ] **Semantic fallback** — When no keyword matches, use a local embedding-based similarity check against the knowledge base before reaching the generic fallback
- [ ] **Conversation context window** — Give Aether awareness of the last N full exchanges, not just the last N user inputs
- [ ] **Plugin / skill system** — Allow optional modules (e.g., a calculator skill, a unit converter skill) that Aether can invoke when it detects the right intent
- [ ] **Voice input** (Android + Web) — Use the platform's speech-to-text API to allow hands-free chatting; all processing stays local

---

## Ongoing / Evergreen

Tasks that apply continuously across all milestones.

- [ ] **Unit tests** — Add test coverage for `AetherBrain` (Android/Kotlin), the `think()` function (Desktop/Java), and `aether.js` (Web/Jest or similar)
- [ ] **CI pipeline** — GitHub Actions workflow to build the Android APK and run tests on every push
- [ ] **Changelog** — Maintain a `CHANGELOG.md` alongside this roadmap
- [ ] **Linux / macOS Desktop support** — The Swing app is cross-platform; document and test builds outside Windows
- [ ] **Issue tracker hygiene** — Label issues with `platform:android`, `platform:web`, `platform:desktop`, `ai-engine`, `ui`, `bug`

---

## Ideas Backlog

Not yet scheduled — worth revisiting as the project matures.

- Desktop system tray mode (minimize to tray, keep running in background)
- Android widget — a small home screen widget for quick one-line queries
- Export conversation as `.txt` or `.md`
- Multi-language knowledge bases (Spanish, French, etc.)
- Web: shareable conversation link via URL hash (no server needed — encode history in the URL)
- Desktop: global hotkey to bring the window to front from anywhere

---

*Last updated: September 2026 · AetherMind v0.9*
