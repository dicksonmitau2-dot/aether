#!/usr/bin/env bash
# Serve the web client and shared knowledge map from the repo root.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"
cp -f shared/knowledge.json web/knowledge.json

PORT="${PORT:-8080}"
echo
echo "  AetherMind web"
echo "  Open:  http://127.0.0.1:${PORT}/web/"
echo "  Stop:  Ctrl+C"
echo
exec python3 -m http.server "$PORT"
