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


if __name__ == "__main__":
    unittest.main()
