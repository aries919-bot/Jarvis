import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REPORT = (ROOT / "KARIYER_KAYNAK_DENETIMI.md").read_text(encoding="utf-8")
CANDIDATES = (ROOT / "career" / "adaylar.txt").read_text(encoding="utf-8")


def rows():
    result = []
    for line in CANDIDATES.splitlines():
        if not line or line.startswith("#"):
            continue
        parts = [part.strip() for part in line.split(" | ")]
        if len(parts) != 5:
            raise AssertionError(f"Kaynak satırı 5 alana ayrılmadı: {line}")
        result.append(parts)
    return result


class K1CAuditReportTests(unittest.TestCase):
    def test_exactly_twenty_unique_source_rows(self):
        entries = rows()
        self.assertEqual(20, len(entries))
        self.assertEqual(20, len({entry[1] for entry in entries}))
        self.assertEqual(20, len({entry[3] for entry in entries}))

    def test_modes_recount_to_nine_eight_three(self):
        entries = rows()
        modes = [entry[0] for entry in entries]
        self.assertEqual(5, modes.count("ÇALIŞIYOR"))
        self.assertEqual(4, modes.count("ENGELLİ"))
        self.assertEqual(8, modes.count("KAPALI_GEÇİCİ"))
        self.assertEqual(3, modes.count("YALNIZ_YÖNLENDİRME"))
        self.assertEqual(20, len(modes))

    def test_kariyer_kapisi_is_redirect_only(self):
        entries = {entry[1]: entry for entry in rows()}
        for source_id in ("kariyerkapisi", "msvk-kariyerkapisi", "izbb-kariyerkapisi"):
            self.assertEqual("YALNIZ_YÖNLENDİRME", entries[source_id][0])
        self.assertIn("kazınmaz", REPORT.lower())
        self.assertIn("Custom Tabs", REPORT)

    def test_tls_five_and_timeout_three_are_closed(self):
        entries = {entry[1]: entry for entry in rows()}
        tls = ("ilan-www", "tuseb-duyurular", "tuseb-ik", "ptt-duyurular", "ilan-apex-extra")
        timeouts = ("resmigazete-ilanlar", "tpao-personel-pdf", "resmigazete-root-extra")
        for source_id in tls + timeouts:
            self.assertEqual("KAPALI_GEÇİCİ", entries[source_id][0])
        self.assertIn("TLS — 5 URL", REPORT)
        self.assertIn("Timeout — 3 URL", REPORT)

    def test_latest_ci_and_counts_are_in_report(self):
        self.assertIn("45429536647", REPORT)
        self.assertIn("5 ÇALIŞIYOR / 15 ENGELLİ / 0 ÖLÜ", REPORT)
        self.assertIn("1 (e-Şube) / 43 doğrulanmış ilan", REPORT)

    def test_expired_yhgm_and_botas_job_are_not_current_jobs(self):
        self.assertIn("publishedAt=2025-11-12", REPORT)
        self.assertIn("deadline=2025-10-13", REPORT)
        self.assertIn("publishedAt=2025-11-05", REPORT)
        self.assertIn("deadline=2025-11-14", REPORT)
        self.assertGreaterEqual(REPORT.count("freshnessStatus=Süresi geçti"), 2)


if __name__ == "__main__":
    unittest.main()
