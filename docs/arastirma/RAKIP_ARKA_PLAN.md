# Rakipler arka planda nasıl çalışıyor — 25 Kasım 2026 (Claude, 2. tur)

CHATGPT_GELISTIRME_NOTU.md'nin devamıdır. Soru: Aries'te olmayıp rakiplerde olan özellikler nasıl çalışıyor, ne kullanıyorlar, Aries neden geride kaldı? Kaynaklar sonda. “Çıkarım” işareti Claude'un yorumudur, kaynaklı bilgi değildir.

## 1. Borsa

**Ghostfolio (açık kaynak portföy takibi).** NestJS + Angular + Prisma + Nx + TypeScript; PostgreSQL; Redis destekli arka plan veri toplama kuyrukları; Yahoo Finance, CoinGecko, Alpha Vantage gibi birden çok sağlayıcı. Sağlayıcı isteklerinin zaman aşımı varsayılan 2000 ms. Premium gelir “profesyonel veri sağlayıcıları” ve altyapıyı karşılıyor.
- Aries'e ders: veri çekme istemci telefonda değil, kuyruklu sunucu işinde olmalı; her sağlayıcıya kısa zaman aşımı; ücretsiz kaynak + yedek sıra. Aries'in Actions cron → JSON → Worker hattı bunun hafif karşılığı (çıkarım).

**KAP (Kamuyu Aydınlatma Platformu).** KAP verisi web sitesi, mobil uygulama, KAP Veri Yayın Hizmeti ve SWIFT üzerinden dağıtılıyor; veri yayın kuruluşları da eş zamanlı yayınlıyor. Ana sayfa yalnız o günkü son 50 bildirimi gösteriyor, toplu indirme 150 bildirimle sınırlı. Açık kaynak sarmalayıcılar: `kap-client` (şirket/fon bildirimleri; API başlangıç ve bitiş tarihinin aynı takvim yılında olmasını istiyor → yıl yıl döngü), `pykap` (finansal raporları ayrıştırır), `kap-mcp`, `borsapy` (TradingView OHLCV/akış/sinyal + KAP duyuruları + TEFAS + TCMB EVDS).
- Çıkarım: bu sarmalayıcılar resmî sözleşmeli API değil, KAP'ın sitesinin uç noktalarını kullanıyor olabilir; kullanım şartı ve robots kontrolü yapılmadan Aries'e alınmamalı (NOT_C1'deki robots disiplini burada da geçerli).

## 2. İş & Kariyer

**Simplify / ApplyTalon (otomatik doldurma).** Simplify Workday, Lever, Greenhouse, iCIMS, Taleo gibi ATS sitelerinde formu dolduruyor, özgeçmişteki eksik anahtar kelimeleri gösteriyor, yapay zekâ ile özgeçmiş/kapak mektubu üretiyor. ApplyTalon her ATS için ayrı “adapter” yazdığını söylüyor (Workday, Greenhouse, Lever, Ashby, SmartRecruiters…) ve verinin tarayıcıda kaldığını, yapay zekâ metni üretmediğini belirtiyor.
- Ders: arka plandaki sır tarayıcı eklentisi + ATS başına form adaptörü. Android uygulamada eklenti yok; Aries için karşılığı: “paylaş-hedefi” ile ilanı alıp özetlemek, Custom Tabs ile resmî sayfaya yollamak. Otomatik gönderme eklenmez (Aries kuralı zaten onay şartı koyuyor).

**AIHawk (otomatik başvuru ajanı).** LLM ile başvuruyu kişiselleştiriyor; etiketlerine göre Selenium/kazıma kullanıyor; otomatik başvuru özelliği artık açık kaynak değil, ücretli bir iş sitesine (laboro.co) taşındı; basın hataların ve riskin altını çizdi. Başka bir projede “taslak yazan + inceleyen” iki ajan deseni var: bir ajan özgeçmiş/mektup taslağı yazıyor, ikincisi denetliyor; boşluğu dürüstçe kapatamıyorsa bunu söylüyor.
- Ders: kitlesel otomatik başvuru hem hata hem hesap riski; değerli olan “taslak + denetçi” deseni. Aries'in AGENTS.md'sindeki yazıcı/denetçi düzeni ürünün içine de uyarlanabilir: CV uyarlama → denetçi her cümleyi ana CV'deki kanıtla karşılaştırır (çıkarım).

## 3. Film

**Jellyfin.** Üstveri birden çok sağlayıcıdan geliyor (varsayılanlar + eklenti kataloğundan TheTVDB, fanart.tv, AniDB; kendi sağlayıcını yazmak için Plugin API). Sağlayıcı önceliği kütüphane başına ayarlanıyor; diziler için varsayılan TMDB. “TheIntroDB” eklentisi TMDB kimliğiyle giriş/özet/jenerik zaman damgalarını çekip Jellyfin'in “Media Segments” özelliğine yazıyor; oynat'a basınca değil, zamanlanmış tarama görevinde dolduruluyor.
- Ders: sağlayıcı soyutlaması + önceden hesaplama. Aries'te film verisi tek TMDB istemcisine bağlı; sağlayıcı kayıt defteri (TMDB, TVMaze, OMDb…) ve “ağır işi önceden yap” ilkesi kazanım.

**Öneri sistemleri (açık kaynak örnekler).** Frameo: pgvector ile film + kullanıcı zevk gömmeleri üzerinde kosinüs benzerliği, nedenleri Gemini yazıyor, sonuçlar Redis'te 30 dk önbellekte. Movie-RecSys: serbest metindeki “ruh halini” Gemini yapılandırılmış filtreye çeviriyor, sıralamayı klasik model (TF-IDF içerik tabanlı, işbirlikçi filtreleme) yapıyor, cevap bu sonuçlara dayanıyor. CineMatch ve benzeri projeler TMDB'de oy sayısı eşiği koyup özet+tür+anahtar kelimeyi tek metne birleştiriyor; TF-IDF ve kosinüs benzerliği önceden hesaplanıyor.
- Ders: LLM sıralamayı yapmıyor; yalnız niyeti anlıyor ve gerekçeyi yazıyor. Sıralama deterministik ve ucuz. Aries için 0 TL uyumlu yol: TMDB üstverisinden cihazda/sunucuda TF-IDF (vektör veritabanı gerekmez) + LLM yalnız “neden” cümlesi için (çıkarım).

## 4. Asistan çekirdeği

**Home Assistant Assist.** Boru hattı: uyandırma kelimesi → konuşmayı metne çevirme → niyet tanıma → seslendirme. Sohbet ajanı metni bir niyete eşliyor; entegrasyonlar kendi ajanlarını ekleyebiliyor (LLM dahil). Telefonlarda uyandırma kelimesi cihazda (microWakeWord) çalışıyor, ses yalnız uyanınca gönderiliyor. Bileşenleri “Wyoming” adlı küçük JSON-TCP protokolü birbirine bağlıyor. Tamamen yerel bir hat (Speech-to-Phrase + Piper) yalnız temel komutları kaldırıyor; gelişmiş konuşma için LLM ajanı ekleniyor.
- Ders: **önce kural/niyet yolu, LLM yedek.** Bilinen komut (alarm, hatırlatma, arama) milisaniyede ve ücretsiz çözülür; yalnız kalanı LLM'e gider. Aries'te `Commands.kt` zaten böyle bir katman; bunu açık bir “niyet → araç → LLM yedek” sıralamasına çevirmek hız ve maliyet kazandırır (çıkarım).

**Pipecat (gerçek zamanlı ses ajanı çerçevesi).** Kare tabanlı boru hattı: VAD → STT → LLM → TTS; her şey paralel: LLM sonraki cümleleri üretirken önceki cümleler zaten seslendiriliyor. LLM'in token akışı TTS için cümle/parça boyutuna yeniden bölünüyor. Üretim örneklerinde yankı bastırma (bot konuşurken mikrofonu kapatma) ve sözünü kesme (barge-in) kapıları var. Bir yazara göre ses ajanının işinin ~%80'i modelden bağımsız.
- Ders: “hızlı hissettirme” streaming + cümle bazlı seslendirmeden geliyor. Aries'te streaming yok (B1 bekliyor); cümle bitince Android TTS kuyruğuna atmak büyük algılanan hız kazancı (çıkarım). Sözünü kesme ve yankı bastırma ayrıca tasarlanmalı.

## 5. Aries neden geride kaldı? (çıkarım; HARITA/NOT_C1 notlarına dayanır, kanıt değil)

1. **Süreç ağırlığı > özellik hızı.** Her iş ayrı dal+PR+CI+hakem+telefon doğrulaması gerektiriyor; C1 eşiği (10 kaynak/100 ilan) diğer her şeyi durdurmuş durumda. Kalite için iyi, hız için pahalı.
2. **Dallar birleşmedi.** Özellik kodu üç dalda; `main` eski. Rakiplerde tek sürüm olduğu için “var” sayılıyorlar.
3. **Altyapı sürtünmesi.** Private depoda Actions koşucusu atanmadı (`runner_id=0`), günlerce test koşulamadı; ücretsiz public runner ile dolanıldı.
4. **Kırılgan veri kaynakları.** Arama motoru kazıma, kapalı/girişli siteler, robots 401. Rakipler resmî API'ye, lisanslı veriye veya aylarca biriktirilmiş veri katmanına dayanıyor.
5. **Ücretsiz + zayıf model zinciri.** Küçük modellere küçük işler verilmesi güvenli ama mimari işleri yavaşlatıyor (AGENTS.md: zor iş Claude'a).
6. **Tek kişi, 0 TL.** Rakiplerin çoğunda ekip veya ücretli veri var.

## Kaynaklar

- Ghostfolio: https://github.com/casitu/ghostfolio · https://openapps.pro/apps/ghostfolio · https://github.com/dtslvr/ghostfolio · https://cdn.jsdelivr.net/gh/ghostfolio/ghostfolio@main/README.md
- KAP: https://www.mkk.com.tr/en/corporate-governance-services/kap-public-disclosure-platform · https://kap.org.tr/tr/about/genel-bilgi · https://kap.org.tr/tr/api/about/content-file/402832a195b3017d0195bd17e2e07e9a · https://pypi.org/project/kap-client/ · https://github.com/cemsinano/pykap · https://pypi.org/project/kap-mcp/ · https://docsearch.algolia.com/mcp/docs/repo/saidsurucu/borsapy
- Simplify: https://chromewebstore.google.com/publisher/simplify-jobs/ua5e47b24fb6adb335148b99574a58244 · ApplyTalon: https://addons.mozilla.org/firefox/addon/applytalon/
- AIHawk: https://www.sourcepulse.org/projects/1831324 · https://awesome.ecosyste.ms/projects/github.com%2Ffeder-cr%2FJobs_Applier_AI_Agent_AIHawk · https://dev.to/max_quimby/ai-job-search-githubs-hottest-agent-applies-for-you-34nf
- Jellyfin: https://jellyfin.org/docs/general/server/metadata · https://github.com/TheIntroDB/jellyfin-plugin
- Öneri sistemleri: https://github.com/notinsane-dev/Frameo · https://github.com/jadzoghaib/Movie-RecSys · https://nlp.fi.muni.cz/aiproject/ui/macka_libor2026/cinematch.pdf · https://othman-shbeir.github.io/projects/2_project
- Home Assistant: https://developers.home-assistant.io/docs/voice/pipelines/ · https://developers.home-assistant.io/docs/voice/overview · https://www.home-assistant.io/voice_control/about_wake_word · https://www.home-assistant.io/blog/2025/10/22/voice-chapter-11 · https://dev.to/kunal_d6a8fea2309e1571ee7/local-ai-voice-assistant-stack-2026-whisper-piper-ollama-wired-together-572l
- Pipecat: https://docs.pipecat.ai/pipecat/learn/overview.md · https://docs.nvidia.com/nemo/labs-voice-agent/about/architecture · https://protolabsai.github.io/protoVoice/reference/pipeline-shape.html · https://dev.to/mohamedamine_benhima/what-makes-pipecat-different-from-other-voice-agent-frameworks-2n0
