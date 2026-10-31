#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
OUTPUT="$(mktemp -d)"
trap 'rm -rf "$OUTPUT"' EXIT HUP INT TERM
javac -encoding UTF-8 -d "$OUTPUT" src/setquest/*.java tests/setquest/SmokeTest.java
java -Djava.awt.headless=true -cp "$OUTPUT" setquest.SmokeTest
