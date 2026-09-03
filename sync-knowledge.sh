#!/usr/bin/env bash
# Copy shared/knowledge.json into web/ so `cd web && python3 -m http.server` works.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cp "$ROOT/shared/knowledge.json" "$ROOT/web/knowledge.json"
echo "Synced shared/knowledge.json -> web/knowledge.json"
