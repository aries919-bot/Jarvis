# C1 — jarvis-cloud PR #5 parçalama planı

Durum: PLAN / uygulanmadı
Kaynak PR: https://github.com/aries919-bot/jarvis-cloud/pull/5
Kaynak HEAD: `d78df6104265cca25de67617ccb5031ba3d3bfdb`
Hakem sınırı: PR başına 60.000 UTF-8 patch baytı.

## Erişim sınırı
Bu plan mevcut ChatGPT GitHub bağlantısının okuyabildiği PR #5'e dayanır. Başka bir GitHub OAuth bağlantısının 404 vermesiyle çelişmez; o bağlantının erişimi varmış gibi iddia edilmez. PR açık/taslak; hiçbir dosya değiştirilmedi, PR/branch oluşturulmadı, merge veya deploy yapılmadı.

## Patch baytları
PR patch'i dosya dosya tekrar alınıp UTF-8 olarak sayıldı. Toplam **114.946 bayt**. Önceki 114.958 sayımı 12 bayt farklıydı; bu son tekrar sayımı esas alınır. Kaynak dosyanın tam boyutu değil, diff patch baytı ölçülür.

| Dosya | Patch baytı |
|---|---:|
| .github/workflows/validate.yml | 2.478 |
| package.json | 130 |
| public-input/career/esube_kamu_ilanlari.json | 18.147 |
| scripts/c1-worker-d1-smoke.mjs | 9.449 |
| scripts/career-feed-live.mjs | 5.190 |
| src/career-feed-store.js | 11.247 |
| src/career-feed.js | 24.174 |
| src/esube-input.js | 4.595 |
| src/index.js | 3.568 |
| tests/career-feed-route.test.mjs | 5.778 |
| tests/career-feed-state.test.mjs | 6.998 |
| tests/career-feed.test.mjs | 17.826 |
| tests/esube-input.test.mjs | 5.366 |
| **Toplam** | **114.946** |

## PR sırası (testler kodla aynı PR'da)
Her PR'ın kendi testleri içinde olacak. Zincir sırayla merge edilir; sonraki PR, önceki gerekli PR'ın merge edilmiş main HEAD'ini temel alır.

| Sıra | İçerik | Dosyalar | Patch baytı |
|---|---|---|---:|
| A | CI iskeleti | validate.yml + package.json | **2.608** |
| B | Feed çekirdeği + test | career-feed.js + tests/career-feed.test.mjs | **42.000** |
| C | D1 store + Worker route + testler | career-feed-store.js + tests/career-feed-state.test.mjs + index.js + tests/career-feed-route.test.mjs | **27.591** |
| D | e-Şube adaptörü/toplayıcı + test | esube-input.js + career-feed-live.mjs + tests/esube-input.test.mjs | **15.151** |
| E | Local Worker/D1 smoke | c1-worker-d1-smoke.mjs | **9.449** |
| F | Snapshot verisi, kodsuz PR | esube_kamu_ilanlari.json | **18.147** |

Bütün gruplar 60.000 bayt altında. Parçalama sırasında değişen her PR'ın gerçek patch'i yeniden ölçülmelidir. D1 migration/schema dosyası varsa C grubuna ve C'nin testlerine eklenip tekrar ölçülmelidir.

## Kabul kapıları
1. A ilk sırada merge edilir; PR event'inde CI testleri çalışır. Schedule yalnız default branch'te çalışır; main'e merge sonrası gerçek schedule Actions koşusu görülmeden günlük yenileme tamamlandı denmez. Mevcut workflow'daki push branch filtresi default branch'e göre düzeltilmelidir.
2. B core kuralları ve tüm feed testlerini birlikte taşır.
3. C store, GET/POST route ve state/route testlerini birlikte taşır; B'ye bağlıdır.
4. D adapter, collector ve adapter testlerini birlikte taşır; B/C sözleşmesine bağlıdır.
5. E local Worker/D1 smoke'tur; local PASS production kanıtı değildir.
6. F yalnız JSON snapshot'tır. Tarih uydurulmaz; publishedAt yoksa null, deadline/collectedAt/firstSeenAt ayrıdır; geçmiş deadline güncel sayılmaz.
7. Her PR'da exact base/head SHA, dosya listesi, UTF-8 patch baytı, yasaklı desen taraması, kendi test sonucu, CI URL ve Hakem kararı kaydedilir. Testi başarısız PR merge edilmez.
8. Zincirden biri RED olursa sonraki PR merge edilmez.
9. Production GET için JARVIS_WORKER_URL repository variable ve JARVIS_WORKER_TOKEN repository secret kullanıcı tarafından GitHub ayarlarında tanımlanır. Değerler sohbete/log/commit'e yazılmaz; eksikse production GET BLOCKED kalır.
10. Ashby robots 401 için bu plan kapsamında hedef API çağrısı yok; önce resmi kullanım koşulları doğrulanır.

## Kapsam dışı
- Özel sektör deadline-unknown adaylarını 100 ilan eşiğine katma kararı alınmadı; mevcut doğrulama eşiği değişmez.
- Production POST, deploy veya secret okuma/yazma yapılmaz.

## Sonraki adım
Bu belge yalnız plan. PR A-F oluşturulmadı. Her parça ayrı test, CI ve Hakem onayıyla sırayla hazırlanmalıdır.
