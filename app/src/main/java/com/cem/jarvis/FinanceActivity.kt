package com.cem.jarvis

import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class FinanceActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("j", MODE_PRIVATE) }
    private lateinit var marketStatus: TextView
    private lateinit var marketRows: LinearLayout
    private lateinit var symbolInput: EditText
    private lateinit var apiKeyInput: EditText
    private lateinit var lookupRows: LinearLayout
    private lateinit var stockRows: LinearLayout
    private lateinit var alertRows: LinearLayout
    private lateinit var apiKeyStatus: TextView
    private var latestMarket: TurkishMarketSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor(Pal.BG)
        window.navigationBarColor = Color.parseColor(Pal.BG)
        window.decorView.systemUiVisibility = 0
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(buildScreen())
        refreshAll()
    }

    private fun buildScreen(): ScrollView {
        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor(Pal.BG))
            clipToPadding = false
        }
        val content = Ui.column(this).apply { setPadding(dp(16), dp(18), dp(16), dp(28)) }
        val header = Ui.row(this)
        header.addView(Ui.button(this, "‹") { finish() }, LinearLayout.LayoutParams(dp(48), dp(48)))
        val heading = Ui.column(this).apply {
            addView(Ui.text(this@FinanceActivity, "Piyasa Merkezi", 22f, Pal.TEXT, true))
            addView(Ui.text(this@FinanceActivity, "Altın · Döviz · Borsa İstanbul", 12f, Pal.MUTED))
        }
        header.addView(heading, LinearLayout.LayoutParams(0, -2, 1f).apply { leftMargin = dp(10) })
        header.addView(Ui.button(this, "Yenile") { refreshAll() })
        content.addView(header)
        content.addView(Ui.text(
            this,
            "Fiyatları tek yerde izle; kaynağı ve veri zamanını her zaman kontrol et.",
            13f, Pal.MUTED
        ).apply { setPadding(dp(4), dp(10), dp(4), dp(8)) })

        val disclaimer = panel()
        disclaimer.addView(Ui.text(this, "BİLGİLENDİRME", 11f, Pal.ACCENT, true))
        disclaimer.addView(Ui.text(
            this,
            "Bu ekran yatırım tavsiyesi vermez. Altın/döviz verisi üçüncü taraf açık akıştan, hisse fiyatı Finnhub hesabından alınır. Veriler gecikmeli, eksik veya eski olabilir; işlem öncesinde banka, aracı kurum veya Borsa İstanbul ekranından doğrula.",
            12f, Pal.MUTED
        ))
        content.addView(disclaimer, margins(4, 12))

        content.addView(Ui.section(this, "Altın ve döviz"))
        val marketCard = panel()
        marketStatus = Ui.text(this, "Piyasa verisi alınıyor…", 12f, Pal.MUTED)
        marketCard.addView(marketStatus)
        marketRows = Ui.column(this).apply { setPadding(0, dp(8), 0, 0) }
        marketCard.addView(marketRows)
        marketCard.addView(Ui.button(this, "Altın ve dövizi yenile") { refreshMarket() }, margins(8))
        content.addView(marketCard, margins(bottom = 12))

        content.addView(Ui.section(this, "Hisse izleme listesi"))
        val stockCard = panel()
        stockCard.addView(Ui.text(this, "Finnhub bağlantısı", 15f, Pal.TEXT, true))
        stockCard.addView(Ui.text(
            this,
            "Finnhub API anahtarını buraya kaydet. Aries anahtarı Android Keystore ile şifreler; anahtar kaynak koduna veya sohbete yazılmaz. Uygulama yedeği başka cihaza taşınırsa yeniden girmen gerekebilir.",
            12f, Pal.MUTED
        ), margins(4, 8))
        apiKeyInput = Ui.input(this, "Finnhub API anahtarı").apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSingleLine(true)
        }
        stockCard.addView(apiKeyInput, margins(bottom = 6))
        apiKeyStatus = Ui.text(
            this,
            if (!FinanceKeyStore.hasKey(this)) "API anahtarı kaydedilmedi."
            else "Finnhub anahtarı Android Keystore ile şifreli.",
            11f, Pal.MUTED
        )
        stockCard.addView(apiKeyStatus)
        stockCard.addView(Ui.button(this, "Android Keystore ile şifrele ve kaydet") { saveFinnhubKey() }, margins(6, 14))

        stockCard.addView(Ui.text(this, "Hisse ara", 14f, Pal.TEXT, true))
        symbolInput = Ui.input(this, "Hisse kodu veya şirket adı").apply { setSingleLine(true) }
        stockCard.addView(symbolInput, margins(6, 6))
        stockCard.addView(Ui.button(this, "Finnhub'da sembol ara") { searchSymbols() })
        lookupRows = Ui.column(this).apply { setPadding(0, dp(6), 0, 0) }
        stockCard.addView(lookupRows)

        val divider = android.view.View(this).apply { setBackgroundColor(Color.parseColor(Pal.LINE)) }
        stockCard.addView(divider, LinearLayout.LayoutParams(-1, dp(1)).apply {
            topMargin = dp(12); bottomMargin = dp(12)
        })
        stockCard.addView(Ui.text(this, "Takip ettiklerin", 14f, Pal.TEXT, true))
        stockCard.addView(Ui.text(
            this,
            "Arama sonucunda Finnhub'ın verdiği tam sembolü ekle. BIST kapsamı ve gecikme Finnhub'ın hesap/veri desteğine bağlıdır.",
            12f, Pal.MUTED
        ), margins(4, 6))
        stockRows = Ui.column(this)
        stockCard.addView(stockRows)
        stockCard.addView(Ui.button(this, "Hisse fiyatlarını güncelle") { refreshStocks() }, margins(8))
        content.addView(stockCard, margins(bottom = 12))

        content.addView(Ui.section(this, "Fiyat alarmları"))
        val alertCard = panel()
        alertCard.addView(Ui.text(
            this,
            "Kaydettiğin eşikler ağ bağlantısı olduğunda yaklaşık saatte bir kontrol edilir. Kaynak verisi eskiyse alarm üretilmez.",
            12f, Pal.MUTED
        ))
        alertRows = Ui.column(this).apply { setPadding(0, dp(8), 0, 0) }
        alertCard.addView(alertRows)
        content.addView(alertCard, margins(bottom = 12))
        content.addView(Ui.text(
            this,
            "Kaynaklar: Altın/döviz — currency-api-tr (TCMB, Truncgil ve diğer listelenen kaynaklar); hisse — Finnhub. Fiyatlar kesin işlem fiyatı değildir.",
            11f, Pal.MUTED
        ).apply { setPadding(dp(4), dp(4), dp(4), dp(8)) })
        scroll.addView(content, android.widget.FrameLayout.LayoutParams(-1, -2))
        return scroll
    }

    private fun refreshAll() {
        if (::marketRows.isInitialized) refreshMarket()
        if (::stockRows.isInitialized) refreshStocks()
        if (::alertRows.isInitialized) renderAlerts()
    }

    private fun refreshMarket() {
        if (!::marketStatus.isInitialized) return
        marketStatus.text = "Altın ve döviz verisi alınıyor…"
        Thread {
            val result = runCatching { FinanceData.fetchTurkishMarket() }
            runOnUiThread {
                result.onSuccess {
                    latestMarket = it
                    renderMarket(it)
                }.onFailure { error ->
                    val cached = latestMarket
                    marketStatus.text = if (cached == null) {
                        "Veri alınamadı: " + safeError(error)
                    } else {
                        "Yenileme başarısız. Son başarılı veri gösteriliyor; güncelliğini kontrol et."
                    }
                    if (cached != null) renderMarket(cached, " · SON BAŞARILI VERİ")
                }
            }
        }.start()
    }

    private fun renderMarket(snapshot: TurkishMarketSnapshot, suffix: String = "") {
        marketRows.removeAllViews()
        val stale = snapshot.stale
        val time = formatIso(snapshot.updatedAt).ifBlank { snapshot.date.ifBlank { "Tarih yok" } }
        marketStatus.text = (if (stale) "ESKİ / GÜNCELLİĞİ DOĞRULANAMADI · Veri zamanı: " else "Veri zamanı: ") + time +
            suffix + " · Kaynak: " + snapshot.sources.joinToString(", ")
        marketStatus.setTextColor(Color.parseColor(if (stale) Pal.AMBER else Pal.MUTED))
        val groups = listOf(
            "ALTIN" to snapshot.prices.filter { it.id.startsWith("xau_") },
            "DÖVİZ" to snapshot.prices.filterNot { it.id.startsWith("xau_") }
        )
        for ((title, prices) in groups) {
            if (prices.isEmpty()) continue
            marketRows.addView(Ui.section(this, title))
            for (price in prices) {
                val line = Ui.row(this).apply {
                    setPadding(dp(10), dp(8), dp(4), dp(8))
                    background = Ui.rounded(Pal.CARD2, Pal.LINE, dp(12).toFloat())
                }
                val labels = Ui.column(this)
                labels.addView(Ui.text(this, price.title, 13f, Pal.TEXT, true))
                labels.addView(Ui.text(
                    this,
                    sourceLabel(price.source) + " · " + if (price.stale || stale) "eski veri" else "güncellik kaynağa bağlı",
                    10f, if (price.stale || stale) Pal.AMBER else Pal.MUTED
                ))
                line.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
                val values = Ui.column(this).apply { gravity = android.view.Gravity.END }
                values.addView(Ui.text(this, "Alış  " + formatTry(price.bid) + " ₺", 12f, Pal.GREEN, true))
                values.addView(Ui.text(this, "Satış " + formatTry(price.ask) + " ₺", 12f, Pal.TEXT))
                line.addView(values)
                marketRows.addView(line, margins(bottom = 5))
                if (price.id.startsWith("xau_")) {
                    marketRows.addView(Ui.button(this, price.title + " için fiyat alarmı ekle") {
                        chooseAlert("gold:" + price.id, price.title)
                    }, margins(bottom = 8))
                }
            }
        }
    }

    private fun saveFinnhubKey() {
        val key = apiKeyInput.text.toString().trim()
        if (key.isBlank()) {
            apiKeyStatus.text = if (!FinanceKeyStore.hasKey(this)) {
                "Anahtar girilmedi."
            } else "Mevcut kayıtlı anahtar korundu."
            return
        }
        if (key.length < 8 || key.any { it.isWhitespace() }) {
            apiKeyStatus.text = "Anahtar biçimi geçersiz. Finnhub panelinden kopyaladığını kontrol et."
            return
        }
        if (!FinanceKeyStore.save(this, key)) {
            apiKeyStatus.text = "Anahtar Android Keystore ile saklanamadı. Cihaz güvenlik durumunu kontrol et."
            return
        }
        apiKeyInput.text.clear()
        apiKeyStatus.text = "Finnhub anahtarı Android Keystore ile şifrelendi."
        refreshStocks()
    }

    private fun searchSymbols() {
        val key = FinanceKeyStore.read(this)
        val query = symbolInput.text.toString().trim()
        if (key.isBlank()) {
            lookupRows.removeAllViews()
            lookupRows.addView(Ui.text(this, "Önce Finnhub API anahtarını kaydet.", 12f, Pal.AMBER))
            return
        }
        if (query.isBlank()) {
            symbolInput.error = "Hisse kodu veya şirket adı gir."
            return
        }
        lookupRows.removeAllViews()
        lookupRows.addView(Ui.text(this, "Semboller aranıyor…", 12f, Pal.MUTED))
        Thread {
            val result = runCatching { FinanceData.searchFinnhubSymbols(query, key) }
            runOnUiThread {
                lookupRows.removeAllViews()
                result.onSuccess { candidates ->
                    if (candidates.isEmpty()) {
                        lookupRows.addView(Ui.text(this, "Sembol bulunamadı. Finnhub bu kodu desteklemiyor olabilir.", 12f, Pal.AMBER))
                    } else {
                        candidates.forEach { candidate ->
                            val row = Ui.row(this)
                            row.addView(Ui.text(
                                this,
                                candidate.symbol + "\n" + candidate.description,
                                12f, Pal.TEXT
                            ), LinearLayout.LayoutParams(0, -2, 1f))
                            row.addView(Ui.button(this, "Ekle") {
                                Store(this).addWatch(candidate.symbol)
                                lookupRows.removeAllViews()
                                symbolInput.text.clear()
                                refreshStocks()
                            })
                            lookupRows.addView(row, margins(bottom = 5))
                        }
                    }
                }.onFailure { error ->
                    lookupRows.addView(Ui.text(this, safeError(error), 12f, Pal.AMBER))
                }
            }
        }.start()
    }

    private fun refreshStocks() {
        if (!::stockRows.isInitialized) return
        stockRows.removeAllViews()
        val watch = Store(this).watch().distinct().take(12)
        val key = FinanceKeyStore.read(this)
        if (watch.isEmpty()) {
            stockRows.addView(Ui.text(this, "Henüz hisse eklemedin. Yukarıdaki aramayla sembol bulabilirsin.", 12f, Pal.MUTED))
            return
        }
        if (key.isBlank()) {
            stockRows.addView(Ui.text(this, "Takip listen kayıtlı. Fiyatları görmek için Finnhub anahtarını kaydet.", 12f, Pal.AMBER))
            watch.forEach { stockRows.addView(watchOnlyRow(it), margins(bottom = 5)) }
            return
        }
        stockRows.addView(Ui.text(this, "Hisse fiyatları alınıyor…", 12f, Pal.MUTED))
        Thread {
            val results = watch.map { symbol ->
                symbol to runCatching { FinanceData.fetchFinnhubQuote(symbol, key) }
            }
            runOnUiThread {
                stockRows.removeAllViews()
                results.forEach { (symbol, result) ->
                    result.onSuccess { quote ->
                        stockRows.addView(quoteRow(quote), margins(bottom = 7))
                    }.onFailure { error ->
                        stockRows.addView(errorQuoteRow(symbol, safeError(error)), margins(bottom = 7))
                    }
                }
            }
        }.start()
    }

    private fun watchOnlyRow(symbol: String): android.view.View {
        val row = Ui.row(this)
        row.addView(Ui.text(this, symbol, 13f, Pal.TEXT, true), LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(Ui.button(this, "Sil") { removeWatch(symbol) })
        return row
    }

    private fun quoteRow(quote: FinanceQuote): LinearLayout {
        val card = panel()
        val top = Ui.row(this)
        val labels = Ui.column(this)
        labels.addView(Ui.text(this, quote.symbol, 14f, Pal.TEXT, true))
        labels.addView(Ui.text(
            this,
            formatTry(quote.current) + " · " + (quote.changePercent?.let { "%+.2f%%".format(Locale.US, it) } ?: "değişim yok"),
            15f, if ((quote.changePercent ?: 0.0) >= 0) Pal.GREEN else Pal.RED, true
        ))
        labels.addView(Ui.text(this, quoteTimeLabel(quote.timestampSeconds), 10f, Pal.MUTED))
        top.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(Ui.button(this, "Sil") { removeWatch(quote.symbol) })
        card.addView(top)
        card.addView(Ui.button(this, "Fiyat alarmı ekle") { chooseAlert(quote.symbol, quote.symbol) }, margins(6))
        card.addView(Ui.button(this, "Ayrıntılar") { showQuoteDetails(quote) }, margins(4))
        return card
    }

    private fun errorQuoteRow(symbol: String, error: String): LinearLayout {
        val row = panel()
        val labels = Ui.text(this, symbol + "\n" + error, 11f, Pal.AMBER)
        val line = Ui.row(this)
        line.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
        line.addView(Ui.button(this, "Sil") { removeWatch(symbol) })
        row.addView(line)
        return row
    }

    private fun showQuoteDetails(quote: FinanceQuote) {
        val message = "Son fiyat: " + formatTry(quote.current) + "\n" +
            "Günlük değişim: " + (quote.change?.let { "%+.2f".format(Locale.US, it) } ?: "yok") + "\n" +
            "Değişim yüzdesi: " + (quote.changePercent?.let { "%+.2f%%".format(Locale.US, it) } ?: "yok") + "\n" +
            "Önceki kapanış: " + (quote.previousClose?.let { formatTry(it) } ?: "yok") + "\n" +
            "Veri zamanı: " + quoteTimeLabel(quote.timestampSeconds) + "\n\n" +
            "Kaynak: Finnhub. Bu bilgi yatırım tavsiyesi değildir; fiyat gecikmeli olabilir."
        AlertDialog.Builder(this).setTitle(quote.symbol).setMessage(message)
            .setPositiveButton("Kapat", null).show()
    }

    private fun chooseAlert(key: String, title: String) {
        AlertDialog.Builder(this).setTitle(title + " fiyat alarmı")
            .setItems(arrayOf("Hedefin üzerine çıkınca", "Hedefin altına düşünce")) { _, index ->
                enterAlertThreshold(key, title, if (index == 0) "above" else "below")
            }.setNegativeButton("Vazgeç", null).show()
    }

    private fun enterAlertThreshold(key: String, title: String, direction: String) {
        val unitHint = if (key.startsWith("gold:")) "Hedef fiyat (TL)" else "Hedef fiyat (sembolün para birimi)"
        val input = Ui.input(this, unitHint).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
        }
        AlertDialog.Builder(this).setTitle(if (direction == "above") "Üst eşik" else "Alt eşik")
            .setMessage(title + " için fiyat sınırını gir. Hisse için para birimi Finnhub sembolüne bağlıdır.")
            .setView(input)
            .setPositiveButton("Alarmı kaydet") { _, _ ->
                val target = input.text.toString().trim().replace(",", ".").toDoubleOrNull()
                if (target == null || !target.isFinite() || target <= 0.0) {
                    android.widget.Toast.makeText(this, "Geçerli bir fiyat gir.", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    FinanceAlerts.add(this, key, title, direction, target)
                    renderAlerts()
                    android.widget.Toast.makeText(this, "Fiyat alarmı kaydedildi.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("İptal", null).show()
    }

    private fun renderAlerts() {
        if (!::alertRows.isInitialized) return
        alertRows.removeAllViews()
        val alerts = FinanceAlerts.read(this)
        if (alerts.length() == 0) {
            alertRows.addView(Ui.text(this, "Henüz fiyat alarmı yok.", 12f, Pal.MUTED))
            return
        }
        for (i in 0 until alerts.length()) {
            val item = alerts.optJSONObject(i) ?: continue
            val direction = if (item.optString("direction") == "above") "üzerine çıkınca" else "altına düşünce"
            val fired = item.optBoolean("fired", false)
            val unit = if (item.optString("key").startsWith("gold:")) " ₺" else ""
            val text = item.optString("title") + " · " + formatTry(item.optDouble("target")) +
                unit + " " + direction + if (fired) " · TETİKLENDİ" else " · etkin"
            val row = Ui.row(this)
            row.addView(Ui.text(this, text, 11f, if (fired) Pal.GREEN else Pal.TEXT),
                LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(Ui.button(this, "Sil") {
                FinanceAlerts.remove(this, item.optString("id"))
                renderAlerts()
            })
            alertRows.addView(row, margins(bottom = 5))
        }
    }

    private fun removeWatch(symbol: String) {
        val remaining = Store(this).watch().filterNot { it.equals(symbol, true) }
        prefs.edit().putString("watch", remaining.joinToString(",")).apply()
        refreshStocks()
    }

    private fun sourceLabel(source: String): String = when (source.lowercase(Locale.ROOT)) {
        "tcmb" -> "TCMB"
        "truncgil" -> "Truncgil"
        else -> source.uppercase(Locale("tr", "TR"))
    }

    private fun quoteTimeLabel(seconds: Long): String {
        if (seconds <= 0L) return "Veri zamanı sağlayıcı tarafından verilmedi"
        val time = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))
            .format(Date(seconds * 1000L))
        val age = System.currentTimeMillis() - seconds * 1000L
        return if (age > TimeUnit.HOURS.toMillis(24)) "Eski veri · " + time else "Veri zamanı · " + time
    }

    private fun formatIso(value: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(value) ?: return value
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(date)
        } catch (_: Exception) { value }
    }

    private fun formatTry(value: Double): String =
        DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("tr", "TR"))).format(value)

    private fun safeError(error: Throwable): String =
        (error.message ?: "Bilinmeyen bağlantı hatası").take(180)

    private fun panel(): LinearLayout = Ui.column(this).apply {
        background = Ui.rounded(Pal.CARD, Pal.LINE, dp(18).toFloat())
        setPadding(dp(14), dp(12), dp(14), dp(12))
    }

    private fun margins(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(-1, -2).apply {
            if (top > 0) topMargin = dp(top)
            if (bottom > 0) bottomMargin = dp(bottom)
        }

    private fun dp(value: Int): Int = Ui.dp(this, value)
}

object FinanceAlerts {
    private const val KEY = "finance_alerts"
    private const val WORK_NAME = "aries-finance-price-alerts"
    private const val CHANNEL_ID = "aries_finance_alerts"

    fun read(context: Context): JSONArray = try {
        JSONArray(context.getSharedPreferences("j", Context.MODE_PRIVATE).getString(KEY, "[]"))
    } catch (_: Exception) { JSONArray() }

    fun add(context: Context, key: String, title: String, direction: String, target: Double) {
        val prefs = context.getSharedPreferences("j", Context.MODE_PRIVATE)
        val items = read(context)
        items.put(JSONObject().put("id", System.currentTimeMillis().toString())
            .put("key", key).put("title", title.take(100))
            .put("direction", if (direction == "above") "above" else "below")
            .put("target", target).put("fired", false))
        prefs.edit().putString(KEY, items.toString()).apply()
        schedule(context)
    }

    fun remove(context: Context, id: String) {
        val old = read(context)
        val next = JSONArray()
        for (i in 0 until old.length()) {
            val item = old.optJSONObject(i) ?: continue
            if (item.optString("id") != id) next.put(item)
        }
        context.getSharedPreferences("j", Context.MODE_PRIVATE).edit().putString(KEY, next.toString()).apply()
        if (next.length() == 0) WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun schedule(context: Context) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request = PeriodicWorkRequestBuilder<FinanceAlertWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request
        )
    }

    fun notify(context: Context, id: Int, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Aries fiyat alarmları", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, CHANNEL_ID)
        else Notification.Builder(context)
        builder.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title)
            .setContentText(message).setStyle(Notification.BigTextStyle().bigText(message)).setAutoCancel(true)
        manager.notify(id, builder.build())
    }
}

class FinanceAlertWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("j", Context.MODE_PRIVATE)
        val alerts = FinanceAlerts.read(applicationContext)
        if (alerts.length() == 0) return Result.success()
        if (Build.VERSION.SDK_INT >= 33 &&
            applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val market = runCatching { FinanceData.fetchTurkishMarket() }.getOrNull()
        val token = FinanceKeyStore.read(applicationContext)
        var changed = false
        for (i in 0 until alerts.length()) {
            val item = alerts.optJSONObject(i) ?: continue
            if (item.optBoolean("fired", false)) continue
            val key = item.optString("key")
            val price: Double?
            val fresh: Boolean
            if (key.startsWith("gold:")) {
                val point = market?.prices?.firstOrNull { it.id == key.removePrefix("gold:") }
                price = point?.bid
                fresh = point != null && !point.stale && market?.stale == false
            } else {
                if (token.isBlank()) continue
                val quote = runCatching { FinanceData.fetchFinnhubQuote(key, token) }.getOrNull()
                price = quote?.current
                val age = if ((quote?.timestampSeconds ?: 0L) > 0L) {
                    System.currentTimeMillis() - (quote!!.timestampSeconds * 1000L)
                } else Long.MAX_VALUE
                fresh = quote != null && age in 0..TimeUnit.HOURS.toMillis(24)
            }
            if (price == null || !fresh) continue
            val target = item.optDouble("target", Double.NaN)
            if (!target.isFinite() || target <= 0.0) continue
            val hit = if (item.optString("direction") == "above") price >= target else price <= target
            if (!hit) continue
            val title = item.optString("title", key)
            val value = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("tr", "TR"))).format(price)
            FinanceAlerts.notify(
                applicationContext, key.hashCode(), "Aries · Fiyat alarmı",
                title + " hedef fiyatına ulaştı. Son alınan fiyat: " + value +
                    (if (key.startsWith("gold:")) " TL." else " (sembolün işlem para biriminde).") +
                    " Kaynak güncelliğini ve veri saatini kontrol et."
            )
            item.put("fired", true)
            changed = true
        }
        if (changed) prefs.edit().putString("finance_alerts", alerts.toString()).apply()
        return Result.success()
    }
}
