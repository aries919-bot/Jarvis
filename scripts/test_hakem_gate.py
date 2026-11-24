import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))

from hakem_gate import deterministic_findings


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
        diff = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n+++ b/scripts/hakem_gate.py\n"
            "@@ -1,0 +2 @@\n"
            "+requests.get(url, verify=False)\n"
        )
        self.assertTrue(any("TLS" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_added_browser_ua_in_checker_is_flagged(self):
        diff = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n+++ b/scripts/hakem_gate.py\n"
            "@@ -1,0 +2 @@\n"
            "+headers['User-Agent'] = 'Mozilla/5.0'\n"
        )
        self.assertTrue(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_hunk_added_content_starting_with_plus_plus_plus_is_scanned(self):
        diff = (
            "diff --git a/config.txt b/config.txt\n"
            "--- a/config.txt\n+++ b/config.txt\n"
            "@@ -1,0 +2 @@\n"
            "+++ b/verify=False\n"
        )
        self.assertTrue(any("TLS" in item for item in deterministic_findings(diff, "cline/K1")))

    def test_checker_pattern_file_is_excluded_from_self_scan(self):
        source_line = 'UA_PATTERN = r"(?:Moz" + "illa)/[0-9]"'
        diff = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n+++ b/scripts/hakem_gate.py\n"
            "@@ -1 +1 @@\n+" + source_line + "\n"
        )
        self.assertFalse(any("Tarayıcı" in item for item in deterministic_findings(diff, "cline/K1")))


if __name__ == "__main__":
    unittest.main()

