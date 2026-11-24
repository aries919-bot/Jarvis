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
    def test_exactly_twenty_eight_unique_source_rows(self):
        entries = rows()
        self.assertEqual(28, len(entries))
        self.assertEqual(28, len({entry[1] for entry in entries}))
        self.assertEqual(28, len({entry[3] for entry in entries}))

    def test_modes_recount_to_eleven_six_eight_three(self):
        entries = rows()
        modes = [entry[0] for entry in entries]
        self.assertEqual(11, modes.count("ÇALIŞIYOR"))
        self.assertEqual(6, modes.count("ENGELLİ"))
        self.assertEqual(8, modes.count("KAPALI_GEÇİCİ"))
        self.assertEqual(3, modes.count("YALNIZ_YÖNLENDİRME"))
        self.assertEqual(28, len(modes))

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
        self.assertIn("robots timeout — 3 url", REPORT.lower())

    def test_latest_ci_and_counts_are_in_report(self):
        self.assertIn("45482883679", REPORT)
        self.assertIn("11 ÇALIŞIYOR / 17 ENGELLİ / 0 ÖLÜ", REPORT)
        self.assertIn("28 benzersiz URL", REPORT)
        self.assertIn("1 kaynak / 43 e-Şube kaydı", REPORT)


    def test_four_new_institution_sources_match_the_28_url_measurement(self):
        entries = {entry[1]: entry for entry in rows()}
        for source_id in (
            "atk-duyuru-2026-2", "atk-ek1-2026-2",
            "anadolu-sozlesmeli-2026", "duzce-ogretim-elemani-2026",
            "tarsus-ogretim-elemani-2026", "tarsus-ogretim-elemani-pdf-2026",
        ):
            self.assertEqual("ÇALIŞIYOR", entries[source_id][0])
        self.assertEqual("ENGELLİ", entries["anadolu-sozlesmeli-pdf-2026"][0])
        self.assertEqual("ENGELLİ", entries["turkak-guvenlik-2026"][0])
        self.assertIn("6 kaynak, 5 fresh /1 stale", REPORT)
        self.assertIn("4 aktif kaynak / 46 doğrulanmış açık kart", REPORT)

    def test_expired_yhgm_and_botas_job_are_not_current_jobs(self):
        self.assertIn("publishedAt=2025-11-12", REPORT)
        self.assertIn("deadline=2025-10-13", REPORT)
        self.assertIn("publishedAt=2025-11-05", REPORT)
        self.assertIn("deadline=2025-11-14", REPORT)
        self.assertGreaterEqual(REPORT.count("freshnessStatus=Süresi geçti"), 2)


    def test_report_uses_latest_k1b_run_and_artifact(self):
        self.assertIn("45482883679", REPORT)
        self.assertIn("14963011109", REPORT)
        self.assertIn("2026-11-24T20:38:24Z", REPORT)
        self.assertIn("ece04d4e672234f15a7d1677828a2831fd0d59bd", REPORT)

    def test_public_threshold_is_separate_and_red_below_ten_by_one_hundred(self):
        self.assertIn("4 aktif kaynak / 46 doğrulanmış açık kart", REPORT)
        self.assertIn("10 kaynak / 100 doğrulanmış ilan", REPORT)
        self.assertIn("genel sonuç **RED**", REPORT)

    def test_access_url_count_is_not_ad_count(self):
        self.assertIn("28 benzersiz URL", REPORT)
        self.assertIn("gerçek açık ilan / ilan sayısı kanıtı değildir", REPORT)


if __name__ == "__main__":
    unittest.main()
