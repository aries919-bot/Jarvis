#!/usr/bin/env bash
set -Eeuo pipefail

mkdir -p h1-emulator-diagnostics
exec > >(tee -a h1-emulator.log) 2>&1

collect_diagnostics() {
  local label="$1"
  local dir="h1-emulator-diagnostics/$label"
  mkdir -p "$dir"
  adb devices -l > "$dir/adb-devices.txt" 2>&1 || true
  adb shell getprop > "$dir/getprop.txt" 2>&1 || true
  adb shell service check package > "$dir/package-service.txt" 2>&1 || true
  adb shell cmd package list packages -3 > "$dir/third-party-packages.txt" 2>&1 || true
  adb shell df -h /data > "$dir/data-space.txt" 2>&1 || true
  adb shell dumpsys diskstats > "$dir/diskstats.txt" 2>&1 || true
  adb logcat -d -t 3000 > "$dir/logcat.txt" 2>&1 || true
}

on_error() {
  local status="$?"
  trap - ERR
  echo "H1 emulator command failed (exit=$status); collecting device diagnostics."
  collect_diagnostics failure
  exit "$status"
}
trap on_error ERR

echo "Waiting for Android device..."
adb wait-for-device

booted=""
for attempt in $(seq 1 90); do
  booted="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [[ "$booted" == "1" ]]; then
    break
  fi
  sleep 2
done
if [[ "$booted" != "1" ]]; then
  echo "ERROR: emulator did not report sys.boot_completed=1 within 180 seconds."
  collect_diagnostics boot-timeout
  exit 20
fi

echo "Checking Package Manager responsiveness..."
adb shell service check package
adb shell cmd package list packages >/dev/null
adb shell df -h /data
collect_diagnostics preflight

echo "Starting H1 instrumentation tests..."
gradle connectedDebugAndroidTest --no-daemon --stacktrace --console=plain
