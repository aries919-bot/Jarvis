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
  adb shell pidof system_server > "$dir/system-server-pid.txt" 2>&1 || true
  adb shell ps -A -o PID,NAME > "$dir/process-list.txt" 2>&1 || true
  adb shell service check package > "$dir/package-service.txt" 2>&1 || true
  adb shell service list > "$dir/services.txt" 2>&1 || true
  adb shell cmd package list packages -3 > "$dir/third-party-packages.txt" 2>&1 || true
  adb shell df -h /data > "$dir/data-space.txt" 2>&1 || true
  adb shell dumpsys diskstats > "$dir/diskstats.txt" 2>&1 || true
  adb logcat -b events -d -v brief > "$dir/events-log.txt" 2>&1 || true
  adb logcat -b crash -d > "$dir/crash-buffer.txt" 2>&1 || true
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

system_server_pid() {
  local pid
  pid="$(adb shell pidof system_server 2>/dev/null | tr -d '\r' | awk '{print $1}' || true)"
  if [[ -n "$pid" ]]; then
    printf '%s\n' "$pid"
    return 0
  fi
  adb shell ps -A -o PID,NAME 2>/dev/null |
    awk '$2 == "system_server" { print $1; exit }' |
    tr -d '\r'
}

latest_system_server_boot_pid() {
  adb logcat -b events -d -v brief -s boot_progress_enable_screen 2>/dev/null |
    sed -nE 's/.*boot_progress_enable_screen\([[:space:]]*([0-9]+)\).*/\1/p' |
    tail -n 1 |
    tr -d '\r'
}

package_manager_ready() {
  local service_line packages
  service_line="$(adb shell service check package 2>&1 | tr -d '\r' | tail -n 1)" || return 1
  if [[ "$service_line" != "Service package: found" ]]; then
    echo "Package Manager is not ready: $service_line"
    return 1
  fi
  packages="$(adb shell cmd package list packages 2>&1 | tr -d '\r')" || return 1
  if ! grep -q '^package:' <<< "$packages"; then
    echo "Package Manager returned no package records: $packages"
    return 1
  fi
}

wait_for_android_framework_ready() {
  local pid marker stable_pid stable_marker
  local attempts="${H1_FRAMEWORK_WAIT_ATTEMPTS:-150}"
  local interval="${H1_FRAMEWORK_POLL_SECONDS:-2}"
  local stability="${H1_FRAMEWORK_STABILITY_SECONDS:-5}"

  echo "Waiting for the current system_server generation and Package Manager..."
  for attempt in $(seq 1 "$attempts"); do
    pid="$(system_server_pid || true)"
    marker="$(latest_system_server_boot_pid || true)"
    if [[ -n "$pid" && "$pid" == "$marker" ]] && package_manager_ready; then
      echo "system_server PID $pid reported boot_progress_enable_screen; checking stability for ${stability}s."
      sleep "$stability"
      stable_pid="$(system_server_pid || true)"
      stable_marker="$(latest_system_server_boot_pid || true)"
      if [[ "$stable_pid" == "$pid" && "$stable_marker" == "$pid" ]] && package_manager_ready; then
        printf '%s\n' "$pid" > h1-emulator-diagnostics/preflight/system-server-pid.txt
        echo "Android framework ready: system_server PID $pid; Package Manager found and responsive."
        return 0
      fi
      echo "Android framework changed during stability check; continuing readiness wait."
    fi
    sleep "$interval"
  done

  echo "ERROR: no stable system_server generation with a responsive Package Manager."
  collect_diagnostics framework-not-ready
  return 21
}

echo "Waiting for Android device..."
adb wait-for-device

booted=""
for attempt in $(seq 1 "${H1_BOOT_WAIT_ATTEMPTS:-90}"); do
  booted="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [[ "$booted" == "1" ]]; then
    break
  fi
  sleep "${H1_BOOT_WAIT_INTERVAL_SECONDS:-2}"
done
if [[ "$booted" != "1" ]]; then
  echo "ERROR: emulator did not report sys.boot_completed=1 within the boot wait window."
  collect_diagnostics boot-timeout
  exit 20
fi

if ! wait_for_android_framework_ready; then
  exit 21
fi

echo "Starting H1 instrumentation tests..."
gradle connectedDebugAndroidTest --no-daemon --stacktrace --console=plain
