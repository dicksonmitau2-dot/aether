#!/usr/bin/env bash
# Build AetherMind.jar using javac/jar on PATH or JAVA_HOME.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

resolve_tool() {
  local name="$1"
  if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/$name" ]]; then
    echo "$JAVA_HOME/bin/$name"
    return
  fi
  command -v "$name"
}

JAVAC="$(resolve_tool javac || true)"
JAR="$(resolve_tool jar || true)"
JAVA="$(resolve_tool java || true)"

if [[ -z "$JAVAC" || -z "$JAR" ]]; then
  echo "AetherMind: javac/jar not found."
  echo "This machine has a JRE but needs a JDK."
  echo
  echo "  Debian/Kali/Ubuntu:  sudo apt install openjdk-21-jdk"
  echo "  Fedora:              sudo dnf install java-21-openjdk-devel"
  echo "  macOS (Homebrew):    brew install openjdk@21"
  echo
  echo "Then re-run ./build.sh"
  exit 1
fi

cp -f shared/knowledge.json web/knowledge.json

echo "[1/3] Compiling..."
"$JAVAC" --release 11 AetherMindApp.java AetherBrain.java

echo "[2/3] Packaging AetherMind.jar..."
shopt -s nullglob
"$JAR" cfm AetherMind.jar MANIFEST.MF \
  AetherMindApp.class AetherBrain.class \
  AetherMindApp\$*.class AetherBrain\$*.class \
  -C shared knowledge.json

echo "[3/3] Done: $ROOT/AetherMind.jar"
echo "Run with:  $JAVA -jar AetherMind.jar"
echo "       or:  ./run-desktop.sh"
