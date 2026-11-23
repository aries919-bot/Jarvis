#!/usr/bin/env python3
from __future__ import annotations
import datetime as dt, importlib.util, json, unittest
from pathlib import Path
p=Path(__file__).with_name("career_source_audit.py")
spec=importlib.util.spec_from_file_location("career_source_audit",p); mod=importlib.util.module_from_spec(spec); spec.loader.exec_module(mod)

class Tests(unittest.TestCase):
    def test_registry_20_unique_and_no_login_gated_boards(self):
        rows=json.loads(mod.REGISTRY.read_text(encoding="utf-8"))["sources"]; mod.validate(rows)
        self.assertEqual(20,len(rows)); self.assertEqual(len(rows),len({x["url"] for x in rows}))
        self.assertFalse(any("linkedin.com" in x["url"] or "indeed.com" in x["url"] for x in rows))
    def test_fingerprint_uses_employer_role_and_city(self):
        a=mod.listing_fingerprint("T.C. Zafer Kalkınma Ajansı","Uzman Personel","Kütahya")
        b=mod.listing_fingerprint("Zafer Kalkınma Ajansı","UZMAN-PERSONEL","KÜTAHYA")
        c=mod.listing_fingerprint("Zafer Kalkınma Ajansı","Uzman Personel","Manisa")
        self.assertEqual(a,b); self.assertNotEqual(a,c)
    def test_open_announced_and_expired_dates(self):
        today=dt.date(2026,11,23)
        self.assertEqual("open",mod.application_window("16.11.2026 - 30.11.2026",today)["status"])
        self.assertEqual("announced_not_open",mod.application_window("25.11.2026 - 14.12.2026",today)["status"])
        self.assertEqual("expired",mod.application_window("01.10.2026 - 10.10.2026",today)["status"])
    def test_missing_deadline_is_unknown_not_guessed(self):
        self.assertEqual("unknown",mod.application_window("Sözleşmeli personel alımı",dt.date(2026,11,23))["status"])
    def test_job_and_process_are_separate(self):
        self.assertEqual("job_candidate",mod.kind("Sözleşmeli Personel Alım İlanı","https://example.gov.tr/ilan"))
        self.assertEqual("process_notice",mod.kind("Kura Sonucu","https://example.gov.tr/sonuc"))
    def test_login_captcha_detection(self):
        self.assertIn("captcha",mod.BLOCK); self.assertIn("giriş yap",mod.LOGIN)

if __name__=="__main__": unittest.main(verbosity=2)
