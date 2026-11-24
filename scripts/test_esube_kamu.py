import importlib.util, json, os, tempfile, unittest
from datetime import date
_p = os.path.join(os.path.dirname(__file__), "esube_kamu_okuyucu.py")
_s = importlib.util.spec_from_file_location("esube", _p)
e = importlib.util.module_from_spec(_s); _s.loader.exec_module(e)

# GOREV_ESUBE.txt'deki ornek satir (duz metin, ASCII Turkce)
ORNEK = ("Diger Tahmil-Tahliye ve Ilgili Yuk Tasima Isciler MILLI SAVUNMA "
         "BAKANLIGI PERSONEL GENEL MUDURLUGU Kamu / Daimi / Tam Zamanli 6 "
         "Acik Pozisyon Ulke Geneli Basvuru (Calisma Yeri: MUGLA / MUGLA "
         "MERKEZ) 00009905681 27.11.2026 3 Gun Kaldi")

# Ayni ilacin siteden gelen Turkce karsiligi (ilk <a> sonrasi ayracli)
CANLI = ("Diğer Tahmil-Tahliye ve İlgili Yük Taşıma İşçileri" + e.AYRAC +
         " MİLLİ SAVUNMA BAKANLIĞI PERSONEL GENEL MÜDÜRLÜĞÜ Kamu / Daimi / "
         "Tam Zamanlı 6 Açık Pozisyon Ülke Geneli Başvuru (Çalışma Yeri: "
         "MUĞLA / MUĞLA MERKEZ) 00009905681 27.11.2026 3 Gün Kaldı "
         "Favorilere Ekle")


class Katlama(unittest.TestCase):
    def test_uzunlugu_korur(self):
        for s in ("İĞŞÜÇÖ Işıklı Âdemoğlu", "MİLLİ SAVUNMA BAKANLIĞI", ""):
            self.assertEqual(len(e.katla(s)), len(s), s)

    def test_katlama_degeri(self):
        self.assertEqual(e.katla("Kısmi Süreli / Tam Zamanlı"),
                         "kismi sureli / tam zamanli")


class SatirAyristirma(unittest.TestCase):
    def test_ornek_satir(self):
        il = e.satir_ayristir(ORNEK)
        self.assertIsNotNone(il)
        self.assertEqual(il["ilan_no"], "00009905681")
        self.assertEqual(il["pozisyon"],
                         "Diger Tahmil-Tahliye ve Ilgili Yuk Tasima Isciler")
        self.assertEqual(il["kurum"],
                         "MILLI SAVUNMA BAKANLIGI PERSONEL GENEL MUDURLUGU")
        self.assertEqual(il["tur"], "Kamu / Daimi / Tam Zamanli")
        self.assertEqual(il["acik_pozisyon"], 6)
        self.assertEqual(il["calisma_yeri"],
                         "Ulke Geneli Basvuru (Calisma Yeri: MUGLA / MUGLA MERKEZ)")
        self.assertEqual(il["son_basvuru"], "27.11.2026")

    def test_canli_turkce_satir_ayracla(self):
        il = e.satir_ayristir(CANLI)
        self.assertIsNotNone(il)
        self.assertEqual(il["ilan_no"], "00009905681")
        self.assertEqual(il["pozisyon"],
                         "Diğer Tahmil-Tahliye ve İlgili Yük Taşıma İşçileri")
        self.assertEqual(il["kurum"],
                         "MİLLİ SAVUNMA BAKANLIĞI PERSONEL GENEL MÜDÜRLÜĞÜ")
        self.assertEqual(il["tur"], "Kamu / Daimi / Tam Zamanlı")
        self.assertEqual(il["acik_pozisyon"], 6)
        self.assertEqual(il["son_basvuru"], "27.11.2026")

    def test_ilan_no_basinda_dort_sifir(self):
        self.assertIsNone(e.satir_ayristir(ORNEK.replace("00009905681",
                                                         "12349905681")))

    def test_son_basvuru_tarihi_sart(self):
        self.assertEqual(e.tarih_oku("27.11.2026"), date(2026, 11, 27))
        self.assertIsNone(e.tarih_oku("31.02.2026"))
        self.assertIsNone(e.tarih_oku("tarih yok"))
        self.assertIsNone(e.satir_ayristir(
            ORNEK.replace("27.11.2026", "31.02.2026")))

    def test_ilan_olmayan_satir(self):
        for s in ("Sırala : İl | Son Başvuru Tarihi", "",
                  "Menu basligi 00009905681 27.11.2026"):
            self.assertIsNone(e.satir_ayristir(s), s)

    def test_ilan_tarihi_alani_uydurulmaz(self):
        self.assertNotIn("ilan_tarihi", e.satir_ayristir(ORNEK))
        self.assertEqual(sorted(e.satir_ayristir(ORNEK)),
                         sorted(["ilan_no", "pozisyon", "kurum", "tur",
                                 "acik_pozisyon", "calisma_yeri",
                                 "son_basvuru"]))


class SayfaAyristirma(unittest.TestCase):
    SATIR = ('<tr bgcolor="#F0F0F0"><td><div><a onclick="PopupJobDetails('
             "&#39;00009905681&#39;,&#39;Kamu&#39;);\"><b>Bedenden Sorumlu "
             'Bayan Giyecek Satinalma Iscisi</b></a></div>'
             '<span id="x_ctlIsverenDL"><b>T.C. KAYSERI VALILIGI</b></span> '
             "<span>Kamu</span> / Daimi / Kısmi Zamanlı <strong><span>3"
             "</span></strong> Açık Pozisyon <span>KAYSERI / KOCASINAN</span>"
             ' <a>00009905681</a> <span title="Son Başvuru Tarihi">'
             "05.12.2026</span></td></tr>")

    def test_tr_satiri_duz_metne_donur(self):
        satirlar = e.ilan_satirlari("<table>" + self.SATIR * 2 + "</table>")
        self.assertEqual(len(satirlar), 2)
        self.assertIn(e.AYRAC, satirlar[0])

    def test_ilan_no_tekillestirilir(self):
        ilanlar = e.sayfa_parsele("<table>" + self.SATIR * 2 + "</table>")
        self.assertEqual(len(ilanlar), 1)
        self.assertEqual(ilanlar[0]["pozisyon"],
                         "Bedenden Sorumlu Bayan Giyecek Satinalma Iscisi")
        self.assertEqual(ilanlar[0]["kurum"], "T.C. KAYSERI VALILIGI")
        self.assertEqual(ilanlar[0]["tur"], "Kamu / Daimi / Kısmi Zamanlı")
        self.assertEqual(ilanlar[0]["acik_pozisyon"], 3)
        self.assertEqual(ilanlar[0]["calisma_yeri"], "KAYSERI / KOCASINAN")
        self.assertEqual(ilanlar[0]["son_basvuru"], "05.12.2026")

    def test_menu_satiri_elence(self):
        self.assertEqual(e.ilan_satirlari("<tr><td>Ana Sayfa</td></tr>"), [])


class FormVePager(unittest.TestCase):
    SAYFA = ('<form><input type="hidden" name="__VIEWSTATE" value="a+b" />'
             '<input type="text" name="ctl04$ara" value="" />'
             '<input type="radio" name="ctl04$IsyeriTuruRadios"'
             ' value="ozelSektorRadio" checked="checked" />'
             '<input type="radio" name="ctl04$IsyeriTuruRadios"'
             ' value="kamuRadio" />'
             '<input type="checkbox" name="ctl04$ulkeGeneliChk" value="on" />'
             '<input type="checkbox" name="ctl04$engelli" value="on"'
             ' checked="checked" />'
             '<select name="ctl04$ctlIl"><option value="">Tumu</option>'
             '<option value="06" selected="selected">Ankara</option>'
             "</select></form>")

    def test_form_sozeri(self):
        f = e.form_kur(self.SAYFA)
        self.assertEqual(f["__VIEWSTATE"], "a+b")
        self.assertEqual(f["ctl04$ara"], "")
        self.assertEqual(f["ctl04$IsyeriTuruRadios"], "ozelSektorRadio")
        self.assertEqual(f["ctl04$engelli"], "on")
        self.assertNotIn("ctl04$ulkeGeneliChk", f)
        self.assertEqual(f["ctl04$ctlIl"], "06")

    def test_btnNext_durumu(self):
        acik = '<a id="ctl04_ctlDataPagerDetay_btnNext" class="btn" href="x">'
        kapali = ('<a id="ctl04_ctlDataPagerDetay_btnNext"'
                  ' class="aspNetDisabled btn">')
        self.assertTrue(e.sonraki_sayfa_var(acik))
        self.assertFalse(e.sonraki_sayfa_var(kapali))
        self.assertFalse(e.sonraki_sayfa_var("<a id=baska>sonraki</a>"))


class AciklikVeIlkGorulme(unittest.TestCase):
    def test_acik_mi(self):
        bugun = date(2026, 11, 24)
        self.assertTrue(e.acik_mi("24.11.2026", bugun))
        self.assertTrue(e.acik_mi("05.12.2026", bugun))
        self.assertFalse(e.acik_mi("23.11.2026", bugun))
        self.assertFalse(e.acik_mi("31.02.2026", bugun))

    def test_ilk_gorulme_korunur(self):
        with tempfile.TemporaryDirectory() as d:
            yol = os.path.join(d, "ilanlar.json")
            with open(yol, "w", encoding="utf-8") as fh:
                json.dump({"ilanlar": [{"ilan_no": "00009905681",
                                        "ilk_gorulme": "2026-11-01T05:00:00"}]},
                          fh, ensure_ascii=False)
            harita = e.onceki_ilk_gorulme(yol)
            self.assertEqual(harita["00009905681"], "2026-11-01T05:00:00")
            self.assertNotIn("00001111111", harita)
        self.assertEqual(e.onceki_ilk_gorulme("/olmayan/dosya.json"), {})

    def test_uretim_deyerleri(self):
        self.assertEqual(e.USER_AGENT, "AriesKaynakTara/1.0 "
                         "(+https://github.com/aries919-bot/Jarvis)")
        self.assertEqual(e.CIKIS_ROBOTS, 3)
        self.assertEqual(e.MAKS_SAYFA, 40)
        self.assertEqual(e.BEKLEME, 2.0)


if __name__ == "__main__":
    unittest.main()
