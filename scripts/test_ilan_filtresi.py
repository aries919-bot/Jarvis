import importlib.util, os, unittest
_p = os.path.join(os.path.dirname(__file__), "career_source_audit.py")
_s = importlib.util.spec_from_file_location("k1m", _p)
k1 = importlib.util.module_from_spec(_s); _s.loader.exec_module(k1)


class IlanFiltresi(unittest.TestCase):
    def test_menu_basliklari_elenir(self):
        for t in ("İşveren Üyeliği", "Çerez Aydınlatma Metni", "Teşkilat Şeması",
                  "Log in", "Skip to main content", "Genel Müdür",
                  "Gizlilik ve Çerez Politikası", "E-HİZMETLER", "TR"):
            self.assertFalse(k1.is_real_listing(t), t)

    def test_gercek_ilanlar_gecer(self):
        for t in ("Sağlık Bakanlığı 5000 sözleşmeli personel alım ilanı",
                  "KPSS 2026/5 tercih kılavuzu ve kura duyurusu",
                  "Belediyesi zabıta memuru alım ilanı"):
            self.assertTrue(k1.is_real_listing(t), t)


if __name__ == "__main__":
    unittest.main()
