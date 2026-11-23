# K1 - Kariyer Kaynak Denetimi

## Amaç ve Kapsam
K1 işi kamu kariyer kaynaklarının okunabilirliğini ölçer.
Dosyalar: career/sources.json, scripts/career_source_audit.py,
scripts/test_career_source_audit.py, career-audit-report,
.github/workflows/career-source-audit.yml (career-source-audit.yml).

## Kaynak Matrisi

| id | adres | otomasyon | ilan |
|---|---|---|---|
| iskur-acik-is-ilanlari | https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx | manuel | 0 |
| iskur-kurumsal-duyurular | https://www.iskur.gov.tr/duyurular | acik | 8 |
| resmigazete-guncel-sayi | https://www.resmigazete.gov.tr/ | acik | 6 |
| csb-duyurular | https://www.csb.gov.tr/duyurular | manuel | 0 |
| osym-duyurular | https://www.osym.gov.tr/Duyurular | kapali | 0 |
| euraxess-jobs | https://euraxess.ec.europa.eu/jobs/search | acik | 10 |
| eures-find-a-job | https://www.eures.europa.eu/find-a-job_en | kapali | 0 |
| kariyer-kapisi | https://isealimkariyerkapisi.cbiko.gov.tr/ | kapali | 0 |
| unjobs | https://unjobs.org/ | kapali | 25 |

## robots.txt Kuralı ve Sonuçları
robots.txt, RFC 9309 uyarınca okunur; hedef yol için /robots.txt kuralı uygulanır.
Sonuç değerleri: izinli, reddedildi, yok, gecersiz, erisilemedi.

| id | sonuç | robots adresi |
|---|---|---|
| iskur-acik-is-ilanlari | izinli | https://esube.iskur.gov.tr/robots.txt |
| iskur-kurumsal-duyurular | izinli | https://www.iskur.gov.tr/robots.txt |
| resmigazete-guncel-sayi | izinli | https://www.resmigazete.gov.tr/robots.txt |
| csb-duyurular | izinli | https://www.csb.gov.tr/robots.txt |
| osym-duyurular | gecersiz | https://www.osym.gov.tr/robots.txt |
| euraxess-jobs | izinli | https://euraxess.ec.europa.eu/robots.txt |
| eures-find-a-job | erisilemedi | https://www.eures.europa.eu/robots.txt |
| kariyer-kapisi | erisilemedi | https://isealimkariyerkapisi.cbiko.gov.tr/robots.txt |
| unjobs | reddedildi | https://unjobs.org/robots.txt |

## Otomasyon Kararı
Kaynak yalnız robots izinli, giriş istemiyor, JS ve POST gerekmiyorsa açılır.
Eşik: --min-ok 3 (en az 3 açık kaynak).

## Bilinen Kusurlar
- K1-BG-1: normalize_title ve dedupe_listings sayısal girdide TypeError verir.
- K1-BG-2: denetim betiğinde CLI main/fetch yok; CI adımı bu yüzden rapor üretmez, test paketi kapıdır.

## Güvenlik
Giriş, CAPTCHA aşma, parola ve anahtar yok. Yalnız HTTPS.
