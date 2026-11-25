import sys
import unittest
from unittest.mock import MagicMock, patch
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))

import hakem_gate
from hakem_gate import deterministic_findings, deterministic_only_fallback, test_log_is_successful


class HakemGateTests(unittest.TestCase):
    def test_tls_verification_cannot_be_disabled(self):
        diff = 'requests.get(url, verify' + '=' + 'False)'
        self.assertTrue(any("TLS" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_robots_bypass_is_rejected(self):
        diff = "robots" + ".txt bypass"
        self.assertTrue(any("robots" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_github_token_pattern_is_rejected(self):
        diff = "ghp" + "_" + ("A" * 24)
        self.assertTrue(any("Token" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_main_target_is_rejected(self):
        self.assertTrue(any("main" in item for item in deterministic_findings("safe diff", "main")))

    def test_collected_timestamp_cannot_be_publication_timestamp(self):
        diff = "published" + "At" + " = " + "collected" + "At"
        self.assertTrue(any("tarihi" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_browser_user_agent_is_rejected(self):
        diff = "Mozilla" + "/5.0"
        self.assertTrue(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_referee_source_does_not_flag_its_own_pattern_definitions(self):
        source = (ROOT / "scripts" / "hakem_gate.py").read_text(encoding="utf-8")
        findings = deterministic_findings(source, "cline/K1")
        self.assertFalse(any("yayın tarihi" in item for item in findings))

    def test_missing_cloudflare_credentials_use_deterministic_manual_fallback(self):
        decision, message = deterministic_only_fallback("", "")
        self.assertEqual(decision, "ONAY")
        self.assertIn("insan hakem incelemesinin yerine geçmez", message)

    def test_partial_cloudflare_credentials_also_use_manual_fallback(self):
        decision, message = deterministic_only_fallback("account-only", "")
        self.assertEqual(decision, "ONAY")
        self.assertIn("Opsiyonel Cloudflare AI", message)

    def test_complete_cloudflare_credentials_keep_optional_ai_review(self):
        self.assertIsNone(deterministic_only_fallback("account", "token"))

    def test_test_log_requires_real_successful_unittest_summary(self):
        log = "Ran 2 tests in 0.01s\n\nOK\nTest job result: success\n"
        self.assertTrue(test_log_is_successful(log))

    def test_test_log_rejects_failed_result(self):
        log = "Ran 2 tests in 0.01s\n\nFAILED (failures=1)\nTest job result: success\n"
        self.assertFalse(test_log_is_successful(log))

    def test_test_log_rejects_zero_or_missing_test_summary(self):
        self.assertFalse(test_log_is_successful("Ran 0 tests in 0.0s\n\nOK\nTest job result: success\n"))
        self.assertFalse(test_log_is_successful("OK\nTest job result: success\n"))

    def test_test_log_rejects_unsuccessful_test_job(self):
        log = "Ran 2 tests in 0.01s\n\nOK\nTest job result: failure\n"
        self.assertFalse(test_log_is_successful(log))

    def test_deleted_browser_user_agent_is_not_flagged(self):
        ua = "Mozilla" + "/5.0"
        diff = (
            "diff --git a/old.py b/old.py\n"
            "--- a/old.py\n+++ b/old.py\n"
            "@@ -1 +0,0 @@\n-" + ua + "\n"
        )
        self.assertFalse(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_added_browser_user_agent_is_flagged(self):
        ua = "Mozilla" + "/5.0"
        diff = (
            "diff --git a/new.py b/new.py\n"
            "--- a/new.py\n+++ b/new.py\n"
            "@@ -0,0 +1 @@\n+" + ua + "\n"
        )
        self.assertTrue(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_added_verify_false_in_checker_is_flagged(self):
        unsafe_call = "requests.get(url, verify" + "=" + "False)"
        diff = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n+++ b/scripts/hakem_gate.py\n"
            "@@ -1,0 +2 @@\n"
            "+" + unsafe_call + "\n"
        )
        self.assertTrue(any("TLS" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_added_browser_ua_in_checker_is_flagged(self):
        ua = "Mozilla" + "/5.0"
        user_agent_line = "headers['User-Agent'] = '" + ua + "'"
        diff = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n+++ b/scripts/hakem_gate.py\n"
            "@@ -1,0 +2 @@\n"
            "+" + user_agent_line + "\n"
        )
        self.assertTrue(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_hunk_added_content_starting_with_plus_plus_plus_is_scanned(self):
        unsafe_line = "+++" + " b/verify" + "=" + "False"
        diff = (
            "diff --git a/config.txt b/config.txt\n"
            "--- a/config.txt\n+++ b/config.txt\n"
            "@@ -1,0 +2 @@\n"
            + unsafe_line + "\n"
        )
        self.assertTrue(any("TLS" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_checker_pattern_file_is_excluded_from_self_scan(self):
        pattern_line = '(re.compile(r"Mozilla' + "/5.0" + '", re.I), "UA pattern")'
        diff = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n+++ b/scripts/hakem_gate.py\n"
            "@@ -1 +1 @@\n+        " + pattern_line + "\n"
        )
        self.assertFalse(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_get_diff_accepts_response_at_byte_limit(self):
        response = MagicMock()
        response.__enter__.return_value.read.return_value = b"x" * 8
        with patch.object(hakem_gate, "MAX_DIFF_BYTES", 8), patch(
            "hakem_gate.urllib.request.urlopen", return_value=response
        ):
            self.assertEqual(hakem_gate.get_diff("owner/repo", 61, "test-token"), "x" * 8)

    def test_get_diff_rejects_response_over_byte_limit(self):
        response = MagicMock()
        response.__enter__.return_value.read.return_value = b"x" * 9
        with patch.object(hakem_gate, "MAX_DIFF_BYTES", 8), patch(
            "hakem_gate.urllib.request.urlopen", return_value=response
        ):
            with self.assertRaisesRegex(RuntimeError, "8 bayt sınırını aştı"):
                hakem_gate.get_diff("owner/repo", 61, "test-token")


if __name__ == "__main__":
    unittest.main()

