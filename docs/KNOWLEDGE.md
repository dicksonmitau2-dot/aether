# Knowledge map inventory (v0.9)

Single table of every keyword currently hardcoded in the three brains. Use this when syncing topics (ROADMAP v1.0).

Matching is **first substring hit**, not whole-word. Order in this table is Android insertion order (the largest map). Keys marked "early" are especially dangerous because they steal later, more specific keys.

| Key | Desktop | Web | Android | Notes |
|-----|:-------:|:---:|:-------:|-------|
| hello | yes | yes | yes | |
| hi | yes | yes | yes | Fires inside `this`, `which`, … |
| hey | — | yes | yes | |
| how are you | yes | yes | yes | |
| who are you | yes | yes | yes | |
| what are you | — | yes | yes | |
| name | yes | yes | yes | |
| help | yes | yes | yes | |
| weather | yes | yes | yes | |
| joke | yes | yes | yes | |
| code | yes | yes | yes | |
| java | yes | yes | yes | |
| kotlin | — | — | yes | Android-only is reasonable |
| python | yes | yes | yes | |
| android | — | — | yes | Android-only is reasonable |
| ai | yes | yes | yes | Fires inside `said`, `wait`, `fair` |
| hack | yes | yes | yes | |
| love | yes | yes | yes | |
| life | yes | yes | yes | |
| bye | yes | yes | yes | Web: also a hard exit command |
| goodbye | — | yes | yes | Web: also a hard exit command |
| exit | yes | yes | yes | Hard exit on all platforms (handled before brain on Desktop/Android/Web) |
| quit | yes | yes | yes | Hard exit on all platforms |
| stupid | yes | yes | yes | |
| smart | yes | yes | yes | |
| memory | yes | yes | yes | Distinct from remember/earlier/before recall |
| time | yes | yes | yes | |
| thanks | yes | yes | yes | |
| thank you | — | yes | yes | |
| what can you do | yes | yes | yes | |
| good | yes | yes | yes | Steals `goodbye` if scanned first |
| bad | yes | yes | yes | |
| bored | yes | yes | yes | |
| music | yes | yes | yes | |
| game | yes | yes | yes | |
| error | yes | yes | yes | |
| bug | yes | yes | yes | |
| phone | — | yes | yes | |
| mobile | — | yes | — | Web-only |
| cool | — | yes | yes | |
| wow | — | yes | yes | |
| yes | — | yes | yes | |
| no | — | yes | yes | Fires inside `know`, `another` |
| ok | — | yes | yes | |
| lol | — | yes | yes | |
| haha | — | yes | yes | |
| happy | — | — | yes | |
| sad | — | — | yes | |
| hungry | — | — | yes | |
| tired | — | — | yes | |
| 42 | — | — | yes | |

**Counts:** Desktop 31 · Web 44 · Android 50 · union 51

Recall phrases (not keys): `remember`, `earlier`, `before` — only consulted when **no** keyword matched.
