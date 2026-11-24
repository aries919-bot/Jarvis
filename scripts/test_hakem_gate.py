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

    def test_deleted_browser_user_agent_is_ignored(self):
        patch = (
            "diff --git a/scripts/render_probe.py b/scripts/render_probe.py\n"
            "--- a/scripts/render_probe.py\n"
            "+++ b/scripts/render_probe.py\n"
            "@@ -1 +0,0 @@\n"
            "-Mozilla" + "/5.0 Chrome" + "/120.0\n"
        )
        findings = deterministic_findings(patch, "cline/K1")
        self.assertFalse(any("Tarayıcı" in item for item in findings))

    def test_added_browser_user_agent_is_rejected(self):
        patch = (
            "diff --git a/example.py b/example.py\n"
            "--- a/example.py\n"
            "+++ b/example.py\n"
            "@@ -0,0 +1 @@\n"
            "+Mozilla" + "/5.0 Chrome/120.0\n"
        )
        findings = deterministic_findings(patch, "cline/K1")
        self.assertTrue(any("Tarayıcı" in item for item in findings))

    def test_checker_source_file_is_excluded_from_pattern_scan(self):
        patch = (
            "diff --git a/scripts/hakem_gate.py b/scripts/hakem_gate.py\n"
            "--- a/scripts/hakem_gate.py\n"
            "+++ b/scripts/hakem_gate.py\n"
            "@@ -1 +1 @@\n"
            "+publishedAt = " + "collectedAt\n"
            "+Mozilla" + "/5.0\n"
        )
        findings = deterministic_findings(patch, "cline/K1")
        self.assertFalse(any("yayın tarihi" in item or "Tarayıcı" in item for item in findings))


if __name__ == "__main__":
    unittest.main()
