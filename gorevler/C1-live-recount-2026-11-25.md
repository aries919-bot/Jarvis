# C1 / H1 canlı yeniden sayım — 25.11.2026

## 1. e-Şube canlı ölçüm
- Actions: [#45513413529, attempt 2](https://github.com/aries919-bot/Jarvis/actions/runs/45513413529), job `139250985235`, SUCCESS. Log: `robots: izinli`; sayfa 1: 15, sayfa 2: 15, sayfa 3: 12; toplam 42; 7 kurum.
- `durum` commit: `bbb663d` (`esube kamu ilanlari 2026-11-25T13:49Z`). JSON blob: `b9a967b0960575f8696c9c0814b97f1aa54a7035`.
- `collectedAt=2026-11-25T13:49:08Z`; 3 sayfa; 42 ham / 42 benzersiz ilan_no / 0 tekrar / 42 acik=true / 0 gecmis. 42/42 kayıtta ilk görülme zamanı var. Kaynak yayın tarihi sunmuyor: `publishedAt=null` (JSON'da alan yok).
- 42 kaydın tamamında son başvuru `27.11.2026`.

| e-Şube işvereni | İlan |
|---|---:|
| MSB Personel Genel Müdürlüğü | 20 |
| Emniyet Genel Müdürlüğü | 10 |
| Durağan Belediyesi | 2 |
| Ondokuz Mayıs Üniversitesi sağlık merkezi | 7 |
| Devlet Tiyatroları Genel Müdürlüğü | 1 |
| Elazığ Karayolları 8. Bölge Müdürlüğü | 1 |
| Türkiye Denizcilik İşletmeleri | 1 |
| **Toplam** | **42** |

## 2. H2 resmî duyuru okuyucusu
- Actions: [#45540249638, attempt 2](https://github.com/aries919-bot/Jarvis/actions/runs/45540249638), job `139252154310`, SUCCESS. Artifact: [15028921924](https://github.com/aries919-bot/Jarvis/actions/runs/45540249638/artifacts/15028921924).
- 19 kaynak kaydı / 17 fresh / 2 stale. Artifact JSON'larında 19 dosya; kaynak `collectedAt=2026-11-25T13:50:40Z`.
- **13 doğrulanmış açık iş ilanı kartı / 11 ayrı kurum.** Pozisyon/kadro sayısı kart sayısına eklenmedi.

| Resmî kurum | Kart |
|---|---:|
| Anadolu Üniversitesi | 1 |
| Atatürk Üniversitesi | 2 |
| Balıkesir Üniversitesi | 1 |
| Doğu Karadeniz Kalkınma Ajansı (DOKA) | 1 |
| Düzce Üniversitesi | 1 |
| Mardin Artuklu Üniversitesi | 1 |
| Marmara Üniversitesi | 2 |
| Orta Anadolu Kalkınma Ajansı (ORAN) | 1 |
| Tarsus Üniversitesi | 1 |
| Türkiye Turizm Tanıtım ve Geliştirme Ajansı (TGA) | 1 |
| Zafer Kalkınma Ajansı | 1 |
| **Toplam** | **13** |

- Stale iki kaynak kaydı: BOTAŞ (network error) ve Mardin Artuklu öğretim üyesi duyurusu (deadline kanıtı yok).
- Açık sayaç dışı: Adli Tıp Kurumu başvurusu 30.11.2026 09:00 +03'te açılacağı için henüz açık değil; Munzur Üniversitesi'nin iki duyurusunda son gün 25.11.2026 fakat kesin kapanış saati kanıtlı değil; YHGM kaydı 2025 kura duyurusu, `contentType=lottery` ve `Süresi geçti`.
- Balıkesir Üniversitesi ve TGA kartlarında yayın tarihi kaynakta yok; `publishedAt=null` kaldı. Kaynak deadline ve açık başvuruyu doğruladığı için H2 okuyucu bu kartları 13 sayısına dahil etti.

## 3. Kamu kapısı ve entegrasyon
- Ayrı iki canlı okuyucunun ham toplamı: **42 e-Şube + 13 H2 = 55 kart**. Bu sayı, ortak Worker/D1 feed'i kanıtı değildir; cross-feed tekilleştirme henüz üretim entegrasyonunda doğrulanmadı.
- Hedef 10 kaynak / 100 doğrulanmış açık ilan. Kamu ilan sayısı **55/100**, dolayısıyla **RED**. H2 manifesti 19 kaynak kaydı (17 fresh / 2 stale), 11 ayrı kurum; e-Şube 1 feed ve 7 işveren içerir.
- H2 public feed PR [#61](https://github.com/aries919-bot/Jarvis/pull/61) açık/taslak. 5 dosya / +967 / -30; diff 72.234 karakter. `scripts/hakem_gate.py` içindeki `MAX_DIFF_BYTES=60_000` sınırını aştığı için Hakem [#45540323511](https://github.com/aries919-bot/Jarvis/actions/runs/45540323511) RED. Python tests 160/160, H2 PR tests 59/59 ve K1-B source access başarılı; diff sınırı/hakem sonucu geçilmiş değil. PR birleştirilmedi.
- Worker entegrasyon PR'ı [jarvis-cloud #9](https://github.com/aries919-bot/jarvis-cloud/pull/9) açık/taslak; formal review 0. Exact-SHA test koşusunda 46/46 Node, 11/11 local Wrangler+D1 ve 17/17 regression senaryosu geçti; fakat [#45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605) nihai kararı RED ve production authenticated GET BLOCKED. Özel repo Actions #45517394728 ve #45517353291 3 saniye içinde, steps oluşmadan failure; log alınamadı. Production deploy/POST yok.

## 4. H1 kart şeması
- [PR #63](https://github.com/aries919-bot/Jarvis/pull/63), `is/H1` tabanına bağlı taslak düzeltme: kategori enumları `Büyük özel` ve `Kamu bağlı şirket`; kaynak yayın tarihi sağlamazsa `publishedAt=null` ile doğrulanmış, deadline'ı açık ilan sayılabilir. `collectedAt` ve `firstSeenAt` yayın tarihi yerine geçmez.
- [H1 Actions #45628989453](https://github.com/aries919-bot/Jarvis/actions/runs/45628989453): `python -m unittest scripts.test_h1_schema -v`; **14/14 PASS**. PR taslak, bağımsız formal review yok; birleştirme yok.

## KARAR
C1 RED: 55/100; ortak Worker GET/D1 ve cross-feed entegrasyon kanıtı yok. H1 şema düzeltmesi 14/14 CI PASS fakat formal review/merge bekliyor. Hiçbir PR birleştirilmedi, production deploy yapılmadı.

## SONRAKİ ADIM
1. H2 kaynak PR #61'i 60 KB Hakem sınırına uygun, küçük ve ayrı kapsamlı değişikliklere bölmeden gate'i gevşetme.
2. Worker URL/token değişkenlerinin GitHub settings'te bulunduğunu gizli değerleri açığa çıkarmadan doğrula; authenticated production GET ve D1 smoke kanıtını al.
3. Resmî ve robots-izinli kaynaklardan en az 45 ek doğrulanmış açık ilan kartı bul; deadline/publishedAt/firstSeenAt kurallarını değiştirme.
4. PR #63 için bağımsız inceleme iste; test PASS tek başına merge izni değildir.