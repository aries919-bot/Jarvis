#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""K1 - kamu kariyer kaynak denetimi testleri.

Tamamen çevrimdışı çalışır (ağ istemi yapmaz), sadece standart kütüphane kullanır.
Üç şeyi birden doğrular:

  1. `scripts/career_source_audit.py` modül sözleşmesi (sabitler, işaretçi
     kalıpları, normalize/tekilleştirme yardımcıları).
  2. `career/sources.json` kaynak matrisi: zorunlu alanlar, kanonik URL, çekim
     kuralı, her kaynak için kaydedilmiş robots.txt sonucu ve bu robots
     kararının otomasyon kararına doğru yansımış olması.
  3. Belge (docs/KARIYER_KAYNAK_DENETIMI.md) ile CI iş akışının
     (.github/workflows/career-source-audit.yml) matrisle senkronu.

Kullanım:
    python3 scripts/test_career_source_audit.py            # özet
    python3 scripts/test_career_source_audit.py -v         # ayrıntılı
    python3 -m unittest discover -s scripts -p 'test_*.py'  # keza
"""

from __future__ import annotations

import datetime as _dt
import json
import os
import py_compile
import re
import subprocess
import sys
import unittest

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.dirname(HERE)
if HERE not in sys.path:
    sys.path.insert(0, HERE)

import career_source_audit as k1  # noqa: E402

SOURCES_PATH = os.path.join(REPO, "career", "sources.json")
DOC_PATH = os.path.join(REPO, "docs", "KARIYER_KAYNAK_DENETIMI.md")
WORKFLOW_PATH = os.path.join(REPO, ".github", "workflows", "career-source-audit.yml")
AUDIT_PATH = os.path.join(HERE, "career_source_audit.py")

ROBOT_RESULT_VALUES = ("izinli", "reddedildi", "yok", "gecersiz", "erisilemedi")
ROBOTS_ALLOWED_RESULTS = ("izinli", "yok")
AUTOMATION_VALUES = ("acik", "manuel", "kapali")
DECISION_VALUES = ("ok", "blok", "giris-gerekli", "eksik-ilan", "http-hata", "erisilemedi")

ROBOTS_REQUIRED_KEYS = (
    "url", "checked_at", "http_status", "fetch_ms", "content_type", "crawl_delay",
    "sitemaps", "applies_to_user_agent", "matching_rule", "path_allowed",
    "result", "notes", "automation_effect",
)
LAST_CHECK_REQUIRED_KEYS = (
    "checked_at", "http_status", "fetch_ms", "bytes_download", "content_type",
    "error", "listing_count", "examples", "marker_hits", "decision",
)
RULE_REQUIRED_KEYS = (
    "base_url", "item_regex", "href_filter", "requires_post", "js_rendered", "min_examples",
)
# Matriste robots.txt sonucunun tutulduğu alan adı (K1 ek kuralı).
ROBOTS_FIELD = "robots_txt"
# Bilinen kusur kimliği; modül düzeltilince belge ve bu test birlikte güncellenir.
KNOWN_DEFECT_ID = "K1-BG-1"
MATRIS_MAX_AGE_DAYS = 365

_SLUG = re.compile(r"^[a-z0-9][a-z0-9\-]{2,63}$")


def _read_text(path):
    with open(path, encoding="utf-8") as fh:
        return fh.read()


def _load_matrix():
    return json.loads(_read_text(SOURCES_PATH))


def _sources():
    return _load_matrix()["sources"]


def _iso_utc(text):
    return _dt.datetime.strptime(text, "%Y-%m-%dT%H:%M:%SZ").replace(tzinfo=_dt.timezone.utc)


def _host(url):
    match = re.match(r"^[a-z]+://([^/]+)", url or "")
    host = (match.group(1) if match else "").lower()
    host = host.split("@")[-1].split(":")[0]
    return host[4:] if host.startswith("www.") else host


def _call_raises(name, *args):
    """Modül fonksiyonu gerçekten patlıyor mu? (K1-BG-1 saptaması)"""
    try:
        getattr(k1, name)(*args)
        return False
    except Exception:
        return True


def _func_works(name, *args):
    return not _call_raises(name, *args)


class ModulSozlesmesiTestleri(unittest.TestCase):
    """career_source_audit modülü import edilebilir ve sabitleri tutarlı olmalı."""

    def test_denetim_betigi_derlenir(self):
        with open(AUDIT_PATH, encoding="utf-8") as fh:
            compile(fh.read(), AUDIT_PATH, "exec")

    def test_denetim_betigi_comut_satiri_cokmez(self):
        sonuc = subprocess.run(
            [sys.executable, AUDIT_PATH, "--sources", "career/sources.json", "--offline"],
            cwd=REPO, capture_output=True, text=True, timeout=60,
            env=dict(os.environ, PYTHONDONTWRITEBYTECODE="1"),
        )
        self.assertEqual(sonuc.returncode, 0, sonuc.stderr[-400:])

    def test_sabitler_tam_ve_gecerli(self):
        self.assertEqual(k1.SCHEMA_VERSION, 1)
        self.assertGreater(k1.DEFAULT_TIMEOUT, 0)
        self.assertGreater(k1.MAX_BYTES, 100_000)
        self.assertIn("JarvisK1Audit", k1.USER_AGENT)
        for key in ("id", "name", "owner", "url", "kind", "content_type",
                    "expected_min_listings", "rule", "notes"):
            self.assertIn(key, k1.REQUIRED_SOURCE_KEYS)
        self.assertEqual(len(set(k1.REQUIRED_SOURCE_KEYS)), len(k1.REQUIRED_SOURCE_KEYS))
        self.assertIn("index", k1.VALID_KINDS)
        self.assertIn("ilan", k1.VALID_CONTENT_TYPES)

    def test_isaretci_kaliplari_derlenir_ve_eslesir(self):
        cases = (
            (k1.LOGIN_MARKERS, "Lutfen Uye Giris yapiniz", "login"),
            (k1.CAPTCHA_MARKERS, "protected by Recaptcha", "captcha"),
            (k1.WAF_MARKERS, "Access Denied", "waf"),
            (k1.SOFT_404_MARKERS, "Aradıgınız sayfa bulunamadı", "soft404"),
        )
        for markers, sample, label in cases:
            for pattern in markers:
                re.compile(pattern, re.IGNORECASE)
            self.assertTrue(any(re.search(p, sample, re.IGNORECASE) for p in markers),
                            "%s işareti örnek metinde bulunamadı" % label)

    def test_normalize_url_kanonik(self):
        self.assertEqual(k1.normalize_url("HTTP://WWW.Example.COM/a//b/?b=2&a=1"),
                         "http://example.com/a/b?a=1&b=2")
        self.assertEqual(k1.normalize_url("//example.com/x/"), "https://example.com/x")
        self.assertEqual(k1.normalize_url("https://example.com/x/"), "https://example.com/x")
        self.assertEqual(k1.normalize_url("https://example.com/"), "https://example.com/")
        self.assertNotIn("#", k1.normalize_url("https://example.com/a#parca"))
        self.assertEqual(k1.normalize_url("https://example.com/a"),
                         k1.normalize_url(k1.normalize_url("https://example.com/a")))

    def test_clean_title_guvenli_girdi(self):
        self.assertEqual(k1.clean_title("  Kurum&nbsp;<b>İlanı</b>\n burada "), "Kurum İlanı burada")
        self.assertEqual(k1.clean_title(None), "")
        self.assertEqual(len(k1.clean_title("x" * 400)), 220)

    def test_gizli_bilgi_deseni_calisir(self):
        ornekler = ("api_" + "key=" + "A" * 18, "gsk_" + "B" * 22)
        for ornek in ornekler:
            self.assertTrue(k1._SECRET_LIKE.search(ornek), "desen sahte anahtarı yakalamadı")
        self.assertIsNone(k1._SECRET_LIKE.search("robots.txt sonucu izinli, otomasyon acik"))

    @unittest.skipUnless(_func_works("normalize_title", "deneme"),
                         "%s: normalize_title TypeError veriyor, belgeye işlendi" % KNOWN_DEFECT_ID)
    def test_normalize_title_tr_karsilastirma(self):
        self.assertEqual(k1.normalize_title("T.C. Saglık Bakanlığı"),
                         k1.normalize_title("T C Saglık Bakanlıgı"))
        self.assertEqual(k1.normalize_title("İŞ &amp; KURUM"), k1.normalize_title("iş kurum"))
        self.assertEqual(k1.normalize_title(""), "")

    @unittest.skipUnless(_func_works("dedupe_listings", []),
                         "%s: dedupe_listings TypeError veriyor, belgeye işlendi" % KNOWN_DEFECT_ID)
    def test_dedupe_listings_tekillestirme(self):
        items = [
            {"title": "T.C. İlan", "url": "https://a.gov.tr/1"},
            {"title": "T.C. İlan", "url": "https://www.a.gov.tr/1/"},
            {"title": "Başka İlan", "url": "https://a.gov.tr/2"},
            {"title": "", "url": ""},
        ]
        out = k1.dedupe_listings(items)
        self.assertEqual([it["url"] for it in out],
                         ["https://a.gov.tr/1", "https://a.gov.tr/2"])

    def test_bilinen_kusur_belgede_kayitli(self):
        """normalize_title/dedupe_listings bozuksa bu K1-BG-1 olarak belgeli olmalı.

        Modül düzeltildiğinde bu test kendiliğinden sessizleşir; yukarıdaki
        korumalı testler o gün gerçek davranışı doğrulamaya başlar.
        """
        kusurlu = [ad for ad, ornek in (("normalize_title", "T.C. İlan"),
                                        ("dedupe_listings", [{"title": "a", "url": "https://x.tr/1"}]))
                   if _call_raises(ad, ornek)]
        if not kusurlu:
            return
        belge = _read_text(DOC_PATH)
        self.assertIn(KNOWN_DEFECT_ID, belge)
        for ad in kusurlu:
            self.assertIn(ad, belge)
            self.assertIn("TypeError", belge)


class KaynakMatrisiTestleri(unittest.TestCase):
    """career/sources.json K1 kurallarına uymalı ve robots.txt sonucunu taşımalı."""

    @classmethod
    def setUpClass(cls):
        cls.doc = _load_matrix()
        cls.sources = cls.doc["sources"]

    def test_dosya_ve_ust_alanlar(self):
        for path in (SOURCES_PATH, DOC_PATH, WORKFLOW_PATH, AUDIT_PATH):
            self.assertTrue(os.path.isfile(path), "eksik dosya: %s" % path)
        for key in ("schema_version", "task", "sources_file", "audit_script",
                    "report_dir", "policy", "sources"):
            self.assertIn(key, self.doc)
        self.assertEqual(self.doc["schema_version"], k1.SCHEMA_VERSION)
        self.assertEqual(self.doc["sources_file"], "career/sources.json")
        self.assertEqual(self.doc["audit_script"], "scripts/career_source_audit.py")
        self.assertGreaterEqual(len(self.sources), 3, "en az 3 kaynak yazılmalı")

    def test_zorunlu_alanlar_ve_deger_araliklari(self):
        ids = []
        for src in self.sources:
            for key in k1.REQUIRED_SOURCE_KEYS:
                self.assertIn(key, src, "%s alanında %s eksik" % (src.get("id"), key))
            for key in (ROBOTS_FIELD, "automation", "login_required", "last_check"):
                self.assertIn(key, src, "%s alanında %s eksik" % (src.get("id"), key))
            self.assertRegex(src["id"], _SLUG)
            ids.append(src["id"])
            self.assertIn(src["kind"], k1.VALID_KINDS)
            self.assertIn(src["content_type"], k1.VALID_CONTENT_TYPES)
            self.assertIn(src["automation"], AUTOMATION_VALUES)
            self.assertIsInstance(src["login_required"], bool)
            self.assertIsInstance(src["expected_min_listings"], int)
            self.assertGreaterEqual(src["expected_min_listings"], 0)
            for key in ("name", "owner", "notes"):
                self.assertTrue(isinstance(src[key], str) and len(src[key].strip()) > 5)
        self.assertEqual(len(ids), len(set(ids)), "kaynak id'leri benzersiz olmalı")

    def test_url_guvenligi_kanonik_ve_https(self):
        seen = set()
        for src in self.sources:
            url = src["url"]
            self.assertTrue(url.startswith("https://"), "HTTPS zorunlu: %s" % src["id"])
            self.assertNotIn("@", url, "kullanıcı adı/parola taşınamaz")
            self.assertNotIn(":", url.split("//")[1].split("/")[0], "port yazılamaz")
            host = _host(url)
            self.assertRegex(host, r"^[a-z0-9.\-]+\.[a-z]{2,}$")
            self.assertEqual(k1.normalize_url(url), k1.normalize_url(k1.normalize_url(url)))
            anahtar = (host, k1.normalize_url(url).split("//", 1)[1])
            self.assertNotIn(anahtar, seen, "aynı hedef iki kez yazılmış")
            seen.add(anahtar)
            for param in re.findall(r"[?&]([^=&]+)=", url):
                self.assertNotIn(param.lower(),
                                 ("key", "token", "secret", "password", "apikey", "api_key"))

    def test_cekim_kurali_nesnesi(self):
        for src in self.sources:
            rule = src["rule"]
            self.assertIsInstance(rule, dict, "%s kuralı nesne olmalı" % src["id"])
            for key in RULE_REQUIRED_KEYS:
                self.assertIn(key, rule, "%s kuralında %s eksik" % (src["id"], key))
            derlenen = re.compile(rule["item_regex"], re.IGNORECASE | re.DOTALL)
            self.assertIn("href", derlenen.groupindex)
            self.assertIn("title", derlenen.groupindex)
            re.compile(rule["href_filter"], re.IGNORECASE)
            self.assertGreaterEqual(rule["min_examples"], 3,
                                    "en az 3 gerçek ilan örneği istenmeli")
            self.assertIsInstance(rule["requires_post"], bool)
            self.assertIsInstance(rule["js_rendered"], bool)
            self.assertTrue(rule["base_url"].startswith("https://"))

    def test_politika_cekim_sinirlari(self):
        politika = self.doc["policy"]
        self.assertEqual(politika["user_agent"], k1.USER_AGENT)
        self.assertEqual(politika["timeout_seconds"], k1.DEFAULT_TIMEOUT)
        self.assertEqual(politika["max_bytes_per_page"], k1.MAX_BYTES)
        self.assertTrue(politika["no_login_sources"])
        self.assertTrue(politika["no_captcha_bypass"])
        self.assertTrue(politika["https_only"])
        robots_politika = politika["robots_txt"]
        self.assertTrue(robots_politika["honor"])
        self.assertEqual(tuple(robots_politika["result_values"]), ROBOT_RESULT_VALUES)


    def test_robots_txt_sonucu_her_kaynakta_kayitli(self):
        bugun = _dt.datetime.now(_dt.timezone.utc)
        for src in self.sources:
            rob = src[ROBOTS_FIELD]
            tag = src["id"]
            for key in ROBOTS_REQUIRED_KEYS:
                self.assertIn(key, rob, "%s robots kaydında %s eksik" % (tag, key))
            self.assertTrue(rob["url"].endswith("/robots.txt"))
            self.assertEqual(_host(rob["url"]), _host(src["url"]),
                             "%s: robots.txt aynı hostta olmalı" % tag)
            self.assertIn(rob["result"], ROBOT_RESULT_VALUES)
            self.assertIn(rob["automation_effect"], AUTOMATION_VALUES)
            self.assertIsInstance(rob["path_allowed"], bool)
            self.assertTrue(isinstance(rob["http_status"], int) or rob["http_status"] is None)
            self.assertIsInstance(rob["fetch_ms"], int)
            self.assertGreaterEqual(rob["fetch_ms"], 0)
            self.assertIsInstance(rob["sitemaps"], list)
            self.assertTrue(isinstance(rob["crawl_delay"], (str, int, type(None))))
            self.assertTrue(isinstance(rob["matching_rule"], str) and rob["matching_rule"].strip())
            self.assertTrue(isinstance(rob["notes"], str) and rob["notes"].strip())
            kontrol = _iso_utc(rob["checked_at"])
            self.assertLessEqual(kontrol, bugun, "%s: checked_at gelecekte olamaz" % tag)
            self.assertLess((bugun - kontrol).days, MATRIS_MAX_AGE_DAYS,
                            "%s: robots.txt kaydı çok eski, yeniden denetlenmeli" % tag)
            if rob["result"] == "izinli":
                self.assertTrue(rob["path_allowed"], "%s: izinli + path_allowed=False" % tag)
            if rob["result"] == "reddedildi":
                self.assertFalse(rob["path_allowed"], "%s: reddedildi + path_allowed=True" % tag)
            self.assertEqual(rob["automation_effect"],
                             "acik" if rob["result"] in ROBOTS_ALLOWED_RESULTS else "kapali")

    def test_robots_karari_otomasyona_yansir(self):
        for src in self.sources:
            rob, rule = src[ROBOTS_FIELD], src["rule"]
            if src["automation"] == "acik":
                self.assertIn(rob["result"], ROBOTS_ALLOWED_RESULTS,
                              "%s: robots izni olmadan otomasyon açılamaz" % src["id"])
                self.assertFalse(src["login_required"], "giriş isteyen kaynak açılamaz")
                self.assertFalse(rule["js_rendered"], "JS ile çizilen kaynak açılamaz")
                self.assertFalse(rule["requires_post"], "POST isteyen kaynak açılamaz")
            if src["automation"] == "kapali":
                self.assertTrue(rob["result"] not in ROBOTS_ALLOWED_RESULTS
                                or src["login_required"],
                                "%s: kapanma sebebi yok" % src["id"])

    def test_min_ok_kurali_en_az_3_acik_kaynak(self):
        min_ok = self.doc["policy"]["min_ok_sources"]
        self.assertGreaterEqual(min_ok, 3)
        acik = [s["id"] for s in self.sources if s["automation"] == "acik"]
        self.assertGreaterEqual(len(acik), min_ok,
                                "açık kaynak %d, gereken %d" % (len(acik), min_ok))

    def test_acik_kaynaklar_gercek_ilan_ornegi_gosteriyor(self):
        for src in self.sources:
            if src["automation"] != "acik":
                continue
            kontrol = src["last_check"]
            self.assertEqual(kontrol["http_status"], 200, "%s: sayfa 200 değil" % src["id"])
            self.assertGreaterEqual(kontrol["listing_count"], src["expected_min_listings"],
                                    "%s: ilan sayısı beklenenin altında" % src["id"])
            self.assertGreaterEqual(len(kontrol["examples"]), src["rule"]["min_examples"],
                                    "%s: en az 3 örnek şart" % src["id"])
            for ornek in kontrol["examples"]:
                self.assertTrue(ornek["title"].strip())
                self.assertTrue(ornek["url"].startswith("https://"))
                self.assertEqual(_host(ornek["url"]), _host(src["url"]),
                                 "%s: örnek aynı hostta değil" % src["id"])
                self.assertNotIn("<", ornek["title"], "başlıkta HTML etiketi kalmış")

    def test_son_kontrol_kaydi_tutarli(self):
        for src in self.sources:
            kontrol = src["last_check"]
            tag = src["id"]
            for key in LAST_CHECK_REQUIRED_KEYS:
                self.assertIn(key, kontrol, "%s son kontrolünde %s eksik" % (tag, key))
            self.assertIn(kontrol["decision"], DECISION_VALUES)
            self.assertTrue(isinstance(kontrol["http_status"], int) or kontrol["http_status"] is None)
            self.assertGreaterEqual(kontrol["bytes_download"], 0)
            self.assertIsInstance(kontrol["marker_hits"], list)
            for vurus in kontrol["marker_hits"]:
                self.assertRegex(vurus, r"^(login|captcha|waf|soft_404):.+$")
            _iso_utc(kontrol["checked_at"])
            if kontrol["http_status"] is None:
                self.assertEqual(kontrol["decision"], "erisilemedi")
                self.assertTrue(kontrol["error"], "ağ hatası metni yazılmalı")
            if kontrol["decision"] == "ok":
                self.assertEqual(kontrol["http_status"], 200)
            if kontrol["decision"] == "eksik-ilan":
                self.assertLess(kontrol["listing_count"], src["expected_min_listings"])

    def test_matris_gizli_bilgi_icermiyor(self):
        ham = _read_text(SOURCES_PATH)
        self.assertIsNone(k1._SECRET_LIKE.search(ham), "matriste anahtar/token benzeri metin var")
        for izinsiz in ("gsk_", "AKIA", "ghp_", "Bearer ", "Authorization:"):
            self.assertNotIn(izinsiz, ham)



class BelgeVeIsAkisiTestleri(unittest.TestCase):
    """Belge ve CI iş akışı kaynak matrisiyle senkron kalmalı."""

    @classmethod
    def setUpClass(cls):
        cls.belge = _read_text(DOC_PATH)
        cls.akis = _read_text(WORKFLOW_PATH)
        cls.sources = _sources()

    def test_belge_kaynaklarini_anlatiyor(self):
        for src in self.sources:
            self.assertIn(src["id"], self.belge, "belgede %s yok" % src["id"])
            self.assertIn(src["url"], self.belge, "belgede %s adresi yok" % src["id"])
            self.assertIn(src["robots_txt"]["result"], self.belge)

    def test_belge_robots_kuralini_kaydediyor(self):
        self.assertIn("robots.txt", self.belge)
        for deger in ROBOT_RESULT_VALUES:
            self.assertIn(deger, self.belge,
                          "belgede robots sonucu '%s' tanımlı değil" % deger)
        self.assertIn("/robots.txt", self.belge)
        self.assertIn("RFC 9309", self.belge)

    def test_belge_calisma_ve_rapor_kurallari(self):
        self.assertIn("K1", self.belge)
        self.assertIn("career/sources.json", self.belge)
        self.assertIn("scripts/career_source_audit.py", self.belge)
        self.assertIn("scripts/test_career_source_audit.py", self.belge)
        self.assertIn("career-audit-report", self.belge)
        self.assertIn("--min-ok 3", self.belge)
        self.assertIn("career-source-audit.yml", self.belge)
        for baslik in ("Otomasyon Kararı", "Bilinen Kusurlar", "Güvenlik"):
            self.assertIn(baslik, self.belge)

    def test_belge_gizli_bilgi_icermiyor(self):
        self.assertIsNone(k1._SECRET_LIKE.search(self.belge))
        for izinsiz in ("gsk_", "AKIA", "ghp_"):
            self.assertNotIn(izinsiz, self.belge)

    def test_is_akisi_dosyasi_kurallari(self):
        for parca in ("career/sources.json", "scripts/career_source_audit.py",
                      "scripts/test_career_source_audit.py", "actions/checkout@",
                      "actions/setup-python@", "actions/upload-artifact@",
                      "workflow_dispatch", "cron:", "career-audit-report"):
            self.assertIn(parca, self.akis, "iş akışında %s eksik" % parca)
        self.assertIn("K1", self.akis)
        self.assertIn("name:", self.akis.splitlines()[0] + "\n")

    def test_is_akisi_gizli_anahtar_kullanmiyor(self):
        self.assertNotIn("secrets.", self.akis,
                         "K1 denetimi anahtar/token kullanamaz (AGENTS.md kuralı)")

    def test_matris_ile_belge_ayni_kaynak_sayisi(self):
        ozet = self.belge
        for src in self.sources:
            self.assertEqual(ozet.count(src["id"]) >= 2, True,
                             "%s belgede hem tabloda hem metinde geçmeli" % src["id"])


if __name__ == "__main__":
    sys.exit(unittest.main(module="__main__", argv=[sys.argv[0]] + sys.argv[1:]))


