# H1 ilk açılış SQLite I/O performans düzeltmesi

## Bulgu
PR #71 ilk HEAD'inde CareerNewsVerificationActivity.onCreate() içinde store.load(storyId) ve ardından koşulsuz store.save(record) ana UI thread üzerinde çalışıyordu. Mevcut kayıt her açılışta tekrar yazılıyordu.

## Uygulanan düzeltme
- PR #71 branch feat/h1-news-store-ui-20261125, Activity blob b7a46b0e638f5261eb9705f48bf43912ffa64195.
- onCreate önce showLoading(title) gösteriyor.
- store.load(storyId) tek FIFO ioExecutor içinde çalışıyor.
- Kayıt varsa doğrudan mevcut kayıt kullanılıyor ve gereksiz SQLite save yapılmıyor.
- Kayıt yoksa store.save(initialRecord) yalnız bir kez çalışıyor.
- Sonuç runOnUiThread ile ekrana aktarılıyor; closing/isFinishing kontrolü var.
- onDestroy closing=true yapıyor ve storage.close() işini executor kuyruğuna ekliyor; böylece önceden kuyruğa girmiş I/O işlemleriyle yarışmıyor.
- Kullanıcının daha sonraki save işlemleri aynı executor üzerinde.

## Kanıt / retest
- Exact Activity satır kontrolü: showLoading line 78; ioExecutor line 79; store.load line 81; only-if-missing store.save line 86; onDestroy/closing/storage.close lines 123–127; subsequent save executor line 351–352.
- APK/unit run #45692016951 ve emulator smoke #45692017824, önceki exact HEAD b8a78126 üzerinde SUCCESS. Sonraki H1 Haber QA commitleri aynı async açılış davranışını koruyor.
- Güncel HEAD 0eaaa74153dd3eacdd66c701b9727c2b8978983f için APK/unit #45699134292 ve merge-ref emulator #45699134296 yeni koşular başlatıldı; son gözlemde ikisi de IN_PROGRESS. Bu rapor yazılırken yeni koşuların PASS sonucu yok.
- Gerçek telefonda açılış/geri dönüş ve hızlı ardışık açma testi yapılmadı.

## KARAR
**KOD DÜZELTMESİ UYGULANDI.** P2 ana-thread SQLite I/O işi kod kapsamı bakımından tamamlandı. Exact-head CI sonucu ve fiziksel telefon testi hâlâ kabul kapısıdır; PR #71 merge edilmedi.
