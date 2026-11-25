#!/usr/bin/env bash
set -euo pipefail

# Keep the Gradle pipeline inside this Bash process so pipefail is effective.
gradle connectedDebugAndroidTest --no-daemon --stacktrace --console=plain 2>&1 | tee h1-emulator.log
