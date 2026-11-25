# ARIES-CORE-CAREER-MEDIA-AUDIT-20261125

## Görev
Aries çekirdek dosyaları, İş & Kariyer/Haber ve Film & Dizi mimarisini mevcut repo durumuna göre denetle; rakip uygulama, resmî API/doküman ve açık kaynak projeleriyle karşılaştır.

## Başlangıç durumu (önce kontrol edildi)
- Durum kaynağı: durum dalındaki docs/HARITA.md ve docs/NOT_C1.md.
- Repo: aries919-bot/Jarvis.
- Varsayılan main: 63e877d96cb910e6d98d56834ae55ae8edafab2a.
- C1 H2 PR #61: open/draft, base cline/K1, HEAD f2e21dadc2d19ac144814e4f23fef00cf02c4ad9.
- H2 live #45536720514: 15 source records /13 fresh /2 stale /13 total cards; 10 unique institutions /11 verified open listing cards /114 positions.
- e-Şube: 42 open records in separate snapshot; cross-feed dedup/single Worker feed not evidenced.
- General Python tests #45536310829: 154/154 PASS; H2 exact reader #45536720514: 53/53 PASS; deterministic Hakem #45536808964 SUCCESS, but formal GitHub review 0.
- C1 remains RED: 11 verified cards against 100; production Worker GET/D1 missing.
- Research-only for film; no film code edits and no merge/deploy.

## Findings requiring follow-up
1. Main and feature branches carry different code/build versions; produce a single exact-main APK/QA proof.
2. Career source catalog includes Indeed/LinkedIn despite NOT_C1 prohibition.
3. trustedDomains contains broad edu.tr despite a four-host university allowlist.
4. Career news cards use ACTION_VIEW instead of required Custom Tabs.
5. HTML search engine scraping/Chrome-like User-Agent remains in legacy client-side search/research path.
6. CareerActivity, CareerMonitor, MediaSourceBrowser, MainActivity are oversized monoliths.
7. AI research often uses search snippets without fetching/verifying original source.
8. Store writes chats as one SharedPreferences JSON blob; keys are in Preferences while manifest enables backup.
9. Career page reader's DNS/private-IP protection is not proven against DNS rebinding.
10. Media source and security changes are split across unmerged PR #1/#2/#4; PR #4 has no run associated with its current HEAD.
11. WebView has JavaScript/DOM storage/third-party cookie support and a Chrome-like UA; media request headers can include credentials. Restrict by exact origin and provider policy.
12. C1 current evidence in HARITA is older than PR #61; refresh status documentation after source and feed changes settle.

## Rakip araştırması
- İş: Simplify, Huntr, Greenhouse Job Board API, Lever Postings API, Ashby Public Job Posting API, ATS aggregator repositories.
- Film: Jellyfin metadata providers/plugins/scheduled tasks, Stremio addon manifest/catalog/meta/stream/subtitles protocol, TMDb/TVmaze season metadata, Watchmode availability links, Android Media3 HttpEngine/Cronet/OkHttp/cache.
- Asistan çekirdeği: Home Assistant Assist pipeline and Pipecat streaming/frame pipeline.

## Kabul
- Her sayı kaynak logundan yeniden sayılmalı; positions and listing cards remain separate.
- Public hiring data remains a separate report and 0 means RED.
- No forbidden sources, robots bypass, browser impersonation, login/CAPTCHA bypass, SSL-disable, or secret/PII/KPSS/e-Devlet collection.
- Film analysis only; no D1/film implementation ahead of C1.
- No merge/deploy in this audit.
- Final answer format: KARAR, Kanıt, Eksik, Telefonda denenecek, SONRAKİ ADIM.

## Rapor
docs/arastirma/ARIES_CEKIRDEK_KARIYER_FILM_DENETIMI_2026-11-25.md
