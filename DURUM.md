# DURUM — Aries

## Son güncelleme: 25 Kasım 2026

## Yetkili takip dosyaları
- İş sırası ve kararlar: `docs/HARITA.md`
- Sert kurallar ve H1/C1 kabul kapıları: `docs/NOT_C1.md`
- Mimari envanter ve düzenleme planı: `gorevler/ARIES-MIMARI-DAGINIKLIK-2026-11-25.md`

## Aktif iş
- **H1 PR #71:** HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 10 dosya / +2.578 / -26; 20 model +10 store +4 Android instrumentation =34 test.
- Exact-head APK/unit [#45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) SUCCESS.
- Exact-head Android emulator/SQLite [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) son kontrolde IN_PROGRESS; test PASS sayılmaz. Gerçek telefon testi ayrıca gerekli. PR taslak/açık; merge yok.
- **C1 PR #70:** exact-head [#45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) SUCCESS ve yalnız kaynak/UA kapsamı için öz-QA ONAY. H1 kabulinden sonra sıradaki iş.
- **C1 private PR #11:** RED; son doğrulanmış kamu sayımı 12 kaynak /55 açık ilan; 100 ilan eşiğine 45 eksik. Production Worker authenticated GET/D1 ve şehir kanıtlı cross-source dedup eksik.

## Mimari envanter özeti
- `CareerActivity.kt` yaklaşık 2.795 satır; `CareerMonitor.kt` yaklaşık 2.049 satır. `CareerMonitor.kt` altı sorumluluk grubunu tek dosyada tutuyor.
- `MainActivity.kt` yaklaşık 1.802 satır; `Ai.kt` yaklaşık 735 satır ve birden çok ağ/arama görevini barındırıyor.
- Media klasöründe 5 eski `.pre-*`/yedek dosya var; bu H1/C1 iş akışında değiştirilmeyecek.
- Eski `AGENTS.md`/`DURUM.md` talimatları güncellendi. `denetle.py` Groq'a diff gönderdiği için güvenlik incelemesi tamamlanana dek çalıştırılmaz.

## Çalışma kuralı
H1 exact-head emülatör sonucu kapanmadan H1 merge yok. H1 kabulinden sonra PR #70. Ardından C1'in 10 kaynak /100 doğrulanmış ilan ve production GET/D1 kapıları. Film/tema alanı bu iş akışına dahil değildir.
