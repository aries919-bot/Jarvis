# C1 — İzinli resmî sayfalarda ek bağlantı keşfi (25.11.2026)

## Amaç
Robots izni doğrulanmış resmî HTML sayfalarından yalnızca sayfa başlığı, tarih metaverisi, kısa metin özeti ve aynı resmî host allowlist'indeki PDF/iframe bağlantılarını çıkarmak. PDF dosyaları indirilmez.

## Ön koşul / kanıt
Önceki exact-path robots denetimi PR #59:
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45533905500
- Job: 138935687474 SUCCESS
- Artifact: 14986480255
- 404 robots.txt, RFC 9309 §2.3.1.3 kapsamında robots dosyasının bulunmaması olarak ele alındı; 200 boş robots dosyası da yasak kuralı içermediğinden erişilebilir. 401/403/5xx/timeout/redirect UNKNOWN/fail-closed.
- Atatürk, Mardin, Balıkesir, ORAN, TGA, Zafer, DOKA ve YÖKAK sayfaları/ölçülen ek yolları erişilebilir.

## Bu görevin izinli HTML sayfaları
- Atatürk Üniversitesi 20.11.2026 ve 16.11.2026 ilan sayfaları
- Mardin Artuklu Üniversitesi 20.11.2026 öğretim üyesi ve öğretim görevlisi sayfaları
- Balıkesir Üniversitesi 20.11.2026 öğretim elemanı sayfası
- ORAN'ın kendi personel alım duyurusu
- Zafer Kalkınma Ajansı ilan sayfası ve DOKA'daki resmî PDF kopyası
- DOKA'nın kendi personel alım duyurusu
- TGA kariyer ve pozisyon sayfaları
- YÖKAK uzman yardımcısı duyurusu (başvuru 01.12.2026'da açılır; güncel açık sayıya eklenmez)

## Güvenlik
- User-Agent: AriesKaynakTara/1.0
- Her HTML sayfasından önce o hostun /robots.txt dosyası istenir.
- Yalnız HTTP 200 robots ve path ALLOW ise HTML sayfasına GET gönderilir.
- robots 404 = NO_ROBOTS_FILE ve RFC 9309 §2.3.1.3 uyarınca erişim serbest; robots 200 boş gövde de yasak kuralı yok anlamına gelir. 401/403/5xx/timeout/redirect/ağ hatası UNKNOWN/fail-closed.
- TLS varsayılan doğrulaması açık; yönlendirme takip edilmez.
- Yalnızca kaynakta belirtilmiş resmî host allowlist'indeki linkler raporlanır. Dış linkler atılır.
- PDF/ek dosyalar indirilmez; yalnız URL keşfi yapılır. Bir sonraki ayrı robots ölçümünde her ekin tam yolu test edilir.

## Güncel kadro sayımı (25.11.2026 canlı HTML/link denetimi)
- TGA: **13 pozisyon**, publishedAt=2026-11-03, deadline=2026-12-04. 6 rol türü kariyer portalında listeleniyor; toplam 13 pozisyon resmî kariyer sayfasında açıkça yazıyor. Çevrimiçi başvuru yalnız TGA kariyer sayfasından.
- ORAN: **7 pozisyon** (6 uzman + 1 destek), sayfa 16.11.2026'da yayımlanmış; ilanın kendi tablosunda İlan Tarihi=19.10.2026, başvuru 09.11.2026–03.12.2026. Başvuru e-Devlet/Kariyer Kapısı üzerinden.
- Zafer: **2 uzman**, publishedAt=2026-11-04, deadline=2026-12-14; resmi PDF DOKA sayfasında bulundu ve tam PDF yolu robots denetiminden geçti.
- DOKA: **1 uzman**, publishedAt=2026-11-03, deadline=2026-12-06; resmî PDF yolu robots denetiminden geçti.
- Şimdilik HTML/PDF içeriğiyle doğrulanmış: **4 kaynak / 23 açık pozisyon**.
- Atatürk: 20.11 ilanı 1 ve 16.11 ilanı 70 pozisyon adayı; PDF yolları bulundu ve robots denetiminden geçti, ancak PDF tablo sayımı H2 reader aşamasında yapılacak.
- Mardin Artuklu: 36 öğretim üyesi + 4 öğretim görevlisi = 40 aday; iki PDF yolu bulundu ve robots denetiminden geçti; PDF sayımı H2 reader aşamasında.
- Balıkesir: 7 kadro adayı; PDF yolu bulundu ve robots denetiminden geçti; PDF sayımı H2 reader aşamasında.
- YÖKAK: 5 uzman yardımcısı; başvuru 01.12.2026'da açılır, güncel açık sayıya eklenmez.
- Önceki e-Şube/H2 snapshot'ları 12 saat sınırını aştı; güncel toplam sayılmaz. Canlı yenileme ayrı adım.

## Kabul ölçütü
1. Exact-head Actions syntax + unit tests SUCCESS.
2. Her sayfa için robots status, ALLOW/NO_ROBOTS_FILE/UNKNOWN, HTTP status ve page_requested kaydı.
3. PDF bağlantı keşfi yalnız allowlist hostlarından; PDF indirme yok.
4. Tam PDF yolları PR #59 robots-only workflow'unda ölçüldü; bir sonraki görevde H2 reader PDF metnini parse edip kadro sayılarını doğrulayacak.
5. C1 kamu hedefi 10 kaynak / 100 doğrulanmış açık ilan; altında RED.

## Karar
**RED / H2 entegrasyonuna geçiş.** Link-audit run #45533908007 (job 138935696949) SUCCESS; artifact 14986240689; collectedAt=2026-11-25T02:35:03.268382Z. Robots-first test run #45533917072 (job 138935726682) SUCCESS. Dört kaynakta 23 açık pozisyon HTML/PDF metniyle doğrulandı; Atatürk/Mardin/Balıkesir PDF kadro tabloları henüz H2 reader tarafından sayılmadı. Bu PR yalnız HTML link/metaveri keşfidir; H2 kaynak kaydı, Worker, D1, merge veya deploy yapmaz.
