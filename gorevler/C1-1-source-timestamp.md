# C1-1 — Kaynak snapshot zamanı ile son başarılı okuma zamanını ayır

## Durum
AÇIK — hakem düzeltmesi / kanıt bekliyor

## Bulgu
C1-1 exact-head QA #45495956811 raporunda e-Şube JSON girdisinin gerçek `cekim_zamani` değeri `2026-11-24T11:57:55Z`, feed'in `collectedAt` değeri ise `2026-11-24T21:55:56.592Z` idi. `mapEsubePublicJson()` kaynak nesnesine snapshot zamanını koymadığı için `mergeCareerFeeds()` başarılı her kaynağın `lastGoodAt` değerini genel feed `collectedAt` zamanına eşitliyor. Böylece e-Şube verisinin gerçek kaynaktan alındığı zaman ile Actions tarafından işlendiği zaman aynıymış gibi görünüyor.

## İstenen düzeltme
- e-Şube kaynak nesnesine gerçek input `cekim_zamani` değerini ISO `collectedAt` olarak ekle.
- `mergeCareerFeeds()` başarılı kaynak için `lastGoodAt = attempted.collectedAt || feed.collectedAt` uygulasın; `lastAttemptAt` her zaman mevcut feed'in işlem zamanında kalsın.
- Diğer ATS kaynaklarında kaynak timestamp alanı yoksa mevcut davranış korunmalı.
- Regresyon testleri kaynak snapshot ve işleme zamanlarının birbirinden ayrıldığını, mevcut per-source last-good ve `firstSeenAt` davranışının bozulmadığını göstermeli.

## Güvenlik ve kapsam
Yalnız C1 veri doğruluğu. Robots, auth, toplama yöntemi, Worker route, token veya Android dosyalarına dokunma. Kaynak snapshot yeniliği 12 saat sınırını aşarsa e-Şube stale kalmalı; zaman damgasını taze göstermek için yapay güncelleme yapılmamalı.

## Kabul kanıtı
- [ ] Exact private HEAD üzerinde `npm run test:c1` yeşil.
- [ ] Worker + D1 yerel smoke yeşil.
- [ ] Gerçek e-Şube artifact'inde `lastGoodAt` kaynak `cekim_zamani`, `lastAttemptAt` Actions `collectedAt` olarak ayrışıyor.
- [ ] PR diff yalnız belirtilen C1 dosyaları; Hakem ONAY ve bağımsız inceleme.
- [ ] Production Worker/D1 ayrı doğrulanır; yerel smoke production kanıtı sayılmaz.

## Önceki kanıt
- [Exact-head QA #45495956811](https://github.com/aries919-bot/Jarvis/actions/runs/45495956811)
- Artifact #14966529829; `collectedAt=2026-11-24T21:55:56.592Z`, e-Şube `cekim_zamani=2026-11-24T11:57:55Z`.
- Kaynak sayımı o koşuda: 16 kaynak, 8 fresh /8 stale; özel 85 deadline-unknown /0 doğrulanmış açık; kamu e-Şube 1 kaynak /42 açık (o audit saatinde taze, şu an 12 saatlik sınır dışında).
