#!/usr/bin/env python3
"""Offline regression tests for the repository review and code-map tools."""
from __future__ import annotations

import importlib.util
import subprocess
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("denetle", ROOT / "denetle.py")
assert SPEC and SPEC.loader
DENETLE = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(DENETLE)

QA_SPEC = importlib.util.spec_from_file_location(
    "qa_report", ROOT / "scripts" / "qa" / "build_qa_report.py"
)
assert QA_SPEC and QA_SPEC.loader
BUILD_QA = importlib.util.module_from_spec(QA_SPEC)
QA_SPEC.loader.exec_module(BUILD_QA)


class DiffReviewTests(unittest.TestCase):
    def test_secret_after_legacy_12000_character_limit_is_detected(self) -> None:
        diff = "x" * 15_000 + '\n+token = "ghp_' + "A" * 36 + '"\n'
        hits = DENETLE.find_secret_hits(diff)
        self.assertTrue(hits)
        self.assertGreater(hits[0][0], 1)

    def test_all_added_lines_are_present_in_review_chunks(self) -> None:
        sections = []
        expected = set()
        for file_index in range(4):
            path = f"src/File{file_index}.kt"
            lines = [
                f"+unique_marker_{file_index}_{line_index}_" + "x" * 80 + "\n"
                for line_index in range(400)
            ]
            expected.update(line.rstrip("\n") for line in lines)
            sections.append(
                f"diff --git a/{path} b/{path}\n"
                f"index abc..def 100644\n--- a/{path}\n+++ b/{path}\n"
                "@@ -1,1 +1,400 @@\n" + "".join(lines)
            )
        chunks = DENETLE.split_diff("".join(sections))
        self.assertGreater(len(chunks), 4)
        seen = set()
        for chunk in chunks:
            seen.update(line for line in chunk.splitlines() if line.startswith("+unique_marker_"))
        self.assertEqual(expected, seen)

    def test_verdict_requires_an_exact_final_line(self) -> None:
        self.assertEqual("ONAY", DENETLE._verdict("İnceleme tamamlandı\nONAY"))
        self.assertEqual("RED", DENETLE._verdict("ONAY. Bütün sorunlar kapandı"))
        self.assertEqual("RED", DENETLE._verdict("ONAY\nRED"))
        self.assertEqual("RED", DENETLE._verdict(""))


class ClientSecretBuildTests(unittest.TestCase):
    def test_tmdb_token_is_not_embedded_in_mobile_apk(self) -> None:
        build_file = (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        android_workflow = (ROOT / ".github" / "workflows" / "android.yml").read_text(encoding="utf-8")
        qa_workflow = (ROOT / ".github" / "workflows" / "aries-full-app-qa.yml").read_text(encoding="utf-8")
        store_file = (ROOT / "app" / "src" / "main" / "java" / "com" / "cem" / "jarvis" / "Store.kt").read_text(encoding="utf-8")
        main_activity = (ROOT / "app" / "src" / "main" / "java" / "com" / "cem" / "jarvis" / "MainActivity.kt").read_text(encoding="utf-8")
        self.assertIn('val tmdbToken: String = ""', build_file)
        self.assertNotIn('System.getenv("TMDB_TOKEN")', build_file)
        self.assertNotIn('TMDB_TOKEN: ${{ secrets.TMDB_TOKEN }}', android_workflow)
        self.assertNotIn('TMDB_TOKEN: ${{ secrets.TMDB_TOKEN }}', qa_workflow)

    def test_cloud_api_key_is_keystore_encrypted_and_legacy_is_migrated(self) -> None:
        self.assertIn("object CloudKeyStore", store_file)
        self.assertIn('Cipher.getInstance("AES/GCM/NoPadding")', store_file)
        self.assertIn('.remove(LEGACY_PREF)', store_file)
        self.assertIn('CloudKeyStore.read(this)', main_activity)
        self.assertIn('CloudKeyStore.save(this, value)', main_activity)
        self.assertNotIn('store.str("bulut")', main_activity)
        self.assertIn('CloudKeyStore.read(applicationContext)', main_activity)
        self.assertIn("CloudKeyStore.clear(appContext)", store_file)


class QaLogcatTests(unittest.TestCase):
    def _scan(self, text: str) -> list[dict[str, str]]:
        with tempfile.TemporaryDirectory() as tmp:
            (Path(tmp) / "logcat.txt").write_text(text, encoding="utf-8")
            return BUILD_QA.scan_logcat(Path(tmp))

    def test_system_anr_is_not_reported_as_aries_error(self) -> None:
        logcat = (
            "ActivityManager: ANR in com.google.android.googlequicksearchbox:search\\n"
            "ActivityManager: PID: 2942\\n"
            "ActivityManager: Reason: Broadcast pkg=com.google.android.googlequicksearchbox\\n"
        )
        self.assertEqual([], self._scan(logcat))

    def test_aries_anr_is_reported(self) -> None:
        logcat = (
            "ActivityManager: ANR in com.cem.jarvis\\n"
            "ActivityManager: PID: 2942\\n"
            "ActivityManager: Reason: Input dispatching timed out\\n"
        )
        findings = self._scan(logcat)
        self.assertEqual(["anr"], [item["category"] for item in findings])
        self.assertEqual(["error"], [item["severity"] for item in findings])

    def test_system_fatal_exception_is_not_reported_as_aries_error(self) -> None:
        logcat = (
            "FATAL EXCEPTION: main\\n"
            "Process: com.google.android.googlequicksearchbox, PID: 2942\\n"
            "java.lang.IllegalStateException: unrelated\\n"
        )
        self.assertEqual([], self._scan(logcat))

    def test_aries_fatal_exception_is_reported(self) -> None:
        logcat = (
            "FATAL EXCEPTION: main\\n"
            "Process: com.cem.jarvis, PID: 2942\\n"
            "java.lang.IllegalStateException: app failure\\n"
        )
        findings = self._scan(logcat)
        self.assertEqual(["crash"], [item["category"] for item in findings])
        self.assertEqual(["error"], [item["severity"] for item in findings])


class CodeMapTests(unittest.TestCase):
    def test_map_script_generates_current_source_and_test_indexes(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            repo = Path(tmp)
            for relative in (
                "app/src/main/java/com/cem/jarvis/MainActivity.kt",
                "app/src/main/java/com/cem/jarvis/Ai.kt",
                "app/src/test/java/com/cem/jarvis/SmokeTest.kt",
                "app/src/androidTest/java/com/cem/jarvis/UiSmokeTest.kt",
                ".github/workflows/android.yml",
                "AGENTS.md",
            ):
                path = repo / relative
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_text("// fixture\n", encoding="utf-8")
            script = repo / "harita.sh"
            script.write_text((ROOT / "harita.sh").read_text(encoding="utf-8"), encoding="utf-8")
            subprocess.run(["sh", str(script)], check=True, capture_output=True, text=True)
            generated = (repo / "HARITA.md").read_text(encoding="utf-8")
            self.assertIn("MainActivity.kt", generated)
            self.assertIn("Ai.kt", generated)
            self.assertIn("SmokeTest.kt", generated)
            self.assertIn("UiSmokeTest.kt", generated)
            self.assertIn("android.yml", generated)


if __name__ == "__main__":
    unittest.main(verbosity=2)
