# Aries Full Application QA

This QA lane is designed to test the whole Android application, not only İş & Kariyer.

## Automated coverage

- Android Lint, existing JVM unit tests and debug APK build.
- Emulator UI smoke test on Android API 29, 34 and 35.
- Main home, sidebar, settings, assistant/tasks/reminders, files, Film & TV, and the six CareerActivity tabs when reachable.
- Per-screen screenshot and UI hierarchy XML.
- Heuristic checks for clickable targets smaller than 48dp and controls without text/content descriptions.
- Static navigation source scan, JUnit failures, Lint results, and Logcat crash/ANR indicators.
- HTML, JSON and Markdown report artefacts retained by GitHub Actions.

## Where to find reports

Open the GitHub Actions run for Aries Full Application QA. Download:
- aries-static-unit-qa
- aries-full-app-qa-android-29
- aries-full-app-qa-android-34
- aries-full-app-qa-android-35

Each emulator artifact includes qa-output/report/index.html, report.json, screenshots, XML hierarchies, the ui-audit.json bundle and Logcat.

## Safety boundaries

The test does not send calls or messages, submit job applications, enter or reveal API keys, delete user data, or grant notification-listener/device-admin access. Runtime permission prompts may be accepted inside the disposable emulator so the UI can continue.

## What automation cannot decide

Automated checks are signals, not a final design judgement. Contrast, visual hierarchy, confusing labels, excessive content density, visual consistency and whether a flow is understandable must be reviewed using the saved screenshots. OEM-specific behaviours (Xiaomi/MIUI background limits, real Bluetooth devices, SIM/network behaviour, battery optimisation and notification delivery) require a physical-device pass or a device-farm run. Live job legitimacy must be verified against current employer/institution sources, not inferred from a successful emulator test.

The source audit intentionally reports screens without a discoverable navigation route for review; it does not silently claim that every constructed Activity/page is reachable.
