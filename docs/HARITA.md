# HARITA (tek gercek kaynak, kisa tut)
Son guncelleme: 2026-11-23

K1 Kaynak denetimi: DEVAM (dal cline/K1)
- Yapildi: matris, robots denetimi, main(), ilan filtresi, 30 test yesil
- Olcum: gercek kamu ilani DOGRULANMADI (0). Menu linkleri filtrelendi.
- Arac: scripts/kaynak_tara.py (jsonld > rss > sitemap > html dener,
  calisani career/calisan_kaynaklar.json + career/kaynak_kayit.json'a yazar)
- Actions: .github/workflows/kaynak-tarama.yml kaydi `durum` dalina yazar
- Yeni aday kaynak: career/adaylar.txt'ye https adresi ekle
- Bekleyen: ilan.gov.tr (sunucu sertifika zinciri eksik), Kariyer Kapisi
  dogru adres (bilinmiyor), K1-BG-2 CI adimi dogrulama
- Not: dal film/oynatici commitlerini de tasiyor, PR acmadan once ayir
- Kural: giris/CAPTCHA asilmaz, robots'a uyulur, kazima yok

C1 Sunucu toplayici: YAPILMADI (K1 olcumune bagli)
C2-C5, A1-A2, B1, D1, E1, Z1: YAPILMADI

SONRAKI ADIM: kaynak_tara.py ciktisini oku, calisan kaynaklari genislet
