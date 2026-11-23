#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""K1 - ISKUR ilan sayfasinin tarayici-render yoklamasi (tek sayfa acilisi).

Akis:
  1. robots.txt indirilir; ONCE sayfa yolu denetlenir. Sayfa yolu kapaliysa
     tarayici hic acilmaz (cikis kodu 2).
  2. Chromium headless ile sayfa TEK kez acilir ve `networkidle` beklenir.
     Tiklama / giris / form doldurma YAPILMAZ; sayfa salt okunur.
  3. Rapor (stdout):
       (a) yuklenme sirasinda giden tum XHR/fetch adresleri + HTTP kodu ve
           her birinin robots Disallow yoluna girip girmedigi,
       (b) render sonrasi ilan benzeri satir sayisi + ilk 10 satirin metni,
       (c) dd.mm.yyyy tarih sayisi.
  4. Render edilmis HTML `render_ciktisi.html` olarak kaydedilir.

Cikis kodlari: 0 tamam | 2 robots engeli | 3 tarayici/render hatasi.
Anahtar/token kullanilmaz.

Sinirlar:
  * robots kurali yalnizca secilen User-agent grubu icin cozulur (RFC 9309);
    XHR/fetch adresleri icin host basina robots.txt TEK kez indirilir (en fazla
    6 host, fazlasi "bilinmiyor" olarak raporlanir).
  * Satir sayimi yapidan yapilir: gizli saymak icin hidden / aria-hidden /
    satir ici display:none yeterlidir, CSS sinifi ile gizlenenler sayilabilir.

Kullanim:
  python3 scripts/render_probe.py                        # canli render
  python3 scripts/render_probe.py --html kayitli.html    # tarayicisiz cozum
  python3 scripts/render_probe.py --url U --out render_ciktisi.html
"""

from __future__ import annotations

import argparse
import importlib.util
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
from html.parser import HTMLParser

HEDEF = "https://iskur.gov.tr/ilanlar/kurumdisi-kamu-isci-alim-ilanlari/"
UA = ("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
      "Chrome/126.0.0.0 Safari/537.36")
TARIH_RE = re.compile(r"\b\d{2}\.\d{2}\.\d{4}\b")
ROBOTS_MAX_BAYT = 300_000
ROBOTS_EN_FAZLA_HOST = 6
# Icerik satiri sayilmayacak kapsayicilar (iclerindeki metin/agac atlanir).
ATLA_ETIKET = {"script", "style", "noscript", "template", "svg", "iframe"}
KAPALI_ETIKET = {"area", "base", "br", "col", "embed", "hr", "img", "input",
                 "link", "meta", "param", "source", "track", "wbr"}
MENU_ETIKET = {"nav", "header", "footer", "aside", "select", "option"}
# Menu / erisilebilirlik paneli / sehir listesi / harita gibi kapsayici adlari.
MENU_IPUCU = ("menu", "nav", "footer", "sidebar", "cookie", "banner",
              "breadcrumb", "accessibility", "ac-panel", "ac-box", "speak",
              "reading", "skip", "social", "search", "city", "harita", "map",
              "slider", "swiper", "login", "giris", "language")


def kes(sira, uzunluk=170):
    sira = re.sub(r"\s+", " ", str(sira or "")).strip()
    return sira[:uzunluk] + ("..." if len(sira) > uzunluk else "")


# --- robots.txt (RFC 9309: en uzun eslesen desen kazanir, esitlikte Allow) ---
def robots_gruplari(metin):
    """robots metnini gruplara ayirir: [{ua:[...], kural:[(tur,desen)]}]"""
    gruplar, g = [], None
    for satir in (metin or "").splitlines():
        satir = satir.split("#", 1)[0].strip()
        if not satir or ":" not in satir:
            continue
        alan, _, deger = satir.partition(":")
        alan, deger = alan.strip().lower(), deger.strip()
        if alan == "user-agent":
            if g is None or g["kural"]:
                g = {"ua": [], "kural": []}
                gruplar.append(g)
            g["ua"].append(deger.lower())
        elif alan in ("allow", "disallow") and g is not None:
            g["kural"].append((alan, deger))
    return gruplar


def ua_uyar(desen, ua):
    desen = (desen or "").strip().lower()
    if desen in ("", "*"):
        return desen == "*"
    desenli = ".*".join(re.escape(x) for x in desen.split("*"))
    return re.search(desenli, ua.lower()) is not None


def grup_sec(gruplar, ua):
    """UA'ya uyan en ozel grup (en uzun token); yoksa '*' grubu."""
    secili, en_uzun = None, -1
    for g in gruplar:
        for desen in g["ua"]:
            if ua_uyar(desen, ua):
                uzunluk = 0 if desen == "*" else len(desen)
                if uzunluk > en_uzun:
                    secili, en_uzun = g, uzunluk
    return secili


def yol_uyar(desen, hedef):
    desen = urllib.parse.unquote(desen or "")
    son = desen.endswith("$")
    cekirdek = desen[:-1] if son else desen
    if not cekirdek:
        return True
    parcalar = [re.escape(x) for x in cekirdek.split("*")]
    rex = ("^" if cekirdek.startswith("/") else "^.*") + ".*".join(parcalar)
    return re.match(rex + ("$" if son else ""), hedef) is not None


def hedef_yol(url):
    p = urllib.parse.urlsplit(url)
    return (p.path or "/") + (("?" + p.query) if p.query else "")


def yol_karari(grup, hedef):
    """(izinli_mi, eslesen_desen). Bos 'Disallow:' tum yollari serbest birakir."""
    if grup is None:
        return True, ""
    en_iyi = None
    for tur, desen in grup["kural"]:
        if not desen:
            if en_iyi is None:
                en_iyi = ("allow", "")
            continue
        if not yol_uyar(desen, hedef):
            continue
        if (en_iyi is None or len(desen) > len(en_iyi[1])
                or (len(desen) == len(en_iyi[1]) and tur == "allow")):
            en_iyi = (tur, desen)
    if en_iyi is None:
        return True, ""
    return en_iyi[0] == "allow", en_iyi[1]


class Robots(object):
    """Host basina robots.txt'i TEK kez indirir ve verilen URL icin karar verir."""

    def __init__(self, ua=UA):
        self.ua = ua
        self.hostlar = {}

    def host(self, url):
        p = urllib.parse.urlsplit(url)
        anahtar = "%s://%s" % (p.scheme, p.netloc)
        if anahtar not in self.hostlar:
            if len(self.hostlar) >= ROBOTS_EN_FAZLA_HOST:
                self.hostlar[anahtar] = {"durum": None, "grup": None, "bayt": 0,
                                         "kaynak": anahtar + "/robots.txt",
                                         "not": "host limiti asildi"}
            else:
                self.hostlar[anahtar] = self._getir(anahtar)
        return self.hostlar[anahtar]

    def _getir(self, anahtar):
        kaynak = anahtar + "/robots.txt"
        kayit = {"durum": None, "grup": None, "bayt": 0, "kaynak": kaynak, "not": ""}
        istek = urllib.request.Request(kaynak, headers={
            "User-Agent": self.ua, "Accept": "text/plain,*/*"})
        try:
            with urllib.request.urlopen(istek, timeout=25) as yanit:
                ham = yanit.read(ROBOTS_MAX_BAYT)
                kayit["durum"], kayit["bayt"] = yanit.status, len(ham)
                kayit["grup"] = grup_sec(robots_gruplari(
                    ham.decode("utf-8", "replace")), self.ua)
        except urllib.error.HTTPError as hata:
            kayit["durum"], kayit["not"] = hata.code, "HTTP %s" % hata.code
        except Exception as hata:
            kayit["not"] = (type(hata).__name__ + ": " + str(hata))[:70]
        return kayit

    def karar(self, url):
        """(izinli | None, eslesen_desen, not). None = degerlendirilemedi."""
        kayit = self.host(url)
        if kayit["durum"] is None:
            return None, "", kayit["not"] or "robots okunamadi"
        if kayit["durum"] != 200:
            return True, "", "robots HTTP %s" % kayit["durum"]
        if kayit["grup"] is None:
            return True, "", "uygun User-agent grubu yok"
        izinli, desen = yol_karari(kayit["grup"], hedef_yol(url))
        return izinli, desen, ""


# --- render sonrasi HTML agaci (standart kutuphane, toleransli) -------------
class Dugum(object):
    __slots__ = ("tag", "attrs", "kids", "parent", "soz")

    def __init__(self, tag, attrs=None, parent=None):
        self.tag = tag
        self.attrs = dict(attrs or {})
        self.kids = []
        self.parent = parent
        self.soz = []

    @property
    def etiketler(self):
        return " ".join([self.attrs.get("id", ""), self.attrs.get("class", ""),
                         self.attrs.get("role", "")]).strip()

    def kisa(self):
        sira = self.tag
        if self.attrs.get("id"):
            sira += "#" + self.attrs["id"][:28]
        elif self.attrs.get("class"):
            sira += "." + self.attrs["class"].split()[0][:28]
        return sira


class Agac(HTMLParser):
    """HTML'i ebeveyn/evlat baglantili agaca cevirir; menu sayilmayan kisimleri
    sonradan elemek icin tag + id/class bilgisi tasinir."""

    def __init__(self):
        HTMLParser.__init__(self, convert_charrefs=True)
        self.kok = Dugum("#root")
        self.yigin = [self.kok]
        self.dugumler = []
        self.atla = 0

    def _ekle(self, tag, attrs):
        dugum = Dugum(tag, attrs, self.yigin[-1])
        self.yigin[-1].kids.append(dugum)
        self.dugumler.append(dugum)
        return dugum

    def handle_starttag(self, tag, attrs):
        dugum = self._ekle(tag, attrs)
        if tag in ATLA_ETIKET:
            self.atla += 1
        if tag not in KAPALI_ETIKET:
            self.yigin.append(dugum)

    def handle_startendtag(self, tag, attrs):
        self._ekle(tag, attrs)

    def handle_endtag(self, tag):
        for i in range(len(self.yigin) - 1, 0, -1):
            if self.yigin[i].tag == tag:
                if tag in ATLA_ETIKET:
                    self.atla -= 1
                del self.yigin[i:]
                return
        # eslesmeyen kapanis etiketi: yok say (gercek sayfalar bozuk olabiliyor)

    def handle_data(self, veri):
        if not self.atla:
            self.yigin[-1].soz.append(veri)


def metin(dugum, sinir=600):
    parcalar = []
    uzunluk = [0]

    def dolas(u):
        if uzunluk[0] > sinir or u.tag in ATLA_ETIKET:
            return
        for sira in u.soz:
            parcalar.append(sira)
            uzunluk[0] += len(sira)
        for c in u.kids:
            dolas(c)

    dolas(dugum)
    return re.sub(r"\s+", " ", " ".join(parcalar)).strip()[:sinir]


def gorunmez(dugum):
    if dugum.attrs.get("hidden") is not None:
        return True
    if str(dugum.attrs.get("aria-hidden", "")).lower() == "true":
        return True
    return bool(re.search(r"display\s*:\s*none", dugum.attrs.get("style", ""), re.I))


def menu_icinde(dugum):
    """Menu / header / footer / sehir listesi / harita gibi kapsayicilar satir sayilmaz."""
    u, adim = dugum, 0
    while u is not None and u.tag != "#root" and adim < 12:
        if u.tag in MENU_ETIKET:
            return True
        etiketler = u.etiketler.lower()
        if etiketler and any(ipucu in etiketler for ipucu in MENU_IPUCU):
            return True
        u, adim = u.parent, adim + 1
    return False


def ilan_filtresi():
    """Menu basliklarini eleyen mevcut K1 filtresini yeniden kullanir."""
    yol = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                       "career_source_audit.py")
    try:
        spec = importlib.util.spec_from_file_location("k1_denetim", yol)
        mod = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(mod)
        return mod.is_real_listing, "career_source_audit.is_real_listing"
    except Exception as hata:
        basit = lambda t: len(t) >= 12 and len(t.split()) >= 3
        return basit, "basit uzunluk filtresi (%s)" % type(hata).__name__


def aday_kapsayicilar(dugumler, filtre, en_az=3):
    """Ayni ebeveyn altinda tekrarlanan benzer cocuk gruplarini skorlar.
    Skor: (ilan suzgesinden gecen, uzunlugu uygun, ham cocuk sayisi)."""
    adaylar = []
    for ebeveyn in dugumler:
        if (ebeveyn.tag in ATLA_ETIKET
                or ebeveyn.tag in ("html", "body", "#root", "head")
                or menu_icinde(ebeveyn) or gorunmez(ebeveyn)):
            continue
        gruplar = {}
        for cocuk in ebeveyn.kids:
            if (cocuk.tag in ATLA_ETIKET or gorunmez(cocuk)
                    or cocuk.tag in ("link", "meta", "title", "base")
                    or menu_icinde(cocuk)):
                continue
            gruplar.setdefault((cocuk.tag, cocuk.attrs.get("class", "")), []).append(cocuk)
        for (tag, sinif), cocuklar in gruplar.items():
            if len(cocuklar) < en_az:
                continue
            yazi = [metin(c, 400) for c in cocuklar]
            uygun = [t for t in yazi if 12 <= len(t) <= 500]
            ilan = [t for t in uygun if filtre(t)]
            kisa_yol = ebeveyn.kisa() + " > " + tag
            adaylar.append((len(ilan), len(uygun), len(cocuklar), kisa_yol, ilan, uygun))
    adaylar.sort(key=lambda a: (a[0], a[1], a[2]), reverse=True)
    return adaylar


def satir_raporu(dugumler):
    filtre, filtre_adi = ilan_filtresi()
    adaylar = aday_kapsayicilar(dugumler, filtre)
    sayim = {}
    for dugum in dugumler:
        sayim[dugum.tag] = sayim.get(dugum.tag, 0) + 1
    rapor = {"kapsayici": "-", "ham": 0, "uygun": 0, "ilan": 0, "satirlar": [],
             "filtre": filtre_adi, "dom": sayim, "dugum": len(dugumler),
             "yedek": []}
    if adaylar:
        ilan, uygun, ham, yol, ilan_liste, uygun_liste = adaylar[0]
        rapor.update(kapsayici=yol, ham=ham, uygun=uygun, ilan=ilan,
                     satirlar=ilan_liste or uygun_liste)
        for skor in adaylar[1:4]:
            rapor["yedek"].append("%s (ilan %d, uygun %d)" % (skor[3], skor[0], skor[1]))
    return rapor


# --- canli render -----------------------------------------------------------
def render(url, settle_ms):
    """Sayfayi TEK kez acar, networkidle bekler, HTML + XHR kaydi ile doner."""
    from playwright.sync_api import sync_playwright
    kayit = {"xhr": [], "hatalar": [], "js": []}
    with sync_playwright() as oyun:
        tarayici = oyun.chromium.launch(
            headless=True, args=["--no-sandbox", "--disable-dev-shm-usage"])
        sayfa = tarayici.new_page(user_agent=UA, locale="tr-TR",
                                  viewport={"width": 1440, "height": 900})

        def yanit(veri):
            istek = veri.request
            if istek.resource_type in ("xhr", "fetch"):
                kayit["xhr"].append((veri.status, istek.resource_type,
                                     istek.method, veri.url))

        def dusen(istek):
            kayit["hatalar"].append((istek.resource_type, istek.url,
                                     (istek.failure or "")[:70]))

        sayfa.on("response", yanit)
        sayfa.on("requestfailed", dusen)
        sayfa.on("pageerror", lambda hata: kayit["js"].append(str(hata)[:100]))

        durum, bekleme, baslik, gorunur = "?", "networkidle", "", ""
        try:
            ilk = sayfa.goto(url, wait_until="networkidle", timeout=45000)
            durum = ilk.status if ilk else "?"
        except Exception as hata:
            bekleme = "networkidle ulasilamadi (%s)" % type(hata).__name__
        try:
            sayfa.wait_for_timeout(settle_ms)
            gorunur = sayfa.evaluate("() => document.body ? document.body.innerText : ''")
            baslik = sayfa.title()
        except Exception:
            pass
        govde = sayfa.content()
        tarayici.close()
    kayit.update(govde=govde, gorunur=gorunur or "", baslik=baslik,
                 durum=durum, bekleme=bekleme)
    return kayit


def robots_baslik(rob, url):
    kayit = rob.host(url)
    print("## robots.txt")
    print("  kaynak  : %s (HTTP %s, %s bayt)%s" % (
        kayit["kaynak"], kayit["durum"], kayit.get("bayt", 0),
        ("  [" + kayit["not"] + "]") if kayit["not"] else ""))
    grup = kayit.get("grup")
    if grup:
        dis = sum(1 for tur, _ in grup["kural"] if tur == "disallow")
        print("  grup    : %s -> %d kural (%d disallow, %d allow)" % (
            ", ".join(grup["ua"]), len(grup["kural"]), dis,
            len(grup["kural"]) - dis))
    izinli, desen, not_ = rob.karar(url)
    if izinli is None:
        print("  sayfa   : DEGERLENDIRILEMEDI (%s)" % not_)
    else:
        print("  sayfa   : %s%s%s" % ("IZINLI" if izinli else "DISALLOW",
                                      ("  " + desen) if desen else "",
                                      ("  [" + not_ + "]") if not_ else ""))


def xhr_raporu(kayit, rob):
    tekil = {}
    for durum, tip, _yontem, url in kayit["xhr"]:
        satir = tekil.setdefault(url, {"durum": [], "tip": tip, "adet": 0})
        satir["durum"].append(durum)
        satir["adet"] += 1
    print("## (a) XHR / fetch istekleri (%d tekil adres, %d cagri)"
          % (len(tekil), len(kayit["xhr"])))
    for url, satir in sorted(tekil.items(), key=lambda kv: -kv[1]["adet"]):
        izinli, desen, not_ = rob.karar(url)
        karar = ("bilinmiyor:" + (not_ or "?")) if izinli is None else (
            "IZINLI" if izinli else "DISALLOW " + desen)
        kodler = "/".join(str(k) for k in dict.fromkeys(satir["durum"]))
        print("  %-7s %-6s x%-3d %-26s %s" % (
            kodler, satir["tip"], satir["adet"], kes(karar, 24), kes(url, 130)))
    if not tekil:
        print("  0 istek: yuklenme sirasinda XHR/fetch gidmedi")
    for tip, url, hata in kayit["hatalar"][:10]:
        print("  BASARISIZ [%s] %s (%s)" % (tip, kes(url, 110), hata))
    if kayit["js"]:
        print("  JS hatasi: %d -> %s" % (len(kayit["js"]), kes(kayit["js"][0], 110)))


def main(argv=None):
    ap = argparse.ArgumentParser(
        description="ISKUR ilan sayfasinin tarayici-render yoklamasi (robots once)")
    ap.add_argument("--url", default=HEDEF, help="yoklanacak sayfa adresi")
    ap.add_argument("--out", default="render_ciktisi.html",
                    help="render edilmis HTML'in kaydedilecegi dosya")
    ap.add_argument("--html", help="render yerine kayitli HTML dosyasini cozer")
    ap.add_argument("--settle", type=int, default=5000,
                    help="networkidle sonrasinda ek bekleme (ms)")
    a = ap.parse_args(argv)

    print("== ISKUR render yoklamasi ==")
    print("hedef      : %s" % a.url)
    print("user-agent : %s" % UA)

    rob = Robots()
    if a.html:
        with open(a.html, encoding="utf-8", errors="replace") as fh:
            govde = fh.read()
        sonuc = {"xhr": [], "hatalar": [], "js": []}
        gorunur, durum, bekleme, baslik = "", "dosya", "kayitli HTML", ""
        kayit_notu = a.html
        print("\n## robots.txt\n  atlandi: --html modunda ag istegi yapilmadi")
    else:
        print()
        robots_baslik(rob, a.url)
        izinli, desen, not_ = rob.karar(a.url)
        if izinli is False:
            print("\nROBOTS ENGINI: sayfa yolu Disallow (%s) -> tarayici ACILMADI."
                  % desen)
            return 2
        print("\ntarayici: chromium headless, tek sayfa acilisi...")
        try:
            sonuc = render(a.url, a.settle)
        except ImportError as hata:
            print("PLAYWRIGHT KURULU DEGIL: %s" % hata)
            return 3
        except Exception as hata:
            print("RENDER HATASI: %s: %s" % (type(hata).__name__, str(hata)[:200]))
            return 3
        govde = sonuc["govde"]
        gorunur, durum = sonuc["gorunur"], sonuc["durum"]
        bekleme, baslik = sonuc["bekleme"], sonuc["baslik"]
        try:
            with open(a.out, "w", encoding="utf-8") as fh:
                fh.write(govde)
            kayit_notu = "%s (%d bayt)" % (a.out, os.path.getsize(a.out))
        except OSError as hata:
            kayit_notu = "YAZILAMADI: %s" % str(hata)[:120]

    print()
    xhr_raporu(sonuc, rob)

    agac = Agac()
    agac.feed(govde)
    agac.close()
    rapor = satir_raporu(agac.dugumler)
    sayim = rapor["dom"]
    if not gorunur:
        gorunur, etik = metin(agac.kok, 2_000_000), "DOM metni"
    else:
        etik = "gorunur metin"
    print("\n## (b) ilan benzeri satirlar (render sonrasi)")
    print("  aday kapsayici: %s" % rapor["kapsayici"])
    print("  ilan suzgeci  : %s" % rapor["filtre"])
    print("  ilan benzeri  : %d satir (ham %d, uzunlugu uygun %d)"
          % (rapor["ilan"], rapor["ham"], rapor["uygun"]))
    if rapor["ilan"] == 0 and rapor["uygun"]:
        print("  NOT: satirlar ilan suzgesinden gecmedi; uzunlugu uygun satirlar listelenir.")
    if rapor["yedek"]:
        print("  diger adaylar : %s" % "; ".join(rapor["yedek"]))
    print("  DOM           : %d dugum | table %d | tr %d | li %d | a %d" % (
        rapor["dugum"], sayim.get("table", 0), sayim.get("tr", 0),
        sayim.get("li", 0), sayim.get("a", 0)))
    ilk10 = rapor["satirlar"][:10]
    print("  ilk %d satir:" % len(ilk10))
    for sira, yazi in enumerate(ilk10, 1):
        print("   %2d. %s" % (sira, kes(yazi, 160)))

    print("\n## (c) dd.mm.yyyy tarih sayisi")
    print("  %s       : %d" % (etik, len(TARIH_RE.findall(gorunur))))
    print("  render HTML    : %d" % len(TARIH_RE.findall(govde)))

    print("\n## ozet")
    print("  sayfa HTTP     : %s" % durum)
    print("  bekleme        : %s (ek %d ms settle)" % (bekleme, a.settle))
    print("  sayfa basligi  : %s" % kes(baslik, 110))
    print("  HTML kaydi     : %s" % kayit_notu)
    return 0


if __name__ == "__main__":
    sys.exit(main())
