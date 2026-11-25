import json
import sys
import tempfile
import unittest
from datetime import date
from email.message import Message
from pathlib import Path
from unittest import mock

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/"scripts"))
import h2_announcement_reader as h2


def ok(body=b"ok", status=200, headers=None):
    return h2.FetchResponse(status,headers or Message(),body)


class H2ReaderTests(unittest.TestCase):
    def test_yhgm_publication_and_deadline_are_separate(self):
        source = (
            "<h1>Taşra Teşkilatı 2.764 Sürekli İşçi Alım Kurası</h1>"
            "<p>12.11.2025 Tarihli Duyuru:</p><p>Kura 12 Kasım 2025 tarihinde çekilmiştir.</p>"
            "<p>Başvurular 08/10/2025 – 13/10/2025 tarihleri arasında alınmıştır.</p>"
        ).encode()
        item = h2.parse_yhgm(source,Message(),"2026-11-24T10:00:00Z","2026-11-24T10:00:00Z",date(2026,11,24))
        self.assertEqual("2025-11-12",item["publishedAt"])
        self.assertEqual("2025-10-13",item["deadline"])
        self.assertEqual("lottery",item["contentType"])
        self.assertEqual("Süresi geçti",item["freshnessStatus"])

    def test_botas_publication_is_not_title_exam_date(self):
        text = "223 Kişilik Personel Alım Duyurusu Tarih: 5/11/2025"
        self.assertEqual("2025-11-05",h2.explicit_published_at(text))

    def test_yhgm_actual_word_order_extracts_deadline_after_date_range(self):
        text = (
            "2.764 sürekli işçi alımı için 08/10/2025 – 13/10/2025 tarihleri "
            "arasında İŞKUR tarafından alınan başvurular Bakanlığımıza bildirilmiştir."
        )
        self.assertEqual("2025-10-13",h2.application_deadline(text,"başvurular"))

    def test_botas_robots_redirect_to_official_apex_is_followed(self):
        calls=[]
        def requester(url,limit):
            calls.append(url)
            if url == "https://www.botas.gov.tr/robots.txt":
                headers=Message()
                headers["Location"]="https://botas.gov.tr/robots.txt"
                return ok(b"",302,headers)
            if url == "https://botas.gov.tr/robots.txt":
                return ok(b"User-agent: *\\nAllow: /")
            if url == "https://www.botas.gov.tr/notice":
                headers=Message()
                headers["Location"]="https://botas.gov.tr/notice"
                return ok(b"",302,headers)
            if url == "https://botas.gov.tr/notice":
                return ok(b"official page")
            raise AssertionError("Unexpected request: "+url)
        body,final_url,_=h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)
        self.assertEqual(b"official page",body)
        self.assertEqual("https://botas.gov.tr/notice",final_url)
        self.assertIn("https://botas.gov.tr/robots.txt",calls)

    def test_robots_redirect_to_unapproved_host_is_rejected(self):
        def requester(url,limit):
            if url.endswith("/robots.txt"):
                headers=Message()
                headers["Location"]="https://evil.example/robots.txt"
                return ok(b"",302,headers)
            raise AssertionError("Target request must not be made")
        with self.assertRaises(h2.SourceError):
            h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)

    def test_botas_pdf_deadline_is_end_of_iskur_range(self):
        pdf = (
            "223 Kişilik Personel Alım Duyurusu İlgili ilanda istenilen KPSS puan türünden "
            "en az 60 puan almış olmaları gerekmektedir. İlanlar, "
            "10.11.2025-14.11.2025 tarihleri arasında yayımlanacaktır."
        )
        self.assertEqual("2025-11-14",h2.application_deadline(pdf,"İlanlar"))

    def test_botas_result_publication_is_not_title_exam_date(self):
        text = (
            "10 Kasım 2025 / 14 Kasım 2025 Tarihli Müfettiş Yardımcılığı Alımına "
            "Yönelik Sonuç Duyurusu Tarih: 21/1/2026"
        )
        item = h2.parse_botas_result(
            text.encode(),Message(),"2026-11-24T10:00:00Z","2026-11-24T10:00:00Z",date(2026,11,24)
        )
        self.assertEqual("2026-01-21",item["publishedAt"])
        self.assertIsNone(item["deadline"])
        self.assertEqual("result",item["contentType"])
        self.assertEqual("Süresi geçti",item["freshnessStatus"])

    def test_robots_disallow_prevents_target_request(self):
        calls=[]
        def requester(url,limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\nDisallow: /private")
            return ok(b"page")
        with self.assertRaises(h2.SourceError):
            h2.fetch_public_url("https://www.botas.gov.tr/private/page",requester)
        self.assertEqual(["https://www.botas.gov.tr/robots.txt"],calls)

    def test_robots_timeout_never_fetches_target(self):
        calls=[]
        def requester(url,limit):
            calls.append(url)
            return h2.FetchResponse(None,None,b"","TIMEOUT: robots")
        with self.assertRaises(h2.SourceError):
            h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)
        self.assertEqual(1,len(calls))
        self.assertTrue(calls[0].endswith("/robots.txt"))

    def test_403_500_empty_and_timeout_fail_closed(self):
        for status,body,error in ((403,b"blocked",None),(500,b"failed",None),(200,b"",None)):
            calls=[]
            def requester(url,limit,status=status,body=body,error=error):
                calls.append(url)
                if url.endswith("/robots.txt"):
                    return ok(b"User-agent: *\nAllow: /")
                return h2.FetchResponse(status,Message(),body,error)
            with self.assertRaises(h2.SourceError):
                h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)
            self.assertEqual(2,len(calls))

    def test_page_timeout_is_retried_once_after_robots_allow(self):
        calls=[]
        def requester(url,limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\\nAllow: /")
            if len([x for x in calls if not x.endswith("/robots.txt")]) == 1:
                return h2.FetchResponse(None,None,b"","TIMEOUT: first page timeout")
            return ok(b"page after one retry")
        with mock.patch.object(h2.time,"sleep"):
            body,final_url,_=h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)
        self.assertEqual(b"page after one retry",body)
        self.assertEqual("https://www.botas.gov.tr/notice",final_url)
        self.assertEqual(2,len([x for x in calls if not x.endswith("/robots.txt")]))

    def test_page_timeout_retries_only_once_then_fails_closed(self):
        calls=[]
        def requester(url,limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\\nAllow: /")
            return h2.FetchResponse(None,None,b"","TIMEOUT: page")
        with mock.patch.object(h2.time,"sleep"):
            with self.assertRaisesRegex(h2.SourceError,"TIMEOUT_RETRY_FAILED"):
                h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)
        self.assertEqual(2,len([x for x in calls if not x.endswith("/robots.txt")]))

    def test_official_link_map_keeps_only_same_official_host_family(self):
        page=h2.PageParser()
        page.feed(
            '<a href="/Icerik/223-kisilik-personel-alim-duyu/1203">'
            '223 Kişilik Personel Alım Duyurusu</a>'
            '<a href="https://www.botas.gov.tr/uploads/dosyaYoneticisi/559215-lan-tablo.pdf">'
            'tıklayınız</a>'
            '<a href="https://evil.example/attack">outside</a>'
        )
        links=h2.official_link_map(page,"https://www.botas.gov.tr/Kategori/duyurular/2")
        self.assertIn("https://www.botas.gov.tr/Icerik/223-kisilik-personel-alim-duyu/1203",links)
        self.assertIn("https://www.botas.gov.tr/uploads/dosyaYoneticisi/559215-lan-tablo.pdf",links)
        self.assertNotIn("https://evil.example/attack",links)

    def test_fresh_feed_updates_last_good_but_stale_feed_does_not(self):
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp)
            out=root/"out"
            last=root/"last-good"
            fresh={"schemaVersion":1,"sourceId":"yhgm-kura","sourceStatus":"fresh","collectedAt":"2026-11-24T10:00:00Z","items":[{"id":"x","firstSeenAt":"2026-11-01T00:00:00Z"}]}
            stale={"schemaVersion":1,"sourceId":"yhgm-kura","sourceStatus":"stale","collectedAt":"2026-11-24T10:00:00Z","items":[]}
            h2.write_feed(fresh,out,last)
            h2.write_feed(stale,out,last)
            saved=json.loads((last/"yhgm-kura.json").read_text(encoding="utf-8"))
            rendered=json.loads((out/"yhgm-kura.json").read_text(encoding="utf-8"))
            self.assertEqual("fresh",saved["sourceStatus"])
            self.assertEqual("x",saved["items"][0]["id"])
            self.assertEqual("stale",rendered["sourceStatus"])

    def test_redirect_to_other_host_is_rejected(self):
        headers=Message()
        headers["Location"]="https://evil.example/elsewhere"
        def requester(url,limit):
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\nAllow: /")
            return ok(b"",302,headers)
        with self.assertRaises(h2.SourceError):
            h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)

    def test_first_seen_is_preserved_for_known_ids(self):
        old={"items":[{"id":"same","firstSeenAt":"2026-10-01T00:00:00Z"}]}
        items=[{"id":"same"},{"id":"new"}]
        h2.assign_first_seen(items,old,"2026-11-24T10:00:00Z")
        self.assertEqual("2026-10-01T00:00:00Z",items[0]["firstSeenAt"])
        self.assertEqual("2026-11-24T10:00:00Z",items[1]["firstSeenAt"])

    def test_source_failure_preserves_only_that_source_last_good(self):
        old={"schemaVersion":1,"sourceId":"botas-duyurular",
             "collectedAt":"2026-11-20T00:00:00Z","sourceStatus":"fresh",
             "items":[{"id":"old","firstSeenAt":"2026-11-01T00:00:00Z","sourceStatus":"fresh"}]}
        stale=h2.stale_feed("botas-duyurular","2026-11-24T10:00:00Z","offline",old)
        self.assertEqual("2026-11-20T00:00:00Z",stale["collectedAt"])
        self.assertEqual("stale",stale["sourceStatus"])
        self.assertEqual("stale",stale["items"][0]["sourceStatus"])
        self.assertEqual("old",stale["items"][0]["id"])

    def test_tls_context_does_not_disable_verification(self):
        with mock.patch.object(h2.urllib.request,"build_opener") as build:
            h2.request_once("https://www.botas.gov.tr/test",100)
        handler=build.call_args.args[0]
        self.assertTrue(handler._context.verify_mode == 2)  # ssl.CERT_REQUIRED
        self.assertTrue(handler._context.check_hostname)


    def test_turkish_month_name_deadline_range_is_parsed(self):
        text = "Başvurular 30 Kasım 2026 Pazartesi başlayıp 06 Aralık 2026 Pazar günü sona erecektir."
        self.assertEqual("2026-12-06", h2.application_deadline(text, "başvurular"))

    def test_atk_notice_separates_published_and_deadline(self):
        source = {
            "itemId":"atk-2026-2-181-pozisyon",
            "institution":"Adli Tıp Kurumu Başkanlığı",
            "title":"2026-2 sözleşmeli personel alımı — 181 pozisyon",
            "category":"Kamu","contentType":"job",
            "url":"https://atk.gov.tr/haber20.11.26.html",
            "publicationMode":"leading-date",
            "deadlineContext":"başvurular",
            "applicationOpensAt":"2026-11-30T09:00:00+03:00",
            "expectedText":["Adli Tıp Kurumu Başkanlığı","181 pozisyon"],
            "howToApply":{"method":"Kariyer Kapısı","url":"https://kariyerkapisi.gov.tr/isealim","instructions":"Okuyucu giriş yapmaz."},
            "conditions":["2026 KPSS puan türünden en az 70."]
        }
        page = (
            "20/11/2026 DUYURU Adli Tıp Kurumu Başkanlığı 181 pozisyon için "
            "2026 KPSS ilgili puan türünden en az 70. Başvurular "
            "30 Kasım 2026 Pazartesi başlayıp 06 Aralık 2026 Pazar günü sona erer."
        )
        item = h2.parse_official_notice(source,page,"Ek-1 pozisyon tablosu",
            "2026-11-24T19:00:00Z","2026-11-24T19:00:00Z",date(2026,11,24))
        self.assertEqual("2026-11-20",item["publishedAt"])
        self.assertEqual("2026-12-06",item["deadline"])
        self.assertEqual("Doğrulanamadı",item["freshnessStatus"])
        self.assertEqual("2026-11-30T09:00:00+03:00",item["applicationOpensAt"])
        self.assertEqual("job",item["contentType"])
        self.assertEqual("https://kariyerkapisi.gov.tr/isealim",item["howToApply"]["url"])


    def test_atk_upcoming_application_is_not_marked_open_before_start(self):
        status = h2.status_for(
            "2026-12-06","2026-11-20","job",date(2026,11,24),
            "2026-11-30T09:00:00+03:00","2026-11-24T20:00:39Z"
        )
        self.assertEqual("Doğrulanamadı",status)

    def test_atk_application_becomes_verified_after_start(self):
        status = h2.status_for(
            "2026-12-06","2026-11-20","job",date(2026,11,30),
            "2026-11-30T09:00:00+03:00","2026-11-30T06:15:00Z"
        )
        self.assertEqual("Doğrulandı",status)

    def test_anadolu_added_date_and_deadline_are_separate(self):
        source = {
            "itemId":"anadolu-2026-4b-1-personel",
            "institution":"Anadolu Üniversitesi",
            "title":"Sözleşmeli personel alımı ilanı — 1 pozisyon",
            "category":"Kamu","contentType":"job",
            "url":"https://anadolu.edu.tr/duyurular/sozlesmeli-personel-alimi-ilani-1794568099",
            "publicationMode":"anadolu-added-date",
            "deadlineContext":"başvurular",
            "expectedText":["Sözleşmeli Personel Alımı İlanı","1 (bir) sözleşmeli personel"],
            "howToApply":{"method":"Kariyer Kapısı","url":"https://kariyerkapisi.gov.tr/isealim","instructions":"e-Devlet üzerinden."},
            "conditions":["657 4/B; KPSS(B) puan sıralaması."]
        }
        page = (
            "Sözleşmeli Personel Alımı İlanı Eklenme Tarihi: 13 Kasım 2026 "
            "657 4/B kapsamında 1 (bir) sözleşmeli personel alınacaktır. "
            "Başvurular 16.11.2026 - 30.11.2026 tarihleri arasında alınır."
        )
        item = h2.parse_official_notice(source,page,"",
            "2026-11-24T19:00:00Z","2026-11-24T19:00:00Z",date(2026,11,24))
        self.assertEqual("2026-11-13",item["publishedAt"])
        self.assertEqual("2026-11-30",item["deadline"])
        self.assertEqual("Doğrulandı",item["freshnessStatus"])

    def test_duzce_pdf_title_date_and_deadline_are_separate(self):
        source = {
            "itemId":"duzce-2026-11-23-ogretim-elemani-5-kadro",
            "institution":"Düzce Üniversitesi",
            "title":"23.11.2026 tarihli öğretim elemanı alımı — 5 kadro",
            "category":"Kamu","contentType":"job",
            "url":"https://cdn.duzce.edu.tr/File/GetFile/4455f177-5bf2-414d-a4c6-9de77131bf51",
            "publicationMode":"duzce-title",
            "deadlineContext":"başvuru",
            "expectedText":["DÜZCE ÜNİVERSİTESİ","ÖĞRETİM ELEMANI ALIMI İLANI","Başvuru Tarihleri"],
            "howToApply":{"method":"Akademik ilan sistemi","url":"http://abas.duzce.edu.tr","instructions":"Tek kadro."},
            "conditions":["ALES en az 70; kadroya göre lisans ve dil şartı."]
        }
        pdf = (
            "DÜZCE ÜNİVERSİTESİ 23.11.2026 TARİHLİ ÖĞRETİM ELEMANI ALIMI İLANI "
            "Başvuru Tarihleri: 23.11.2026 - 07.12.2026. ALES 70."
        )
        item = h2.parse_official_notice(source,pdf,"",
            "2026-11-24T19:00:00Z","2026-11-24T19:00:00Z",date(2026,11,24))
        self.assertEqual("2026-11-23",item["publishedAt"])
        self.assertEqual("2026-12-07",item["deadline"])

    def test_tarsus_notice_uses_official_gazette_date_not_other_dates(self):
        source = {
            "itemId":"tarsus-2026-11-23-ogretim-gorevlisi-3-kadro",
            "institution":"Tarsus Üniversitesi",
            "title":"23.11.2026 öğretim görevlisi alımı — 3 kadro",
            "category":"Kamu","contentType":"job",
            "url":"https://personel.tarsus.edu.tr/tr/news-detail/ogretim-elemani-ilani/17895",
            "publicationMode":"tarsus-gazette",
            "deadlineContext":"başvuru tarihleri",
            "expectedText":["Öğretim Elemanı İlanı","23.11.2026","07.12.2026"],
            "howToApply":{"method":"Şahsen/posta","url":"https://personel.tarsus.edu.tr/tr/news-detail/ogretim-elemani-ilani/17895","instructions":"İlan metnindeki birime."},
            "conditions":["3 öğretim görevlisi kadrosu; ALES 70."]
        }
        page = (
            "Öğretim Elemanı İlanı 23 Kasım 2026. İlanın Yayımlandığı Resmî Gazete Tarih ve Sayısı: "
            "23.11.2026 / 33439. İlana Başvuru Tarihleri: 23.11.2026 - 07.12.2026 (Mesai Bitimi)."
        )
        item = h2.parse_official_notice(source,page,
            "Başvuru Tarihi 23.11.2026 Son Başvuru Tarihi 07.12.2026",
            "2026-11-24T19:00:00Z","2026-11-24T19:00:00Z",date(2026,11,24))
        self.assertEqual("2026-11-23",item["publishedAt"])
        self.assertEqual("2026-12-07",item["deadline"])

    def test_official_parser_fails_closed_without_explicit_publication_or_deadline(self):
        source = {
            "itemId":"incomplete","institution":"Örnek Kurum","title":"Personel duyurusu",
            "category":"Kamu","contentType":"job","url":"https://example.gov.tr/duyuru",
            "publicationMode":"labelled","deadlineContext":"başvuru",
            "expectedText":["Personel duyurusu"],
            "howToApply":{"method":"Resmî duyuru","url":"https://example.gov.tr/duyuru","instructions":"Kontrol edin."},
            "conditions":[]
        }
        with self.assertRaisesRegex(h2.SourceError,"yayın tarihi"):
            h2.parse_official_notice(source,"Personel duyurusu; başvuru dönemi yakında.",
                "","2026-11-24T19:00:00Z","2026-11-24T19:00:00Z",date(2026,11,24))

    def test_official_collector_checks_linked_pdf_only_on_same_official_host(self):
        source = {
            "id":"tarsus-test","reader":"official","format":"html",
            "url":"https://personel.tarsus.edu.tr/test",
            "pdfUrl":"https://personel.tarsus.edu.tr/test.pdf"
        }
        calls = []
        def requester(url,limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\\nAllow: /")
            if url == source["url"]:
                return ok(b'<h1>Notice</h1><a href="/test.pdf">Official PDF</a>',headers=Message())
            if url == source["pdfUrl"]:
                return ok(b"%PDF-fake")
            raise AssertionError("Unexpected target request: "+url)
        source.update({
            "itemId":"tarsus-test-item","institution":"Tarsus Üniversitesi","title":"Öğretim elemanı",
            "category":"Kamu","contentType":"job","publicationMode":"tarsus-gazette",
            "deadlineContext":"başvuru tarihleri","expectedText":["Notice"],
            "howToApply":{"method":"Resmî","url":source["url"],"instructions":"İlan metni."},
            "conditions":[]
        })
        pdf_text = "Öğretim Elemanı İlanı İlanın Yayımlandığı Resmî Gazete Tarih ve Sayısı: 23.11.2026 İlana Başvuru Tarihleri: 23.11.2026 - 07.12.2026"
        with mock.patch.object(h2,"extract_pdf_text",return_value=pdf_text):
            feed = h2.collect_official(source,requester,"2026-11-24T19:00:00Z",None,date(2026,11,24))
        self.assertEqual("fresh",feed["sourceStatus"])
        self.assertEqual("2026-12-07",feed["items"][0]["deadline"])
        self.assertEqual("2026-11-23",feed["items"][0]["publishedAt"])
        self.assertEqual("https://personel.tarsus.edu.tr/test.pdf",calls[-1])
        self.assertFalse(any("kariyerkapisi" in call for call in calls))


    def test_independently_measured_same_host_pdf_is_allowed_without_html_anchor(self):
        source = {
            "id":"tarsus-test","reader":"official","format":"html",
            "url":"https://personel.tarsus.edu.tr/test",
            "pdfUrl":"https://personel.tarsus.edu.tr/test.pdf",
            "pdfUrlPremeasured":True,
            "itemId":"tarsus-test-item","institution":"Tarsus Üniversitesi",
            "title":"Öğretim elemanı","category":"Kamu","contentType":"job",
            "publicationMode":"tarsus-gazette","deadlineContext":"başvuru tarihleri",
            "expectedText":["Notice"],
            "howToApply":{"method":"Resmî","url":"https://personel.tarsus.edu.tr/test","instructions":"İlan metni."},
            "conditions":[]
        }
        calls = []
        def requester(url,limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\\nAllow: /")
            if url == source["url"]:
                return ok(b"<h1>Notice</h1>")
            if url == source["pdfUrl"]:
                return ok(b"%PDF-fake")
            raise AssertionError("Unexpected request: "+url)
        pdf_text = (
            "Öğretim Elemanı İlanı İlanın Yayımlandığı Resmî Gazete Tarih ve Sayısı: "
            "23.11.2026 İlana Başvuru Tarihleri: 23.11.2026 - 07.12.2026"
        )
        with mock.patch.object(h2,"extract_pdf_text",return_value=pdf_text):
            feed = h2.collect_official(source,requester,"2026-11-24T19:00:00Z",None,date(2026,11,24))
        self.assertEqual("fresh",feed["sourceStatus"])
        self.assertIn(source["pdfUrl"],calls)

    def test_premeasured_pdf_from_other_host_is_still_rejected(self):
        source = {
            "id":"bad-pdf","reader":"official","format":"html",
            "url":"https://personel.tarsus.edu.tr/test",
            "pdfUrl":"https://evil.example/test.pdf","pdfUrlPremeasured":True
        }
        def requester(url,limit):
            if url.endswith("/robots.txt"):
                return ok(b"User-agent: *\\nAllow: /")
            if url == source["url"]:
                return ok(b"<h1>Notice</h1>")
            raise AssertionError("Foreign PDF must not be requested")
        with self.assertRaisesRegex(h2.SourceError,"farklı resmî host"):
            h2.collect_official(source,requester,"2026-11-24T19:00:00Z",None,date(2026,11,24))


    def test_pdf_href_with_spaces_uses_measured_url_encoded_target(self):
        page = h2.PageParser()
        page.feed('<a href="/Files/Resmi Gazete İlan 23.11.2026.pdf">İlan metni</a>')
        expected = "https://personel.tarsus.edu.tr/Files/Resmi%20Gazete%20%C4%B0lan%2023.11.2026.pdf"
        found = h2.official_link_target(page,"https://personel.tarsus.edu.tr/tr/news/notice",expected)
        self.assertIsNotNone(found)
        expected_path = h2.urllib.parse.unquote(h2.urllib.parse.urlsplit(expected).path)
        found_path = h2.urllib.parse.unquote(h2.urllib.parse.urlsplit(found).path)
        self.assertEqual(expected_path.casefold(),found_path.casefold())

    def test_config_includes_only_k1b_measured_reader_sources(self):
        cfg = json.loads(h2.CONFIG.read_text(encoding="utf-8"))
        self.assertEqual(15,len(cfg["sources"]))
        self.assertEqual({"yhgm","botas","official"},{s["reader"] for s in cfg["sources"]})
        self.assertTrue({"atk-personel-2026-2","anadolu-sozlesmeli-2026","duzce-ogretim-elemani-2026","tarsus-ogretim-elemani-2026"}.issubset({s["id"] for s in cfg["sources"]}))

    def test_config_is_limited_to_measured_official_sources(self):
        cfg=json.loads(h2.CONFIG.read_text(encoding="utf-8"))
        self.assertEqual({"yhgm","botas","official"},{s["reader"] for s in cfg["sources"]})
        self.assertEqual(6,len(cfg["sources"]))


    def test_source_categories_match_h1_allowed_enum(self):
        cfg = json.loads(h2.CONFIG.read_text(encoding="utf-8"))
        allowed = {"Kamu", "Kura", "KPSS", "KPSS'siz", "Büyük özel", "Kamu bağlı şirket"}
        self.assertTrue(all(source.get("category") in allowed for source in cfg["sources"]))
        botas = next(source for source in cfg["sources"] if source["id"] == "botas-duyurular")
        self.assertEqual("Kamu bağlı şirket", botas["category"])


    def test_empty_200_robots_file_allows_public_page(self):
        calls=[]
        def requester(url,limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return ok(b"")
            return ok(b"public page")
        body,final_url,_=h2.fetch_public_url("https://atauni.edu.tr/notice",requester)
        self.assertEqual(b"public page",body)
        self.assertEqual("https://atauni.edu.tr/notice",final_url)
        self.assertEqual(2,len(calls))

    def test_balikesir_personel_and_baunwebapi_are_same_official_host_family(self):
        self.assertTrue(h2.same_official_host(
            "https://personel.balikesir.edu.tr/notice",
            "https://baunwebapi.balikesir.edu.tr/uploads/notice.pdf"
        ))

    def test_oran_table_publication_date_is_explicit(self):
        text="ORAN Personel Alım İlanı İlan Tarihi 19/10/2026 Başvuru Tarihleri 09/11/2026 - 03/12/2026"
        self.assertEqual("2026-10-19",h2.explicit_published_at(text,mode="oran-table-date"))

    def test_doka_and_zafer_page_dates_parse_turkish_month_abbreviation(self):
        doka="02 Kas 2026 T.C. Doğu Karadeniz Kalkınma Ajansı Personel Alım İlanı"
        zafer="03 Kas 2026 T.C. Zafer Kalkınma Ajansı Personel Alım İlanı"
        self.assertEqual("2026-11-02",h2.explicit_published_at(doka,mode="doka-page-date"))
        self.assertEqual("2026-11-03",h2.explicit_published_at(zafer,mode="zafer-page-date"))

    def test_ataturk_page_date_is_not_taken_from_numeric_title_date(self):
        text="20 Kasım 2026 Atatürk Üniversitesi Öğretim Üyesi Alım İlanı 20.11.2026 Son Başvuru Tarihi 04.12.2026"
        self.assertEqual("2026-11-20",h2.explicit_published_at(text,mode="ataturk-page-date"))

    def test_single_labeled_deadline_is_parsed_without_title_date(self):
        text="Atatürk Üniversitesi Öğretim Üyesi Alım İlanı 20.11.2026 Son Başvuru Tarihi: 04.12.2026"
        self.assertEqual("2026-12-04",h2.application_deadline(text,"son başvuru"))

    def test_null_published_at_is_preserved_only_when_source_opts_in(self):
        source={
            "itemId":"tga-test","institution":"TGA","title":"TGA personel alımı — 13 pozisyon",
            "category":"Kamu","contentType":"job","url":"https://tga.gov.tr/kariyer",
            "publicationMode":"labelled","allowNullPublishedAt":True,
            "deadlineContext":"başvuru tarihleri","expectedText":["13 pozisyon","04.12.2026"],
            "positionCount":13,
            "howToApply":{"method":"TGA kariyer sayfası","url":"https://tga.gov.tr/kariyer","instructions":"Çevrimiçi."},
            "conditions":["Pozisyona özel şartlar resmî duyurudadır."]
        }
        page="TGA personel alımı 13 pozisyon. Başvuru Tarihleri 03.11.2026 - 04.12.2026."
        item=h2.parse_official_notice(
            source,page,"","2026-11-25T02:40:00Z","2026-11-25T02:40:00Z",date(2026,11,25)
        )
        self.assertIsNone(item["publishedAt"])
        self.assertEqual(13,item["positionCount"])
        self.assertEqual("2026-12-04",item["deadline"])
        self.assertEqual("Doğrulandı",item["freshnessStatus"])

    def test_zafer_official_url_override_is_used_in_card(self):
        source={
            "itemId":"zafer-test","institution":"Zafer Kalkınma Ajansı","title":"2 uzman",
            "category":"Kamu","contentType":"job",
            "url":"https://www.doka.org.tr/announcement-detail.php?id=1222",
            "officialUrl":"https://www.zafer.gov.tr/duyurular/227",
            "publicationMode":"zafer-page-date","deadlineContext":"başvuru tarihleri",
            "expectedText":["Zafer Kalkınma Ajansı","14.12.2026"],"positionCount":2,
            "howToApply":{"method":"Kariyer Kapısı","url":"https://www.zafer.gov.tr/duyurular/227","instructions":"Resmî duyuru."},
            "conditions":[]
        }
        page="03 Kas 2026 T.C. Zafer Kalkınma Ajansı Personel Alım İlanı"
        pdf="2 (iki) Uzman Personel Başvuru Tarihleri 25.11.2026 - 14.12.2026"
        item=h2.parse_official_notice(
            source,page,pdf,"2026-11-25T02:40:00Z","2026-11-25T02:40:00Z",date(2026,11,25)
        )
        self.assertEqual("https://www.zafer.gov.tr/duyurular/227",item["sourceUrl"])
        self.assertEqual(2,item["positionCount"])
        self.assertEqual("2026-11-03",item["publishedAt"])

    def test_h2_page_parser_ignores_script_and_style_text(self):
        page=h2.PageParser()
        page.feed("<html><head><style>.hide { display:none }</style><script>fake text</script></head><body><p>Official notice</p></body></html>")
        self.assertIn("Official notice",page.text)
        self.assertNotIn("fake text",page.text)
        self.assertNotIn("display:none",page.text)



if __name__ == "__main__":
    unittest.main()
