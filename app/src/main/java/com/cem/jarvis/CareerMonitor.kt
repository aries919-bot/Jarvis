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
        Source("Kariyer Kapısı – Kamu işe alım", "https://isealimkariyerkapisi.cbiko.gov.tr/", "Kamu"),
        Source("ÖSYM – KPSS tercih ve yerleştirme duyuruları", "https://www.osym.gov.tr/Duyurular/Index", "Kamu / KPSS"),
        Source("Kamu İlan Sistemi", "https://kamuilan.sbb.gov.tr/", "Kamu"),
        Source("İŞKUR e-Şube", "https://esube.iskur.gov.tr/", "İŞKUR / açık iş"),
        Source("İŞKUR Açık İş Haritası", "https://acikisharita.iskur.gov.tr/", "İŞKUR / açık iş"),
        Source("ilan.gov.tr – Personel alımı", "https://www.ilan.gov.tr/", "Resmî ilan"),
        Source("Resmî Gazete", "https://www.resmigazete.gov.tr/", "Resmî ilan"),
        Source("Yerel Yönetimler Genel Müdürlüğü", "https://yerelyonetimler.csb.gov.tr/", "Belediye"),
        Source("Malatya Büyükşehir Belediyesi", "https://www.malatya.bel.tr/", "Belediye"),
        Source("Battalgazi Belediyesi", "https://www.battalgazi.bel.tr/", "Belediye"),
        Source("Yeşilyurt Belediyesi", "https://yesilyurt.bel.tr/", "Belediye"),
        Source("Malatya TSO – günlük İŞKUR listesi", "https://www.malatyatso.org.tr/iskur-ilanlari", "İŞKUR keşif", false),
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
        "cbiko.gov.tr", "osym.gov.tr", "sbb.gov.tr", "iskur.gov.tr", "mke.gov.tr", "etimaden.gov.tr", "tubitak.gov.tr", "turkseker.gov.tr", "sisecam.com", "turktelekomkariyer.com.tr", "ilan.gov.tr", "resmigazete.gov.tr",
        "csb.gov.tr", "malatya.bel.tr", "battalgazi.bel.tr", "yesilyurt.bel.tr", "aselsan.com", "tcdd.gov.tr", "tcddtasimacilik.gov.tr",
        "teias.gov.tr", "euas.gov.tr", "tusas.com", "botas.gov.tr", "tupras.com.tr", "tupras.gethirex.com", "tai.com.tr",
        "roketsan.com.tr", "tei.com.tr", "havelsan.com", "enerjisa.com.tr", "malatyatso.org.tr"
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

    fun isSecondary(url: String): Boolean = url.contains("malatyatso.org.tr", ignoreCase = true)

    fun categoryFor(url: String): String {
        val host = runCatching { java.net.URI(url).host.orEmpty().lowercase(Locale.ROOT) }.getOrDefault("")
        return when {
            isSecondary(url) || host.contains("iskur.gov.tr") -> "İŞKUR / KPSS'siz işçi"
            host.contains("osym.gov.tr") || host.contains("cbiko.gov.tr") ||
                host.contains("sbb.gov.tr") || host.contains("ilan.gov.tr") ||
                host.contains("resmigazete.gov.tr") -> "Kamu / KPSS ve merkezi yerleştirme"
            host.contains("malatya.bel.tr") || host.contains("battalgazi.bel.tr") ||
                host.contains("yesilyurt.bel.tr") || host.contains("csb.gov.tr") -> "Belediye / kamu"
            host.contains("tcdd") || host.contains("teias.gov.tr") || host.contains("euas.gov.tr") ||
                host.contains("mke.gov.tr") || host.contains("etimaden.gov.tr") ||
                host.contains("botas.gov.tr") || host.contains("turkseker.gov.tr") -> "Kamu kurumu / kamu şirketi"
            else -> "Özel sektör / büyük şirket"
        }
    }

    fun isLikelyJobResult(title: String, snippet: String): Boolean {
        val text = (title + " " + snippet).lowercase(Locale("tr", "TR"))
        val indicators = listOf(
            "personel al", "işçi al", "is ilan", "iş ilan", "açık pozisyon", "acik pozisyon",
            "kariyer", "başvuru", "basvuru", "son başvuru", "son basvuru", "teknisyen",
            "tekniker", "memur", "işkur", "iskur", "kpss", "üretim", "uretim",
            "bakım", "bakim", "elektrik", "pano", "operatör", "operator"
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
                return if (minScore == null) {
                    "P94 İLE TERCİH DEĞERLENDİRMESİ · Merkezi yerleştirmede herkese uygulanan sabit bir taban puan varsayılmaz. 63,23366 puanınla tercih hakkın, ilgili nitelik kodları ve kontenjan/puan sıralamasına bağlıdır."
                } else {
                    "İLK PUAN KONTROLÜ GEÇİLDİ · İlanda görünen P94 alt sınırı 63,23366 puanını aşmıyor; öğrenim, nitelik kodu ve diğer şartlar ayrıca kontrol edilmeli."
                }
            }
            return "KPSS ŞARTI BELİRSİZ · Puan türü ve varsa alt sınır ilanın tam metninden doğrulanmalı; uygunluk kesinleştirilmedi."
        }

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
        return "NİTELİK KODLARI KONTROL EDİLMELİ · Kodların tamamı ÖSYM kılavuzundaki açıklamalarıyla karşılaştırılmalı."
    }

    fun extractQualificationCodes(text: String): Set<Int> =
        Regex("(?<!\\d)(20\\d{2})(?!\\d)").findAll(text).map { it.value.toInt() }.toSet()

    fun normalizeForFingerprint(value: String): String = normalize(value)
        .replace(Regex("[^a-z0-9 ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    fun relevance(title: String, snippet: String, city: String): Int {
        val text = normalize("$title $snippet")
        val terms = listOf(
            "elektrik", "pano", "kablolama", "bakim", "teknisyen", "tekniker",
            "otomasyon", "uretim", "isci", "personel", "memur", "operator",
            "depo", "satis", "kasiyer", "paketleme", "tekstil", "gida",
            "lojistik", "temizlik", "sofor", "guvenlik", "cagri merkezi"
        )
        var score = terms.count { text.contains(normalize(it)) } * 8
        if (city.isNotBlank() && text.contains(normalize(city))) score += 20
        if (listOf("son basvuru", "basvuru tarihi", "personel alimi", "acik pozisyon", "is ilani")
                .any(text::contains)) score += 8
        return score.coerceAtMost(100)
    }
}

data class CareerScanBatch(
    val hits: List<Net.Hit>,
    val successfulQueries: Int,
    val failedQueries: Int,
    val totalQueries: Int
) {
    val hasAnySuccessfulQuery: Boolean get() = successfulQueries > 0
}

/** DuckDuckGo sonuçları yalnızca keşif içindir; kayda giren bağlantılar güvenilir resmî alan adlarıyla sınırlıdır. */
/** Arama motoru yalnızca keşif katmanıdır. Her gösterilen sonuç güvenilir kurum alan adıyla sınırlanır;
 * sayfanın güncel ilan olduğunu doğrulamaz. Başvuru şartı ve son tarih ilanın kendisinden okunmalıdır. */
object CareerSearchEngine {
    private fun queryList(category: Int, role: String, city: String): List<String> {
        val r = role.ifBlank { "elektrik teknisyeni pano montaj bakım üretim işçi personel" }
        val c = city.ifBlank { "Malatya Türkiye" }
        val publicKpss = listOf(
            "site:osym.gov.tr KPSS-2026/2 ortaöğretim P94 merkezi yerleştirme tercih kılavuzu",
            "site:osym.gov.tr KPSS ortaöğretim P94 2026/2 nitelik kodu 2023 2001",
            "site:kamuilan.sbb.gov.tr personel alımı lise ortaöğretim KPSS P94 2026 $c",
            "site:isealimkariyerkapisi.cbiko.gov.tr lise ortaöğretim personel alımı KPSS 2026 $c",
            "site:ilan.gov.tr personel alımı lise ortaöğretim KPSS 2026 $c",
            "site:resmigazete.gov.tr personel alımı atama ortaöğretim KPSS 2026 $c"
        )
        val publicNoKpss = listOf(
            "site:esube.iskur.gov.tr açık iş ilanı $r $c 2026",
            "site:acikisharita.iskur.gov.tr Malatya elektrik teknisyen işçi 2026",
            "site:yerelyonetimler.csb.gov.tr belediye işçi personel alımı 2026 Malatya",
            "site:malatya.bel.tr personel alımı iş ilanı 2026",
            "site:battalgazi.bel.tr personel işçi alımı 2026",
            "site:yesilyurt.bel.tr personel işçi alımı 2026",
            "site:malatyatso.org.tr/iskur-ilanlari Malatya açık iş ilanları 2026"
        )
        val privateSector = listOf(
            "site:akariyer.aselsan.com teknisyen elektrik üretim açık pozisyon",
            "site:kariyer.tusas.com teknisyen elektrik bakım açık pozisyon 2026",
            "site:kariyer.roketsan.com.tr/jobs teknisyen elektronik üretim bakım 2026",
            "site:tupras.gethirex.com/o/tupras elektrik teknisyen bakım açık pozisyon",
            "site:sisecam.com/tr/kariyer elektrik bakım teknisyen üretim açık pozisyon",
            "site:turktelekomkariyer.com.tr teknisyen teknik personel açık pozisyon"
        )
        val publicEmployers = listOf(
            "site:teias.gov.tr personel alımı işçi teknisyen 2026",
            "site:euas.gov.tr personel alımı teknisyen işçi 2026",
            "site:kariyer.mke.gov.tr elektrik teknisyen personel ilanları",
            "site:etimaden.gov.tr personel işçi KPSS alımı duyurular 2026",
            "site:kariyer.tubitak.gov.tr teknik personel açık pozisyon 2026",
            "site:turkseker.gov.tr personel işçi alımı duyurular 2026",
            "site:botas.gov.tr personel alımı teknisyen 2026",
            "site:tcdd.gov.tr personel alımı işçi teknisyen 2026",
            "site:tcddtasimacilik.gov.tr duyurular personel alımı teknisyen 2026"
        )
        val postAssociate = listOf(
            "elektrik teknikeri ön lisans mezunu KPSS P93 personel alımı 2026",
            "elektrik teknikeri MYO mezunu özel sektör açık pozisyon 2026",
            "site:teias.gov.tr OR site:euas.gov.tr elektrik teknikeri ön lisans mezunu 2026",
            "site:aselsan.com OR site:tusas.com elektrik teknikeri ön lisans mezunu 2026"
        )
        val extra = "$r $c iş ilanı başvuru şartları son başvuru tarihi 2026"
        return when (category) {
            1 -> publicKpss + listOf(extra)
            2 -> publicNoKpss + listOf(extra)
            3 -> privateSector + listOf(extra)
            4 -> publicEmployers + privateSector
            5 -> postAssociate
            else -> publicKpss + publicNoKpss + publicEmployers + privateSector + listOf(extra)
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

    private fun collect(queries: List<String>, timeoutSeconds: Long): CareerScanBatch {
        if (queries.isEmpty()) return CareerScanBatch(emptyList(), 0, 0, 0)
        val executor = Executors.newFixedThreadPool(minOf(6, queries.size))
        return try {
            val futures = executor.invokeAll(
                queries.map { query -> Callable { Net.webSearch(query) } },
                timeoutSeconds,
                TimeUnit.SECONDS
            )
            val found = ArrayList<Net.Hit>()
            var succeeded = 0
            var failed = 0
            futures.forEach { future ->
                if (future.isCancelled) {
                    failed++
                } else {
                    try {
                        found.addAll(future.get())
                        succeeded++
                    } catch (_: Exception) {
                        failed++
                    }
                }
            }
            val seen = HashSet<String>()
            val filtered = found.filter { hit ->
                CareerSourceCatalog.isTrustedUrl(hit.url) &&
                    CareerSourceCatalog.isLikelyJobResult(hit.title, hit.snippet) &&
                    seen.add(fingerprint(hit.title, hit.url))
            }.sortedByDescending {
                CareerEligibility.relevance(it.title, it.snippet, "Malatya")
            }
            CareerScanBatch(filtered, succeeded, failed, queries.size)
        } finally {
            executor.shutdownNow()
        }
    }

    fun searchBatch(category: Int, role: String, city: String): CareerScanBatch =
        collect(queryList(category, role, city), 75)

    fun search(category: Int, role: String, city: String): List<Net.Hit> {
        val batch = searchBatch(category, role, city)
        if (!batch.hasAnySuccessfulQuery) throw java.io.IOException("Arama kaynaklarına ulaşılamadı.")
        return batch.hits
    }

    fun dailySearchBatch(): CareerScanBatch {
        val queries = listOf(
            "site:osym.gov.tr KPSS-2026/2 ortaöğretim P94 merkezi yerleştirme tercih kılavuzu",
            "site:osym.gov.tr KPSS ortaöğretim P94 lise 2026 tercih nitelik kodları",
            "site:kamuilan.sbb.gov.tr ortaöğretim lise KPSS P94 personel alımı 2026",
            "site:isealimkariyerkapisi.cbiko.gov.tr lise personel alımı ortaöğretim KPSS 2026",
            "site:ilan.gov.tr personel alımı lise KPSS 2026",
            "site:resmigazete.gov.tr atama personel alımı 2026",
            "site:esube.iskur.gov.tr Malatya elektrik teknisyen bakım işçi açık iş ilanı 2026",
            "site:acikisharita.iskur.gov.tr Malatya açık iş ilanları 2026",
            "site:malatyatso.org.tr/iskur-ilanlari Malatya İŞKUR ilanları elektrik işçi 2026",
            "site:malatya.bel.tr OR site:battalgazi.bel.tr OR site:yesilyurt.bel.tr işçi personel alımı 2026",
            "site:yerelyonetimler.csb.gov.tr belediye personel alımı işçi 2026",
            "site:akariyer.aselsan.com teknisyen elektrik açık pozisyon",
            "site:kariyer.tusas.com teknisyen elektrik bakım açık pozisyon 2026",
            "site:kariyer.roketsan.com.tr/jobs elektronik üretim teknisyeni bakım 2026",
            "site:teias.gov.tr OR site:euas.gov.tr OR site:botas.gov.tr personel alımı işçi teknisyen 2026",
            "site:tcdd.gov.tr OR site:tcddtasimacilik.gov.tr personel alımı işçi teknisyen 2026",
            "site:kariyer.mke.gov.tr OR site:etimaden.gov.tr OR site:kariyer.tubitak.gov.tr personel alımı teknisyen 2026",
            "site:tupras.gethirex.com OR site:yetenek.tei.com.tr OR site:havelsan.com teknisyen kariyer açık pozisyon",
            "site:sisecam.com/tr/kariyer OR site:turktelekomkariyer.com.tr açık pozisyon üretim teknisyen 2026",
            "site:enerjisa.com.tr/tr/enerjisa-hakkinda/kariyer elektrik teknisyen personel 2026"
        )
        return collect(queries, 90)
    }

    fun dailySearch(): List<Net.Hit> {
        val batch = dailySearchBatch()
        if (!batch.hasAnySuccessfulQuery) throw java.io.IOException("Günlük kaynak taramasında hiçbir arama sorgusu başarıyla tamamlanmadı.")
        return batch.hits
    }
}

object CareerMonitorScheduler {
    const val CHANNEL_ID = "aries_career_alerts"
    private const val UNIQUE_WORK = "aries_daily_career_scan"
    private const val UNIQUE_NOW = "aries_career_scan_now"

    private fun constraints() = androidx.work.Constraints.Builder()
        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
        .build()

    /** WorkManager kalıcı iş kuyruğudur; Android pil yönetimi nedeniyle tam saat garantisi vermez. */
    fun schedule(context: Context) {
        val now = Calendar.getInstance()
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 20)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }
        val delay = (target.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
        val request = androidx.work.PeriodicWorkRequestBuilder<CareerDailyWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints())
            .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(UNIQUE_WORK)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleIfEnabled(context: Context) {
        val enabled = context.getSharedPreferences("j", Context.MODE_PRIVATE)
            .getBoolean("career_monitor_enabled", false)
        if (enabled) schedule(context) else cancel(context)
    }

    fun runNow(context: Context) {
        val request = androidx.work.OneTimeWorkRequestBuilder<CareerDailyWorker>()
            .setConstraints(constraints())
            .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(UNIQUE_NOW)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_NOW,
            androidx.work.ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK)
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_NOW)
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "İş ilanı bildirimleri", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

    fun notifyNewJobs(context: Context, count: Int, first: Net.Hit?) {
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
            .setContentTitle("Aries: yeni iş ilanı bağlantısı")
            .setContentText("$count yeni resmî/kurumsal kaynak sonucu bulundu.")
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setStyle(Notification.BigTextStyle().bigText(
                (first?.title?.plus(" · ") ?: "") +
                    "$count yeni sonuç. İlanın hâlâ açık olduğunu ve başvuru koşullarını resmî sayfada doğrula."
            ))
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(74023, builder.build())
    }
}

class CareerDailyWorker(context: Context, params: androidx.work.WorkerParameters) :
    androidx.work.Worker(context, params) {

    override fun doWork(): androidx.work.ListenableWorker.Result {
        val prefs = applicationContext.getSharedPreferences("j", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("career_monitor_enabled", false)) return androidx.work.ListenableWorker.Result.success()
        return try {
            val batch = CareerSearchEngine.dailySearchBatch()
            if (!batch.hasAnySuccessfulQuery) {
                prefs.edit()
                    .putString("career_monitor_last_error",
                        "Kaynak taraması başarısız: hiçbir arama sorgusu tamamlanamadı. İnternet erişimi/arama servisi kontrol edilecek ve yeniden denenecek.")
                    .apply()
                return androidx.work.ListenableWorker.Result.retry()
            }

            val hits = batch.hits
            val previous = prefs.getStringSet("career_monitor_seen", emptySet()).orEmpty()
            val firstRun = !prefs.getBoolean("career_monitor_initialized", false)
            val seen = previous.toMutableSet()
            val fresh = if (firstRun) hits else hits.filter { hit ->
                seen.add(CareerSearchEngine.fingerprint(hit.title, hit.url))
            }
            hits.forEach { hit -> seen.add(CareerSearchEngine.fingerprint(hit.title, hit.url)) }
            val boundedSeen = seen.sorted().takeLast(2000).toSet()

            val json = JSONArray()
            hits.take(60).forEach { hit ->
                val category = CareerSourceCatalog.categoryFor(hit.url)
                json.put(
                    JSONObject()
                        .put("title", hit.title)
                        .put("url", hit.url)
                        .put("snippet", hit.snippet)
                        .put("source", CareerSourceCatalog.sourceName(hit.url))
                        .put("category", category)
                        .put("eligibility", CareerEligibility.assess(hit.title, hit.snippet, hit.url))
                        .put("listingStatus", if (CareerSourceCatalog.isLikelyJobResult(hit.title, hit.snippet))
                            "İlan adayı · güncellik ve açık başvuru durumu sayfada doğrulanmalı"
                        else "Kariyer/duyuru kaynağı · açık ilan olduğu doğrulanmadı")
                        .put("fingerprint", CareerSearchEngine.fingerprint(hit.title, hit.url))
                )
            }
            val partialWarning = if (batch.failedQueries > 0) {
                "\${batch.successfulQueries}/\${batch.totalQueries} arama sorgusu tamamlandı; sonuçlar kısmi olabilir."
            } else null

            prefs.edit()
                .putStringSet("career_monitor_seen", boundedSeen)
                .putString("career_monitor_results", json.toString())
                .putLong("career_monitor_last_scan", System.currentTimeMillis())
                .putInt("career_monitor_new_count", fresh.size)
                .putBoolean("career_monitor_initialized", true)
                .putInt("career_monitor_successful_queries", batch.successfulQueries)
                .putInt("career_monitor_total_queries", batch.totalQueries)
                .putString("career_monitor_last_error", partialWarning)
                .apply()

            if (fresh.isNotEmpty()) CareerMonitorScheduler.notifyNewJobs(applicationContext, fresh.size, fresh.first())
            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            prefs.edit()
                .putString("career_monitor_last_error",
                    "Tarama tamamlanamadı: \${e.message ?: "bağlantı veya kaynak hatası"}")
                .apply()
            androidx.work.ListenableWorker.Result.retry()
        }
    }
}
