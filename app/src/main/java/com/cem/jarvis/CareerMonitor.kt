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
        Source("e-Devlet – İş ve Kariyer hizmetleri", "https://www.turkiye.gov.tr/arama?aranan=kamu+al%C4%B1m", "Kamu / e-Devlet hizmetleri"),
        Source("Malatya Turgut Özal Üniversitesi – Personel Daire Başkanlığı", "https://personel.ozal.edu.tr/announcements-all", "Üniversite / kamu personel"),
        Source("İnönü Üniversitesi – Personel Daire Başkanlığı", "https://www.inonu.edu.tr/personeldb/duyurular", "Üniversite / kamu personel"),
        Source("Fırat Üniversitesi – Personel Daire Başkanlığı", "https://personeldb.firat.edu.tr/announcements-all", "Üniversite / kamu personel"),
        Source("Adıyaman Üniversitesi – Personel Daire Başkanlığı", "https://pdb.adiyaman.edu.tr/tr/duyurular", "Üniversite / kamu personel"),
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
        Source("İçişleri Bakanlığı – duyurular", "https://www.icisleri.gov.tr/duyurular", "Kamu / bakanlık"),
        Source("Millî Savunma Bakanlığı – Personel Temin Sistemi", "https://personeltemin.msb.gov.tr/", "Kamu / personel temin"),
        Source("Jandarma Genel Komutanlığı – duyurular", "https://jandarma.gov.tr/duyurular", "Kamu / güvenlik personeli"),
        Source("Sahil Güvenlik Komutanlığı", "https://www.sg.gov.tr/", "Kamu / güvenlik personeli"),
        Source("Çalışma ve Sosyal Güvenlik Bakanlığı – duyurular", "https://www.csgb.gov.tr/duyurular/", "Kamu / bakanlık"),
        Source("Kültür ve Turizm Bakanlığı – Personel Genel Müdürlüğü", "https://pgm.ktb.gov.tr/", "Kamu / personel genel müdürlüğü"),
        Source("Savunma Sanayii Başkanlığı – duyurular", "https://www.ssb.gov.tr/duyurular", "Kamu / savunma sanayii"),
        Source("Türkiye Turizm Tanıtım ve Geliştirme Ajansı – kariyer", "https://tga.gov.tr/kariyer", "Kamu bağlantılı / kurumsal kariyer"),
        Source("Sağlık Bakanlığı – personel duyuruları", "https://www.saglik.gov.tr/", "Kamu / bakanlık"),
        Source("Adalet Bakanlığı – personel duyuruları", "https://www.adalet.gov.tr/", "Kamu / bakanlık"),
        Source("Aile ve Sosyal Hizmetler Bakanlığı", "https://www.aile.gov.tr/", "Kamu / bakanlık"),
        Source("Millî Eğitim Bakanlığı – personel duyuruları", "https://www.meb.gov.tr/", "Kamu / bakanlık"),
        Source("Gençlik ve Spor Bakanlığı", "https://www.gsb.gov.tr/", "Kamu / bakanlık"),
        Source("Tarım ve Orman Bakanlığı", "https://www.tarimorman.gov.tr/", "Kamu / bakanlık"),
        Source("Ulaştırma ve Altyapı Bakanlığı", "https://www.uab.gov.tr/duyurular", "Kamu / bakanlık"),
        Source("Devlet Su İşleri Genel Müdürlüğü", "https://www.dsi.gov.tr/", "Kamu / genel müdürlük"),
        Source("Devlet Hava Meydanları İşletmesi", "https://www.dhmi.gov.tr/", "Kamu / KİT"),
        Source("Devlet Malzeme Ofisi", "https://www.dmo.gov.tr/", "Kamu / KİT"),
        Source("Devlet Arşivleri Başkanlığı", "https://www.devletarsivleri.gov.tr/", "Kamu / başkanlık"),
        Source("Darphane ve Damga Matbaası", "https://www.darphane.gov.tr/", "Kamu / KİT"),
        Source("Gelir İdaresi Başkanlığı", "https://www.gib.gov.tr/", "Kamu / başkanlık"),
        Source("Göç İdaresi Başkanlığı", "https://www.goc.gov.tr/", "Kamu / başkanlık"),
        Source("Sosyal Güvenlik Kurumu", "https://www.sgk.gov.tr/", "Kamu / başkanlık"),
        Source("AFAD", "https://www.afad.gov.tr/", "Kamu / başkanlık"),
        Source("Kıyı Emniyeti Genel Müdürlüğü", "https://www.kiyiemniyeti.gov.tr/", "Kamu / KİT"),
        Source("PTT – duyurular", "https://www.ptt.gov.tr/", "Kamu / KİT"),
        Source("İller Bankası", "https://www.ilbank.gov.tr/", "Kamu / KİT"),
        Source("KOSGEB", "https://www.kosgeb.gov.tr/", "Kamu / başkanlık"),
        Source("Kalkınma ajansları – resmî ilan keşfi", "https://www.sanayi.gov.tr/", "Kamu / bağlı kuruluş"),
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
        "turkiye.gov.tr", "ozal.edu.tr", "inonu.edu.tr", "firat.edu.tr", "adiyaman.edu.tr",
        "cbiko.gov.tr", "kariyerkapisi.gov.tr", "osym.gov.tr", "sbb.gov.tr", "iskur.gov.tr", "mke.gov.tr", "etimaden.gov.tr", "tubitak.gov.tr", "turkseker.gov.tr", "sisecam.com", "turktelekomkariyer.com.tr", "ilan.gov.tr", "resmigazete.gov.tr",
        "csb.gov.tr", "ogm.gov.tr", "kgm.gov.tr", "ant.gov.tr", "gov.tr", "bel.tr", "malatya.bel.tr", "battalgazi.bel.tr", "yesilyurt.bel.tr", "aselsan.com", "tcdd.gov.tr", "tcddtasimacilik.gov.tr",
        "teias.gov.tr", "euas.gov.tr", "tusas.com", "botas.gov.tr", "tupras.com.tr", "tupras.gethirex.com", "tai.com.tr",
        "roketsan.com.tr", "tei.com.tr", "havelsan.com", "enerjisa.com.tr", "malatyatso.org.tr",
        "malorsa.org.tr", "kariyer.net", "eleman.net", "secretcv.com", "yenibiris.com",
        "indeed.com", "linkedin.com", "isbul.net", "manpower.com.tr", "jooble.org",
        "pladis.wd3.myworkdayjobs.com", "kockariyerim.com", "koccareers.com", "sabanci.com",
        "borusan.com", "eczacibasi.com", "anadolukariyerim.com", "calik.com", "zorlu.com.tr", "vestelkariyer.com",
        "vestel.com.tr", "migroskurumsal.com", "careerscci.com", "turkcell.com.tr",
        "vodafone.com", "sutas.com.tr", "tirsan.com.tr"
    )

    /** Parse a URL host without Android framework classes so policy tests run on the JVM too. */
    private fun normalizedHost(url: String): String? {
        val uri = runCatching { java.net.URI(url) }.getOrNull() ?: return null
        if (!uri.scheme.equals("https", ignoreCase = true) &&
            !uri.scheme.equals("http", ignoreCase = true)
        ) return null
        return uri.host?.lowercase(Locale.ROOT)?.removePrefix("www.")?.trimEnd('.')?.takeIf { it.isNotBlank() }
    }

    fun isTrustedUrl(url: String): Boolean {
        val uri = runCatching { java.net.URI(url) }.getOrNull() ?: return false
        if (!uri.scheme.equals("https", ignoreCase = true)) return false
        val host = normalizedHost(url) ?: return false
        return trustedDomains.any { host == it || host.endsWith(".$it") }
    }

    fun sourceName(url: String): String {
        val host = normalizedHost(url).orEmpty()
        if (host.isBlank()) return "Bilinmeyen kaynak"
        return sources.firstOrNull { source ->
            val sourceHost = normalizedHost(source.url).orEmpty()
            sourceHost.isNotBlank() &&
                (host == sourceHost || host.endsWith(".$sourceHost") || sourceHost.endsWith(".$host"))
        }?.name ?: host
    }

    fun isSecondary(url: String): Boolean {
        val host = normalizedHost(url).orEmpty()
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
            host == "turkiye.gov.tr" || host.endsWith(".turkiye.gov.tr") -> "Kamu / e-Devlet hizmetleri"
            listOf("ozal.edu.tr", "inonu.edu.tr", "firat.edu.tr", "adiyaman.edu.tr")
                .any { host == it || host.endsWith(".$it") } -> "Üniversite / kamu personel alımı"
            host.contains("osym.gov.tr") || host.contains("cbiko.gov.tr") || host.contains("kariyerkapisi.gov.tr") ||
                host.contains("sbb.gov.tr") || host.contains("ilan.gov.tr") ||
                host.contains("resmigazete.gov.tr") -> "Kamu / KPSS ve merkezi yerleştirme"
            host.endsWith(".bel.tr") || host.contains("malatya.bel.tr") ||
                host.contains("battalgazi.bel.tr") || host.contains("yesilyurt.bel.tr") -> "Belediye / kamu"
            host.endsWith(".gov.tr") || host.contains("csb.gov.tr") -> "Kamu / resmî kurum"
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


    /**
     * Separates public recruitment, lottery rounds, KPSS placements and private employers.
     * Generic job boards are deliberately not guessed as public or private.
     */
    fun recruitmentGroup(title: String, snippet: String, url: String): String {
        val text = (title + " " + snippet).lowercase(Locale("tr", "TR"))
            .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
            .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        val host = normalizedHost(url).orEmpty()
        val kuraTerms = listOf(
            "noter kurasi", "kura cekimi", "kura cekilisi", "kura tarihi",
            "kuraya katilacak", "kura sonucu", "kura sonuclari",
            "asil ve yedek", "asil aday", "yedek aday", "kurada asil"
        )
        val noKpssTerms = listOf(
            "kpss siz", "kpss sarti aranmaz", "kpss sarti aranmamaktadir",
            "kpss puani aranmayacaktir", "kpss puani istenmez",
            "kpss puani bulunma sarti aranmaz"
        )
        val publicLinkedDomains = listOf(
            "gov.tr", "bel.tr", "edu.tr", "iskur.gov.tr",
            "aselsan.com", "tusas.com", "roketsan.com.tr", "havelsan.com",
            "mke.gov.tr", "teias.gov.tr", "euas.gov.tr", "tcdd.gov.tr",
            "tcddtasimacilik.gov.tr", "dsi.gov.tr", "dhmi.gov.tr", "dmo.gov.tr",
            "etimaden.gov.tr", "botas.gov.tr", "turkseker.gov.tr",
            "kiyiemniyeti.gov.tr", "ptt.gov.tr", "ilbank.gov.tr", "tga.gov.tr",
            "tubitak.gov.tr"
        )
        val publicCategory = categoryFor(url).lowercase(Locale("tr", "TR"))
        val namedPublicEmployer = listOf(
            "orman genel mudurlugu", "karayollari genel mudurlugu",
            "devlet su isleri", "devlet hava meydanlari", "enerji ve tabii kaynaklar",
            "turkiye elektrik iletim", "elektrik uretim", "tcdd", "ptt",
            "milli egitim bakanligi", "saglik bakanligi", "adalet bakanligi",
            "icisleri bakanligi", "belediyesi", "universitesi", "jandarma",
            "sahil guvenlik", "il ozel idaresi"
        ).any(text::contains)
        val publicLinked = publicLinkedDomains.any { host == it || host.endsWith(".$it") } ||
            publicCategory.contains("kamu") || publicCategory.contains("belediye") ||
            publicCategory.contains("universite") || publicCategory.contains("iskur") ||
            publicCategory.contains("resmi ilan") || host == "turkiye.gov.tr" ||
            host.endsWith(".turkiye.gov.tr") || namedPublicEmployer

        if (publicLinked && noKpssTerms.any(text::contains)) {
            return "KPSS'siz kamu alımı"
        }
        if (kuraTerms.any(text::contains)) {
            return if (publicLinked) "Kura ile kamu alımı"
            else "Kura duyurusu · statü doğrulanmalı"
        }
        if (text.contains("kpss")) {
            return if (publicLinked) "KPSS ile kamu alımı"
            else "KPSS / şartı doğrulanmalı"
        }
        if (publicLinked) return "Kamu / kamu bağlantılı"
        if (isSecondary(url)) return "Platform / sektör doğrulanmalı"
        if (publicCategory.contains("özel sektör") ||
            publicCategory.contains("işveren") || publicCategory.contains("kariyer portalı")) {
            return "Özel sektör"
        }
        return "Diğer / doğrulanmalı"
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

        val hasCodeContext = listOf("nitelik kod", "ozel sart kod", "aranan nitelik kod").any(text::contains)
        val codes = if (hasCodeContext) extractQualificationCodes(text) else emptySet()
        // A code can describe a required diploma field; it cannot prove the candidate's own field.
        // Keep the requirement visible until the diploma's exact field/branch has been confirmed.
        val qualificationNote = codes.takeIf { it.isNotEmpty() }?.let(::assessQualificationCodes)

        if (hasExplicitAssociateRequirement(text)) {
            return "MEZUNİYET SONRASI · İlan ön lisans mezuniyeti istiyor. Henüz ön lisans mezunu değilsen bu şartı karşılamazsın."
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
            if (p94 || text.contains("ortaogretim") || text.contains("orta ogretim")) {
                val base = if (minScore == null) {
                    "P94 İLE TERCİH DEĞERLENDİRMESİ · İlan ortaöğretim KPSS/P94 ifadesi içeriyor. Kullanıcı puanı uygulamada doğrulanmadığı için puan sıralaması veya yerleşme ihtimali hesaplanmadı; nitelik kodları ve diğer şartlar ayrıca kontrol edilmeli."
                } else {
                    "P94 PUAN ŞARTI VAR · İlanda açıkça en az $minScore puan şartı yazıyor. Kullanıcı KPSS puanı uygulama profilinde doğrulanmadığı için bu şartın karşılanıp karşılanmadığı belirlenemiyor; kendi sonuç belgenle karşılaştır."
                }
                return if (qualificationNote == null) base else "$base $qualificationNote"
            }
            return "KPSS ŞARTI BELİRSİZ · Puan türü ve varsa alt sınır ilanın tam metninden doğrulanmalı; uygunluk kesinleştirilmedi."
        }

        if (qualificationNote != null) return qualificationNote
        if (listOf("elektrik", "pano", "kablolama", "bakim", "teknisyen", "teknik lise")
                .any(text::contains)) {
            return "TEKNİK ALAN İFADESİ VAR · İlan başlık/özetinde elektrik veya teknik görev geçiyor. Diploma alanı/dalı, istenen öğrenim düzeyi, deneyim, vardiya, ehliyet ve sertifika şartlarını tam metinden karşılaştır."
        }
        return "ŞARTLARI DOĞRULA · Başlık/özet tek başına yeterli değil; eğitim, şehir, deneyim ve son başvuru tarihi kaynak sayfasında kontrol edilmeli."
    }

    fun assessQualificationCodes(codes: Set<Int>): String {
        val notes = mutableListOf<String>()
        if (QUALIFICATION_CODE_ELECTRIC_ELECTRONICS in codes) {
            notes += "2023 kodu için ÖSYM kılavuzunda yazan ortaöğretim alan/dal şartını lise diplomanla karşılaştır. Uygulama senin alanını doğrulamadı."
        }
        if (QUALIFICATION_CODE_RAIL_SYSTEMS in codes) {
            notes += "2086 kodunun güncel kılavuzdaki tanımını ve istediği alan/dalı ayrıca kontrol et; uygulama bu kodu otomatik ret sebebi saymaz."
        }
        if (QUALIFICATION_CODE_ANY_HIGH_SCHOOL in codes) {
            notes += "2001 kodu herhangi bir ortaöğretim alanından mezuniyet şartını ifade eder; mezuniyet belgen ve ilandaki diğer özel şartlar yine kontrol edilmeli."
        }
        return if (notes.isEmpty()) {
            "NİTELİK KODLARI KONTROL EDİLMELİ · Kodların tamamını güncel ÖSYM kılavuzundaki açıklamalarla ve diploma bilgilerinle karşılaştır."
        } else {
            "NİTELİK KODU KONTROLÜ · " + notes.joinToString(" ")
        }
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
        if (eligibility.startsWith("PUAN") || eligibility.startsWith("MEZUNİYET")) {
            reasons += "Zorunlu şart uyuşmazlığı olabilir; tam kılavuzu kontrol et"
        } else if (eligibility.startsWith("NİTELİK KODU KONTROLÜ")) {
            reasons += "Diplomadaki alan/dal ile nitelik kodunu güncel kılavuzdan karşılaştır"
        }
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
            "site:resmigazete.gov.tr personel alımı atama ortaöğretim KPSS $year $c",
            "site:personel.ozal.edu.tr OR site:inonu.edu.tr/personeldb OR site:personeldb.firat.edu.tr OR site:pdb.adiyaman.edu.tr 4/B sözleşmeli personel alımı KPSS ortaöğretim $year",
            "site:icisleri.gov.tr OR site:personeltemin.msb.gov.tr OR site:jandarma.gov.tr OR site:sg.gov.tr personel temin memur işçi sınav kura sonuç $year",
            "site:csgb.gov.tr OR site:pgm.ktb.gov.tr OR site:ssb.gov.tr sözleşmeli personel alımı başvuru sınav yedek sonuç $year"
        )
        val publicNoKpss = listOf(
            "site:esube.iskur.gov.tr açık iş ilanı $r $c $year",
            "site:acikisharita.iskur.gov.tr $c elektrik teknisyen işçi açık iş $year",
            "site:yerelyonetimler.csb.gov.tr belediye işçi personel alımı $year $c",
            "site:malatya.bel.tr personel alımı iş ilanı $year",
            "site:battalgazi.bel.tr personel işçi alımı $year",
            "site:yesilyurt.bel.tr personel işçi alımı $year",
            "site:gov.tr personel alımı KPSS ortaöğretim lise P94 $year",
            "site:gov.tr sürekli işçi alımı İŞKUR kura noter kurası $year",
            "site:gov.tr sözleşmeli personel alım ilanı son başvuru $year",
            "site:gov.tr elektrik teknisyeni tekniker bakım onarım personel alımı $year",
            "site:gov.tr kura sonuçları asıl yedek sınav mülakat personel $year",
            "site:bel.tr belediye şirketi personel işçi alımı başvuru $year $c",
            "site:malatyatso.org.tr İŞKUR açık iş ilanları $year",
            "site:malorsa.org.tr personel alım ilanı Malatya OSB $year",
            "site:personel.ozal.edu.tr OR site:inonu.edu.tr/personeldb OR site:personeldb.firat.edu.tr OR site:pdb.adiyaman.edu.tr İŞKUR kura personel işçi alımı sonuçları $year",
            "site:icisleri.gov.tr OR site:jandarma.gov.tr OR site:sg.gov.tr sözleşmeli destek personeli işçi alımı başvuru kura sonuç $year",
            "site:personeltemin.msb.gov.tr uzman erbaş teknik sınıf personel temin başvuru sonuç $year"
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
        // Balanced quick search: include nationwide placements, nearby universities, IŞKUR,
        // kura notices, one employer portal and one job board without exceeding the fast-search cap.
        val generalQuick = listOf(
            "$r $c güncel iş ilanı personel alımı başvuru $year",
            "site:esube.iskur.gov.tr $r $c açık iş ilanı $year",
            "site:iskur.gov.tr $r $c işçi alımı başvuru $year"
        ) + publicKpss.take(2) + publicKpss.drop(7).take(2) +
            publicNoKpss.take(2) + kuraQueries.take(1) +
            privateSector.take(1) + platforms.take(1)

        val dailyPrivate = privateSector + platforms + listOf(
            "günlük işçi alımı üretim paketleme depo vardiyalı personel $year $c",
            "servis yemek sigorta sunulan fabrika üretim iş ilanı $year $c"
        )
        val municipal = publicNoKpss.filter { it.contains("bel.tr") || it.contains("yerelyonetimler") } +
            listOf(
                "site:bel.tr belediye şirketi işçi personel alımı $year $c",
                "site:bel.tr zabıta destek personeli temizlik şoför alımı $year $c",
                "site:gov.tr belediye iştiraki personel alımı başvuru $year $c"
            )
        return when (category) {
            1 -> publicKpss + kuraQueries + listOf(extra)
            2 -> publicNoKpss + kuraQueries + listOf(extra)
            3 -> privateSector.take(10) + platforms.take(5) + listOf(extra)
            4 -> privateSector.take(12) + kuraQueries + listOf(extra)
            5 -> postAssociate
            6 -> kuraQueries + publicKpss.take(3) + listOf(extra)
            7 -> dailyPrivate + listOf(extra)
            8 -> municipal + kuraQueries.take(3) + listOf(extra)
            else -> generalQuick
        }.distinct()
    }

    private fun normalizedUrl(url: String): String {
        return try {
            val uri = java.net.URI(url.trim())
            val host = (uri.host ?: "").lowercase(Locale.ROOT).removePrefix("www.")
            val path = (uri.rawPath ?: "/").ifBlank { "/" }.trimEnd('/').ifBlank { "/" }
            val retained = (uri.rawQuery ?: "").split("&")
                .filter { it.isNotBlank() }
                .filterNot {
                    val key = it.substringBefore("=").lowercase(Locale.ROOT)
                    key.startsWith("utm_") || key in setOf(
                        "fbclid", "gclid", "ref", "source", "mc_cid", "mc_eid", "_ga", "tracking_id"
                    )
                }
                .sorted()
            "https://$host$path" + if (retained.isEmpty()) "" else "?" + retained.joinToString("&")
        } catch (_: Exception) {
            url.substringBefore("#").trimEnd('/').lowercase(Locale.ROOT)
        }
    }

    private val duplicateNoise = setOf(
        "is", "ilan", "ilani", "ilanlari", "alimi", "alim", "personel",
        "basvuru", "duyuru", "acik", "pozisyon", "kariyer", "aday", "liste",
        "genel", "icin", "ve", "ile", "de", "da", "2026", "2025", "2027"
    )

    private fun duplicateTitleKey(title: String): String =
        CareerEligibility.normalizeForFingerprint(title)
            .split(" ")
            .filter { it.length > 2 && it !in duplicateNoise }
            .distinct()
            .sorted()
            .joinToString("|")

    private fun duplicateTokens(hit: Net.Hit): Set<String> =
        CareerEligibility.normalizeForFingerprint(hit.title + " " + hit.snippet)
            .split(" ")
            .filter { it.length > 2 && it !in duplicateNoise }
            .toSet()

    /** True only for the same canonical URL or a strong title-and-content match. */
    fun areDuplicates(first: Net.Hit, second: Net.Hit): Boolean {
        if (normalizedUrl(first.url) == normalizedUrl(second.url)) return true
        val firstTitle = duplicateTitleKey(first.title)
        val secondTitle = duplicateTitleKey(second.title)
        if (firstTitle.isBlank() || firstTitle != secondTitle ||
            firstTitle.split("|").size < 3) return false
        val a = duplicateTokens(first)
        val b = duplicateTokens(second)
        if (a.size < 4 || b.size < 4) return false
        val union = (a + b).size
        val overlap = a.intersect(b).size.toDouble() / union.toDouble()
        return overlap >= 0.70
    }

    private fun resultQuality(hit: Net.Hit): Int =
        (if (CareerSourceCatalog.isSecondary(hit.url)) 0 else 1000) +
            hit.snippet.length.coerceAtMost(900) +
            hit.title.length.coerceAtMost(180)

    /** Deduplicates provider copies while preferring the original employer/institution page. */
    fun deduplicateHits(hits: Collection<Net.Hit>): List<Net.Hit> {
        val unique = ArrayList<Net.Hit>()
        val byUrl = HashMap<String, Int>()
        val byTitle = HashMap<String, MutableList<Int>>()
        hits.forEach { candidate ->
            val urlKey = normalizedUrl(candidate.url)
            val titleKey = duplicateTitleKey(candidate.title)
            var duplicateIndex = byUrl[urlKey]
            if (duplicateIndex == null && titleKey.split("|").size >= 3) {
                duplicateIndex = byTitle[titleKey].orEmpty().firstOrNull { index ->
                    areDuplicates(unique[index], candidate)
                }
            }
            if (duplicateIndex == null) {
                val index = unique.size
                unique.add(candidate)
                byUrl[urlKey] = index
                if (titleKey.split("|").size >= 3) {
                    byTitle.getOrPut(titleKey) { ArrayList() }.add(index)
                }
            } else {
                val current = unique[duplicateIndex]
                if (resultQuality(candidate) > resultQuality(current)) {
                    unique[duplicateIndex] = candidate
                    byUrl[urlKey] = duplicateIndex
                }
            }
        }
        return unique
    }

    fun friendlyDiagnostics(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val value = raw.lowercase(Locale.ROOT)
        val issues = linkedSetOf<String>()
        if (value.contains("duckduckgo")) issues += "DuckDuckGo güvenlik kontrolü"
        if (value.contains("brave") && (value.contains("429") || value.contains("rate"))) {
            issues += "Brave geçici istek sınırı"
        } else if (value.contains("brave")) issues += "Brave geçici erişim sorunu"
        if (value.contains("bing") && (value.contains("okunamad") || value.contains("parse") || value.contains("sonuç bağlantıları"))) {
            issues += "Bing sonuç sayfası okunamadı"
        } else if (value.contains("bing")) issues += "Bing geçici erişim sorunu"
        if (issues.isEmpty()) issues += "bazı arama servisleri yanıt vermedi"
        return "Bazı arama servislerinde geçici sorun: " + issues.take(3).joinToString(", ") +
            ". Sonuçlar eksik olabilir; daha sonra yeniden tarama yap."
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

    /**
     * A search provider is useful only when it yields at least one trusted, job-like result.
     * A trusted institution homepage alone must not suppress fallback providers.
     */
    internal fun containsUsableJobResult(hits: Collection<Net.Hit>): Boolean =
        hits.any { hit ->
            CareerSourceCatalog.isTrustedUrl(hit.url) &&
                CareerSourceCatalog.isLikelyJobResult(hit.title, hit.snippet)
        }

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
                                if (results.isEmpty()) {
                                    errors += "$provider: sonuçlar okunamadı veya boş yanıt döndü"
                                } else {
                                    providerResponses++
                                    results.forEach { hit ->
                                        val key = fingerprint(hit.title, hit.url)
                                        if (key !in found) found[key] = hit
                                    }
                                }
                            } catch (error: Exception) {
                                val detail = error.message?.take(180).orEmpty().ifBlank { error.javaClass.simpleName }
                                errors += provider + ": " + detail
                            }
                        }

                        attempt("DuckDuckGo", { Net.webSearch(query, 8) })
                        var hasUsableJobResult = containsUsableJobResult(found.values)
                        if (!hasUsableJobResult) {
                            attempt("Bing", { Net.webSearchBing(query, 8) })
                            hasUsableJobResult = containsUsableJobResult(found.values)
                        }
                        if (!hasUsableJobResult) {
                            attempt("Google Haberler RSS", { Net.webSearchGoogleNews(query, 4) })
                            hasUsableJobResult = containsUsableJobResult(found.values)
                        }
                        if (!hasUsableJobResult) {
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
            val found = outcomes.flatMap { it.hits }.filter { hit ->
                CareerSourceCatalog.isTrustedUrl(hit.url) &&
                    CareerSourceCatalog.isLikelyJobResult(hit.title, hit.snippet)
            }
            val filtered = deduplicateHits(found).sortedByDescending {
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
            "site:resmigazete.gov.tr atama personel alımı $year",
            "site:personel.ozal.edu.tr OR site:inonu.edu.tr/personeldb OR site:personeldb.firat.edu.tr OR site:pdb.adiyaman.edu.tr 4/B personel alımı ön değerlendirme yedek sonuç kura $year"
        )
        val iskUrAndLocal = listOf(
            "site:esube.iskur.gov.tr $primaryCity açık iş elektrik teknisyen üretim depo işçi $year",
            "site:acikisharita.iskur.gov.tr $primaryCity açık iş ilanları elektrik üretim depo $year",
            "site:malatyatso.org.tr İŞKUR iş ilanları Malatya Yeşilyurt Battalgazi $year",
            "site:malorsa.org.tr Malatya Merkez 1 OSB personel alımı iş ilanı $year",
            "site:malatya.bel.tr OR site:battalgazi.bel.tr OR site:yesilyurt.bel.tr personel işçi alımı $year",
            "site:yerelyonetimler.csb.gov.tr belediye personel işçi alımı $year",
            "site:personel.ozal.edu.tr OR site:inonu.edu.tr/personeldb OR site:personeldb.firat.edu.tr OR site:pdb.adiyaman.edu.tr İŞKUR Gençlik Programı kura sonuçları personel alımı $year",
            "site:icisleri.gov.tr OR site:csgb.gov.tr OR site:pgm.ktb.gov.tr OR site:ssb.gov.tr personel alım ilanı başvuru sınav sonuç yedek $year",
            "site:personeltemin.msb.gov.tr OR site:jandarma.gov.tr OR site:sg.gov.tr personel temin kura sınav sonuç duyuru $year"
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
            "site:pladis.wd3.myworkdayjobs.com OR site:sutas.com.tr OR site:tirsan.com.tr Türkiye üretim teknisyen personel",
            "site:kiyiemniyeti.gov.tr OR site:dhmi.gov.tr OR site:dsi.gov.tr işçi personel alımı $year",
            "site:ptt.gov.tr OR site:ilbank.gov.tr OR site:dmo.gov.tr personel alımı kariyer $year",
            "site:afad.gov.tr OR site:sgk.gov.tr OR site:gib.gov.tr personel alımı $year",
            "site:tarimorman.gov.tr OR site:saglik.gov.tr OR site:adalet.gov.tr personel alımı $year"
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
        "SINAV SONUCU" to listOf(
            "sinav sonucu", "sinav sonuclari", "mulakat sonucu", "mulakat sonuclari",
            "sozlu sinav sonucu", "sozlu sinav sonuclari", "yazili sinav sonucu", "yazili sinav sonuclari"
        ),
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
        val isResultStage =
            stage?.startsWith("NOTER KURASI SONUCU") == true ||
                stage?.startsWith("SINAV SONUCU") == true ||
                stage?.startsWith("NİHAİ SONUÇ") == true
        val markerPattern = when {
            isResultStage -> Regex(
                """(?i)(?:kura(?:sı)?\s+sonuç(?:ları|lari|lar|u)?|sınav\s+sonuç(?:ları|lari|lar|u)?|sinav\s+sonuc(?:lari|lar|u)?|mülakat\s+sonuç(?:ları|lari|lar|u)?|mulakat\s+sonuc(?:lari|lar|u)?|nihai\s+sonuç(?:ları|lari|lar|u)?|yerleştirme\s+sonuç(?:ları|lari|lar|u)?|yerlestirme\s+sonuc(?:lari|lar|u)?|sonuçların\s+açıklanacağı|sonuclarin\s+aciklanacagi)"""
            )
            stage?.startsWith("BAŞVURU") == true ->
                Regex("""(?i)(?:son\s+başvuru(?:\s+tarihi)?|son\s+müracaat(?:\s+tarihi)?|başvuruların\s+son\s+günü|son\s+basvuru(?:\s+tarihi)?)""")
            stage?.startsWith("EVRAK") == true ->
                Regex("""(?i)(?:evrak\s+teslim|belge(?:lerin)?\s+teslim|evrak\s+kontrol|belge\s+kontrol)""")
            stage?.startsWith("KURA TARİHİ") == true ->
                Regex("""(?i)(?:kura\s+(?:çekimi|çekilişi|tarihi|cekimi|cekilisi)|kuraya\s+katılacak|kuraya\s+katilacak|noter\s+kurası|noter\s+kurasi|noter\s+huzurunda)""")
            stage?.startsWith("SINAV /") == true ->
                Regex("""(?i)(?:sınav(?:ın|a|i)?\s*(?:yeri|tarihi)?|sinav(?:in|a|i)?\s*(?:yeri|tarihi)?|mülakat(?:ın)?\s+tarihi|mulakat(?:in)?\s+tarihi)""")
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
        val publicationIntent = listOf(
            "duyurulacak", "ilan edilecek", "aciklanacak", "yayimlanacak", "yayinlanacak",
            "aciklanmasi beklen", "duyurulmasi beklen", "belli olacak"
        ).any(normalizedAfterDate::contains)
        // For result stages, schedule a reminder only when the date is explicitly announced
        // as a future publication date. For other stages, that wording is an announcement
        // date, not the event date (for example, a later notice about a kura date).
        if (isResultStage && !publicationIntent) return null
        if (!isResultStage && publicationIntent) return null
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
        workManager.cancelUniqueWork(baseName + "-result")
        workManager.cancelUniqueWork(baseName + "-dayof")

        val eventAt = event.eventAt ?: return
        enqueueProcessReminder(context, event, baseName + "-day", eventAt - TimeUnit.DAYS.toMillis(1))
        val isResultStage = event.stage.startsWith("NOTER KURASI SONUCU") ||
            event.stage.startsWith("SINAV SONUCU") ||
            event.stage.startsWith("NİHAİ SONUÇ")
        if (isResultStage) {
            // Alert at the published result date; when no hour was supplied this is a
            // morning check-in, not a claim that the institution announced 09:00.
            enqueueProcessReminder(context, event, baseName + "-result", eventAt)
        } else if (event.hasExplicitTime) {
            enqueueProcessReminder(context, event, baseName + "-hour", eventAt - TimeUnit.HOURS.toMillis(1))
        } else {
            enqueueProcessReminder(context, event, baseName + "-dayof", eventAt)
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
            "result" -> "Bugün sonuç duyurusu bekleniyor: " + time +
                if (hasExplicitTime) "" else " · Kurum saat belirtmedi; bu yalnızca günlük kontrol hatırlatmasıdır."
            "dayof" -> "Bugün takip günü: " + time + " · Duyuruda saat belirtilmemiş."
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
                event.stage != "BAŞVURU / YENİ ALIM" &&
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

