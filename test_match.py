#!/usr/bin/env python3
"""Spec tests for whole-word matching against shared/knowledge.json."""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent
data = json.loads((ROOT / "shared" / "knowledge.json").read_text(encoding="utf-8"))
topics = sorted(data["topics"], key=lambda t: len(t["key"]), reverse=True)


def match_key(text: str) -> str | None:
    lower = text.strip().lower()
    for t in topics:
        key = t["key"]
        if re.search(r"\b" + re.escape(key) + r"\b", lower):
            return key
    return None


CASES = [
    ("hello", "hello"),
    ("hello there", "hello"),
    ("this is fine", None),          # must not hit "hi"
    ("I said nothing", None),        # must not hit "ai" inside "said"
    ("goodbye", "goodbye"),          # not "good"
    ("I know", None),                # must not hit "no"
    ("tell me a joke", "joke"),
    ("how are you", "how are you"),
    ("what did I say earlier", None),  # recall path, no keyword
    ("42", "42"),
    ("hi", "hi"),
]


def main() -> int:
    failed = 0
    for text, expected in CASES:
        got = match_key(text)
        ok = got == expected
        mark = "ok" if ok else "FAIL"
        if not ok:
            failed += 1
        print(f"  {mark:4}  {text!r:28} expected={expected!r:16} got={got!r}")
    print()
    if failed:
        print(f"{failed} failed")
        return 1
    print(f"{len(CASES)} passed  ({len(topics)} topics in shared/knowledge.json)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
