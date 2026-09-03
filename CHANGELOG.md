# Changelog

All notable changes to AetherMind are listed here. Version numbers follow the UI badge (`v0.9`, …).

## [Unreleased]

Usability pass on top of v0.9.

- Single knowledge map: `shared/knowledge.json` on Desktop, Android, and Web
- Whole-word matching, longest key first (`this` no longer matches `hi`)
- Chat history restored on relaunch on all three surfaces
- Typewriter replies on Android; `exit`/`quit` only for hard stop
- Portable `./build.sh`, `./run-web.sh`, `./run-desktop.sh`
- Docs: [docs/HOW_TO_RUN.md](docs/HOW_TO_RUN.md), [docs/STATUS.md](docs/STATUS.md)

## [0.9] — 2026-09

Initial snapshot across three local surfaces.

- Desktop Java Swing app (`AetherMindApp.java`) with typewriter replies and an 8-message memory buffer
- Android Kotlin app (`com.aethermind.app`) targeting API 35, min API 26
- Static web client (`web/`) with clickable boot hints and mobile viewport handling
- Keyword brain (no model, no network) with per-platform knowledge maps
- Dark cyan-on-black chat UI and ONLINE / THINKING / OFFLINE status
