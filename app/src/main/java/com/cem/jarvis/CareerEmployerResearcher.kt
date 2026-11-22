package com.cem.jarvis

import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.Locale

data class CareerResearchLink(
    val title: String,
    val url: String,
    val snippet: String
)

data class CareerEmployerResearch(
    val sourceUrl: String,
    val finalUrl: String?,
    val sourceHost: String,
    val httpStatus: Int?,
    val pageTitle: String?,
    val pageDescription: String?,
    val structuredEmployer: String?,
    val employerName: String?,
    val sourceAssessment: String,
    val employerAssessment: String,
    val companySearchQuery: String,
    val companyResults: List<CareerResearchLink>,
    val registrySearchQuery: String?,
    val registryResults: List<CareerResearchLink>,
    val limitations: String,
    val visibleText: String? = null
) {
    fun toPromptText(): String = buildString {
        appendLine("KAYNAK SAYFASI KONTROLÜ")
        appendLine("İlan bağlantısı: " + sourceUrl)
        appendLine("Son yönlendirilen adres: " + (finalUrl ?: "alınamadı"))
        appendLine("Alan adı: " + sourceHost)
        appendLine("HTTP yanıtı: " + (httpStatus?.toString() ?: "alınamadı"))
        appendLine("Sayfa başlığı: " + (pageTitle ?: "çıkarılamadı"))
        appendLine("Sayfa açıklaması: " + (pageDescription ?: "çıkarılamadı"))
        appendLine("Sayfadan alınan görünen metin (kısmi; sayfanın tamamı olmayabilir):")
        appendLine(visibleText?.take(9000) ?: "Görünen metin alınamadı.")
        appendLine("Yapılandırılmış verideki işveren: " + (structuredEmployer ?: "bulunamadı"))
        appendLine("İşveren adı: " + (employerName ?: "doğrulanamadı"))
        appendLine("Kaynak değerlendirmesi: " + sourceAssessment)
        appendLine("İşveren değerlendirmesi: " + employerAssessment)
        appendLine()
        appendLine("RESMÎ ŞİRKET / TİCARET SİCİLİ BAĞLANTILARI")
        appendLine("MERSİS · Ticaret Bakanlığı: https://mersis.ticaret.gov.tr/")
        appendLine("Türkiye Ticaret Sicili Gazetesi · TOBB: https://www.ticaretsicil.gov.tr/")
        appendLine("Bu portallar uygulama tarafından kullanıcı girişiyle sorgulanmış değildir; resmî kayıttan son kontrol için bağlantı olarak verilmiştir.")
        appendLine()
        appendLine("ŞİRKET / KURUM WEB ARAŞTIRMASI · sorgu: " + companySearchQuery)
        if (companyResults.isEmpty()) {
            appendLine("Bu sorguda okunabilir arama sonucu alınamadı.")
        } else {
            companyResults.forEachIndexed { index, result ->
                appendLine((index + 1).toString() + ". " + result.title)
                appendLine("   URL: " + result.url)
                appendLine("   Özet: " + result.snippet)
            }
        }
        if (!registrySearchQuery.isNullOrBlank()) {
            appendLine()
            appendLine("TİCARET SİCİLİ / MERSİS KEŞFİ · sorgu: " + registrySearchQuery)
            if (registryResults.isEmpty()) {
                appendLine("Bu sorguda okunabilir sicil sonucu alınamadı.")
            } else {
                registryResults.forEachIndexed { index, result ->
                    appendLine((index + 1).toString() + ". " + result.title)
                    appendLine("   URL: " + result.url)
                    appendLine("   Özet: " + result.snippet)
                }
            }
        }
        appendLine()
        appendLine("SINIRLAR: Arama sonuçları tek başına şirketin hukuken kayıtlı olduğunu, ilanı yayımlayan tarafın işveren olduğunu veya başvurunun açık olduğunu kanıtlamaz. Resmî sicil/kurum kaydı doğrudan açılıp doğrulanmadıysa bunu doğrulanmış gibi sunma.")
        appendLine(limitations)
    }
}

object CareerEmployerResearcher {
    private data class PageInspection(
        val status: Int?,
        val finalUrl: String?,
        val title: String?,
        val description: String?,
        val structuredEmployer: String?,
        val visibleText: String? = null
    )

    private fun plainText(value: String): String = value
        .replace(Regex("(?is)<[^>]+>"), " ")
        .replace("&nbsp;", " ")
        .replace("&#160;", " ")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace(Regex("\\s+"), " ")
        .trim()

    private fun htmlText(value: String): String = plainText(value).take(700)

    private fun extractVisibleText(html: String): String? {
        val body = Regex("""(?is)<body\b[^>]*>(.*?)</body\s*>""")
            .find(html)?.groupValues?.getOrNull(1) ?: html
        val readable = body.replace(
            Regex("""(?is)<(script|style|noscript|svg|iframe|header|footer|nav)\b[^>]*>.*?</\1\s*>"""),
            " "
        )
        return plainText(readable).take(9000).takeIf { it.isNotBlank() }
    }

    private fun attribute(attributes: String, name: String): String? {
        val pattern = Regex(
            "(?is)\\b" + Regex.escape(name) + "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))"
        )
        val match = pattern.find(attributes) ?: return null
        return (1..3).firstNotNullOfOrNull { match.groups[it]?.value }
    }

    internal fun extractStructuredEmployer(html: String): String? {
        val scripts = Regex(
            """(?is)<script\b[^>]*type=["']application/ld\+json["'][^>]*>(.*?)</script\s*>"""
        ).findAll(html)
        for (script in scripts) {
            val block = script.groupValues[1]
            val name = Regex(
                """"hiringOrganization"\s*:\s*\{.{0,500}?"name"\s*:\s*"((?:\\.|[^"])*)""""
            ).find(block)?.groupValues?.getOrNull(1)
            if (!name.isNullOrBlank()) return name.replace("\\/", "/").take(200)
            val jobTitleEmployer = Regex(
                """"@type"\s*:\s*"Organization".{0,300}?"name"\s*:\s*"((?:\\.|[^"])*)""""
            ).find(block)?.groupValues?.getOrNull(1)
            if (!jobTitleEmployer.isNullOrBlank()) return jobTitleEmployer.take(200)
        }
        return null
    }

    private fun inspectHtml(html: String, status: Int?, finalUrl: String?): PageInspection {
        val title = Regex("""(?is)<title\b[^>]*>(.*?)</title\s*>""")
            .find(html)?.groupValues?.getOrNull(1)?.let(::htmlText)?.takeIf { it.isNotBlank() }

        var description: String? = null
        val metas = Regex("""(?is)<meta\b([^>]*)>""").findAll(html)
        for (meta in metas) {
            val attrs = meta.groupValues[1]
            val key = (attribute(attrs, "name") ?: attribute(attrs, "property")).orEmpty()
            if (key.equals("description", true) || key.equals("og:description", true)) {
                description = attribute(attrs, "content")?.let(::htmlText)?.takeIf { it.isNotBlank() }
                if (description != null) break
            }
        }
        return PageInspection(
            status, finalUrl, title, description,
            extractStructuredEmployer(html), extractVisibleText(html)
        )
    }

    private fun readPage(url: String): PageInspection {
        val uri = runCatching { URI(url) }.getOrNull()
            ?: return PageInspection(null, null, null, null, null)
        if (!uri.scheme.equals("https", true) || uri.host.isNullOrBlank()) {
            return PageInspection(null, null, null, null, null)
        }
        val connection = runCatching { URL(url).openConnection() as HttpURLConnection }.getOrNull()
            ?: return PageInspection(null, null, null, null, null)
        return try {
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 6500
            connection.readTimeout = 8000
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/124.0 Mobile Safari/537.36"
            )
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml")
            val status = connection.responseCode
            val finalUrl = connection.url.toString()
            val contentType = connection.contentType.orEmpty()
            if (status !in 200..299 || (!contentType.contains("html", true) && contentType.isNotBlank())) {
                PageInspection(status, finalUrl, null, null, null)
            } else {
                val bytes = connection.inputStream.use { input ->
                    val out = java.io.ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    var total = 0
                    while (total < 220_000) {
                        val count = input.read(buffer, 0, minOf(buffer.size, 220_000 - total))
                        if (count <= 0) break
                        out.write(buffer, 0, count)
                        total += count
                    }
                    out.toByteArray()
                }
                inspectHtml(String(bytes, Charsets.UTF_8), status, finalUrl)
            }
        } catch (_: Exception) {
            PageInspection(null, null, null, null, null)
        } finally {
            connection.disconnect()
        }
    }

    private fun runSearch(query: String): List<CareerResearchLink> {
        if (query.isBlank()) return emptyList()
        val hits = runCatching { Net.webSearch(query, 4) }.getOrElse {
            runCatching { Net.webSearchBing(query, 4) }.getOrDefault(emptyList())
        }
        return hits.asSequence()
            .filter { it.url.startsWith("https://") }
            .distinctBy { it.url.substringBefore("#").lowercase(Locale.ROOT) }
            .take(4)
            .map { CareerResearchLink(it.title.take(220), it.url, it.snippet.take(700)) }
            .toList()
    }

    fun research(hit: Net.Hit): CareerEmployerResearch {
        val uri = runCatching { URI(hit.url) }.getOrNull()
        val host = uri?.host?.lowercase(Locale.ROOT)?.removePrefix("www.").orEmpty()
        val page = readPage(hit.url)
        val mappedSource = CareerSourceCatalog.sourceName(hit.url)
        val genericSourceNames = setOf(
            host, "Bilinmeyen kaynak", "İlan platformu", "İlan platformu / ikincil keşif",
            "Özel sektör / büyük şirket", "Kamu / resmî kurum"
        )
        val structured = page.structuredEmployer?.takeIf { it.isNotBlank() }
        val mappedEmployer = mappedSource.takeIf {
            it.isNotBlank() && it !in genericSourceNames &&
                !CareerSourceCatalog.isSecondary(hit.url)
        }
        val employer = structured ?: mappedEmployer
        val sourceAssessment = when {
            CareerSourceCatalog.isSecondary(hit.url) ->
                "İkincil iş/ilan platformu alan adı. Bu sayfa işverenin kendi sitesi sayılmaz."
            CareerSourceCatalog.isTrustedUrl(hit.url) ->
                "Alan adı Aries'in tanımlı kurumsal/resmî kaynak listesinde bulunuyor; bu tek başına ticaret sicili veya açık ilan kanıtı değildir."
            else ->
                "Alan adı Aries'in tanımlı kaynak listesinde bulunamadı; bağımsız doğrulama gerekiyor."
        }
        val employerAssessment = when {
            structured != null ->
                "Sayfanın yapılandırılmış verisinde işveren adı '$structured' olarak bulundu. Tüzel kişilik/sicil kaydı ayrıca doğrulanmış değildir."
            mappedEmployer != null ->
                "Alan adı bilinen bir kurum/işveren kaynağıyla eşleşiyor: $mappedEmployer. Bu, ilgili sayfanın bu işveren adına açık pozisyon yayımladığını tek başına kanıtlamaz."
            else ->
                "İşveren adı kaynak sayfasından güvenilir biçimde çıkarılamadı. Platform adı işveren adı gibi kullanılmadı."
        }

        val companyQuery = if (employer != null) {
            "\"" + employer + "\" resmi web sitesi faaliyet alanı Türkiye şirket"
        } else {
            "\"" + hit.title.take(140) + "\" \"" + host + "\" ilan işveren şirket resmi kaynak"
        }
        val companyResults = runSearch(companyQuery)
        val registryQuery = employer?.let { "\"" + it + "\" ticaret sicil gazetesi MERSİS şirket kaydı" }
        val registryResults = registryQuery?.let(::runSearch).orEmpty()
        val limitations = if (page.status == null) {
            "İlan sayfası otomatik olarak okunamadı; oturum, bot koruması veya bağlantı engeli olabilir. Sayfaya kullanıcı tarafından manuel bakılmalı."
        } else if (page.status !in 200..299) {
            "İlan sayfası HTTP " + page.status + " döndürdü. Başvuru koşulları ve güncellik doğrulanamadı."
        } else {
            "Sayfa HTTP " + page.status + " yanıtıyla açıldı. Sayfanın açılması ilan içeriğinin gerçek veya güncel olduğunu tek başına doğrulamaz."
        }
        return CareerEmployerResearch(
            sourceUrl = hit.url,
            finalUrl = page.finalUrl,
            sourceHost = host.ifBlank { "bilinmiyor" },
            httpStatus = page.status,
            pageTitle = page.title,
            pageDescription = page.description,
            structuredEmployer = structured,
            employerName = employer,
            sourceAssessment = sourceAssessment,
            employerAssessment = employerAssessment,
            companySearchQuery = companyQuery,
            companyResults = companyResults,
            registrySearchQuery = registryQuery,
            registryResults = registryResults,
            limitations = limitations
        )
    }
}
