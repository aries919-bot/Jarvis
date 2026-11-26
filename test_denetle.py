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
        self.assertEqual(result.splitlines()[-1], "gönderilmedi")
        self.assertTrue(result.startswith("RED:"))
        urlopen.assert_not_called()


if __name__ == "__main__":
    unittest.main()
