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
        Source("İŞKUR e-Şube", "https://esube.iskur.gov.tr/", "Kamu / işçi"),
        Source("İŞKUR Açık İş Haritası", "https://acikisharita.iskur.gov.tr/", "Kamu / işçi"),
        Source("ilan.gov.tr – Personel alımı", "https://www.ilan.gov.tr/", "Resmî ilan"),
        Source("Resmî Gazete", "https://www.resmigazete.gov.tr/", "Resmî ilan"),
        Source("Yerel Yönetimler Genel Müdürlüğü", "https://yerelyonetimler.csb.gov.tr/", "Belediye"),
        Source("Malatya Büyükşehir Belediyesi", "https://www.malatya.bel.tr/", "Belediye"),
        Source("Battalgazi Belediyesi", "https://www.battalgazi.bel.tr/", "Belediye"),
        Source("Yeşilyurt Belediyesi", "https://yesilyurt.bel.tr/", "Belediye"),
        Source("Malatya TSO – günlük İŞKUR listesi", "https://www.malatyatso.org.tr/iskur-ilanlari", "İŞKUR keşif", false),
        Source("ASELSAN Kariyer", "https://akariyer.aselsan.com/", "Özel sektör"),
        Source("TCDD", "https://www.tcdd.gov.tr/", "Kamu şirketi"),
        Source("TCDD Taşımacılık – işe alım duyuruları", "https://www.tcddtasimacilik.gov.tr/duyurular/", "Kamu şirketi"),
        Source("TEİAŞ Duyurular", "https://www.teias.gov.tr/duyurular", "Kamu şirketi"),
        Source("EÜAŞ Kariyer ve Staj", "https://www.euas.gov.tr/kariyer-ve-staj", "Kamu şirketi"),
        Source("TUSAŞ – Elektrik-Elektronik Bakım Teknisyeni", "https://kariyer.tusas.com/ilan-detay/elektrik-elektronik-bakim-teknisyeni", "Özel sektör / doğrudan ilan"),
        Source("TUSAŞ – Genel Başvuru Teknisyen", "https://kariyer.tusas.com/ilan-detay/genel-basvuru-teknisyen", "Özel sektör / genel başvuru"),
        Source("TUSAŞ Kariyer – tüm açık pozisyonlar", "https://kariyer.tusas.com/acik-pozisyonlarimiz", "Özel sektör"),
        Source("BOTAŞ Personel Alımı", "https://www.botas.gov.tr/Sayfa/personel-alimi/124", "Kamu şirketi"),
        Source("TÜPRAŞ Açık İlanlar", "https://tupras.gethirex.com/o/tupras/", "Özel sektör"),
        Source("ROKETSAN Kariyer", "https://kariyer.roketsan.com.tr/jobs", "Özel sektör"),
        Source("TEI Kariyer", "https://yetenek.tei.com.tr/jobs", "Özel sektör"),
        Source("HAVELSAN Kariyer", "https://www.havelsan.com/tr/kariyer", "Özel sektör"),
        Source("Enerjisa Kariyer", "https://www.enerjisa.com.tr/tr/enerjisa-hakkinda/kariyer", "Özel sektör")
    )

    private val trustedDomains = listOf(
        "cbiko.gov.tr", "osym.gov.tr", "sbb.gov.tr", "iskur.gov.tr", "ilan.gov.tr", "resmigazete.gov.tr",
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
}

object CareerEligibility {
    const val KPSS_P94 = 63.23366

    fun assess(title: String, snippet: String): String {
        val text = "$title $snippet".lowercase()
            .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
            .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
        val associateRequired = listOf("on lisans mezunu", "onlisans mezunu", "myo mezunu", "elektrik teknikeri")
            .any { text.contains(it) }
        if (associateRequired && !text.contains("lise mezunu") && !text.contains("meslek lisesi")) {
            return "MEZUNİYET SONRASI · Ön lisans mezuniyeti şartı olabilir; şu an öğrenci olduğun için başvuruya uygun sayılmadı."
        }
        if (text.contains("kpss") && (text.contains("p3") || text.contains("p93"))) {
            return "PUAN TÜRÜ UYUŞMUYOR · Bu ilandaki P3/P93 şartı, ortaöğretim P94 puanınla karşılanmaz."
        }
        if (text.contains("kpss")) {
            val minScore = Regex("(?:en az|asgari|taban|en dusuk)\\s*(\\d{2,3})\\s*puan")
                .find(text)?.groupValues?.getOrNull(1)?.toDoubleOrNull()
            if (minScore != null && minScore > KPSS_P94) {
                return "PUAN ŞARTI UYMUYOR · İlanda görünen taban puan $minScore; P94 puanın 63,23366."
            }
            if (text.contains("p94") || text.contains("ortaogretim") || text.contains("orta ogretim")) {
                return if (minScore != null) "İLK ELEMEDE UYGUN · P94 puan şartı sağlanıyor görünüyor; diğer özel koşullar kontrol edilmeli."
                else "ŞARTLARI DOĞRULA · P94/ortaöğretim ifadesi var; taban puan ve nitelik kodları ilanın tam metninden kontrol edilmeli."
            }
            return "KPSS ŞARTI BELİRSİZ · Puan türü ve taban puan ilanın tam metninden doğrulanmalı."
        }
        if (text.contains("elektrik") || text.contains("pano") || text.contains("bakim") ||
            text.contains("teknisyen") || text.contains("teknik lise")) {
            return "ALAN EŞLEŞMESİ VAR · Elektrik/teknik deneyiminle ilişkili; diploma, deneyim ve sertifika şartları kontrol edilmeli."
        }
        return "ŞARTLARI DOĞRULA · İlan bulundu; eğitim, yaş, deneyim ve başvuru koşulları tam metinden kontrol edilmeli."
    }

    fun relevance(title: String, snippet: String, city: String): Int {
        val text = "$title $snippet".lowercase()
        var score = 0
        listOf("elektrik", "pano", "kablolama", "bakım", "teknisyen", "tekniker", "otomasyon", "üretim", "işçi", "personel", "memur", "operatör", "depo", "satış", "kasiyer", "paketleme", "tekstil", "gıda", "lojistik", "temizlik", "şoför")
            .forEach { if (text.contains(it)) score += 10 }
        if (city.isNotBlank() && text.contains(city.lowercase())) score += 20
        if (text.contains("son başvuru") || text.contains("başvuru tarihi")) score += 5
        return score.coerceAtMost(100)
    }
}

/** DuckDuckGo sonuçları yalnızca keşif içindir; kayda giren bağlantılar güvenilir resmî alan adlarıyla sınırlıdır. */
object CareerSearchEngine {
    private fun queryList(category: Int, role: String, city: String): List<String> {
        val r = role.ifBlank { "elektrik teknisyeni personel işçi" }
        val c = city.ifBlank { "Türkiye" }
        val publicKpss = listOf(
            "site:osym.gov.tr KPSS tercih ortaöğretim P94 personel alımı 2026",
            "site:kamuilan.sbb.gov.tr personel alımı ortaöğretim KPSS P94 2026 $c",
            "site:isealimkariyerkapisi.cbiko.gov.tr KPSS ortaöğretim lise personel 2026",
            "site:ilan.gov.tr personel alımı lise ortaöğretim KPSS 2026 $c",
            "site:resmigazete.gov.tr personel alımı atama KPSS ortaöğretim 2026"
        )
        val publicNoKpss = listOf(
            "site:iskur.gov.tr açık iş ilanı $r $c 2026",
            "site:malatyatso.org.tr/iskur-ilanlari $r Malatya 2026 İŞKUR",
            "site:yerelyonetimler.csb.gov.tr belediye işçi personel alımı 2026",
            "site:malatya.bel.tr personel alımı iş ilanı 2026"
        )
        val privateSector = listOf(
            "site:kariyer.tusas.com teknisyen elektrik açık pozisyon 2026",
            "site:kariyer.roketsan.com.tr/jobs teknisyen bakım elektrik 2026",
            "site:akariyer.aselsan.com teknisyen elektrik üretim kariyer",
            "site:tupras.gethirex.com/o/tupras teknisyen elektrik bakım kariyer"
        )
        val largeEmployers = listOf(
            "site:teias.gov.tr personel alımı işçi teknisyen 2026",
            "site:euas.gov.tr kariyer personel alımı teknisyen 2026",
            "site:botas.gov.tr personel alımı teknisyen 2026",
            "site:tcdd.gov.tr OR site:tcddtasimacilik.gov.tr personel alımı işçi teknisyen 2026",
            "site:tei.com.tr OR site:havelsan.com kariyer teknisyen elektrik 2026"
        )
        val postAssociate = listOf(
            "elektrik teknikeri ön lisans mezunu KPSS P93 personel alımı 2026",
            "elektrik teknikeri MYO mezunu özel sektör tekniker açık pozisyon 2026",
            "site:teias.gov.tr OR site:euas.gov.tr elektrik teknikeri ön lisans mezunu 2026",
            "site:aselsan.com OR site:tusas.com elektrik teknikeri ön lisans teknisyen 2026"
        )
        val extra = "$r $c iş ilanı başvuru şartları 2026"
        return when (category) {
            1 -> publicKpss + listOf(extra)
            2 -> publicNoKpss + listOf(extra)
            3 -> privateSector + listOf(extra)
            4 -> largeEmployers + privateSector.take(2)
            5 -> postAssociate
            else -> publicKpss + publicNoKpss + privateSector + largeEmployers + listOf(extra)
        }
    }

    fun search(category: Int, role: String, city: String): List<Net.Hit> {
        val queries = queryList(category, role, city).distinct()
        val executor = Executors.newFixedThreadPool(minOf(6, queries.size.coerceAtLeast(1)))
        return try {
            val futures = executor.invokeAll(queries.map { query -> Callable { Net.webSearch(query) } }, 28, TimeUnit.SECONDS)
            val found = ArrayList<Net.Hit>()
            futures.forEach { future ->
                if (!future.isCancelled) runCatching { found.addAll(future.get()) }
            }
            val seen = HashSet<String>()
            found.filter { hit ->
                CareerSourceCatalog.isTrustedUrl(hit.url) &&
                    seen.add(hit.url.substringBefore("#").trimEnd('/').lowercase())
            }.sortedByDescending { CareerEligibility.relevance(it.title, it.snippet, city) }
        } finally {
            executor.shutdownNow()
        }
    }

    fun dailySearch(): List<Net.Hit> {
        val queries = listOf(
            "site:osym.gov.tr OR site:kamuilan.sbb.gov.tr OR site:isealimkariyerkapisi.cbiko.gov.tr OR site:ilan.gov.tr OR site:resmigazete.gov.tr KPSS ortaöğretim P94 lise personel alımı 2026",
            "site:iskur.gov.tr OR site:malatyatso.org.tr/iskur-ilanlari Malatya elektrikçi teknisyen bakım işçi ilanı 2026",
            "site:yerelyonetimler.csb.gov.tr OR site:malatya.bel.tr OR site:battalgazi.bel.tr OR site:yesilyurt.bel.tr belediye işçi personel alımı 2026",
            "site:kariyer.tusas.com OR site:kariyer.roketsan.com.tr OR site:akariyer.aselsan.com teknisyen açık pozisyon 2026",
            "site:teias.gov.tr OR site:euas.gov.tr OR site:botas.gov.tr OR site:tcdd.gov.tr OR site:tcddtasimacilik.gov.tr personel alımı işçi teknisyen 2026",
            "site:tupras.gethirex.com OR site:yetenek.tei.com.tr OR site:havelsan.com OR site:enerjisa.com.tr teknisyen elektrik kariyer iş ilanı 2026"
        )
        val executor = Executors.newFixedThreadPool(6)
        return try {
            val futures = executor.invokeAll(queries.map { query -> Callable { Net.webSearch(query) } }, 12, TimeUnit.SECONDS)
            val found = ArrayList<Net.Hit>()
            futures.forEach { future -> if (!future.isCancelled) runCatching { found.addAll(future.get()) } }
            val seen = HashSet<String>()
            found.filter { CareerSourceCatalog.isTrustedUrl(it.url) &&
                CareerEligibility.relevance(it.title, it.snippet, "") > 0 &&
                seen.add(it.url.substringBefore("#").trimEnd('/').lowercase()) }
        } finally {
            executor.shutdownNow()
        }
    }
}

object CareerMonitorScheduler {
    const val CHANNEL_ID = "aries_career_alerts"
    private const val UNIQUE_WORK = "aries_daily_career_scan"

    /** WorkManager persists across process death and reboot; its daily run is intentionally inexact. */
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
            .addTag(UNIQUE_WORK)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK)
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
            .setContentTitle("Aries: yeni iş ilanı bulundu")
            .setContentText("$count yeni resmî/kurumsal kaynak sonucu bulundu. Açıp şartlarını incele.")
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setStyle(Notification.BigTextStyle().bigText(
                (first?.title?.plus(" · ") ?: "") + "$count yeni sonuç. Başvuru tarihi ve şartları ilanın resmî sayfasından doğrula."
            ))
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(74023, builder.build())
    }
}

class CareerDailyWorker(context: Context, params: androidx.work.WorkerParameters) : androidx.work.Worker(context, params) {
    override fun doWork(): androidx.work.Result {
        val prefs = applicationContext.getSharedPreferences("j", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("career_monitor_enabled", false)) return androidx.work.Result.success()
        return try {
            val hits = CareerSearchEngine.dailySearch()
            if (hits.isEmpty()) {
                prefs.edit()
                    .putLong("career_monitor_last_scan", System.currentTimeMillis())
                    .putInt("career_monitor_new_count", 0)
                    .putString("career_monitor_last_error",
                        "Bu taramada doğrulanabilir sonuç alınamadı. Bu, hiç ilan olmadığı anlamına gelmez; bir sonraki günlük kontrolde yeniden taranacak.")
                    .apply()
                return androidx.work.Result.success()
            }
            val previous = prefs.getStringSet("career_monitor_seen", emptySet()).orEmpty()
            val firstRun = !prefs.getBoolean("career_monitor_initialized", false)
            val seen = previous.toMutableSet()
            val fresh = if (firstRun) emptyList() else hits.filter { seen.add(it.url) }
            hits.forEach { seen.add(it.url) }
            val boundedSeen = seen.toList().takeLast(500).toSet()
            val json = JSONArray()
            hits.take(40).forEach { hit ->
                json.put(JSONObject().put("title", hit.title).put("url", hit.url).put("snippet", hit.snippet))
            }
            prefs.edit()
                .putStringSet("career_monitor_seen", boundedSeen)
                .putString("career_monitor_results", json.toString())
                .putLong("career_monitor_last_scan", System.currentTimeMillis())
                .putInt("career_monitor_new_count", fresh.size)
                .putBoolean("career_monitor_initialized", true)
                .remove("career_monitor_last_error")
                .apply()
            if (fresh.isNotEmpty()) CareerMonitorScheduler.notifyNewJobs(applicationContext, fresh.size, fresh.first())
            androidx.work.Result.success()
        } catch (e: Exception) {
            prefs.edit()
                .putString("career_monitor_last_error", "Tarama tamamlanamadı: ${e.message ?: "internet veya arama servisi hatası"}")
                .apply()
            androidx.work.Result.retry()
        }
    }
}
