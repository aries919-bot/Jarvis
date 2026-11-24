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
        for status,body,error in ((403,b"blocked",None),(500,b"failed",None),(200,b"",None),(None,b"","TIMEOUT: page")):
            calls=[]
            def requester(url,limit,status=status,body=body,error=error):
                calls.append(url)
                if url.endswith("/robots.txt"):
                    return ok(b"User-agent: *\nAllow: /")
                return h2.FetchResponse(status,Message(),body,error)
            with self.assertRaises(h2.SourceError):
                h2.fetch_public_url("https://www.botas.gov.tr/notice",requester)
            self.assertEqual(2,len(calls))

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

    def test_config_is_limited_to_two_measured_official_sources(self):
        cfg=json.loads(h2.CONFIG.read_text(encoding="utf-8"))
        self.assertEqual({"yhgm","botas"},{s["reader"] for s in cfg["sources"]})
        self.assertEqual(2,len(cfg["sources"]))


if __name__ == "__main__":
    unittest.main()
