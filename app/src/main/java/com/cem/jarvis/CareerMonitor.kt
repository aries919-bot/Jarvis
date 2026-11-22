package com.cem.jarvis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Resmî kamu ve işveren kariyer sayfaları; MTSO, İŞKUR ilanlarını aktaran ikincil keşif kaynağıdır. */
object CareerSourceCatalog {
    data class Source(val name: String, val url: String, val category: String, val official: Boolean = true)

    val sources = listOf(
        Source("Kariyer Kapısı – Kamu işe alım", "https://kariyerkapisi.gov.tr/", "Kamu"),
        Source("Kariyer Kapısı eski işe alım bağlantısı", "https://isealimkariyerkapisi.cbiko.gov.tr/", "Kamu / eski adres"),
        Source("ÖSYM – KPSS tercih ve yerleştirme duyuruları", "https://www.osym.gov.tr/Duyurular/Index", "Kamu / KPSS"),
        Source("Kamu İlan Sistemi", "https://kamuilan.sbb.gov.tr/", "Kamu"),
        Source("İŞKUR e-Şube", "https://esube.iskur.gov.tr/", "İŞKUR / açık iş"),
        Source("İŞKUR Açık İş Haritası", "https://acikisharita.iskur.gov.tr/", "İŞKUR / açık iş"),
        Source("ilan.gov.tr – Personel alımı", "https://www.ilan.gov.tr/", "Resmî ilan"),
        Source("Resmî Gazete", "https://www.resmigazete.gov.tr/", "Resmî ilan"),
        Source("Orman Genel Müdürlüğü – kura ve işçi alımı", "https://www.ogm.gov.tr/", "Kamu / kura ve işçi alımı"),
        Source("Karayolları Genel Müdürlüğü – kura ve işçi alımı", "https://www.kgm.gov.tr/", "Kamu / kura ve işçi alımı"),
        Source("ANT Başkanlığı – personel ve kura duyuruları", "https://www.ant.gov.tr/haberler.html", "Kamu / kura ve işçi alımı"),
        Source("Yerel Yönetimler Genel Müdürlüğü", "https://yerelyonetimler.csb.gov.tr/", "Belediye"),
        Source("Malatya Büyükşehir Belediyesi", "https://www.malatya.bel.tr/", "Belediye"),
        Source("Battalgazi Belediyesi", "https://www.battalgazi.bel.tr/", "Belediye"),
        Source("Yeşilyurt Belediyesi", "https://yesilyurt.bel.tr/", "Belediye"),
        Source("Malatya TSO – canlı İŞKUR listesi", "https://www.malatyatso.org.tr/", "İŞKUR keşif", false),
        Source("Malatya Merkez 1. OSB – personel duyuruları", "https://malorsa.org.tr/", "Malatya OSB", false),
        Source("Kariyer.net", "https://www.kariyer.net/is-ilanlari", "İlan platformu", false),
        Source("Eleman.net", "https://www.eleman.net/is-ilanlari", "İlan platformu", false),
        Source("SecretCV", "https://www.secretcv.com/", "İlan platformu", false),
        Source("Yenibiriş", "https://www.yenibiris.com/", "İlan platformu", false),
        Source("Indeed Türkiye", "https://tr.indeed.com/", "İlan platformu", false),
        Source("LinkedIn İş İlanları", "https://www.linkedin.com/jobs/", "İlan platformu", false),
        Source("İşbul.net", "https://www.isbul.net/", "İlan platformu", false),
        Source("Manpower Türkiye", "https://www.manpower.com.tr/", "İşe alım platformu", false),
        Source("Jooble Türkiye", "https://tr.jooble.org/", "İlan platformu", false),
        Source("Koç Kariyerim", "https://www.koccareers.com/", "İşveren grubu"),
        Source("Sabancı Kariyer", "https://www.sabanci.com/en/career/", "İşveren grubu"),
        Source("Borusan Kariyer", "https://careers.borusan.com/", "İşveren grubu"),
        Source("Eczacıbaşı Kariyer", "https://careers.eczacibasi.com/", "İşveren grubu"),
        Source("Anadolu Kariyerim", "https://careers.anadolukariyerim.com/", "İşveren grubu"),
        Source("Çalık Holding Kariyer", "https://calik.com/kariyer", "İşveren grubu"),
        Source("Zorlu / Vestel Kariyer", "https://www.zorlu.com.tr/en/careers", "İşveren grubu"),
        Source("Migros Kariyer", "https://www.migroskurumsal.com/kariyer/simdi-basvurun/ilanlar", "Özel sektör"),
        Source("Coca-Cola İçecek Kariyer", "https://careerscci.com/?locale=tr_TR", "Özel sektör"),
        Source("Turkcell Kariyer", "https://kariyerim.turkcell.com.tr/kariyer-firsatlari", "Özel sektör"),
        Source("Vodafone Türkiye Kariyer", "https://jobs.vodafone.com/", "Özel sektör"),
        Source("Sütaş Açık Pozisyonları", "https://www.sutas.com.tr/acik-pozisyonlarimiz", "Özel sektör"),
        Source("Tırsan Kariyer", "https://tirsan.com.tr/ailemize-katilin-143-pg", "Özel sektör"),
        Source("ASELSAN Kariyer", "https://akariyer.aselsan.com/", "Özel sektör"),
        Source("MKE Kariyer – ilanlar", "https://kariyer.mke.gov.tr/ilanlar", "Kamu şirketi / kariyer"),
        Source("Eti Maden Duyurular", "https://www.etimaden.gov.tr/duyurular", "Kamu şirketi"),
        Source("TÜBİTAK Kariyer", "https://kariyer.tubitak.gov.tr/", "Kamu / teknik personel"),
        Source("TÜRKŞEKER Duyurular", "https://www.turkseker.gov.tr/?MenuID=52&ModulID=9", "Kamu şirketi"),
        Source("Şişecam Kariyer", "https://www.sisecam.com/tr/kariyer", "Özel sektör"),
        Source("Türk Telekom Kariyer", "https://www.turktelekomkariyer.com.tr/", "Özel sektör"),
        Source("TCDD", "https://www.tcdd.gov.tr/", "Kamu şirketi"),
        Source("TCDD Taşımacılık – işe alım duyuruları", "https://www.tcddtasimacilik.gov.tr/duyurular/", "Kamu şirketi"),
        Source("TEİAŞ Duyurular", "https://www.teias.gov.tr/duyurular", "Kamu şirketi"),
        Source("EÜAŞ Kariyer ve Staj", "https://www.euas.gov.tr/kariyer-ve-staj", "Kamu şirketi"),
        Source("TUSAŞ – Elektrik-Elektronik Bakım Teknisyeni", "https://kariyer.tusas.com/ilan-detay/elektrik-elektronik-bakim-teknisyeni", "Özel sektör / doğrudan ilan"),
        Source("TUSAŞ – Genel Başvuru Teknisyen", "https://kariyer.tusas.com/ilan-detay/genel-basvuru-teknisyen", "Özel sektör / genel başvuru"),
        Source("TUSAŞ Kariyer – tüm açık pozisyonlar", "https://kariyer.tusas.com/acik-pozisyonlarimiz", "Özel sektör"),
        Source("BOTAŞ Personel Alımı", "https://www.botas.gov.tr/Sayfa/personel-alimi/124", "Kamu şirketi"),
        Source("TÜPRAŞ Açık İlanlar", "https://tupras.gethirex.com/o/tupras/", "Özel sektör"),
        Source("ROKETSAN – Elektronik Üretim Teknisyeni", "https://kariyer.roketsan.com.tr/6ny5xckybt8n4jn8f4prqdsljx.job", "Özel sektör / doğrudan ilan"),
        Source("ROKETSAN Kariyer – tüm açık pozisyonlar", "https://kariyer.roketsan.com.tr/jobs", "Özel sektör"),
        Source("TEI Kariyer", "https://yetenek.tei.com.tr/jobs", "Özel sektör"),
        Source("HAVELSAN Kariyer", "https://www.havelsan.com/tr/kariyer", "Özel sektör"),
        Source("Enerjisa Kariyer", "https://www.enerjisa.com.tr/tr/enerjisa-hakkinda/kariyer", "Özel sektör")
    )

    private val trustedDomains = listOf(
        "cbiko.gov.tr", "kariyerkapisi.gov.tr", "osym.gov.tr", "sbb.gov.tr", "iskur.gov.tr", "mke.gov.tr", "etimaden.gov.tr", "tubitak.gov.tr", "turkseker.gov.tr", "sisecam.com", "turktelekomkariyer.com.tr", "ilan.gov.tr", "resmigazete.gov.tr",
        "csb.gov.tr", "ogm.gov.tr", "kgm.gov.tr", "ant.gov.tr", "malatya.bel.tr", "battalgazi.bel.tr", "yesilyurt.bel.tr", "aselsan.com", "tcdd.gov.tr", "tcddtasimacilik.gov.tr",
        "teias.gov.tr", "euas.gov.tr", "tusas.com", "botas.gov.tr", "tupras.com.tr", "tupras.gethirex.com", "tai.com.tr",
        "roketsan.com.tr", "tei.com.tr", "havelsan.com", "enerjisa.com.tr", "malatyatso.org.tr",
        "malorsa.org.tr", "kariyer.net", "eleman.net", "secretcv.com", "yenibiris.com",
        "indeed.com", "linkedin.com", "isbul.net", "manpower.com.tr", "jooble.org",
        "pladis.wd3.myworkdayjobs.com", "kockariyerim.com", "koccareers.com", "sabanci.com",
        "borusan.com", "eczacibasi.com", "anadolukariyerim.com", "calik.com", "zorlu.com.tr", "vestelkariyer.com",
        "vestel.com.tr", "migroskurumsal.com", "careerscci.com", "turkcell.com.tr",
        "vodafone.com", "sutas.com.tr", "tirsan.com.tr"
    )

    fun isTrustedUrl(url: String): Boolean {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return false
        if (uri.scheme != "https") return false
        val host = uri.host?.lowercase()?.removePrefix("www.") ?: return false
        return trustedDomains.any { host == it || host.endsWith(".$it") }
    }

    fun sourceName(url: String): String {
        val host = runCatching { Uri.parse(url).host.orEmpty().lowercase().removePrefix("www.") }.getOrDefault("")
        return sources.firstOrNull { source ->
            val sourceHost = Uri.parse(source.url).host.orEmpty().lowercase().removePrefix("www.")
            host == sourceHost || host.endsWith(".$sourceHost") || sourceHost.endsWith(".$host")
        }?.name ?: host
    }

    fun isSecondary(url: String): Boolean {
        val host = runCatching { Uri.parse(url).host.orEmpty().lowercase(Locale.ROOT).removePrefix("www.") }
            .getOrDefault("")
        val secondaryDomains = listOf(
            "malatyatso.org.tr", "malorsa.org.tr", "kariyer.net", "eleman.net",
            "secretcv.com", "yenibiris.com", "indeed.com", "linkedin.com", "isbul.net",
            "manpower.com.tr", "jooble.org"
        )
        return secondaryDomains.any { host == it || host.endsWith(".$it") }
    }

    fun categoryFor(url: String): String {
        val host = runCatching { java.net.URI(url).host.orEmpty().lowercase(Locale.ROOT) }.getOrDefault("")
        return when {
            isSecondary(url) && (host.contains("malatyatso") || host.contains("malorsa")) -> "Malatya / İŞKUR keşif kaynağı"
            isSecondary(url) -> "İlan platformu / ikincil keşif"
            host.contains("osym.gov.tr") || host.contains("cbiko.gov.tr") || host.contains("kariyerkapisi.gov.tr") ||
                host.contains("sbb.gov.tr") || host.contains("ilan.gov.tr") ||
                host.contains("resmigazete.gov.tr") -> "Kamu / KPSS ve merkezi yerleştirme"
            host.contains("malatya.bel.tr") || host.contains("battalgazi.bel.tr") ||
                host.contains("yesilyurt.bel.tr") || host.contains("csb.gov.tr") -> "Belediye / kamu"
            host.contains("ogm.gov.tr") || host.contains("kgm.gov.tr") || host.contains("ant.gov.tr") -> "Kamu / kura ve işçi alımı"
            host.contains("tcdd") || host.contains("teias.gov.tr") || host.contains("euas.gov.tr") ||
                host.contains("mke.gov.tr") || host.contains("etimaden.gov.tr") ||
                host.contains("botas.gov.tr") || host.contains("turkseker.gov.tr") -> "Kamu kurumu / kamu şirketi"
            host.contains("iskur.gov.tr") -> "İŞKUR / açık iş"
            host.contains("tusas") || host.contains("roketsan") || host.contains("aselsan") ||
                host.contains("havelsan") || host.contains("tupras") || host.contains("vestel") ||
                host.contains("turkcell") || host.contains("vodafone") || host.contains("migros") ||
                host.contains("kockariyerim") || host.contains("koccareers") || host.contains("sabanci") ||
                host.contains("myworkdayjobs") || host.contains("borusan") ||
                host.contains("eczacibasi") || host.contains("anadolukariyerim") || host.contains("calik") ||
                host.contains("careerscci") || host.contains("sutas") || host.contains("tirsan") -> "İşveren kariyer portalı"
            else -> "Özel sektör / büyük şirket"
        }
    }

    fun isLikelyJobResult(title: String, snippet: String): Boolean {
        val text = (title + " " + snippet).lowercase(Locale("tr", "TR"))
        val indicators = listOf(
            "personel al", "işçi al", "is ilan", "iş ilan", "ilanı", "ilanlari", "ilanları",
            "açık pozisyon", "acik pozisyon", "başvuru", "basvuru", "son başvuru",
            "son basvuru", "teknisyen", "tekniker", "memur", "işkur", "iskur",
            "kpss", "üretim", "uretim", "bakım", "bakim", "elektrik", "pano",
            "operatör", "operator", "depo", "satış", "satis", "kasiyer", "şoför", "sofor",
            "kura", "noter", "asıl aday", "asil aday", "yedek aday", "evrak teslim",
            "evrak kontrol", "sınav tarihi", "sinav tarihi", "sınav sonucu", "sinav sonucu",
            "nihai liste", "yerleştirme sonucu", "yerlestirme sonucu", "kuraya katılacak"
        )
        return indicators.any(text::contains)
    }
}

object CareerEligibility {
    const val KPSS_P94 = 63.23366
    const val HIGH_SCHOOL_FIELD_CODE_ELECTRIC_ELECTRONICS = 6013
    const val HIGH_SCHOOL_BRANCH_CODE_PANEL_MOUNTING = 3
    const val QUALIFICATION_CODE_ELECTRIC_ELECTRONICS = 2023
    const val QUALIFICATION_CODE_RAIL_SYSTEMS = 2086
    const val QUALIFICATION_CODE_ANY_HIGH_SCHOOL = 2001

    private fun normalize(value: String): String = value.lowercase(Locale("tr", "TR"))
        .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
        .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')

    private fun hasExplicitAssociateRequirement(text: String): Boolean {
        val phrases = listOf(
            "on lisans mezunu olmak", "onlisans mezunu olmak", "myo mezunu olmak",
            "on lisans programindan mezun", "onlisans programindan mezun",
            "iki yillik yuksekogretim mezunu", "mezuniyet sarti: on lisans",
            "elektrik programi mezunu olmak"
        )
        return phrases.any(text::contains)
    }

    fun assess(title: String, snippet: String, url: String = ""): String {
        val text = normalize("$title $snippet $url")

        // A hard disqualifying field code must not be overwritten by a soft skill/title match.
        val hasCodeContext = listOf("nitelik kod", "ozel sart kod", "aranan nitelik kod").any(text::contains)
        val codes = if (hasCodeContext) extractQualificationCodes(text) else emptySet()
        if (QUALIFICATION_CODE_RAIL_SYSTEMS in codes) {
            return assessQualificationCodes(codes)
        }
        val qualificationNote = when {
            QUALIFICATION_CODE_ELECTRIC_ELECTRONICS in codes -> assessQualificationCodes(setOf(QUALIFICATION_CODE_ELECTRIC_ELECTRONICS))
            QUALIFICATION_CODE_ANY_HIGH_SCHOOL in codes -> assessQualificationCodes(setOf(QUALIFICATION_CODE_ANY_HIGH_SCHOOL))
            else -> null
        }

        if (hasExplicitAssociateRequirement(text)) {
            return "MEZUNİYET SONRASI · İlan ön lisans mezuniyeti istiyor. 2027’de mezuniyetin tamamlanmadan bu şartı karşılamazsın."
        }

        val requiresKpss = text.contains("kpss")
        val p3 = Regex("\\bp\\s*3\\b").containsMatchIn(text)
        val p93 = Regex("\\bp\\s*93\\b").containsMatchIn(text)
        val p94 = Regex("\\bp\\s*94\\b").containsMatchIn(text)
        if (requiresKpss && (p3 || p93) && !p94) {
            return "PUAN TÜRÜ UYUŞMUYOR · İlan P3/P93 istiyor; ortaöğretim P94 puanın bu puan türlerinin yerine geçmez."
        }

        if (requiresKpss) {
            val minScore = Regex(
                "(?:en az|asgari|taban|en dusuk|min(?:imum)?)\\s*(?:kpss\\s*)?(?:p\\s*94\\s*)?(?:puan(?:i)?\\s*)?(\\d{2,3}(?:[.,]\\d+)?)\\s*puan?"
            ).find(text)?.groupValues?.getOrNull(1)?.replace(',', '.')?.toDoubleOrNull()
            if (minScore != null && minScore > KPSS_P94) {
                return "PUAN ŞARTI UYUŞMUYOR · İlanda açıkça $minScore puan alt sınırı görünüyor; P94 puanın 63,23366."
            }
            if (p94 || text.contains("ortaogretim") || text.contains("orta ogretim")) {
                val base = if (minScore == null) {
                    "P94 İLE TERCİH DEĞERLENDİRMESİ · Merkezi yerleştirmede herkese uygulanan sabit bir taban puan varsayılmaz. 63,23366 puanınla tercih hakkın, ilgili nitelik kodları ve kontenjan/puan sıralamasına bağlıdır."
                } else {
                    "İLK PUAN KONTROLÜ GEÇİLDİ · İlanda görünen P94 alt sınırı 63,23366 puanını aşmıyor; öğrenim, nitelik kodu ve diğer şartlar ayrıca kontrol edilmeli."
                }
                return if (qualificationNote == null) base else "$base $qualificationNote"
            }
            return "KPSS ŞARTI BELİRSİZ · Puan türü ve varsa alt sınır ilanın tam metninden doğrulanmalı; uygunluk kesinleştirilmedi."
        }

        if (qualificationNote != null) return qualificationNote
        if (listOf("elektrik", "pano", "kablolama", "bakim", "teknisyen", "teknik lise")
                .any(text::contains)) {
            return "ALAN EŞLEŞMESİ VAR · Elektrik lise dalınla ilişkili. Diploma dalı, deneyim, vardiya, ehliyet ve sertifika şartları tam metinden kontrol edilmeli."
        }
        return "ŞARTLARI DOĞRULA · Başlık/özet tek başına yeterli değil; eğitim, şehir, deneyim ve son başvuru tarihi kaynak sayfasında kontrol edilmeli."
    }

    fun assessQualificationCodes(codes: Set<Int>): String {
        if (QUALIFICATION_CODE_RAIL_SYSTEMS in codes) {
            return "ALAN KODU UYUŞMUYOR · 2086 Raylı Sistemler alanı/dalı şartıdır; Elektrik Tesisatları ve Pano Montörlüğü dalınla aynı değildir."
        }
        if (QUALIFICATION_CODE_ELECTRIC_ELECTRONICS in codes) {
            return "ALAN KODU EŞLEŞİYOR · 2023 nitelik kodu Elektrik-Elektronik Teknolojisi alanı mezuniyetini kapsar; 6013/3 dalın bu alan içindedir. Diğer kodlar da ayrıca sağlanmalı."
        }
        if (QUALIFICATION_CODE_ANY_HIGH_SCHOOL in codes) {
            return "ORTAÖĞRETİM KODU EŞLEŞİYOR · 2001 herhangi bir ortaöğretim alanından mezuniyeti ifade eder. İlandaki diğer özel şartlar da sağlanmalı."
        }
        return "NİTELİK KODLARI KONTROL EDİLMELİ · Kodların tamamı ÖSYM kılavuzundaki açıklamalarıyla karşılaştırılmalı."
    }

    fun extractQualificationCodes(text: String): Set<Int> =
        Regex("(?<!\\d)(2001|2023|2086)(?!\\d)").findAll(text).map { it.value.toInt() }.toSet()

    fun normalizeForFingerprint(value: String): String = normalize(value)
        .replace(Regex("[^a-z0-9 ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    fun profileMatchScore(title: String, snippet: String, role: String, city: String, eligibility: String): Int {
        val text = normalize("$title $snippet")
        val roleTerms = role.split(',', ';', '/', '|').map { normalize(it.trim()) }.filter { it.length >= 3 }.distinct()
        val roleMatches = roleTerms.count { phrase ->
            phrase in text || phrase.split(" ").any { it.length >= 4 && it in text }
        }.coerceAtMost(4)
        var score = roleMatches * 12
        val localities = city.split(',', ';').map { normalize(it.trim()) }
            .filter { it.length >= 3 && !it.contains("servis") && !it.contains("konaklama") && it != "turkiye geneli" }
        if (localities.any(text::contains)) score += 25
        if (listOf("elektrik", "pano", "kablolama", "bakim", "teknisyen", "tekniker", "otomasyon").any(text::contains)) score += 15
        if (text.contains("kpss") && (text.contains("p94") || text.contains("ortaogretim"))) score += 12
        if (text.contains("son basvuru") || text.contains("basvuru tarihi")) score += 6
        if (eligibility.startsWith("PUAN TÜRÜ UYUŞMUYOR") ||
            eligibility.startsWith("PUAN ŞARTI UYUŞMUYOR") ||
            eligibility.startsWith("MEZUNİYET SONRASI") ||
            eligibility.startsWith("ALAN KODU UYUŞMUYOR")) score -= 35
        return score.coerceIn(0, 100)
    }

    fun profileMatchReason(title: String, snippet: String, role: String, city: String, eligibility: String): String {
        val text = normalize("$title $snippet")
        val reasons = mutableListOf<String>()
        val matchedRoles = role.split(',', ';', '/', '|').map { it.trim() }
            .filter { it.length >= 3 && normalize(it) in text }.take(3)
        if (matchedRoles.isNotEmpty()) reasons += "Pozisyon: " + matchedRoles.joinToString(", ")
        val matchedCity = city.split(',', ';').map { it.trim() }
            .firstOrNull { it.length >= 3 && normalize(it) in text }
        if (matchedCity != null) reasons += "Bölge: $matchedCity"
        if (text.contains("p94") || text.contains("ortaogretim")) reasons += "Ortaöğretim KPSS ifadesi bulundu"
        if (listOf("elektrik", "pano", "teknisyen", "bakim").any(text::contains)) reasons += "Elektrik/teknik alan eşleşmesi"
        if (eligibility.startsWith("PUAN") || eligibility.startsWith("MEZUNİYET") ||
            eligibility.startsWith("ALAN KODU UYUŞMUYOR")) reasons += "Zorunlu şart uyuşmazlığı olabilir; tam kılavuzu kontrol et"
        return if (reasons.isEmpty()) "Arama terimleriyle genel eşleşme; tam ilan metni incelenmeli" else reasons.joinToString(" · ")
    }

    fun relevance(title: String, snippet: String, city: String): Int {
        val text = normalize("$title $snippet")
        val terms = listOf(
            "elektrik", "pano", "kablolama", "bakim", "teknisyen", "tekniker",
            "otomasyon", "uretim", "isci", "personel", "memur", "operator",
            "depo", "satis", "kasiyer", "paketleme", "tekstil", "gida",
            "lojistik", "temizlik", "sofor", "guvenlik", "cagri merkezi"
        )
        var score = terms.count { text.contains(normalize(it)) } * 8
        val localities = city.split(',', ';').map { normalize(it.trim()) }
            .filter { it.isNotBlank() && !it.contains("servis") && !it.contains("konaklama") && it != "turkiye geneli" }
        if (localities.any(text::contains)) score += 20
        if (listOf("son basvuru", "basvuru tarihi", "personel alimi", "acik pozisyon", "is ilani")
                .any(text::contains)) score += 8
        return score.coerceAtMost(100)
    }
}

object CareerListingParser {
    private val deadlinePattern = Regex(
        "(?:son\\s+başvuru(?:\\s+tarihi)?|son\\s+müracaat(?:\\s+tarihi)?|başvuruların\\s+son\\s+günü|basvurularin\\s+son\\s+gunu)[^0-9]{0,45}(\\d{1,2}[./-]\\d{1,2}[./-]\\d{4}|\\d{1,2}\\s+[\\p{L}]+\\s+\\d{4})",
        RegexOption.IGNORE_CASE
    )

    fun deadlineDate(text: String): String? {
        val raw = deadlinePattern.find(text)?.groupValues?.getOrNull(1) ?: return null
        val normalized = raw.replace('/', '.').replace('-', '.').trim()
        val formats = if (normalized.any { it.isLetter() }) {
            listOf("d MMMM yyyy", "dd MMMM yyyy", "d MMM yyyy", "dd MMM yyyy")
        } else {
            listOf("dd.MM.yyyy", "d.M.yyyy")
        }
        for (format in formats) {
            val parser = java.text.SimpleDateFormat(format, Locale("tr", "TR")).apply { isLenient = false }
            val parsed = runCatching { parser.parse(normalized) }.getOrNull() ?: continue
            return java.text.SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).format(parsed)
        }
        return null
    }

    fun deadlineStatus(text: String, nowMillis: Long = System.currentTimeMillis()): String? {
        val dateText = deadlineDate(text) ?: return null
        val parser = java.text.SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).apply { isLenient = false }
        val deadline = runCatching { parser.parse(dateText) }.getOrNull() ?: return null
        val today = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val deadlineDay = Calendar.getInstance().apply {
            time = deadline
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return if (deadlineDay.before(today)) {
            "SÜRESİ GEÇMİŞ · Kaynak metnindeki son başvuru tarihi $dateText geçmiş. Bu kaydı yeni ilanlarla karıştırma; kaynak sayfasında başvurunun kapandığını doğrula."
        } else {
            "SON TARİH BİLGİSİ · Kaynak metninde son başvuru tarihi $dateText görünüyor; başvuru sayfasında teyit et."
        }
    }
}
data class CareerScanBatch(
    val hits: List<Net.Hit>,
    val successfulQueries: Int,
    val failedQueries: Int,
    val totalQueries: Int,
    val errorMessages: List<String> = emptyList()
) {
    val hasAnySuccessfulQuery: Boolean get() = successfulQueries > 0
}

/** DuckDuckGo sonuçları yalnızca keşif içindir; kayda giren bağlantılar güvenilir resmî alan adlarıyla sınırlıdır. */
/** Arama motoru yalnızca keşif katmanıdır. Her gösterilen sonuç güvenilir kurum alan adıyla sınırlanır;
 * sayfanın güncel ilan olduğunu doğrulamaz. Başvuru şartı ve son tarih ilanın kendisinden okunmalıdır. */
object CareerSearchEngine {
    private fun queryList(category: Int, role: String, city: String): List<String> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val r = role.ifBlank { "elektrik teknisyeni pano montaj bakım üretim depo operatör satış işçi personel" }
        val c = city.ifBlank { "Malatya Türkiye" }
        val publicKpss = listOf(
            "site:osym.gov.tr KPSS-$year/2 ortaöğretim P94 merkezi yerleştirme tercih kılavuzu",
            "site:osym.gov.tr KPSS ortaöğretim P94 $year/2 nitelik kodu 2023 2001",
            "site:kamuilan.sbb.gov.tr personel alımı lise ortaöğretim KPSS P94 $year $c",
            "site:kariyerkapisi.gov.tr kamu işe alım personel ilanı lise KPSS $year $c",
            "site:isealimkariyerkapisi.cbiko.gov.tr lise ortaöğretim personel alımı KPSS $year $c",
            "site:ilan.gov.tr personel alımı lise ortaöğretim KPSS $year $c",
            "site:resmigazete.gov.tr personel alımı atama ortaöğretim KPSS $year $c"
        )
        val publicNoKpss = listOf(
            "site:esube.iskur.gov.tr açık iş ilanı $r $c $year",
            "site:acikisharita.iskur.gov.tr $c elektrik teknisyen işçi açık iş $year",
            "site:yerelyonetimler.csb.gov.tr belediye işçi personel alımı $year $c",
            "site:malatya.bel.tr personel alımı iş ilanı $year",
            "site:battalgazi.bel.tr personel işçi alımı $year",
            "site:yesilyurt.bel.tr personel işçi alımı $year",
            "site:malatyatso.org.tr İŞKUR açık iş ilanları $year",
            "site:malorsa.org.tr personel alım ilanı Malatya OSB $year"
        )
        val privateSector = listOf(
            "site:akariyer.aselsan.com teknisyen elektrik üretim açık pozisyon",
            "site:kariyer.tusas.com teknisyen elektrik bakım açık pozisyon $year",
            "site:kariyer.roketsan.com.tr/jobs teknisyen elektronik üretim bakım $year",
            "site:tupras.gethirex.com/o/tupras elektrik teknisyen bakım açık pozisyon",
            "site:sisecam.com/tr/kariyer elektrik bakım teknisyen üretim açık pozisyon",
            "site:turktelekomkariyer.com.tr teknisyen teknik personel açık pozisyon",
            "site:kockariyerim.com elektrik teknisyen üretim iş ilanı",
            "site:sabanci.com/en/career elektrik teknisyen personel iş ilanı",
            "site:careers.borusan.com teknisyen elektrik üretim bakım",
            "site:careers.eczacibasi.com teknisyen elektrik bakım üretim",
            "site:careers.anadolukariyerim.com teknisyen üretim depo personel",
            "site:careers.calik.com elektrik teknisyen iş ilanı",
            "site:vestelkariyer.com/is-ilanlari elektrik teknisyen üretim",
            "site:migroskurumsal.com/kariyer/simdi-basvurun/ilanlar depo mağaza personel",
            "site:careerscci.com elektrik üretim bakım teknisyen",
            "site:kariyerim.turkcell.com.tr/kariyer-firsatlari teknisyen mağaza depo satış",
            "site:jobs.vodafone.com Türkiye teknisyen satış mağaza personel",
            "site:pladis.wd3.myworkdayjobs.com Türkiye üretim operatör personel",
            "site:sutas.com.tr/tr/insan-kaynaklari elektrik teknisyen üretim",
            "site:tirsan.com.tr ailemize-katilin teknisyen üretim personel"
        )
        val platforms = listOf(
            "site:kariyer.net/is-ilani $r $c iş ilanı $year",
            "site:eleman.net/is-ilanlari $r $c iş ilanı $year",
            "site:secretcv.com/is-ilanlari $r $c iş ilanı $year",
            "site:yenibiris.com/is-ilanlari $r $c iş ilanı $year",
            "site:tr.indeed.com/jobs $r $c $year",
            "site:linkedin.com/jobs/view $r $c Türkiye $year",
            "site:isbul.net $r $c iş ilanı $year"
        )
        val postAssociate = listOf(
            "elektrik teknikeri ön lisans mezunu KPSS P93 personel alımı $year",
            "elektrik teknikeri MYO mezunu özel sektör açık pozisyon $year",
            "site:teias.gov.tr OR site:euas.gov.tr elektrik teknikeri ön lisans mezunu $year",
            "site:aselsan.com OR site:tusas.com elektrik teknikeri ön lisans mezunu $year"
        )
        val kuraQueries = listOf(
            "site:ogm.gov.tr noter kurası elektrikçi teknisyen işçi alımı $year",
            "site:kgm.gov.tr noter kurası işçi alımı kura sonuçları $year",
            "site:ant.gov.tr personel alımı noter kurası asıl yedek sonuç $year",
            "site:iskur.gov.tr kura ile işçi alımı başvuru kura sonuçları $year",
            "noter kurası işçi alımı elektrikçi teknisyen Malatya Elazığ Adıyaman $year",
            "kamu işçi alımı evrak kontrol kuraya katılacaklar kura sonucu $year",
            "kamu personel alımı kura tarihi saat asıl yedek liste sonuçları $year",
            "işçi alımı sınav yeri sınav tarihi mülakat sonucu nihai liste $year"
        )
        val extra = "$r $c iş ilanı başvuru şartları son başvuru tarihi $year"
        val generalQuick = listOf(
            "$r $c güncel iş ilanı personel alımı başvuru $year",
            "site:esube.iskur.gov.tr $r $c açık iş ilanı $year",
            "site:iskur.gov.tr $r $c işçi alımı başvuru $year"
        ) + publicKpss.take(2) + publicNoKpss.take(2) +
            kuraQueries.take(2) + privateSector.take(2) + platforms.take(2) + listOf(extra)

        return when (category) {
            1 -> publicKpss + kuraQueries + listOf(extra)
            2 -> publicNoKpss + kuraQueries + listOf(extra)
            3 -> privateSector.take(10) + platforms.take(5) + listOf(extra)
            4 -> privateSector.take(12) + kuraQueries + listOf(extra)
            5 -> postAssociate
            else -> generalQuick
        }.distinct()
    }

    private fun normalizedUrl(url: String): String {
        return try {
            val uri = java.net.URI(url.trim())
            val host = (uri.host ?: "").lowercase(Locale.ROOT)
            val path = (uri.rawPath ?: "/").ifBlank { "/" }.trimEnd('/').ifBlank { "/" }
            val retained = (uri.rawQuery ?: "").split("&")
                .filter { it.isNotBlank() }
                .filterNot {
                    val key = it.substringBefore("=").lowercase(Locale.ROOT)
                    key.startsWith("utm_") || key in setOf("fbclid", "gclid", "ref", "source")
                }
                .sorted()
            "https://$host$path" + if (retained.isEmpty()) "" else "?" + retained.joinToString("&")
        } catch (_: Exception) {
            url.substringBefore("#").trimEnd('/').lowercase(Locale.ROOT)
        }
    }

    fun fingerprint(title: String, url: String): String {
        val normalizedTitle = CareerEligibility.normalizeForFingerprint(title)
        val payload = normalizedTitle + "|" + normalizedUrl(url)
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private data class QueryOutcome(
        val hits: List<Net.Hit>,
        val providerResponses: Int,
        val errors: List<String>
    )

    private fun collect(queries: List<String>, timeoutSeconds: Long, city: String = "Malatya"): CareerScanBatch {
        if (queries.isEmpty()) return CareerScanBatch(emptyList(), 0, 0, 0)
        val executor = Executors.newFixedThreadPool(minOf(4, queries.size))
        return try {
            val futures = executor.invokeAll(
                queries.mapIndexed { queryIndex, query ->
                    Callable {
                        val found = LinkedHashMap<String, Net.Hit>()
                        val errors = ArrayList<String>()
                        var providerResponses = 0

                        fun attempt(provider: String, search: () -> List<Net.Hit>) {
                            try {
                                val results = search()
                                providerResponses++
                                results.forEach { hit ->
                                    val key = fingerprint(hit.title, hit.url)
                                    if (key !in found) found[key] = hit
                                }
                            } catch (error: Exception) {
                                val detail = error.message?.take(180).orEmpty().ifBlank { error.javaClass.simpleName }
                                errors += provider + ": " + detail
                            }
                        }

                        attempt("DuckDuckGo", { Net.webSearch(query, 8) })
                        var trustedCount = found.values.count { CareerSourceCatalog.isTrustedUrl(it.url) }
                        if (trustedCount < 1) {
                            attempt("Bing", { Net.webSearchBing(query, 8) })
                            trustedCount = found.values.count { CareerSourceCatalog.isTrustedUrl(it.url) }
                        }
                        if (trustedCount < 1) {
                            attempt("Google Haberler RSS", { Net.webSearchGoogleNews(query, 4) })
                            trustedCount = found.values.count { CareerSourceCatalog.isTrustedUrl(it.url) }
                        }
                        if (trustedCount < 1) {
                            attempt("Brave", { Net.webSearchBrave(query, 6) })
                        }

                        QueryOutcome(
                            found.values.toList(),
                            providerResponses,
                            errors.map { "Sorgu " + (queryIndex + 1).toString() + ": " + it }
                        )
                    }
                },
                timeoutSeconds,
                TimeUnit.SECONDS
            )

            val outcomes = ArrayList<QueryOutcome>()
            val errors = ArrayList<String>()
            var cancelled = 0
            futures.forEach { future ->
                if (future.isCancelled) {
                    cancelled++
                    errors += "Arama zaman sınırını aştı; bazı sorgular tamamlanamadı."
                } else {
                    try {
                        val outcome = future.get()
                        outcomes += outcome
                        errors += outcome.errors
                    } catch (error: Exception) {
                        errors += "Arama görevi tamamlanamadı: " +
                            (error.cause?.message ?: error.message ?: "bilinmeyen hata")
                    }
                }
            }

            val successful = outcomes.count { it.providerResponses > 0 }
            val failed = (queries.size - successful).coerceAtLeast(cancelled)
            val found = outcomes.flatMap { it.hits }
            val seen = HashSet<String>()
            val filtered = found.filter { hit ->
                CareerSourceCatalog.isTrustedUrl(hit.url) &&
                    CareerSourceCatalog.isLikelyJobResult(hit.title, hit.snippet) &&
                    seen.add(fingerprint(hit.title, hit.url))
            }.sortedByDescending {
                CareerEligibility.relevance(it.title, it.snippet, city)
            }

            CareerScanBatch(
                hits = filtered,
                successfulQueries = successful,
                failedQueries = failed,
                totalQueries = queries.size,
                errorMessages = errors.distinct().take(12)
            )
        } finally {
            executor.shutdownNow()
        }
    }

    /** Manuel aramada bütün kaynakları bekletmemek için en alakalı sorgularla sınırlı tur yapar. */
    fun searchBatch(category: Int, role: String, city: String): CareerScanBatch =
        collect(queryList(category, role, city).take(12), 90, city)

    fun search(category: Int, role: String, city: String): List<Net.Hit> {
        val batch = searchBatch(category, role, city)
        if (!batch.hasAnySuccessfulQuery) {
            val reason = batch.errorMessages.take(3).joinToString(" | ")
            throw java.io.IOException("Arama sağlayıcılarının hiçbiri yanıt vermedi. " + reason)
        }
        return batch.hits
    }

    fun dailySearchBatch(role: String, city: String): CareerScanBatch {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val roleTerms = role.split(',', ';', '/', '|')
            .map { it.trim() }.filter { it.length >= 3 }.distinct().take(6)
        val preferredCities = city.split(',', ';')
            .map { it.trim() }
            .filter { it.length >= 3 && !it.contains("servis", true) && !it.contains("konaklama", true) }
        val primaryCity = preferredCities.firstOrNull() ?: "Malatya"

        val profileQueries = roleTerms.map { term ->
            "$term $primaryCity Türkiye iş ilanı başvuru $year"
        } + listOf(
            "$role $primaryCity personel işçi teknisyen iş ilanı $year",
            "$role Malatya Battalgazi Yeşilyurt Elazığ Adıyaman iş ilanı $year",
            "$role Türkiye geneli servis lojman konaklama iş ilanı $year"
        )
        val kpssQueries = listOf(
            "site:osym.gov.tr KPSS-$year/2 ortaöğretim P94 merkezi yerleştirme tercih kılavuzu",
            "site:osym.gov.tr KPSS ortaöğretim P94 lise $year tercih nitelik kodları",
            "site:kamuilan.sbb.gov.tr ortaöğretim lise KPSS P94 personel alımı $year",
            "site:kariyerkapisi.gov.tr personel işe alım lise KPSS $year",
            "site:isealimkariyerkapisi.cbiko.gov.tr lise personel alımı ortaöğretim KPSS $year",
            "site:ilan.gov.tr personel alımı lise KPSS $year",
            "site:resmigazete.gov.tr atama personel alımı $year"
        )
        val iskUrAndLocal = listOf(
            "site:esube.iskur.gov.tr $primaryCity açık iş elektrik teknisyen üretim depo işçi $year",
            "site:acikisharita.iskur.gov.tr $primaryCity açık iş ilanları elektrik üretim depo $year",
            "site:malatyatso.org.tr İŞKUR iş ilanları Malatya Yeşilyurt Battalgazi $year",
            "site:malorsa.org.tr Malatya Merkez 1 OSB personel alımı iş ilanı $year",
            "site:malatya.bel.tr OR site:battalgazi.bel.tr OR site:yesilyurt.bel.tr personel işçi alımı $year",
            "site:yerelyonetimler.csb.gov.tr belediye personel işçi alımı $year"
        )
        val kuraQueries = listOf(
            "site:ogm.gov.tr noter kurası işçi alımı kuraya katılacaklar kura sonuçları $year",
            "site:kgm.gov.tr noter kurası işçi alımı kura sonuçları asıl yedek $year",
            "site:ant.gov.tr noter kurası personel alımı asıl yedek sınav sonuçları $year",
            "site:iskur.gov.tr kura çekimi işçi alımı kura tarihi sonuç listesi $year",
            "kamu işçi alımı noter kurası evrak kontrol kuraya katılacak aday listesi $year",
            "kamu personel alımı kura sonuçları asıl yedek aday sınav tarihi nihai sonuç $year",
            "Malatya Elazığ Adıyaman kamu işçi alımı kura tarihi kura sonucu $year",
            "işçi alımı sözlü sınav tarihi sınav yeri sınav sonucu nihai liste $year"
        )
        val employerQueries = listOf(
            "site:akariyer.aselsan.com teknisyen elektrik açık pozisyon",
            "site:kariyer.tusas.com teknisyen elektrik bakım açık pozisyon $year",
            "site:kariyer.roketsan.com.tr/jobs elektronik üretim teknisyeni bakım $year",
            "site:teias.gov.tr OR site:euas.gov.tr OR site:botas.gov.tr personel alımı işçi teknisyen $year",
            "site:tcdd.gov.tr OR site:tcddtasimacilik.gov.tr personel alımı işçi teknisyen $year",
            "site:kariyer.mke.gov.tr OR site:etimaden.gov.tr OR site:kariyer.tubitak.gov.tr personel alımı teknisyen $year",
            "site:tupras.gethirex.com OR site:yetenek.tei.com.tr OR site:havelsan.com teknisyen kariyer açık pozisyon",
            "site:sisecam.com/tr/kariyer OR site:turktelekomkariyer.com.tr açık pozisyon üretim teknisyen $year",
            "site:kockariyerim.com OR site:sabanci.com/en/career elektrik teknisyen üretim iş ilanı",
            "site:careers.borusan.com OR site:careers.eczacibasi.com teknisyen elektrik bakım üretim",
            "site:careers.anadolukariyerim.com OR site:careers.calik.com personel teknisyen üretim ilan",
            "site:vestelkariyer.com OR site:migroskurumsal.com/kariyer elektrik teknisyen depo personel",
            "site:careerscci.com OR site:kariyerim.turkcell.com.tr OR site:jobs.vodafone.com Türkiye teknisyen üretim satış",
            "site:pladis.wd3.myworkdayjobs.com OR site:sutas.com.tr OR site:tirsan.com.tr Türkiye üretim teknisyen personel"
        )
        val platformQueries = listOf(
            "site:kariyer.net/is-ilani $role $primaryCity",
            "site:eleman.net/is-ilanlari $role $primaryCity",
            "site:secretcv.com/is-ilanlari $role $primaryCity",
            "site:yenibiris.com/is-ilanlari $role $primaryCity",
            "site:tr.indeed.com/jobs $role $primaryCity",
            "site:linkedin.com/jobs/view $role $primaryCity Türkiye",
            "site:isbul.net $role $primaryCity iş ilanı",
            "site:manpower.com.tr $role $primaryCity",
            "site:tr.jooble.org $role $primaryCity"
        )
        return collect(
            (profileQueries + kpssQueries + iskUrAndLocal + kuraQueries + employerQueries + platformQueries).distinct(),
            240,
            city
        )
    }

    fun dailySearch(role: String = "", city: String = "Malatya"): List<Net.Hit> {
        val batch = dailySearchBatch(role, city)
        if (!batch.hasAnySuccessfulQuery) throw java.io.IOException("Günlük kaynak taramasında hiçbir arama sorgusu başarıyla tamamlanmadı.")
        return batch.hits
    }
}

data class CareerProcessEvent(
    val id: String,
    val title: String,
    val url: String,
    val snippet: String,
    val stage: String,
    val detectedAt: Long,
    val eventAt: Long?,
    val hasExplicitTime: Boolean
)

object CareerRecruitmentTracker {
    private val stageRules = listOf(
        "NİHAİ SONUÇ / YERLEŞTİRME" to listOf("nihai sonuc", "nihai liste", "yerlestirme sonucu", "ise baslama"),
        "SINAV SONUCU" to listOf("sinav sonucu", "mulakat sonucu", "sozlu sinav sonucu", "yazili sinav sonucu"),
        "NOTER KURASI SONUCU · ASIL / YEDEK" to listOf("noter kurasi sonuc", "kura sonucu", "kura sonuclari", "asil ve yedek", "asil aday", "yedek aday"),
        "SINAV / MÜLAKAT TARİHİ" to listOf("sinav tarihi", "sinava girecek", "sozlu sinav", "yazili sinav", "mulakat tarihi"),
        "KURA TARİHİ / KURAYA KATILACAKLAR" to listOf("kura cekimi", "kura cekilisi", "kura tarihi", "kuraya katilacak", "noter kurasi duyurusu", "noter kurasina katilacak", "kura listesi"),
        "EVRAK / BELGE KONTROLÜ" to listOf("evrak kontrol", "belge kontrol", "evrak teslim", "belgelerin teslim", "eksik evrak", "evrak inceleme"),
        "BAŞVURU / YENİ ALIM" to listOf(
            "personel alimi", "isci alimi", "iskur basvuru", "basvurular basladi",
            "son basvuru", "is ilani", "is ilanlari", "acik pozisyon",
            "kariyer firsati", "ise alim", "basvuruya acildi"
        )
    )

    private fun normalize(value: String): String = value.lowercase(Locale("tr", "TR"))
        .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
        .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')

    fun detectStages(title: String, snippet: String): List<String> {
        val text = normalize(title + " " + snippet)
        return stageRules.filter { rule -> rule.second.any(text::contains) }.map { it.first }
    }

    fun detectStage(title: String, snippet: String): String? =
        detectStages(title, snippet).firstOrNull()

    fun parseEventTime(title: String, snippet: String, stage: String? = null): Pair<Long, Boolean>? {
        val text = title + " " + snippet
        val markerPattern = when {
            stage?.startsWith("BAŞVURU") == true ->
                Regex("""(?i)(?:son\s+başvuru(?:\s+tarihi)?|son\s+müracaat(?:\s+tarihi)?|başvuruların\s+son\s+günü|son\s+basvuru(?:\s+tarihi)?)""")
            stage?.startsWith("EVRAK") == true ->
                Regex("""(?i)(?:evrak\s+teslim|belge(?:lerin)?\s+teslim|evrak\s+kontrol|belge\s+kontrol)""")
            stage?.startsWith("KURA TARİHİ") == true ->
                Regex("""(?i)(?:kura\s+(?:çekimi|çekilişi|tarihi|cekimi|cekilisi)|kuraya\s+katılacak|kuraya\s+katilacak|noter\s+kurası|noter\s+kurasi|noter\s+huzurunda)""")
            stage?.startsWith("SINAV /") == true ->
                Regex("""(?i)(?:sınav(?:ın|a|i)?\s*(?:yeri|tarihi)?|sinav(?:in|a|i)?\s*(?:yeri|tarihi)?|mülakat(?:ın)?\s+tarihi|mulakat(?:in)?\s+tarihi)""")
            stage?.startsWith("NOTER KURASI SONUCU") == true ||
                stage?.startsWith("SINAV SONUCU") == true ||
                stage?.startsWith("NİHAİ SONUÇ") == true -> return null
            else -> Regex("""(?i)(?:kura\s+(?:çekimi|tarihi|cekimi)|son\s+başvuru|son\s+basvuru|sınav\s+tarihi|sinav\s+tarihi|evrak\s+teslim|belge\s+teslim)""")
        }
        val marker = markerPattern.find(text) ?: return null
        val tail = text.substring(marker.range.first).take(650)
        val numericDate = Regex("""(?i)(\d{1,2}[./-]\d{1,2}[./-]\d{4})""").find(tail)
        val monthDate = Regex(
            """(?i)(\d{1,2}\s+(?:ocak|şubat|subat|mart|nisan|mayıs|mayis|haziran|temmuz|ağustos|agustos|eylül|eylul|ekim|kasım|kasim|aralık|aralik)\s+\d{4})"""
        ).find(tail)
        val dateMatch = numericDate ?: monthDate ?: return null
        val dateText = dateMatch.groupValues[1]
        val dateFormat = if (numericDate != null) "dd.MM.yyyy" else "d MMMM yyyy"
        val normalizedDate = dateText.replace('/', '.').replace('-', '.')
        val dateParser = java.text.SimpleDateFormat(dateFormat, Locale("tr", "TR")).apply { isLenient = false }
        val parsedDate = runCatching { dateParser.parse(normalizedDate) }.getOrNull() ?: return null
        val afterDate = tail.substring(dateMatch.range.last + 1).take(180)
        val normalizedAfterDate = normalize(afterDate)
        if (listOf("duyurulacak", "ilan edilecek", "aciklanacak", "yayimlanacak").any(normalizedAfterDate::contains)) {
            return null
        }
        val timeText = Regex("""(?i)(?:saat\s*)?([01]?\d|2[0-3])[:.]([0-5]\d)""")
            .find(afterDate)
        val calendar = Calendar.getInstance().apply {
            time = parsedDate
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeText != null) {
                set(Calendar.HOUR_OF_DAY, timeText.groupValues[1].toInt())
                set(Calendar.MINUTE, timeText.groupValues[2].toInt())
            } else {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }
        }
        return calendar.timeInMillis to (timeText != null)
    }

    fun read(prefs: android.content.SharedPreferences): List<CareerProcessEvent> {
        val json = runCatching { JSONArray(prefs.getString("career_process_events", "[]")) }
            .getOrDefault(JSONArray())
        return (0 until json.length()).mapNotNull { index ->
            val item = json.optJSONObject(index) ?: return@mapNotNull null
            val id = item.optString("id")
            val title = item.optString("title")
            val url = item.optString("url")
            val stage = item.optString("stage")
            if (id.isBlank() || title.isBlank() || url.isBlank() || stage.isBlank()) return@mapNotNull null
            CareerProcessEvent(
                id = id,
                title = title,
                url = url,
                snippet = item.optString("snippet"),
                stage = stage,
                detectedAt = item.optLong("detectedAt", 0L),
                eventAt = item.optLong("eventAt", 0L).takeIf { it > 0L },
                hasExplicitTime = item.optBoolean("hasExplicitTime", false)
            )
        }.sortedWith(
            compareBy<CareerProcessEvent> { event ->
                event.eventAt?.takeIf { it >= System.currentTimeMillis() } ?: Long.MAX_VALUE
            }.thenByDescending { it.detectedAt }
        )
    }

    fun recordNew(
        prefs: android.content.SharedPreferences,
        hits: List<Net.Hit>
    ): List<CareerProcessEvent> {
        val seen = prefs.getStringSet("career_process_seen", emptySet()).orEmpty().toMutableSet()
        val events = read(prefs).toMutableList()
        val added = ArrayList<CareerProcessEvent>()
        val now = System.currentTimeMillis()

        hits.forEach hitLoop@ { hit ->
            val stages = detectStages(hit.title, hit.snippet)
            if (stages.isEmpty()) return@hitLoop
            val normalizedTitle = normalize(hit.title).replace(Regex("""\s+"""), " ").trim()
            stages.forEach stageLoop@ { stage ->
                val parsed = parseEventTime(hit.title, hit.snippet, stage)
                val rawId = normalizedTitle + "|" + hit.url.substringBefore("#") + "|" + stage + "|" + (parsed?.first ?: 0L)
                val id = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(rawId.toByteArray(Charsets.UTF_8))
                    .joinToString("") { "%02x".format(it) }
                if (!seen.add(id)) return@stageLoop
                val event = CareerProcessEvent(
                    id = id,
                    title = hit.title,
                    url = hit.url,
                    snippet = hit.snippet,
                    stage = stage,
                    detectedAt = now,
                    eventAt = parsed?.first,
                    hasExplicitTime = parsed?.second ?: false
                )
                added += event
                events.add(0, event)
            }
        }

        while (seen.size > 800) seen.remove(seen.firstOrNull() ?: break)
        val stored = JSONArray()
        events.distinctBy { it.id }.take(120).forEach { event ->
            stored.put(
                JSONObject()
                    .put("id", event.id)
                    .put("title", event.title)
                    .put("url", event.url)
                    .put("snippet", event.snippet)
                    .put("stage", event.stage)
                    .put("detectedAt", event.detectedAt)
                    .put("eventAt", event.eventAt ?: 0L)
                    .put("hasExplicitTime", event.hasExplicitTime)
            )
        }
        prefs.edit()
            .putStringSet("career_process_seen", seen)
            .putString("career_process_events", stored.toString())
            .apply()
        return added
    }
}
object CareerMonitorScheduler {
    const val CHANNEL_ID = "aries_career_alerts"
    private const val UNIQUE_LEGACY = "aries_daily_career_scan"
    private const val UNIQUE_MORNING = "aries_career_morning_scan"
    private const val UNIQUE_EVENING = "aries_career_evening_scan"
    private const val UNIQUE_MANUAL = "aries_career_manual_scan"

    private fun constraints() = androidx.work.Constraints.Builder()
        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
        .build()

    private fun nextDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }
        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
    }

    private fun scheduleSlot(context: Context, uniqueName: String, label: String, hour: Int, minute: Int) {
        val request = androidx.work.PeriodicWorkRequestBuilder<CareerDailyWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(nextDelay(hour, minute), TimeUnit.MILLISECONDS)
            .setConstraints(constraints())
            .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setInputData(androidx.work.workDataOf("manual_run" to false, "scan_slot" to label))
            .addTag(uniqueName)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueName,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    /** Two independent daily windows. WorkManager can defer them under Android battery management. */
    fun schedule(context: Context) {
        val wm = androidx.work.WorkManager.getInstance(context)
        wm.cancelUniqueWork(UNIQUE_LEGACY)
        scheduleSlot(context, UNIQUE_MORNING, "Sabah", 8, 30)
        scheduleSlot(context, UNIQUE_EVENING, "Akşam", 20, 30)
    }

    fun scheduleIfEnabled(context: Context) {
        val enabled = context.getSharedPreferences("j", Context.MODE_PRIVATE)
            .getBoolean("career_monitor_enabled", false)
        if (enabled) schedule(context) else cancel(context)
    }

    /** Manual full-source scan works even when automatic monitoring is switched off. */
    fun runManualNow(context: Context): java.util.UUID {
        val request = androidx.work.OneTimeWorkRequestBuilder<CareerDailyWorker>()
            .setConstraints(constraints())
            .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setInputData(androidx.work.workDataOf("manual_run" to true, "scan_slot" to "Manuel"))
            .addTag(UNIQUE_MANUAL)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_MANUAL,
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
        return request.id
    }

    fun cancel(context: Context) {
        val wm = androidx.work.WorkManager.getInstance(context)
        wm.cancelUniqueWork(UNIQUE_LEGACY)
        wm.cancelUniqueWork(UNIQUE_MORNING)
        wm.cancelUniqueWork(UNIQUE_EVENING)
    }

    fun scheduleProcessReminders(context: Context, event: CareerProcessEvent) {
        val normalized = event.title.lowercase(Locale("tr", "TR"))
            .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
            .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
            .replace(Regex("""\s+"""), " ").trim()
        val familyKey = normalized + "|" + event.url.substringBefore("#") + "|" + event.stage
        val familyId = java.security.MessageDigest.getInstance("SHA-256")
            .digest(familyKey.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }.take(24)
        val baseName = "aries-process-" + familyId
        val workManager = androidx.work.WorkManager.getInstance(context)

        // Aynı duyurunun tarihi değişirse eski hatırlatma iptal edilip yenisi kurulur.
        workManager.cancelUniqueWork(baseName + "-day")
        workManager.cancelUniqueWork(baseName + "-hour")

        val eventAt = event.eventAt ?: return
        enqueueProcessReminder(context, event, baseName + "-day", eventAt - TimeUnit.DAYS.toMillis(1))
        if (event.hasExplicitTime) {
            enqueueProcessReminder(context, event, baseName + "-hour", eventAt - TimeUnit.HOURS.toMillis(1))
        }
    }

    private fun enqueueProcessReminder(
        context: Context,
        event: CareerProcessEvent,
        uniqueName: String,
        reminderAt: Long
    ) {
        val delay = reminderAt - System.currentTimeMillis()
        if (delay <= 0L) return
        val request = androidx.work.OneTimeWorkRequestBuilder<CareerProcessReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                androidx.work.workDataOf(
                    "event_title" to event.title,
                    "event_url" to event.url,
                    "event_stage" to event.stage,
                    "event_at" to (event.eventAt ?: 0L),
                    "has_explicit_time" to event.hasExplicitTime,
                    "reminder_kind" to uniqueName.substringAfterLast("-")
                )
            )
            .build()
        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueName,
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun notifyProcessUpdates(context: Context, events: List<CareerProcessEvent>) {
        if (events.isEmpty()) return
        ensureChannel(context)
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return
        val first = events.first()
        val open = Intent(Intent.ACTION_VIEW, Uri.parse(first.url))
        val requestCode = (first.id.hashCode() and 0x7fffffff).coerceAtLeast(1)
        val pending = PendingIntent.getActivity(
            context, requestCode, open,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        val details = events.take(4).joinToString("\n") { "• " + it.stage + ": " + it.title }
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, CHANNEL_ID)
            else Notification.Builder(context)
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Aries: kura / işe alım süreci güncellendi")
            .setContentText(events.size.toString() + " yeni aşama duyurusu bulundu.")
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setStyle(Notification.BigTextStyle().bigText(details + "\nKaynak sayfasını açıp tarihler ile aday listesini kontrol et."))
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(74025, builder.build())
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "İş ilanı bildirimleri", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

    fun notifyNewJobs(context: Context, count: Int, first: Net.Hit?, slot: String) {
        ensureChannel(context)
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return
        val open = Intent(context, CareerActivity::class.java)
        val pending = PendingIntent.getActivity(
            context, 74022, open,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, CHANNEL_ID)
            else Notification.Builder(context)
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Aries: $slot taramasında yeni ilan bağlantıları")
            .setContentText("$count yeni resmî/kurumsal/platform sonucu bulundu.")
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setStyle(Notification.BigTextStyle().bigText(
                (first?.title?.plus(" · ") ?: "") +
                    "$count yeni sonuç bulundu. İlanın aktifliğini ve son başvuru tarihini kaynak sayfasında doğrula."
            ))
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(if (slot == "Akşam") 74024 else 74023, builder.build())
    }
}

class CareerProcessReminderWorker(context: Context, params: androidx.work.WorkerParameters) :
    androidx.work.Worker(context, params) {

    override fun doWork(): androidx.work.ListenableWorker.Result {
        val title = inputData.getString("event_title").orEmpty()
        val url = inputData.getString("event_url").orEmpty()
        val stage = inputData.getString("event_stage").orEmpty()
        val eventAt = inputData.getLong("event_at", 0L)
        val hasExplicitTime = inputData.getBoolean("has_explicit_time", false)
        val reminderKind = inputData.getString("reminder_kind").orEmpty()
        if (title.isBlank() || url.isBlank() || stage.isBlank()) {
            return androidx.work.ListenableWorker.Result.failure()
        }
        CareerMonitorScheduler.ensureChannel(applicationContext)
        if (Build.VERSION.SDK_INT >= 33 &&
            applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return androidx.work.ListenableWorker.Result.success()

        val time = if (eventAt > 0L) {
            val format = if (hasExplicitTime) "dd.MM.yyyy HH:mm" else "dd.MM.yyyy"
            java.text.SimpleDateFormat(format, Locale("tr", "TR")).format(java.util.Date(eventAt))
        } else "Tarih kaynak duyurusunda ayrıca kontrol edilmeli"
        val message = when (reminderKind) {
            "hour" -> "Etkinliğe yaklaşık 1 saat kaldı: " + time
            else -> "Yarın için takip hatırlatması: " + time +
                if (hasExplicitTime) "" else " · Duyuruda saat belirtilmemiş."
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val requestCode = (url.hashCode() and 0x7fffffff).coerceAtLeast(1)
        val pending = PendingIntent.getActivity(
            applicationContext, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(applicationContext, CareerMonitorScheduler.CHANNEL_ID)
            else Notification.Builder(applicationContext)
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Aries · " + stage)
            .setContentText(message)
            .setStyle(Notification.BigTextStyle().bigText(title + "\n" + message + "\nKaynak: " + url))
            .setAutoCancel(true)
            .setContentIntent(pending)
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(requestCode, builder.build())
        return androidx.work.ListenableWorker.Result.success()
    }
}

class CareerDailyWorker(context: Context, params: androidx.work.WorkerParameters) :
    androidx.work.Worker(context, params) {

    override fun doWork(): androidx.work.ListenableWorker.Result {
        val prefs = applicationContext.getSharedPreferences("j", Context.MODE_PRIVATE)
        val manualRun = inputData.getBoolean("manual_run", false)
        val slot = inputData.getString("scan_slot").orEmpty().ifBlank { if (manualRun) "Manuel" else "Otomatik" }
        if (!manualRun && !prefs.getBoolean("career_monitor_enabled", false)) {
            return androidx.work.ListenableWorker.Result.success()
        }

        prefs.edit()
            .putLong("career_monitor_last_attempt", System.currentTimeMillis())
            .putString("career_monitor_running_slot", slot)
            .putBoolean("career_monitor_is_running", true)
            .apply()

        return try {
            val role = prefs.getString(
                "career_role",
                "elektrik teknisyeni, pano montajı, bakım-onarım, üretim, depo, operatör, satış, beden işçisi"
            ).orEmpty()
            val city = prefs.getString(
                "career_place",
                "Malatya, Battalgazi, Yeşilyurt, Elazığ, Adıyaman; servis/konaklama varsa Türkiye geneli"
            ).orEmpty()
            val batch = CareerSearchEngine.dailySearchBatch(role, city)
            if (!batch.hasAnySuccessfulQuery) {
                prefs.edit()
                    .putBoolean("career_monitor_is_running", false)
                    .putString("career_monitor_last_error",
                        "Kaynak taraması başarısız: hiçbir arama sorgusu tamamlanamadı. " +
                            batch.errorMessages.take(4).joinToString(" | ") +
                            " İnternet/arama servisi kontrol edilip yeniden denenecek.")
                    .apply()
                return androidx.work.ListenableWorker.Result.retry()
            }

            val hits = batch.hits
            val orderedSeen = LinkedHashSet<String>()
            val oldOrder = runCatching {
                JSONArray(prefs.getString("career_monitor_seen_order", "[]"))
            }.getOrDefault(JSONArray())
            for (index in 0 until oldOrder.length()) {
                val fingerprint = oldOrder.optString(index)
                if (fingerprint.isNotBlank()) orderedSeen.add(fingerprint)
            }
            prefs.getStringSet("career_monitor_seen", emptySet()).orEmpty().forEach { orderedSeen.add(it) }
            val firstRun = !prefs.getBoolean("career_monitor_initialized", false)
            val fresh = if (firstRun) hits else hits.filter { hit ->
                val fingerprint = CareerSearchEngine.fingerprint(hit.title, hit.url)
                val isNew = fingerprint !in orderedSeen
                orderedSeen.add(fingerprint)
                isNew
            }
            hits.forEach { hit -> orderedSeen.add(CareerSearchEngine.fingerprint(hit.title, hit.url)) }
            while (orderedSeen.size > 3000) {
                val oldest = orderedSeen.firstOrNull() ?: break
                orderedSeen.remove(oldest)
            }
            val boundedSeen = orderedSeen.toSet()
            val seenOrderJson = JSONArray().apply { orderedSeen.forEach { put(it) } }

            val json = JSONArray()
            val ranked = hits.map { hit ->
                val eligibility = CareerEligibility.assess(hit.title, hit.snippet, hit.url)
                val score = CareerEligibility.profileMatchScore(hit.title, hit.snippet, role, city, eligibility)
                Triple(hit, eligibility, score)
            }.sortedByDescending { it.third }

            ranked.take(100).forEach { (hit, eligibility, score) ->
                val category = CareerSourceCatalog.categoryFor(hit.url)
                val listingStatus = if (CareerSourceCatalog.isLikelyJobResult(hit.title, hit.snippet)) {
                    "İlan adayı · açık olup olmadığı ve başvuru kabulü sayfada doğrulanmalı"
                } else {
                    "Kariyer/duyuru kaynağı · açık ilan olduğu doğrulanmadı"
                }
                json.put(
                    JSONObject()
                        .put("title", hit.title)
                        .put("url", hit.url)
                        .put("snippet", hit.snippet)
                        .put("source", CareerSourceCatalog.sourceName(hit.url))
                        .put("category", category)
                        .put("isSecondary", CareerSourceCatalog.isSecondary(hit.url))
                        .put("eligibility", eligibility)
                        .put("listingStatus", listingStatus)
                        .put("profileMatchScore", score)
                        .put("profileMatchReason", CareerEligibility.profileMatchReason(hit.title, hit.snippet, role, city, eligibility))
                        .put("deadlineDate", CareerListingParser.deadlineDate(hit.snippet) ?: "")
                        .put("deadlineStatus", CareerListingParser.deadlineStatus(hit.snippet) ?: "")
                        .put("fingerprint", CareerSearchEngine.fingerprint(hit.title, hit.url))
                        .put("lastSeenAt", System.currentTimeMillis())
                )
            }

            val partialWarning = if (batch.failedQueries > 0) {
                "${batch.successfulQueries}/${batch.totalQueries} arama sorgusu tamamlandı; sonuçlar kısmi olabilir. " +
                    batch.errorMessages.take(3).joinToString(" | ")
            } else null
            val freshForAlerts = fresh.filter { hit ->
                CareerListingParser.deadlineStatus(hit.snippet)
                    ?.startsWith("SÜRESİ GEÇMİŞ", true) != true
            }
            val processEvents = CareerRecruitmentTracker.recordNew(prefs, hits)
            processEvents.forEach { event ->
                CareerMonitorScheduler.scheduleProcessReminders(applicationContext, event)
            }
            val actionableProcessEvents = processEvents.filter { event ->
                event.stage != "BAŞVURU / YENİ ALIM" ||
                    CareerListingParser.deadlineStatus(event.snippet)
                        ?.startsWith("SÜRESİ GEÇMİŞ", true) != true
            }
            val now = System.currentTimeMillis()

            prefs.edit()
                .putStringSet("career_monitor_seen", boundedSeen)
                .putString("career_monitor_seen_order", seenOrderJson.toString())
                .putString("career_monitor_results", json.toString())
                .putLong("career_monitor_last_scan", now)
                .putString("career_monitor_last_slot", slot)
                .putString("career_monitor_last_run_type", if (manualRun) "Manuel" else "Otomatik")
                .putInt("career_monitor_new_count", freshForAlerts.size)
                .putInt("career_monitor_result_count", hits.size)
                .putBoolean("career_monitor_initialized", true)
                .putInt("career_monitor_successful_queries", batch.successfulQueries)
                .putInt("career_monitor_total_queries", batch.totalQueries)
                .putBoolean("career_monitor_is_running", false)
                .putString("career_monitor_last_error", partialWarning)
                .putString("career_monitor_empty_reason",
                    if (hits.isEmpty()) "Arama sorguları çalıştı ancak bu turda eşleşen ilan bağlantısı bulunamadı. Bu, kaynaklarda ilan olmadığı anlamına gelmez."
                    else "")
                .apply()

            if (!manualRun && freshForAlerts.isNotEmpty()) {
                CareerMonitorScheduler.notifyNewJobs(applicationContext, freshForAlerts.size, freshForAlerts.first(), slot)
            }
            if (!manualRun && actionableProcessEvents.isNotEmpty()) {
                CareerMonitorScheduler.notifyProcessUpdates(applicationContext, actionableProcessEvents)
            }
            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            prefs.edit()
                .putBoolean("career_monitor_is_running", false)
                .putString("career_monitor_last_error",
                    "Tarama tamamlanamadı: ${e.message ?: "bağlantı veya kaynak hatası"}")
                .apply()
            androidx.work.ListenableWorker.Result.retry()
        }
    }
}

