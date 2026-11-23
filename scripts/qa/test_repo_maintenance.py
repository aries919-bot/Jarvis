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
