# DAL TEMİZLİĞİ — 26.11.2026

> Güncel kayıt: 26.11.2026. Bu doküman `durum` dalından `kariyer-modulu` dalına docs-only PR ile taşınmaktadır. Kod, workflow ve Gradle dosyaları bu işin kapsamı dışındadır.
> Kalıcı hedef: `docs/DAL_TEMIZLIGI.md`, `kariyer-modulu` dalı. Dal düzeni konusunda bu dosyaya uy; `docs/NOT_C1.md` ve `docs/TALIMAT.md`'nin diğer kuralları aynen geçerlidir.

## Kurallar (26.11.2026 kararı)
1. **İki ana dal hedefi:** `kariyer-modulu` (çalışan hat ve varsayılan dal) ile `main` (derlenmeyen/yarım kopya; bu PR'da değiştirilmez). Uzun vadeli karar dal sayısını bu iki ana dala indirmektir.
2. **Henüz silinmeyen geçiş dalları:** `film-dizi-v2` (PR #1, #2, #3 ile ilişkili), `cline/K1` ve `durum`. 26.11.2026 itibarıyla bunlar silinmedi. Bağımlılık ve açık PR kontrolü yapılmadan silme yok; bu docs-only PR bu dallara dokunmaz.
3. Tek iş = tek dal = tek PR. Aynı iş için `-clean`, `-final`, `-validated`, `-v2` kopya dalı açma; aynı dala commit at.
4. PR birleşince ya da kapanınca dalın silinebilirliği ayrıca kontrol edilir; silme yalnız açık talimat ve SHA doğrulamasıyla yapılır.
5. **PR #37 açık durumdadır** (26.11.2026 GitHub API kontrolü); tabanı `cline/K1`, baş dalı `fix/hakem-diff-lines`. Birleştirilmedi ve kapatılmadı; açık PR'ın baş dalı olduğu için korunur.
6. PR #56'nın eski tabanı ayrıca denetlenmelidir. Bu kayıt tek başına merge yetkisi vermez.
7. Kökteki `DURUM.md` ve `AGENTS.md` 31 Ekim 2026 OpenCode ortamından kalma eski dokümanlardır. Bu PR'da değiştirilmez; geçerli çalışma notları `docs/TALIMAT.md` + `docs/NOT_C1.md`'dir.
8. `docs/HARITA.md` küçültülmeyecek; boyut/sadeleştirme ayrı, tek amaçlı PR konusudur.

## Açık PR QA — 25.11.2026 (yalnız CI/çakışma; diff incelenmedi)
- #57 H2 kaynakları → `cline/K1`: taslak, 5/5 check yeşil, çakışmasız.
- #58 e-Şube yenileme → `cline/K1`: taslak, 4/4 check yeşil.
- Olası yinelenenler (doğrulanmadı): #35 ↔ #42 (H1), #18 ↔ #25 (tema), #24 (K1-B/K1-C zaten `cline/K1`'de).
- Açık PR sayısı 25 (#37 hariç 24).

## Önceki plan: silinecek dallar (32) — TARİHSEL KAYIT, GÜNCEL TALİMAT DEĞİL
`*` = yerine geçen PR adından/tarihinden çıkarım; commit'ler PR'da durur.

| Dal | SHA | PR | Durum |
|---|---|---|---|
| aries-career-monitor | 701e0bb | #5 | birleşti (kariyer-modulu) |
| career-public-private-separation | 5efc2c4 | #6 | birleşti (kariyer-modulu) |
| career-ui-dedup-separation | 7858dbc | #7 | birleşti (kariyer-modulu) |
| fix/hakem-added-diff-only | 9178c8d | #39 | birleşti (cline/K1) |
| fix/hakem-diff-lines | 0bae86d | #37 | #39 ile yinelenen; 26.11.2026 API kontrolünde #37 hâlâ açık, dal korunuyor |
| is/C1-1-status-20261125 | 49dbd3f | #55 | kapalı; yerine #56 |
| is/H2-clean-20261124 | 06f379a | #50 | kapalı; yerine #51* |
| is/H2-core-20261124 | e3cffee | #51 | birleşti (cline/K1) |
| is/H2-merge-20261125 | 6ac2235 | #53 | birleşti (cline/K1) |
| is/H2-verified-reader | 91642e7 | #40 | kapalı taslak; yerine #51* |
| is/K1-B-clean-20261124 | ece04d4 | #45 | kapalı; #46 ile aynı commit |
| is/K1-B-final-20261124 | ece04d4 | #46 | birleşti (cline/K1) |
| is/K1-B-wide | 7fcee22 | #36 | kapalı; yerine #46* |
| is/K1-C | 04afb7e | #38 | kapalı; yerine #49* |
| is/K1-C-clean-20261124 | 2108453 | #47 | kapalı; yerine #49* |
| is/K1-C-final-20261124 | 8d7d801 | #48 | kapalı; #49 ile aynı commit |
| is/K1-C-validated-20261124 | 8d7d801 | #49 | birleşti (cline/K1) |
| is/K1-F2 | ac887d8 | #33 | kapalı; yerine #44 |
| is/K1-F2-clean-20261124 | ec47951 | #44 | birleşti (cline/K1) |
| is/K1-test-workflow | ae2303f | #34 | birleşti (cline/K1) |
| is/kadro-radari-ve-kpss-2026-2 | 50aac14 | #10 | birleşti (kariyer-modulu) |
| is/kamu-kariyer-arama-ayristirma | 93c7b58 | #8 | birleşti (kariyer-modulu) |
| is/kariyer-google-grounding-kalite-kontrol | 34a65cf | #20 | kapalı taslak; yerine #22* |
| is/kariyer-grounded-arama-temiz | efdd270 | #21 | kapalı taslak; yerine #22* |
| is/kariyer-platform-derinlestirme | 309df90 | #11 | birleşti (kariyer-modulu) |
| is/not-c1-v3 | 6543de3 | #32 | birleşti (durum) |
| is/v3-progress-20261124 | eded5d3 | #41 | kapalı; yerine #43* |
| is/1-18-kariyer-guvenilirlik-ve-akilli-tarih | b5dc61e | #12 | birleşti (kariyer-modulu) |
| is/1-19-ilan-duyuru-ayrimi-sirket-arastirmasi | 47ff8b4 | #13 | birleşti (kariyer-modulu) |
| is/1-20-default-feed-and-source-evidence | 55d44d3 | #19 | kapalı taslak; yerine #22* |
| is/1-20-kariyer-ilan-dogrulama | ec57f35 | #15 | birleşti (kariyer-modulu) |
| is/1-20-tarih-ayristirma-duzeltmesi | c01b21a | #16 | birleşti (kariyer-modulu) |

## Önceki listede dokunulmadı — tarihsel kayıt
- `is/H2-final-20261124`: #52 birleşti ama dal PR'dan sonra ilerlemiş (PR başı 6743599, dal d6e6911); ilerleyen commit'in `cline/K1`'de olup olmadığı doğrulanmadı.
- `is/1-20-ilan-dogrulama-aktiflik`: #14 kapalı, dal PR'dan sonra ilerlemiş.
- `is/K1-F2-rebased`, `feature/aries-finance-center`, `fix-media-url`: PR'sız dallar, amaçları belirsiz.
- Önceki sayımdaki “Kalan 36 dal” ifadesi güncel değildir. Yeni iki-ana-dal kararı ve 26.11.2026 dal işlemlerinden sonraki toplam, GitHub dal listesi yeniden sayılarak raporlanmalıdır.

## Önceki temizlik kaydı — 26.11.2026 (tarihsel; yeni 13 dal kaydından ayrı)

- Cem, GitHub CLI üzerinden SHA kontrollü `gh api DELETE` işlemiyle aşağıdaki 31 dalı sildi.
- Bu bağlantıyla yapılan güncel GitHub dal listesi kontrolü: **56 dal**. Hedef 31 dalın mevcut olanı: **0**.
- **Silinen: 31 / kalan: 56 / atlanan: 1.**
- Tek atlanan dal: `fix/hakem-diff-lines`. PR #37 açık ve bu dal PR'ın başı olduğu için korundu. PR #37'ye dokunulmadı.
- Bu 31 dal kaydı önceki temizlik turunun tarihsel raporudur; 26.11.2026 tarihli yeni 13 dal işlemiyle aynı liste olduğu varsayılmaz.
- `--force` kullanılmadı. PR #76 için bu eski kayıttaki son durum “açık/birleştirilmemiş”tir; yeni dal kararı merge yetkisi oluşturmaz.

### Silindi (31)
1. `aries-career-monitor`
2. `career-public-private-separation`
3. `career-ui-dedup-separation`
4. `fix/hakem-added-diff-only`
5. `is/C1-1-status-20261125`
6. `is/H2-clean-20261124`
7. `is/H2-core-20261124`
8. `is/H2-merge-20261125`
9. `is/H2-verified-reader`
10. `is/K1-B-clean-20261124`
11. `is/K1-B-final-20261124`
12. `is/K1-B-wide`
13. `is/K1-C`
14. `is/K1-C-clean-20261124`
15. `is/K1-C-final-20261124`
16. `is/K1-C-validated-20261124`
17. `is/K1-F2`
18. `is/K1-F2-clean-20261124`
19. `is/K1-test-workflow`
20. `is/kadro-radari-ve-kpss-2026-2`
21. `is/kamu-kariyer-arama-ayristirma`
22. `is/kariyer-google-grounding-kalite-kontrol`
23. `is/kariyer-grounded-arama-temiz`
24. `is/kariyer-platform-derinlestirme`
25. `is/not-c1-v3`
26. `is/v3-progress-20261124`
27. `is/1-18-kariyer-guvenilirlik-ve-akilli-tarih`
28. `is/1-19-ilan-duyuru-ayrimi-sirket-arastirmasi`
29. `is/1-20-default-feed-and-source-evidence`
30. `is/1-20-kariyer-ilan-dogrulama`
31. `is/1-20-tarih-ayristirma-duzeltmesi`

### Atlandı (1)
- `fix/hakem-diff-lines` — PR #37 açık; dal korunuyor.


## 26.11.2026 — 13 dalın SHA doğrulamalı silinmesi (Cem bildirimi)

Cem'in bildirdiği son işlem: **13 dal, SHA eşleşmesi doğrulanarak silindi.** Dal adları ve silme öncesi SHA değerleri Cem tarafından sağlanacaktır; bu nedenle isim veya SHA uydurulmamıştır.

| # | Dal adı | Silme öncesi beklenen SHA | Kayıt |
|---:|---|---|---|
| 1–13 | Cem'in listesi beklenecek | Cem'in SHA listesi beklenecek | Liste gelince tek tek tamamlanacak |

Bu 13 dal kaydı, yukarıdaki tarihsel 31 dal raporuyla birleştirilmemelidir. İki listenin kapsamı karşılaştırılmadan toplam silinen dal sayısı çıkarılmaz.

## 26.11.2026 — iki ana dala inme kararı

- Hedef ana dallar: `kariyer-modulu` + `main`.
- `kariyer-modulu`: çalışan hat ve varsayılan dal.
- `main`: derlenmeyen/yarım kopya; korunur, bu PR'da değiştirilmez.
- `film-dizi-v2` (#1, #2, #3 bağlantılı), `cline/K1` ve `durum`: henüz silinmedi. Bağımlılık/PR kontrolü yapılmadan silme yok.
- Eski “korunan dallar” listesi iki-ana-dal kararına göre güncellenmiştir. Yukarıdaki üç geçiş dalı kalıcı koruma değil, henüz temizlenmemiş durum olarak izlenir.
