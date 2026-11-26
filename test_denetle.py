import unittest
from unittest.mock import patch

import denetle


class FullDiffSecretScanTest(unittest.TestCase):
    def test_secret_after_12000_chars_is_rejected_before_network(self):
        fake_diff = (
            "diff --git a/example.txt b/example.txt\\n"
            + "+" + ("x" * 12050) + "\\n"
            + "+GROQ_API_KEY=sk-" + ("A" * 32) + "\\n"
        )
        with patch("denetle.urllib.request.urlopen") as urlopen:
            result = denetle.review_diff(fake_diff)
        self.assertTrue(result.startswith("RED:"))
        self.assertIn("gizli bilgi bulundu", result)
        urlopen.assert_not_called()

    def test_oversized_file_splits_only_between_hunks(self):
        preamble = (
            "diff --git a/large.kt b/large.kt\\n"
            "index 1111111..2222222 100644\\n"
            "--- a/large.kt\\n"
            "+++ b/large.kt\\n"
        )
        hunk_one = "@@ -1 +1 @@\\n+" + ("a" * 6500) + "\\n"
        hunk_two = "@@ -2 +2 @@\\n+" + ("b" * 6500) + "\\n"
        parts = denetle.split_diff(preamble + hunk_one + hunk_two)
        self.assertIsNotNone(parts)
        self.assertEqual(len(parts), 2)
        self.assertTrue(all(len(part) <= denetle.MAX_CHARS for part in parts))
        self.assertTrue(all(part.startswith(preamble) for part in parts))
        self.assertIn("@@ -1 +1 @@", parts[0])
        self.assertNotIn("@@ -2 +2 @@", parts[0])
        self.assertIn("@@ -2 +2 @@", parts[1])

    def test_single_oversized_hunk_is_rejected(self):
        preamble = (
            "diff --git a/large.kt b/large.kt\\n"
            "--- a/large.kt\\n"
            "+++ b/large.kt\\n"
        )
        oversized_hunk = "@@ -1 +1 @@\\n+" + ("x" * 13000) + "\\n"
        self.assertIsNone(denetle.split_diff(preamble + oversized_hunk))


if __name__ == "__main__":
    unittest.main()
