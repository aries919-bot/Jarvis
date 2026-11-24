# K1-F — açık kimlikli User-Agent

Yalnız şu iki dosyayı düzenle:
- `scripts/render_probe.py`
- `.github/workflows/iskur-render.yml`

Mevcut Chrome/tarayıcı taklidi User-Agent değerini SİLME; açık kimlikli şu değerle değiştir:
`AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`

Başka dosyaya, mantığa, workflow tetikleyicisine veya tarama davranışına dokunma. Robots kurallarına uy; engellenen yolu isteme. Giriş/CAPTCHA aşma yok.

Commit mesajı: `fix: identify Aries source probe user agent`.
Değişikliği `cline/K1` dalına push et.

Bitiş kanıtı:
1. Diff yalnız yukarıdaki iki dosyada ve yalnız User-Agent değişikliği içeriyor.
2. İlgili GitHub Actions çalışması yeşil (SUCCESS).
3. Actions çıktısında kullanılan User-Agent açıkça görünüyor.
