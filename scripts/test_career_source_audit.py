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
    def test_turkish_month_names_produce_date_window(self):
        result=mod.application_window("Başvurular 03 Kasım 2026 tarihinde başlayıp 04 Aralık 2026 tarihinde sona erecektir.",dt.date(2026,11,23))
        self.assertEqual("2026-11-03",result["startDate"])
        self.assertEqual("2026-12-04",result["deadline"])
        self.assertEqual("open",result["status"])
    def test_process_notice_wins_over_job_words(self):
        self.assertEqual("process_notice",mod.kind("2026 Personel Alım Başvuru Değerlendirme Sonuçları","https://example.gov.tr/sonuc"))
    def test_missing_deadline_is_unknown_not_guessed(self):
        self.assertEqual("unknown",mod.application_window("Sözleşmeli personel alımı",dt.date(2026,11,23))["status"])
    def test_job_and_process_are_separate(self):
        self.assertEqual("job_candidate",mod.kind("Sözleşmeli Personel Alım İlanı","https://example.gov.tr/ilan"))
        self.assertEqual("process_notice",mod.kind("Kura Sonucu","https://example.gov.tr/sonuc"))
    def test_company_vacancy_terms_are_recognized(self):
        self.assertEqual("job_candidate",mod.kind("Mühendis Genel Başvuru - Mühendis Ankara", "https://kariyer.tusas.com/ilan-detay/genel-basvuru-muhendis", ["mühendis","teknisyen"]))
        self.assertEqual("job_candidate",mod.kind("CNC Torna Teknisyeni Ankara", "https://kariyer.roketsan.com.tr/ilan.job", ["teknisyen","mühendis"]))
    def test_early_career_programmes_are_not_mixed_with_jobs(self):
        self.assertEqual("programme_candidate",mod.kind("Jet Talent (JT-2026)", "https://yetenek.tei.com.tr/jet.job", [], ["jet talent","back to core"]))
    def test_public_programme_index_reader_is_separate_from_job_vacancies(self):
        from unittest.mock import patch
        source={"id":"tei","name":"TEI programmes","url":"https://example.gov.tr/jobs","probeUrl":"https://example.gov.tr/jobs","readerMode":"html_index","expectedMarkers":["jet talent"],"candidateTerms":["genel başvuru"],"programmeTerms":["jet talent","back to core"]}
        html='<html><body><h1>Jet Talent</h1><p>Early career programme for university students with application and assessment activities, learning sessions and mentoring opportunities.</p><a href="/jet-talent">Jet Talent (JT-2026)</a></body></html>'
        with patch.object(mod,"fetch",return_value=(200,"https://example.gov.tr/jobs","text/html",html)):
            result=mod.probe(source)
        summary=mod.report([source],[result])
        self.assertTrue(result["readerOk"])
        self.assertEqual(0,result["candidateCount"])
        self.assertEqual(1,result["programmeCount"])
        self.assertEqual(1,summary["programmeReaderCount"])
        self.assertEqual(0,summary["vacancyLinkCount"])
        self.assertEqual(1,summary["programmeLinkCount"])
    def test_blank_html_is_not_a_working_reader(self):
        from unittest.mock import patch
        source={"id":"blank","name":"Blank","url":"https://example.gov.tr/","probeUrl":"https://example.gov.tr/","readerMode":"html_index"}
        with patch.object(mod,"fetch",return_value=(200,"https://example.gov.tr/","text/html","")):
            result=mod.probe(source)
        self.assertFalse(result["readerOk"]); self.assertEqual(0,result["candidateCount"])
    def test_network_failure_is_reported_not_silently_ignored(self):
        from unittest.mock import patch
        from urllib.error import URLError
        source={"id":"offline","name":"Offline","url":"https://example.gov.tr/","probeUrl":"https://example.gov.tr/","readerMode":"html_index"}
        with patch.object(mod,"fetch",side_effect=URLError("offline")):
            result=mod.probe(source)
        self.assertEqual("network_error",result["status"]); self.assertFalse(result["readerOk"])
    def test_login_captcha_detection(self):
        self.assertIn("captcha",mod.BLOCK); self.assertIn("giriş yap",mod.LOGIN)

if __name__=="__main__": unittest.main(verbosity=2)
