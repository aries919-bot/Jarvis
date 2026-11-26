# DAL TEMİZLİĞİ — 25.11.2026

> Hazırlayan: Claude. GitHub bağlantısı salt okunur olduğu için QA okumayla yapıldı; dalları Cem SHA doğrulamalı betikle siler (`--force` yok). Kod ile `main`, `durum`, `cline/K1`, `kariyer-modulu`, `film-dizi-v2` içeriği değişmedi.
> Kalıcı hedef: `docs/DAL_TEMIZLIGI.md`, `durum` dalı. Dal düzeni konusunda bu dosyaya uy; `NOT_C1.md` ve `TALIMAT.md`'nin diğer kuralları aynen geçerlidir.

## Kurallar (ChatGPT bozmasın)
1. **KORUNAN dallar** — silme, yeniden adlandırma, force-push yok: `main`, `durum`, `cline/K1`, `kariyer-modulu`, `film-dizi-v2`, `codespace-yedek`, `derleme-1`, `qa/c1-private-runner` ve açık PR'ı olan her dal (baş ya da taban).
2. Aşağıdaki 32 dalı kullanma, aynı adla yeniden oluşturma, PR'larını yeniden açma. İçerikleri birleşmiş (`cline/K1`, `kariyer-modulu`, `durum`) ya da yerine geçen PR'dadır. Geri almak gerekirse PR sayfasında "Restore branch" ya da `refs/pull/<no>/head`.
3. Tek iş = tek dal = tek PR (`is/<iş-id>-<tarih>`). Aynı iş için `-clean`, `-final`, `-validated`, `-v2` kopya dalı açma; aynı dala commit at. (K1-B ×3, K1-C ×4, K1-F2 ×3, H2 ×5 bu yüzden birikti.)
4. PR birleşince ya da kapanınca dalını bu dosyada "Silinebilir" bölümüne ekle; silmeyi Cem yapar.
5. PR #37 kapalı kalır: aynı düzeltme #39 ile birleşti.
6. PR #56'nın tabanı eski: `durum` o günden beri ilerledi (NOT_C1 §8–§11 eklendi). Birleştirmeden önce güncel `durum` üstüne kur, çakışmayı çöz. #56'daki birleştirme yetkisi kuralı (cline/K1'e otomatik, main'e Cem onayı) `durum`da henüz yok; o zamana kadar yazılı kural NOT_C1 §6'dır.
7. Kökteki `DURUM.md` ve `AGENTS.md` 31 Ekim'deki OpenCode ortamını anlatır (eski). Geçerli kurallar: `docs/TALIMAT.md` + `docs/NOT_C1.md`.
8. `docs/HARITA.md` ≈61 KB; TALIMAT "≈100 satır" der. Ayrı, tek amaçlı PR'da küçült.

## Açık PR QA — 25.11.2026 (yalnız CI/çakışma; diff incelenmedi)
- #57 H2 kaynakları → `cline/K1`: taslak, 5/5 check yeşil, çakışmasız.
- #58 e-Şube yenileme → `cline/K1`: taslak, 4/4 check yeşil.
- Olası yinelenenler (doğrulanmadı): #35 ↔ #42 (H1), #18 ↔ #25 (tema), #24 (K1-B/K1-C zaten `cline/K1`'de).
- Açık PR sayısı 25 (#37 hariç 24).

## Silinecek dallar (32)
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

## Dokunulmadı — karar Cem/ChatGPT'de
- `is/H2-final-20261124`: #52 birleşti ama dal PR'dan sonra ilerlemiş (PR başı 6743599, dal d6e6911); ilerleyen commit'in `cline/K1`'de olup olmadığı doğrulanmadı.
- `is/1-20-ilan-dogrulama-aktiflik`: #14 kapalı, dal PR'dan sonra ilerlemiş.
- `is/K1-F2-rebased`, `feature/aries-finance-center`, `fix-media-url`: PR'sız dallar, amaçları belirsiz.
- Kalan 36 dal: 8 korunan + 23 açık PR'lı dal + yukarıdaki 5.

## Silinebilir (sonradan eklenecek)

### 26.11.2026 — GitHub anlık SHA/PR doğrulaması
- İncelenenler: 86 dal ve 31 açık PR. Tablodaki 32 dalın tamamı bulundu; **32/32 SHA öneki güncel tam SHA ile eşleşti**, SHA uyuşmazlığı 0.
- **Cem'in SHA doğrulamalı silme betiğine uygun görünen 31 dal:** aşağıdaki tabloda tam SHA'ları verildi. Hiçbiri açık PR'ın başı veya tabanı değil. Bu QA çalışmasında **silinen dal sayısı 0**; §4 uyarınca silme Cem tarafından yapılır. `--force` kullanılmadı.

| Dal | Güncel tam SHA | Kontrol |
|---|---|---|
| aries-career-monitor | 701e0bb35ffe77c9641bf45725edd41a8f69b988 | SHA eşleşti; açık PR yok |
| career-public-private-separation | 5efc2c48f53d1d68ea5542b9b0bbcaa07ead65d2 | SHA eşleşti; açık PR yok |
| career-ui-dedup-separation | 7858dbc025d03097f176feeb8bd48dbfac524bee | SHA eşleşti; açık PR yok |
| fix/hakem-added-diff-only | 9178c8d8caa8b9fa8cb7340b79b3cf8316ec74ec | SHA eşleşti; açık PR yok |
| is/C1-1-status-20261125 | 49dbd3f3720c15a1f0278b20383407fe08801d3a | SHA eşleşti; açık PR yok |
| is/H2-clean-20261124 | 06f379a119334430379f50b82afc365bab4d8e7e | SHA eşleşti; açık PR yok |
| is/H2-core-20261124 | e3cffee63ba449aed4c1f80e22146b319df50596 | SHA eşleşti; açık PR yok |
| is/H2-merge-20261125 | 6ac2235e06efc50f636b3e0927e51424b86d19dc | SHA eşleşti; açık PR yok |
| is/H2-verified-reader | 91642e74fce880fad67da74bb376f7a49ab29b9b | SHA eşleşti; açık PR yok |
| is/K1-B-clean-20261124 | ece04d4e672234f15a7d1677828a2831fd0d59bd | SHA eşleşti; açık PR yok |
| is/K1-B-final-20261124 | ece04d4e672234f15a7d1677828a2831fd0d59bd | SHA eşleşti; açık PR yok |
| is/K1-B-wide | 7fcee2257e4dfd92bde994e4b6437de1a9f796c4 | SHA eşleşti; açık PR yok |
| is/K1-C | 04afb7e132849f29b0f9e4d30c80890ea86b053b | SHA eşleşti; açık PR yok |
| is/K1-C-clean-20261124 | 21084530ae32fb0fb4d56a916d5b2a224b4f01b3 | SHA eşleşti; açık PR yok |
| is/K1-C-final-20261124 | 8d7d801f69cbd235ca5965ec8cd7c5e274af17d2 | SHA eşleşti; açık PR yok |
| is/K1-C-validated-20261124 | 8d7d801f69cbd235ca5965ec8cd7c5e274af17d2 | SHA eşleşti; açık PR yok |
| is/K1-F2 | ac887d8b049f73e65dfeaba2b914cb07b9c1d4f5 | SHA eşleşti; açık PR yok |
| is/K1-F2-clean-20261124 | ec4795178f5a81711d6bd9fdcc0994e794724ee3 | SHA eşleşti; açık PR yok |
| is/K1-test-workflow | ae2303f4b85c056d8ea27cfebe1422ed28a0cd1b | SHA eşleşti; açık PR yok |
| is/kadro-radari-ve-kpss-2026-2 | 50aac1424d5e90502a676cf487ab7c82d5f8d4b3 | SHA eşleşti; açık PR yok |
| is/kamu-kariyer-arama-ayristirma | 93c7b5800f7a64668724e67ba739187608700ba3 | SHA eşleşti; açık PR yok |
| is/kariyer-google-grounding-kalite-kontrol | 34a65cf60eb0282fbec920309d4fe8198b713ecf | SHA eşleşti; açık PR yok |
| is/kariyer-grounded-arama-temiz | efdd2709d4ac86e4eadd253e97ed9aced83a0fee | SHA eşleşti; açık PR yok |
| is/kariyer-platform-derinlestirme | 309df90fa56e49940af15fd8f84f04ca8a05270f | SHA eşleşti; açık PR yok |
| is/not-c1-v3 | 6543de3110fa9d8366bdc89a25d0a9677ff1efbf | SHA eşleşti; açık PR yok |
| is/v3-progress-20261124 | eded5d3910a69da807ef395b64af3bebc2a77e6b | SHA eşleşti; açık PR yok |
| is/1-18-kariyer-guvenilirlik-ve-akilli-tarih | b5dc61e3d1dfc2c6802a5869d7cc38c7a941efe0 | SHA eşleşti; açık PR yok |
| is/1-19-ilan-duyuru-ayrimi-sirket-arastirmasi | 47ff8b4eca9ba0d65438bc5906c04e44a6deebdd | SHA eşleşti; açık PR yok |
| is/1-20-default-feed-and-source-evidence | 55d44d3d906a8d441d174fa8afe150d60a8a87db | SHA eşleşti; açık PR yok |
| is/1-20-kariyer-ilan-dogrulama | ec57f35c3cccd3c2287220cc743a76bd5b5073a3 | SHA eşleşti; açık PR yok |
| is/1-20-tarih-ayristirma-duzeltmesi | c01b21a03f40199343ffec419986194867979ae3 | SHA eşleşti; açık PR yok |

- **Atlandı (1):** `fix/hakem-diff-lines` — beklenen `0bae86d`, güncel `0bae86dc33d4d15041723a622f57e151726befdf` ile eşleşiyor; ancak GitHub API #37'yi hâlâ açık ve bu dalı PR başı gösteriyor. Açık PR koruma kuralı nedeniyle silinemez. Kaynak tablodaki "#37 kapalı" notu güncel API durumuyla çelişiyor; #37 kapatılmadı.
- **Özet:** silinen 0 / Cem'in betiği için koşulları doğrulanan 31 / açık PR nedeniyle atlanan 1 / SHA uyuşmazlığı 0. Bu kayıt silme işlemi değildir; silme sonrası GitHub'dan tekrar doğrulanmalıdır.

### 26.11.2026 — Kullanıcı yetkisiyle silme talebi / uygulama engeli
- Kullanıcı, §4'teki "silmeyi Cem yapar" kuralını bu iş için açıkça geçersiz kıldı ve 31 dalın silinmesine izin verdi; "MERGE YOK" şartı korundu.
- Silme öncesi aynı kontrolde 87 dal ve 31 açık PR listelendi. İstenen 31 dalın **31/31'i tam SHA ile eşleşti**; her biri açık PR listesinin hem head hem base tarafında aranıp açık PR bulunmadı. SHA uyuşmazlığı 0; PR'ı açık olduğu için atlanan dal 0.
- **Gerçek silme işlemi yapılamadı:** mevcut GitHub bağlantısında dal/ref silme eylemi yok; yalnızca okuma, dal oluşturma ve dosya/PR işlemleri mevcut. fetch yalnızca GET destekliyor. Güvenli bir DELETE çağrısı veya alternatif/force yöntemi denenmedi.
- **Silinen 0 / atlanan 31 (entegrasyon yeteneği yokluğu nedeniyle) / SHA uyuşmazlığı 0 / açık PR nedeniyle atlanan 0.** Silme sonrası doğrulama yapılamadı; 31 dalın tamamı son kontrolde hâlâ mevcuttu.
- Bu kayıt PR #76'nın dalında güncellendi; PR açık kalır, **merge edilmez**.
