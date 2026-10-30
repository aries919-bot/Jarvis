package com.cem.jarvis

import android.util.Xml
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ApiError(val code: Int, msg: String) : RuntimeException(msg) {
    val temporary: Boolean
        get() = code == 429 || code == 500 || code == 502 || code == 503 || code == 504
    val badKey: Boolean
        get() = code == 401 || code == 403
}

object Net {
    fun get(url: String): Pair<Int, String> {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = "GET"
        c.connectTimeout = 8000
        c.readTimeout = 15000
        val code = c.responseCode
        val stream = if (code in 200..299) c.inputStream else c.errorStream
        val body = stream?.bufferedReader()?.use { it.readText() } ?: ""
        return Pair(code, body)
    }

    fun post(url: String, headers: Map<String, String>, body: JSONObject): Pair<Int, String> {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = "POST"
        c.connectTimeout = 8000
        c.readTimeout = 25000
        c.doOutput = true
        c.setRequestProperty("Content-Type", "application/json")
        for ((k, v) in headers) c.setRequestProperty(k, v)
        c.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val stream = if (c.responseCode in 200..299) c.inputStream else c.errorStream
        val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
        return Pair(c.responseCode, text)
    }

    fun newsTitles(topic: String): List<String> {
        val url = "https://news.google.com/rss/search?q=" + URLEncoder.encode(topic, "UTF-8") + "&hl=tr&gl=TR&ceid=TR:tr"
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 8000
        c.readTimeout = 15000
        if (c.responseCode !in 200..299) throw IOException("Haber hatası: " + c.responseCode)
        val titles = ArrayList<String>()
        c.inputStream.use { input ->
            val parser = Xml.newPullParser()
            parser.setInput(input, null)
            var inItem = false
            var inTitle = false
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT && titles.size < 8) {
                if (event == XmlPullParser.START_TAG) {
                    if (parser.name == "item") inItem = true
                    if (inItem && parser.name == "title") inTitle = true
                } else if (event == XmlPullParser.TEXT) {
                    if (inTitle) titles.add((parser.text ?: "").trim())
                } else if (event == XmlPullParser.END_TAG) {
                    if (parser.name == "title") inTitle = false
                    if (parser.name == "item") inItem = false
                }
                event = parser.next()
            }
        }
        return titles.filter { it.isNotEmpty() }
    }


    class Hit(val title: String, val url: String, val snippet: String)

    private fun stripTags(s: String): String =
        s.replace(Regex("<[^>]*>"), "")
            .replace("&amp;", "&").replace("&quot;", "\"").replace("&#x27;", "'")
            .replace("&#39;", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ").trim()

    // Anahtarsız web araması: DuckDuckGo'nun yazı tabanlı sayfasından sonuçları çeker.
    fun webSearch(query: String): List<Hit> {
        val url = "https://html.duckduckgo.com/html/?kl=tr-tr&q=" + URLEncoder.encode(query, "UTF-8")
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = "GET"
        c.connectTimeout = 8000
        c.readTimeout = 10000
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36")
        c.setRequestProperty("Accept-Language", "tr-TR,tr;q=0.9")
        val code = c.responseCode
        if (code !in 200..299) throw IOException("Arama hatası: $code")
        val html = c.inputStream.bufferedReader().use { it.readText() }
        val links = Regex("<a[^>]*class=\"result__a\"[^>]*href=\"([^\"]+)\"[^>]*>(.*?)</a>", RegexOption.DOT_MATCHES_ALL).findAll(html).toList()
        val snips = Regex("<a[^>]*class=\"result__snippet\"[^>]*>(.*?)</a>", RegexOption.DOT_MATCHES_ALL).findAll(html).toList()
        val out = ArrayList<Hit>()
        for (i in links.indices) {
            if (out.size >= 5) break
            var href = links[i].groupValues[1].replace("&amp;", "&")
            val m = Regex("[?&]uddg=([^&]+)").find(href)
            if (m != null) href = java.net.URLDecoder.decode(m.groupValues[1], "UTF-8")
            if (href.startsWith("//")) href = "https:$href"
            val title = stripTags(links[i].groupValues[2])
            val snip = if (i < snips.size) stripTags(snips[i].groupValues[1]) else ""
            if (title.isNotEmpty() && href.startsWith("http")) out.add(Hit(title, href, snip))
        }
        return out
    }

    fun quote(sym: String, token: String): String {
        val url = "https://finnhub.io/api/v1/quote?symbol=" + URLEncoder.encode(sym, "UTF-8") +
            "&token=" + URLEncoder.encode(token, "UTF-8")
        val (code, txt) = get(url)
        if (code == 401 || code == 403) throw IOException("Finnhub anahtarı geçersiz")
        if (code != 200) throw IOException("borsa hatası $code")
        val o = JSONObject(txt)
        val c = o.optDouble("c", 0.0)
        val dp = o.optDouble("dp", 0.0)
        if (c == 0.0) return "$sym: veri yok (bu hisse ücretsiz planda olmayabilir)"
        return "%s: %.2f (%+.2f%%)".format(java.util.Locale.US, sym, c, dp)
    }
}

object Ai {
    val ALL = listOf("groq", "gemini", "mistral")

    const val GEMINI_MODEL = "gemini-flash-latest"
    const val GEMINI_FALLBACK = "gemini-3.1-flash-lite"
    const val GROQ_DEFAULT = "openai/gpt-oss-120b"
    const val MISTRAL_DEFAULT = "mistral-small-latest"

    const val GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
    const val MISTRAL_URL = "https://api.mistral.ai/v1/chat/completions"

    class Source(val title: String, val url: String)
    class Answer(val text: String, val sources: List<Source>)

    fun defaultModel(who: String): String = when (who) {
        "groq" -> GROQ_DEFAULT
        "mistral" -> MISTRAL_DEFAULT
        "bulut" -> "bulut"
        else -> GEMINI_MODEL
    }

    fun displayName(who: String): String = when (who) {
        "groq" -> "Groq"
        "mistral" -> "Mistral"
        "bulut" -> "Aries Bulut"
        else -> "Gemini"
    }

    private fun errText(name: String, code: Int): String = when (code) {
        400 -> "$name isteği reddetti (model adı veya istek biçimi)."
        401, 403 -> "$name anahtarı geçersiz veya yetkisiz."
        404 -> "$name modeli bulunamadı."
        429 -> "$name kullanım sınırına ulaştı."
        500, 502, 503, 504 -> "$name şu an yoğun."
        else -> "$name hatası: $code"
    }

    fun <T> withRetry(block: () -> T): T {
        var last: Exception = IOException("Bağlantı hatası")
        for (attempt in 0..1) {
            try {
                return block()
            } catch (e: ApiError) {
                if (!e.temporary) throw e
                last = e
            } catch (e: IOException) {
                last = e
            }
            if (attempt < 1) Thread.sleep(1000L)
        }
        throw last
    }

    fun chat(
        order: List<String>,
        keys: Map<String, String>,
        models: Map<String, String>,
        sys: String,
        hist: List<Pair<String, String>>
    ): String {
        var last: Exception = RuntimeException("Yapay zekâya ulaşılamadı.")
        for (who in order) {
            val key = keys[who].orEmpty()
            if (key.isBlank()) continue
            val model = models[who].orEmpty().ifBlank { defaultModel(who) }
            try {
                return callProvider(who, key, model, sys, hist)
            } catch (e: Exception) {
                last = e
            }
        }
        throw last
    }

    fun oyVer(token: String, v: Int, a: String) {
        try { Net.post("https://jarvis-cloud.aydincem919.workers.dev/oy_sohbet", mapOf("Authorization" to "Bearer $token"), JSONObject().put("v", v).put("a", a)) } catch (e: Exception) {}
    }

    private const val BULUT_URL = "https://jarvis-cloud.aydincem919.workers.dev/ai"

    private fun bulut(token: String, sys: String, hist: List<Pair<String, String>>): String {
        val msgs = JSONArray()
        for ((r, t) in hist) msgs.put(JSONObject().put("role", r).put("content", t))
        val body = JSONObject().put("system", sys).put("messages", msgs)
        val (code, txt) = Net.post(BULUT_URL, mapOf("Authorization" to "Bearer $token"), body)
        if (code == 200) return JSONObject(txt).getString("answer")
        throw ApiError(code, errText("Aries Bulut", code))
    }

    private fun maskele(s: String): String = s
        .replace(Regex("[\\w.+-]+@[\\w-]+\\.[\\w.]+"), "")
        .replace(Regex("TR\\d{2}[\\d ]{20,30}", RegexOption.IGNORE_CASE), "")
        .replace(Regex("\\+?\\d[\\d ()-]{8,}\\d"), "")
        .replace(Regex("\\s+"), " ").trim()

    private const val HIZLI_URL = "https://jarvis-cloud.aydincem919.workers.dev/hizli"

    fun hizli(token: String, q: String): Answer {
        val body = JSONObject().put("q", q)
        try {
            val hits = Net.webSearch(maskele(q))
            if (hits.size >= 3) body.put("links", JSONArray(hits.map { it.url }))
        } catch (e: Exception) { }
        val (code, txt) = Net.post(HIZLI_URL, mapOf("Authorization" to "Bearer $token"), body)
        if (code != 200) throw ApiError(code, errText("Aries Bulut", code))
        val j = JSONObject(txt)
        val arr = j.optJSONArray("sources") ?: JSONArray()
        val src = ArrayList<Source>()
        for (i in 0 until arr.length()) {
            val u = arr.getString(i)
            src.add(Source(u.removePrefix("https://").removePrefix("http://").substringBefore("/"), u))
        }
        return Answer(j.getString("answer"), src)
    }

    private fun callProvider(who: String, key: String, model: String, sys: String, hist: List<Pair<String, String>>, json: Boolean = true): String {
        if (who == "bulut") return bulut(key, sys, hist)
        return if (who == "gemini") {
            geminiAnswer(key, sys, hist, json, false).text
        } else {
            val url = when (who) {
                "groq" -> GROQ_URL
                else -> MISTRAL_URL
            }
            withRetry { openAi(displayName(who), url, key, model, sys, hist) }
        }
    }

    private fun openAi(name: String, url: String, key: String, model: String, sys: String, hist: List<Pair<String, String>>): String {
        val msgs = JSONArray().put(JSONObject().put("role", "system").put("content", sys))
        for ((r, t) in hist) msgs.put(JSONObject().put("role", r).put("content", t))
        val body = JSONObject()
            .put("model", model)
            .put("messages", msgs)
            .put("temperature", 0.3)
            .put("max_tokens", 600)
        val (code, txt) = Net.post(url, mapOf("Authorization" to "Bearer $key"), body)
        if (code != 200) throw ApiError(code, errText(name, code))
        return JSONObject(txt).getJSONArray("choices").getJSONObject(0)
            .getJSONObject("message").getString("content")
    }

    private fun geminiBody(sys: String, hist: List<Pair<String, String>>, json: Boolean, search: Boolean): JSONObject {
        val contents = JSONArray()
        for ((r, t) in hist) {
            contents.put(
                JSONObject()
                    .put("role", if (r == "user") "user" else "model")
                    .put("parts", JSONArray().put(JSONObject().put("text", t)))
            )
        }
        val gen = JSONObject().put("temperature", 0.3)
        if (json) gen.put("responseMimeType", "application/json")
        val body = JSONObject()
            .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", sys))))
            .put("contents", contents)
            .put("generationConfig", gen)
        if (search) body.put("tools", JSONArray().put(JSONObject().put("google_search", JSONObject())))
        return body
    }

    private fun gemini(key: String, model: String, sys: String, hist: List<Pair<String, String>>, json: Boolean, search: Boolean): JSONObject {
        val (code, txt) = Net.post(
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent",
            mapOf("x-goog-api-key" to key),
            geminiBody(sys, hist, json, search)
        )
        if (code != 200) throw ApiError(code, errText("Gemini", code))
        return JSONObject(txt)
    }

    private fun geminiAnswer(key: String, sys: String, hist: List<Pair<String, String>>, json: Boolean, search: Boolean): Answer {
        val o = try {
            withRetry { gemini(key, GEMINI_MODEL, sys, hist, json, search) }
        } catch (e: ApiError) {
            if (!e.temporary) throw e
            withRetry { gemini(key, GEMINI_FALLBACK, sys, hist, json, search) }
        }
        val cand = o.getJSONArray("candidates").getJSONObject(0)
        val parts = cand.getJSONObject("content").getJSONArray("parts")
        val sb = StringBuilder()
        for (i in 0 until parts.length()) sb.append(parts.getJSONObject(i).optString("text"))
        val sources = ArrayList<Source>()
        val chunks = cand.optJSONObject("groundingMetadata")?.optJSONArray("groundingChunks")
        if (chunks != null) {
            for (i in 0 until chunks.length()) {
                val web = chunks.getJSONObject(i).optJSONObject("web") ?: continue
                sources.add(Source(web.optString("title", "Kaynak"), web.optString("uri")))
            }
        }
        return Answer(sb.toString(), sources)
    }

    // Gemini'nin Google Arama bağlantısı (grounding). Kaynaklar groundingMetadata'dan gelir.
    // Gemini anahtarı yoksa, kotası dolduysa ya da hata verirse çağıran taraf research() ile devam eder.
    fun googleSearch(key: String, query: String): Answer {
        val sys = "Türkçe yanıt ver. Güncel bilgi için Google aramasını kullan. En fazla 4 cümle yaz. Yıldız ve başlık kullanma; cevap sesli okunacak. Bilmiyorsan açıkça söyle."
        val a = geminiAnswer(key, sys, listOf(Pair("user", query)), false, true)
        return Answer(a.text.trim(), a.sources)
    }

    // Önce sayfaları biz çekeriz, sonra hızlı modele "bunlara bakarak yanıtla" deriz.
    fun research(
        order: List<String>,
        keys: Map<String, String>,
        models: Map<String, String>,
        query: String
    ): Answer {
        var hits: List<Net.Hit> = emptyList()
        try { hits = Net.webSearch(query) } catch (e: Exception) { }
        val context = StringBuilder()
        val sources = ArrayList<Source>()
        if (hits.isNotEmpty()) {
            for ((i, h) in hits.withIndex()) {
                context.append("[${i + 1}] ${h.title}: ${h.snippet}\n")
                sources.add(Source(h.title, h.url))
            }
        } else {
            try {
                val news = Net.newsTitles(query)
                for ((i, t) in news.withIndex()) context.append("[${i + 1}] $t\n")
            } catch (e: Exception) { }
        }
        val sys = if (context.isEmpty())
            "Türkçe yanıt ver. En fazla 4 cümle yaz. Yıldız ve başlık kullanma; cevap sesli okunacak. Bilmiyorsan açıkça söyle."
        else
            "Türkçe yanıt ver. Aşağıdaki arama sonuçlarına dayan, uydurma. En fazla 4 cümle yaz. Yıldız ve başlık kullanma; cevap sesli okunacak. Sonuçlar yetersizse bunu söyle.\n\nARAMA SONUÇLARI:\n$context"
        var last: Exception = RuntimeException("Yapay zekâya ulaşılamadı.")
        for (who in order) {
            val key = keys[who].orEmpty()
            if (key.isBlank()) continue
            val model = models[who].orEmpty().ifBlank { defaultModel(who) }
            try {
                val text = callProvider(who, key, model, sys, listOf(Pair("user", query)), false)
                return Answer(text.trim(), sources)
            } catch (e: Exception) {
                last = e
            }
        }
        throw last
    }

    fun translate(
        order: List<String>,
        keys: Map<String, String>,
        models: Map<String, String>,
        lang: String,
        text: String
    ): String {
        var last: Exception = RuntimeException("Yapay zekâya ulaşılamadı.")
        for (who in order) {
            val key = keys[who].orEmpty()
            if (key.isBlank()) continue
            val model = models[who].orEmpty().ifBlank { defaultModel(who) }
            try {
                return callProvider(who, key, model, "Yalnızca çeviriyi yaz.", listOf(Pair("user", "$lang diline çevir: $text")), false).trim()
            } catch (e: Exception) {
                last = e
            }
        }
        throw last
    }

    fun ping(who: String, key: String, model: String): String {
        val hist = listOf(Pair("user", "Sadece 'tamam' yaz."))
        return callProvider(who, key, model.ifBlank { defaultModel(who) }, "Kısa cevap ver.", hist)
    }

    fun parseReply(raw: String): Pair<String, String> {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start < 0 || end <= start) return Pair("", raw.trim())
        return try {
            val json = JSONObject(raw.substring(start, end + 1))
            Pair(json.optString("command").trim(), json.optString("reply").trim())
        } catch (e: Exception) {
            Pair("", raw.trim())
        }
    }

    fun friendly(e: Exception): String = when {
        e is ApiError && e.badKey -> e.message ?: "Anahtar geçersiz."
        e is ApiError -> e.message ?: "Yapay zekâ hatası."
        e is IOException -> "İnternete ulaşamadım."
        else -> e.message ?: "Bir hata oldu."
    }
}