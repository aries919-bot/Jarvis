# C1 — İzinli resmî sayfalarda ek bağlantı keşfi (25.11.2026)

## Amaç
Robots izni doğrulanmış resmî HTML sayfalarından yalnızca sayfa başlığı, tarih metaverisi, kısa metin özeti ve aynı resmî host allowlist'indeki PDF/iframe bağlantılarını çıkarmak. PDF dosyaları indirilmez.

## Ön koşul / kanıt
Önceki robots denetimi PR #59:
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45532975833
- Job: 138932605275 SUCCESS
- Artifact: 14985218092
- Sonuç: Zafer sayfası+PDF ALLOW; DOKA sayfası+PDF ALLOW; Atatürk 20.11 ve 16.11 HTML ALLOW; Balıkesir HTML ALLOW fakat PDF robots 404/UNKNOWN; TGA sayfa yolları ALLOW; YÖKAK ALLOW. Mardin ve ORAN 404/UNKNOWN.
- UNKNOWN kaynaklarda hedef sayfaya/PDF'ye istek yok.

## Bu görevin izinli HTML sayfaları
- Atatürk Üniversitesi 20.11.2026 ilan sayfası
- Atatürk Üniversitesi 16.11.2026 ilan sayfası
- Balıkesir Üniversitesi 20.11.2026 öğretim elemanı duyuru HTML sayfası (PDF eki alınmaz)
- Zafer Kalkınma Ajansı ilan sayfası
- DOKA ilan sayfası
- TGA kariyer ve bilinen pozisyon sayfaları (içerik çelişkisi kontrolü için)
- YÖKAK uzman yardımcısı duyuru sayfası (başvuru 01.12.2026'da açılır; güncel açık sayıya eklenmez)

## Güvenlik
- User-Agent: AriesKaynakTara/1.0
- Her HTML sayfasından önce o hostun /robots.txt dosyası istenir.
- Yalnız HTTP 200 robots ve path ALLOW ise HTML sayfasına GET gönderilir.
- robots 404/401/403/5xx, timeout, redirect veya ağ hatası UNKNOWN; hedef sayfaya istek yok.
- TLS varsayılan doğrulaması açık; yönlendirme takip edilmez.
- Yalnızca kaynakta belirtilmiş resmî host allowlist'indeki linkler raporlanır. Dış linkler atılır.
- PDF/ek dosyalar indirilmez; yalnız URL keşfi yapılır. Bir sonraki ayrı robots ölçümünde her ekin tam yolu test edilir.

## Güncel kadro sayımı (geçici)
- Zafer 2 + DOKA 1 = **3 açık kadro**, tam resmî PDF ve robots izniyle doğrulandı.
- Atatürk 20.11 ilanı 1 kadro ve 16.11 ilanı 70 kadro adayı: resmî HTML sayfaları robots ALLOW, ancak PDF bağlantı yolları henüz doğrulanmadığı için sayılmaz.
- Balıkesir 7 kadro adayı: HTML robots ALLOW, PDF robots 404/UNKNOWN; sayılmaz.
- Mardin 40 ve ORAN 7: robots UNKNOWN; sayılmaz.
- TGA 13 aday pozisyon: resmî sayfa ve portal çelişkisi nedeniyle Doğrulanamadı; sayılmaz.
- YÖKAK 5: başvuru 01.12.2026'da açılır; güncel açık sayıya eklenmez.
- Önceki e-Şube/H2 snapshot'ları 12 saat sınırını aştı; güncel toplam sayılmaz. Canlı yenileme ayrı adım.

## Kabul ölçütü
1. Exact-head Actions syntax + unit tests SUCCESS.
2. Her sayfa için robots status, ALLOW/UNKNOWN, HTTP status ve page_requested kaydı.
3. PDF bağlantı keşfi yalnız allowlist hostlarından; PDF indirme yok.
4. Atatürk PDF yolları bulunursa sonraki görevde exact robots path ölçülür.
5. C1 kamu hedefi 10 kaynak / 100 doğrulanmış açık ilan; altında RED.

## Karar
**RED / bağlantı keşfi sürüyor.** Bu PR yalnız izinli HTML sayfalarının bağlantı/metaveri denetimidir; H2 kaynak kaydı, Worker, D1, merge veya deploy yapmaz.
