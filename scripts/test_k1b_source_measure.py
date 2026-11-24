import json
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))

import k1b_source_measure as measure


class K1BSourceMeasureTests(unittest.TestCase):
    def test_manifest_contains_twenty_unique_https_targets(self):
        sources = measure.read_manifest()
        self.assertEqual(20, len(sources))
        self.assertEqual(20, len({item["url"] for item in sources}))
        self.assertTrue(all(item["url"].startswith("https://") for item in sources))

    def test_robots_disallow_prevents_fetch_permission(self):
        robots = "User-agent: AriesKaynakTara\nDisallow: /private\nAllow: /"
        self.assertFalse(measure.robots_permission_from_text(robots, "https://example.gov.tr/private/jobs"))
        self.assertTrue(measure.robots_permission_from_text(robots, "https://example.gov.tr/public/jobs"))

    def test_wildcard_robots_rule_is_honored(self):
        robots = "User-agent: *\nDisallow: /"
        self.assertFalse(measure.robots_permission_from_text(robots, "https://example.gov.tr/duyurular"))

    def test_dated_announcement_requires_date_near_announcement_context(self):
        self.assertTrue(measure.has_dated_announcement("Personel alım ilanı 25.11.2026 tarihinde yayımlandı."))
        self.assertTrue(measure.has_dated_announcement("Kura duyurusu: 24 Kasım 2026"))
        self.assertFalse(measure.has_dated_announcement("© 2026 Kurum."))
        self.assertFalse(measure.has_dated_announcement("Son güncelleme: 25.11.2026"))

    def test_status_classification(self):
        self.assertEqual("ÇALIŞIYOR", measure.classify_result("ALLOW", 200, 2048))
        self.assertEqual("ENGELLİ", measure.classify_result("DISALLOW", None, 0))
        self.assertEqual("ENGELLİ", measure.classify_result("ALLOW", 403, 0))
        self.assertEqual("ÖLÜ", measure.classify_result("ALLOW", 404, 0))
        self.assertEqual("ÖLÜ", measure.classify_result("ALLOW", 410, 0))
        self.assertEqual("ENGELLİ", measure.classify_result("UNKNOWN", 200, 2048))

    def test_login_or_captcha_is_never_called_working(self):
        self.assertEqual("ENGELLİ", measure.classify_result("ALLOW", 200, 5000, auth_detected=True))

    def test_tls_failure_is_not_bypassed_or_called_dead(self):
        self.assertEqual("ENGELLİ", measure.classify_result("ALLOW", None, 0, error="TLS_CERTIFICATE: test"))

    def test_summary_has_required_columns(self):
        sample = [{
            "id":"x","category":"Kamu","name":"Örnek","url":"https://example.gov.tr/",
            "robots":"İZİN VAR","robotsPermission":"ALLOW","robotsHttp":404,
            "http":200,"bytes":1,"datedAnnouncement":False,"status":"ÇALIŞIYOR","error":None
        }]
        rendered = measure.make_markdown(sample, "2026-11-24T00:00:00Z")
        for title in ("Adres", "Robots", "HTTP", "Bayt", "Tarihli duyuru var mı?", "Durum"):
            self.assertIn(title, rendered)
        self.assertIn("ÇALIŞIYOR 1 / ENGELLİ 0 / ÖLÜ 0", rendered)


if __name__ == "__main__":
    unittest.main()
