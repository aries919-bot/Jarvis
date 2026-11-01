# Is akisi
- Her is ayri dalda: `bash is.sh baslat <ad>`; main'e dogrudan yazma.
- Yazilimci: guclu. Kucuk: yalniz tek dosyalik duzeltme.
- Is bitince denetci (Groq) `git diff main` inceler; onay yoksa duzelt.
- Test gecmeden ve denetim onayi olmadan birlestirme: `bash is.sh bitir`.
- Tavily: gorev basina en fazla 20 arama, basic derinlik. Ayda 1000 kredi var.
- Anahtari/gizli bilgiyi dosyaya veya ciktiya yazma.
# Roller
- guclu: orta/cok dosyali isler. kucuk: yalniz tek dosya, basit isler.
- denetci: yazmaz, sadece ONAY/RED. Onay insandan degil denetciden gelir.
- Zor is (mimari, tasarim): Claude'la planla, sonra uygulat.

## Evren kurali
- Evren (kamu platformu) modellerine Ai.kt (Worker adresi var) ve docs/ klasorunu oldugu gibi verme; gerekirse adres satirlarini cikararak parca ver.
- Anahtar, token ve Worker adresi hicbir modele gitmez.

HARITA KURALI: Her gorevde once HARITA.md ye bak. Dosyayi bastan sona okuma. grep -n ile yeri dogrula, yalniz o araligi oku.

HARITA YENILEME: Kod degistirdiysen isin sonunda sh harita.sh calistir (HARITA.md yi gunceller).
