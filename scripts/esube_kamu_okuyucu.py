#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""K1-E1 - e-sube (ISKUR) kamu isci ilan okuyucusu.

Akis (ASP.NET WebForms, giris/captcha yok):
  1) robots.txt okunur; hedef yol kapaliysa cikis kodu 3 ile cikilir.
  2) Arama sayfasi GET edilir, form sozlugu kurulur (hidden + text + isaretli
     radyo + select varsayilanlari).
  3) Isyeri turu "kamuRadio" secilir, __EVENTTARGET arama komutuna ayarlanir
     ve POST edilir.
  4) Her yanittaki yeni __VIEWSTATE / __EVENTVALIDATION / __VIEWSTATEGENERATOR
     degerleri alinir; sonraki sayfa btnNext ile POST edilerek ilerlenir.
  5) btnNext aspNetDisabled ise ya da yeni ilan no gelmiyorsa durulur.

Kural: tek oturum (cookie jar), istekler arasi 2 sn bekleme, en fazla 40 sayfa,
tarayici taklidi yapmayan User-Agent, sadece standart kutuphane.
Anahtar/token/credential kullanilmaz.

Cikti: career/esube_kamu_ilanlari.json
Cikis kodlari: 0 tamam, 2 ag/sayfa hatasi, 3 robots izni yok.

Kullanim:
  python3 scripts/esube_kamu_okuyucu.py
  python3 scripts/esube_kamu_okuyucu.py --max-sayfa 3 --out /tmp/esube.json
"""

from __future__ import annotations

import argparse
import datetime as _dt
import html
import http.cookiejar
import json
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
import urllib.robotparser

ARANA = "https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx"
KAYNAK = "esube.iskur.gov.tr"
USER_AGENT = "AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)"
BEKLEME = 2.0
MAKS_SAYFA = 40
ZAMAN_ASIMI = 45.0
CIKIS_ROBOTS = 3
CIKIS_AG = 2

ARAMA_HEDEF = "ctl04$ctlAcikIsPageCommand$CommandItem_Search"
SAYFA_HEDEF = "ctl04$ctlDataPagerDetay$btnNext"
KAMU_RADYO = "ctl04$IsyeriTuruRadios"
KAMU_DEGER = "kamuRadio"

# ilan no: 0000 ile baslayan 11 hane
ILAN_NO_RE = re.compile(r"\b(0000\d{7})\b")
TARIH_RE = re.compile(r"\b(\d{2}\.\d{2}\.\d{4})\b")

# Turkce karakterler ASCII'ye katlanir; once katlama sonra kucuk harf yapilir,
# cunku "İ".lower() iki karakter uretir ve indeksleri kaydirir. Katlama 1:1
# oldugu icin katlanmis metindeki indeksler orijinal metinde de gecerlidir.
_KATLA = str.maketrans({
    "Ç": "c", "Ğ": "g", "İ": "i", "Ö": "o", "Ş": "s", "Ü": "u",
    "I": "i", "Â": "a", "Î": "i", "Û": "u",
    "ç": "c", "ğ": "g", "ı": "i", "ö": "o", "ş": "s", "ü": "u",
    "â": "a", "î": "i", "û": "u",
})


def katla(metin):
    """Karsilastirmaya uygun, ASCII'ye katlanmis kucuk harfli metin."""
    return (metin or "").translate(_KATLA).lower()


# --- tek satirlik ilan ayristirmasi -----------------------------------------
# Ornek satir (HTML etiketleri dusurulmus duz metin):
#  Diger Tahmil-Tahliye ve Ilgili Yuk Tasima Isciler MILLI SAVUNMA BAKANLIGI
#  PERSONEL GENEL MUDURLUGU Kamu / Daimi / Tam Zamanli 6 Acik Pozisyon Ulke
#  Geneli Basvuru (Calisma Yeri: MUGLA / MUGLA MERKEZ) 00009905681 27.11.2026
#  3 Gun Kaldi
SATIR_RE = re.compile(
    r"^(?P<kurum>.+?)\s+"
    r"(?P<tur>(?:kamu|ozel)\s*/\s*[^0-9()/]{2,40}?\s*/\s*[^0-9()/]{2,40}?)"
    r"\s+(?P<poz>\d{1,5})\s+acik\s+pozisyon"
    r"\s+(?P<yeri>.*?)\s+"
    r"(?P<ilan_no>0000\d{7})\s+(?P<tarih>\d{2}\.\d{2}\.\d{4})\b",
    re.IGNORECASE,
)

# Pozisyon ile kurumu ayirmak icin: satirdaki ilk <a> etiketi pozisyon
# basligidir; metne cevirirken sonrasina AYRAC konur. Etiket yoksa (dizgi
# degistiyse) tamami buyuk harfli son sozcuk bloku kurum sayilir; o da olmazsa
# kurum koku sozluklerine bakilir.
AYRAC = "\x01"  # ilk <a> etiketinin sonrasina konur; regex bosluguna benzemez
KUCUK_RE = re.compile(r"[a-zçğıöşü]")
KURUM_KOK_RE = re.compile(
    r"(?:bakanlik|baskanlik|genel mudurluk|mudurluk|belediye|universite"
    r"|hastane|banka|kurum|kurulus|valilik|kaymakamlik|idaresi|tesis"
    r"|munessese|enstitu|kolej|okul|fabrika|ortaklik|isletme)",
    re.IGNORECASE,
)


def pozisyon_kurum_ayir(govde):
    """'Pozisyon Kurum' govdesini iki alana boler (ayrac yoksa sezgisel)."""
    govde = re.sub(r"\s+", " ", govde or "").strip()
    sozcuk = govde.split()
    if len(sozcuk) < 2:
        return govde, ""
    i = len(sozcuk)
    while i > 0 and not KUCUK_RE.search(sozcuk[i - 1]):
        i -= 1
    if 0 < i < len(sozcuk):
        return " ".join(sozcuk[:i]), " ".join(sozcuk[i:])
    m = KURUM_KOK_RE.search(katla(govde))
    if m and m.start() > 0:
        return govde[:m.start()].strip(), govde[m.start():].strip()
    return govde, ""


def satir_ayristir(sheet):
    """Tek satirlik ilan metininden alan sozlugu; cozulmezse None."""
    s = re.sub(r"\s+", " ", (sheet or "").strip())
    pozisyon = ""
    if AYRAC in s:
        pozisyon, s = (x.strip() for x in s.split(AYRAC, 1))
    k = katla(s)
    if len(k) != len(s):
        return None  # katlama uzunlugu degistirdi; indeksler guvenli degil
    m = SATIR_RE.match(k)
    if not m:
        return None
    ilan_no = k[m.start("ilan_no"):m.end("ilan_no")]
    tarih = k[m.start("tarih"):m.end("tarih")]
    if not ILAN_NO_RE.fullmatch(ilan_no) or not tarih_oku(tarih):
        return None
    kurum = s[m.start("kurum"):m.end("kurum")].strip()
    if not pozisyon:
        pozisyon, kurum = pozisyon_kurum_ayir(kurum)
    return {
        "ilan_no": ilan_no,
        "pozisyon": pozisyon,
        "kurum": kurum,
        "tur": re.sub(r"\s*/\s*", " / ", s[m.start("tur"):m.end("tur")]).strip(),
        "acik_pozisyon": int(k[m.start("poz"):m.end("poz")]),
        "calisma_yeri": s[m.start("yeri"):m.end("yeri")].strip(),
        "son_basvuru": tarih,
    }


def tarih_oku(sheet):
    """dd.mm.yyyy metnini date'e cevirir; gecersizse None."""
    m = TARIH_RE.search((sheet or "").strip())
    if not m:
        return None
    gun, ay, yil = (int(x) for x in m.group(1).split("."))
    try:
        return _dt.date(yil, ay, gun)
    except ValueError:
        return None


def acik_mi(son_basvuru, bugun=None):
    """Son basvuru tarihi bugunden once degilse ilan aciktir."""
    t = tarih_oku(son_basvuru)
    if t is None:
        return False
    return t >= (bugun or _dt.date.today())


# --- sayfa metnine cevirme ---------------------------------------------------
def metin_yap(parca):
    """HTML parcasini tek satirlik duz metne cevirir (satir ayristirma icin)."""
    t = re.sub(r"<br\s*/?>|</(?:div|span|td|p|b|strong)>", " ", parca or "",
               flags=re.I)
    t = re.sub(r"<[^>]+>", " ", t)
    t = html.unescape(t).replace("\u00a0", " ").replace("\ufeff", " ")
    return re.sub(r"\s+", " ", t).strip()


def ilan_satirlari(sayfa):
    """Ilan tablosundaki <tr> satirlarini duz metin listesi olarak dondurur."""
    out = []
    for tr in re.findall(r"<tr\b.*?</tr>", sayfa or "", re.S | re.I):
        if not ILAN_NO_RE.search(tr):
            continue
        tr = re.sub(r"</a\s*>", " " + AYRAC, tr, count=1, flags=re.I)
        s = metin_yap(tr)
        if s:
            out.append(s)
    return out


def sayfa_parsele(sayfa):
    """Sayfadaki cozulebilen ilanlari (ilan_no tekrarsiz) dondurur."""
    out, gorulen = [], set()
    for satir in ilan_satirlari(sayfa):
        il = satir_ayristir(satir)
        if not il or il["ilan_no"] in gorulen:
            continue
        gorulen.add(il["ilan_no"])
        out.append(il)
    return out


# --- form sozlugu ------------------------------------------------------------
def form_kur(sayfa):
    """Gizli + metin inputlari, isaretli radyo/checkbox ve select varsayilanlari."""
    alan = {}
    for etiket in re.findall(r"<input\b[^>]*>", sayfa or "", re.I):
        ad = re.search(r'name="([^"]+)"', etiket, re.I)
        if not ad:
            continue
        tur = re.search(r'type="([^"]+)"', etiket, re.I)
        tur = tur.group(1).lower() if tur else "text"
        deger = re.search(r'value="([^"]*)"', etiket, re.I)
        deger = html.unescape(deger.group(1)) if deger else ""
        isaretli = "checked" in etiket.lower()
        if tur in ("hidden", "text") or (tur in ("radio", "checkbox") and isaretli):
            alan[ad.group(1)] = deger
    for sec in re.finditer(r'<select\b[^>]*name="([^"]+)"[^>]*>(.*?)</select>',
                           sayfa or "", re.S | re.I):
        secili = (re.search(r'<option[^>]*value="([^"]*)"[^>]*selected',
                            sec.group(2), re.I)
                  or re.search(r'<option[^>]*selected[^>]*value="([^"]*)"',
                               sec.group(2), re.I))
        alan[sec.group(1)] = html.unescape(secili.group(1)) if secili else ""
    return alan


def sonraki_sayfa_var(sayfa):
    """btnNext butonu aktif mi (aspNetDisabled icermiyorsa evet)."""
    for etiket in re.findall(r"<a\b[^>]*>", sayfa or "", re.I):
        if "ctlDataPagerDetay_btnNext" in etiket:
            return "aspnetdisabled" not in etiket.lower()
    return False


# --- tek oturum (cookie jar) -------------------------------------------------
def opener():
    jar = http.cookiejar.CookieJar()
    op = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
    op.addheaders = [("User-Agent", USER_AGENT),
                     ("Accept-Language", "tr,en;q=0.5")]
    return op


def _coz(ham, ctype):
    m = re.search(r"charset=([\w\-]+)", ctype or "", re.I)
    for cs in ((m.group(1) if m else None), "utf-8", "cp1254"):
        if not cs:
            continue
        try:
            return ham.decode(cs)
        except (UnicodeDecodeError, LookupError):
            continue
    return ham.decode("utf-8", "replace")


def _cek(op, istek):
    """(http_durumu, metin); ag hatasinda (None, kisa hata metni)."""
    try:
        with op.open(istek, timeout=ZAMAN_ASIMI) as cevap:
            return cevap.status, _coz(cevap.read(),
                                      cevap.headers.get("Content-Type"))
    except urllib.error.HTTPError as g:
        return g.code, ""
    except (urllib.error.URLError, OSError, ValueError) as g:
        return None, (type(g).__name__ + ": " + str(g))[:160]


def GET(op, url):
    time.sleep(BEKLEME)
    return _cek(op, urllib.request.Request(url))


def POST(op, url, alan):
    time.sleep(BEKLEME)
    govde = urllib.parse.urlencode(alan).encode("utf-8")
    return _cek(op, urllib.request.Request(
        url, data=govde,
        headers={"Content-Type": "application/x-www-form-urlencoded"}))


def robots_kontrol(op, url=ARANA):
    """'izinli' | 'yok' | 'reddedildi' | 'gecersiz' | 'erisilemedi'."""
    p = urllib.parse.urlsplit(url)
    rurl = "%s://%s/robots.txt" % (p.scheme, p.netloc)
    durum, metin = GET(op, rurl)
    if durum is None:
        return "erisilemedi"
    if durum == 404:
        return "yok"
    if (durum != 200 or not metin.strip()
            or re.search(r"<\s*(html|!doctype)", metin[:500], re.I)):
        return "gecersiz"
    rp = urllib.robotparser.RobotFileParser()
    rp.parse(metin.splitlines())
    return "izinli" if rp.can_fetch(USER_AGENT, url) else "reddedildi"


def ara(op, maks_sayfa=MAKS_SAYFA, log=print):
    """Kamu ilanlarini sayfa sayfa toplar; (ilanlar, sayfa_sayisi, hata)."""
    durum, sayfa = GET(op, ARANA)
    if durum != 200 or "__VIEWSTATE" not in sayfa:
        return [], 0, "arama sayfasi alinamadi (HTTP %s)" % durum
    alan = form_kur(sayfa)
    alan[KAMU_RADYO] = KAMU_DEGER
    alan["__EVENTTARGET"] = ARAMA_HEDEF
    alan["__EVENTARGUMENT"] = ""
    durum, sayfa = POST(op, ARANA, alan)
    if durum != 200:
        return [], 0, "arama POST basarisiz (HTTP %s)" % durum

    ilanlar, gorulen, adet = [], set(), 0
    while True:
        adet += 1
        yeniler = [i for i in sayfa_parsele(sayfa) if i["ilan_no"] not in gorulen]
        gorulen.update(i["ilan_no"] for i in yeniler)
        ilanlar += yeniler
        log("sayfa %d: %d yeni ilan (toplam %d)" % (adet, len(yeniler), len(ilanlar)))
        if not yeniler:
            log("yeni ilan no gelmedi -> duruldu")
            break
        if adet >= maks_sayfa:
            log("sayfa sinirina (%d) ulasildi" % maks_sayfa)
            break
        if not sonraki_sayfa_var(sayfa):
            log("btnNext aktif degil (aspNetDisabled) -> son sayfa")
            break
        alan = form_kur(sayfa)
        alan[KAMU_RADYO] = KAMU_DEGER
        alan["__EVENTTARGET"] = SAYFA_HEDEF
        alan["__EVENTARGUMENT"] = ""
        durum, sayfa = POST(op, ARANA, alan)
        if durum != 200:
            return ilanlar, adet, "sayfa POST basarisiz (HTTP %s)" % durum
    return ilanlar, adet, ""


def onceki_ilk_gorulme(yol):
    """Onceki cikti dosyasindan ilan_no -> ilk_gorulme haritasi."""
    try:
        with open(yol, encoding="utf-8") as fh:
            onceki = json.load(fh)
    except (OSError, ValueError):
        return {}
    out = {}
    for il in onceki.get("ilanlar", []):
        if il.get("ilan_no") and il.get("ilk_gorulme"):
            out[il["ilan_no"]] = il["ilk_gorulme"]
    return out


def ozet(ilanlar, adet, yol):
    print("sayfa sayisi : %d" % adet)
    print("toplam ilan  : %d" % len(ilanlar))
    print("kurum sayisi : %d" % len({i["kurum"] for i in ilanlar}))
    print("ilk 5 ilan   :")
    for il in ilanlar[:5]:
        print("  %s  %s | %s | son basvuru %s | %s" % (
            il["ilan_no"], il["pozisyon"][:42], il["kurum"][:34],
            il["son_basvuru"], "ACIK" if il["acik"] else "KAPALI"))
    print("yazildi      : %s" % yol)


def varsayilan_cikti():
    kok = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    return os.path.join(kok, "career", "esube_kamu_ilanlari.json")


def main(argv=None):
    ap = argparse.ArgumentParser(description="e-sube kamu isci ilan okuyucusu")
    ap.add_argument("--out", default=varsayilan_cikti())
    ap.add_argument("--max-sayfa", type=int, default=MAKS_SAYFA)
    a = ap.parse_args(argv)

    op = opener()
    rob = robots_kontrol(op)
    print("robots: %s" % rob)
    if rob not in ("izinli", "yok"):
        print("robots izni yok -> cikis kodu %d" % CIKIS_ROBOTS)
        return CIKIS_ROBOTS

    ilanlar, adet, hata = ara(op, max(1, a.max_sayfa))
    simdi = _dt.datetime.now(_dt.timezone.utc).isoformat(timespec="seconds")
    eski = onceki_ilk_gorulme(a.out)
    bugun = _dt.date.today()
    for il in ilanlar:
        il["ilk_gorulme"] = eski.get(il["ilan_no"], simdi)
        il["acik"] = acik_mi(il["son_basvuru"], bugun)

    belge = {"cekim_zamani": simdi, "kaynak": KAYNAK,
             "sayfa_sayisi": adet, "ilanlar": ilanlar}
    klasor = os.path.dirname(os.path.abspath(a.out))
    os.makedirs(klasor, exist_ok=True)
    with open(a.out, "w", encoding="utf-8") as fh:
        json.dump(belge, fh, ensure_ascii=False, indent=1)

    ozet(ilanlar, adet, a.out)
    if hata:
        print("UYARI: %s" % hata)
        return CIKIS_AG if adet == 0 else 0
    return 0


if __name__ == "__main__":
    sys.exit(main())

