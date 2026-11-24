# K1-B — kaynak yoklama workflow'u

Yalnız tek dosya oluştur: `.github/workflows/kaynak-yokla.yml`.

- Tetikleyiciler: `workflow_dispatch` ve `push` (branches: [`cline/K1`], paths: yalnız `.github/workflows/kaynak-yokla.yml`).
- User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`; tarayıcı taklidi yok.
- Şu üç adres için robots.txt, ana sayfa ve /sitemap.xml durumunu ölç: `https://www.ilan.gov.tr`, `https://kariyerkapisi.gov.tr/isealim`, `https://www.resmigazete.gov.tr`.
- Her istek için `curl -sS -w` ile HTTP kodu, indirilen bayt ve hata bilgisini yazdır. Robots.txt izin vermiyorsa ilgili site yoluna gitme.
- ilan.gov.tr için ayrıca `openssl s_client -connect www.ilan.gov.tr:443 -servername www.ilan.gov.tr` çıktısından sertifika zincirini raporla.
- TLS doğrulamasını kapatma; `curl -k` / `--insecure` kullanma.
- Sonuçları `GITHUB_STEP_SUMMARY` içine yaz: her adres için robots / HTTP / bayt / hata.
- Başka dosyaya dokunma. Commit mesajı: `ci: kaynak yoklama`. `cline/K1` dalına push et.
- Bitiş kanıtı: Actions yeşil ve özet her adres için robots/HTTP/bayt/hata alanlarını gösteriyor.
