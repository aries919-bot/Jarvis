package com.cem.jarvis

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import java.util.Locale

class AriesNotifService : NotificationListenerService() {
    companion object {
        @Volatile var instance: AriesNotifService? = null
        val items: MutableList<String> = java.util.Collections.synchronizedList(ArrayList<String>())
        private val sonlar = HashMap<String, Long>()
        private val sonSoyleme = HashMap<String, Long>()
        private const val TEKRAR_MS = 5000L
        private const val SOYLEME_ARALIK_MS = 10000L
        private val susPaketler = setOf("android", "com.android.systemui")

        fun oku(ctx: Context, say: (String) -> Unit) {
            val svc = instance
            if (svc == null) { say("Bildirim erişimi kapalı. Ayarlar'dan ver."); return }
            val kopya = items.toList()
            if (kopya.isEmpty()) { say("Yeni bildirim yok."); return }
            val sirali = kopya.sortedBy { it.substringAfterLast('|').toLongOrNull() ?: 0L }
            val okunacak = sirali.take(8)
            val parcalar = okunacak.map { satir ->
                val p = satir.split("|")
                val ad = svc.uygulamaAdi(p.getOrNull(0) ?: "")
                val gonderen = p.getOrNull(1) ?: ""
                var metin = p.getOrNull(2) ?: ""
                if (metin.length > 200) metin = metin.substring(0, 200)
                if (gonderen.isBlank()) ad + ": " + metin else ad + ", " + gonderen + ": " + metin
            }
            var sonuc = parcalar.joinToString(" ")
            if (sirali.size > 8) sonuc += " " + (sirali.size - 8) + " bildirim daha var, devam için tekrar söyle."
            items.removeAll(okunacak.toSet())
            say(sonuc)
        }
    }

    private var tts: TextToSpeech? = null
    @Volatile private var ttsHazir = false

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(Locale("tr", "TR"))
                ttsHazir = true
            }
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        tts = null
        ttsHazir = false
        super.onDestroy()
    }

    override fun onListenerConnected() { instance = this }
    override fun onListenerDisconnected() { instance = null }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return
        val e = sbn.notification.extras
        val title = e.getCharSequence("android.title")?.toString() ?: ""
        val text = e.getCharSequence("android.text")?.toString() ?: ""
        if (title.isBlank() && text.isBlank()) return
        val anahtar = sbn.packageName + "|" + text
        val simdi = System.currentTimeMillis()
        val onceki = sonlar[anahtar]
        if (onceki != null && simdi - onceki < TEKRAR_MS) return
        sonlar[anahtar] = simdi
        if (sonlar.size > 100) sonlar.entries.removeAll { simdi - it.value > 60000L }
        items.removeAll { it.startsWith(sbn.packageName + "|" + title + "|" + text + "|") }
        items.add(0, sbn.packageName + "|" + title + "|" + text + "|" + sbn.postTime)
        while (items.size > 30) items.removeAt(items.size - 1)
        if (!getSharedPreferences("j", Context.MODE_PRIVATE).getBoolean("bildirim_soyle", true)) return
        val n = sbn.notification
        if (n.flags and Notification.FLAG_ONGOING_EVENT != 0) return
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        if (n.category == Notification.CATEGORY_TRANSPORT) return
        if (e.containsKey("android.progress")) return
        if (susPaketler.contains(sbn.packageName)) return
        val sonSoz = sonSoyleme[sbn.packageName]
        if (sonSoz != null && simdi - sonSoz < SOYLEME_ARALIK_MS) return
        sonSoyleme[sbn.packageName] = simdi
        val cumle = kaynakCumlesi(sbn.packageName, title)
        soyle(cumle)
    }

    private fun soyle(cumle: String) {
        val engine = tts
        if (!ttsHazir || engine == null) return
        engine.speak(cumle, TextToSpeech.QUEUE_ADD, null, "bildirim")
    }

    private fun uygulamaAdi(paket: String): String = try {
        packageManager.getApplicationLabel(packageManager.getApplicationInfo(paket, 0)).toString()
    } catch (ex: Exception) { paket }

    private fun kaynakCumlesi(paket: String, gonderen: String): String {
        val ad = uygulamaAdi(paket)
        return if (gonderen.isBlank()) ad + ": bildirim geldi." else ad + ", " + gonderen + ": bildirim geldi."
    }
}
