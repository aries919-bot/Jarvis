# H1 ilk açılış SQLite I/O performans düzeltmesi

## Bulgu
PR #71 HEAD 6b392a3aa02d42700743657350d081d7f7ad6aeb, dosya:
app/src/main/java/com/cem/jarvis/CareerNewsVerificationActivity.kt
- onCreate() içinde store.load(storyId) çağrısı ana UI thread üzerinde.
- Ardından store.save(record) koşulsuz çağrılıyor; kayıt zaten varsa da tekrar yazılıyor.
- persist() içindeki sonraki kullanıcı yazımları ayrı Thread üzerinde; ilk açılış bu kuralın istisnası.

## Etki / öncelik
P2 performans. Tek ve küçük sınırlandırılmış kayıt olduğu için veri bütünlüğü blocker'ı değil; ancak soğuk açılışta disk erişimi ekran akışını geciktirebilir. Bu durum UI thread dışı SQLite yazım varsayımını Activity'nin tamamı için geçersiz kılar.

## Düzeltme tasarımı
1. onCreate() önce progress/loading yüzeyi gösterir.
2. Tek ExecutorService ile store.load(storyId) arka planda çalışır.
3. Kayıt yoksa ilk kayıt bir kez save() edilir; kayıt varsa gereksiz save() yapılmaz.
4. Sonuç runOnUiThread ile ekrana aktarılır; Activity kapanmışsa UI güncellemesi atlanır.
5. onDestroy() executor'ı kapatır; aynı story için çift Activity / çift yazım yarışları test edilir.
6. Instrumentation testleri, UI hazır olana kadar öğeyi bekler; Thread.sleep kullanılmaz.

## Kabul testleri
- Yeni story ilk açılışta bir kayıt üretir.
- Var olan story yeniden açılınca kayıt içeriği değişmeden kalır ve gereksiz DB yazımı yapılmaz.
- Yavaş DB simülasyonunda ilk frame bloklanmaz.
- Activity kapanırken arka plan sonucu UI'ye dokunmaz.
- Mevcut reviewHistory, correctionHistory, last-good ve 34 test davranışı korunur.

## Kanıt
- PR #71'e P2 QA yorum id: 7172257752.
- İncelenen PR: https://github.com/aries919-bot/Jarvis/pull/71
- Bu takip işi için mevcut H1 CI koşusu bitmeden kaynak değişikliği yapılmadı.

## KARAR
AYRI KÜÇÜK PR — henüz kod değişmedi. H1 emulator merge-ref doğrulaması ve gerçek telefon testi sırasını bozmaz; performans kabulü öncesi uygulanması önerilir.
