import json
import sys
import unittest
from email.message import Message
from unittest import mock
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

    def test_same_host_paths_are_checked_independently(self):
        robots = "User-agent: *\nDisallow: /ilanlar"
        self.assertFalse(measure.robots_permission_from_text(robots, "https://example.gov.tr/ilanlar"))
        self.assertTrue(measure.robots_permission_from_text(robots, "https://example.gov.tr/"))

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

    def test_allowed_page_timeout_is_retried_once(self):
        source = {"id": "x", "category": "Kamu", "name": "Örnek", "url": "https://example.gov.tr/"}
        robots = {"permission": "ALLOW", "label": "İZİN VAR", "status": 200, "error": None, "parser": None}
        success_headers = Message()
        success_headers["Content-Type"] = "text/html; charset=utf-8"
        with mock.patch.object(measure, "_robots_for", return_value=robots):
            with mock.patch.object(
                measure, "_request_once",
                side_effect=[
                    (None, None, b"", "TIMEOUT: first read timed out"),
                    (200, success_headers, "Personel alım ilanı 24.11.2026".encode("utf-8"), None),
                ],
            ) as request_once:
                with mock.patch.object(measure.time, "sleep"):
                    row = measure.measure_one(source)
        self.assertEqual(2, request_once.call_count)
        self.assertEqual(2, row["attempts"])
        self.assertEqual("ÇALIŞIYOR", row["status"])

    def test_robots_timeout_is_retried_once(self):
        from email.message import Message
        headers = Message()
        headers["Content-Type"] = "text/plain; charset=utf-8"
        robots_body = b"User-agent: AriesKaynakTara\\nAllow: /"
        with mock.patch.object(
            measure, "_request_once",
            side_effect=[
                (None, None, b"", "TIMEOUT: robots read timed out"),
                (200, headers, robots_body, None),
            ],
        ) as request_once:
            with mock.patch.object(measure.time, "sleep"):
                result = measure._robots_for("https://example.gov.tr/jobs")
        self.assertEqual(2, request_once.call_count)
        self.assertEqual("RULES", result["permission"])

    def test_unknown_robots_timeout_is_not_retried_or_fetched(self):
        source = {"id": "x", "category": "Kamu", "name": "Örnek", "url": "https://example.gov.tr/"}
        robots = {"permission": "UNKNOWN", "label": "BİLİNMİYOR", "status": None,
                  "error": "TIMEOUT: robots read timed out", "parser": None}
        with mock.patch.object(measure, "_robots_for", return_value=robots):
            with mock.patch.object(measure, "_request_once") as request_once:
                row = measure.measure_one(source)
        request_once.assert_not_called()
        self.assertEqual(0, row["attempts"])
        self.assertEqual("ENGELLİ", row["status"])

    def test_summary_has_required_columns(self):
        sample = [{
            "id":"x","category":"Kamu","name":"Örnek","url":"https://example.gov.tr/",
            "robots":"İZİN VAR","robotsPermission":"ALLOW","robotsHttp":404,
            "http":200,"bytes":1,"attempts":1,"datedAnnouncement":False,"status":"ÇALIŞIYOR","error":None
        }]
        rendered = measure.make_markdown(sample, "2026-11-24T00:00:00Z")
        for title in ("Adres", "Robots", "HTTP", "Bayt", "Deneme", "Tarihli duyuru var mı?", "Durum"):
            self.assertIn(title, rendered)
        self.assertIn("ÇALIŞIYOR 1 / ENGELLİ 0 / ÖLÜ 0", rendered)


if __name__ == "__main__":
    unittest.main()
