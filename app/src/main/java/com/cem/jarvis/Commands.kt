package com.cem.jarvis

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Commands {
    private fun go(ctx: Context, i: Intent, say: (String) -> Unit, ok: String): Boolean {
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try { ctx.startActivity(i); say(ok) } catch (e: Exception) { say("Bu işlem bu telefonda açılamadı.") }
        return true
    }

    private fun media(ctx: Context, code: Int) {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
    }

    private fun volume(ctx: Context, dir: Int) {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, dir, AudioManager.FLAG_SHOW_UI)
    }

    private fun parseTime(s: String): Pair<Int, Int>? {
        val a = Regex("(?<!\\d)(\\d{1,2})\\s*[:.]\\s*(\\d{2})(?!\\d)").find(s)
        val b = Regex("(?<!\\d)(\\d{3,4})(?!\\d)").find(s)
        val c = Regex("saat\\s*(\\d{1,2})(?!\\d)").find(s)
        var h: Int
        val m: Int
        if (a != null) { h = a.groupValues[1].toInt(); m = a.groupValues[2].toInt() }
        else if (b != null) { val v = b.groupValues[1].toInt(); h = v / 100; m = v % 100 }
        else if (c != null) { h = c.groupValues[1].toInt(); m = 0 }
        else return null
        if (h < 12 && Regex("akşam|öğleden sonra").containsMatchIn(s)) h += 12
        if (h > 23 || m > 59) return null
        return Pair(h, m)
    }

    private fun phoneOf(ctx: Context, name: String): String? {
        if (ctx.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) return null
                for (n in listOf(name.trim(), cleanSuffixes(name)).distinct()) {
            ctx.contentResolver.query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER), android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?", arrayOf("%" + n + "%"), null)?.use { c -> if (c.moveToFirst()) return c.getString(0) }
        }
        return null
    }

    private fun intl(n: String): String {
        val d = n.filter { it.isDigit() }
        return when { d.startsWith("90") && d.length == 12 -> d; d.startsWith("0") -> "90" + d.substring(1); d.length == 10 -> "90" + d; else -> d }
    }

    fun tryHandle(ctx: Context, s: String, say: (String) -> Unit): Boolean {
        if (s.contains("alarm") && Regex("kur|ayarla").containsMatchIn(s)) {
            val t = parseTime(s) ?: return false
            val i = Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, t.first)
                .putExtra(AlarmClock.EXTRA_MINUTES, t.second)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            return go(ctx, i, say, "Alarm kuruldu: %02d:%02d.".format(t.first, t.second))
        }
        if (Regex("zamanlayıcı|sayaç|geri sayım").containsMatchIn(s)) {
            val tm = Regex("(\\d+)\\s*(saniye|sn|dakika|dk|saat)").find(s) ?: return false
            val n = tm.groupValues[1].toInt()
            val secs = n * when (tm.groupValues[2]) { "saniye", "sn" -> 1; "dakika", "dk" -> 60; else -> 3600 }
            val i = Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_LENGTH, secs)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            return go(ctx, i, say, "Zamanlayıcı kuruldu: $n ${tm.groupValues[2]}.")
        }
        if (Regex("müziği (durdur|duraklat)|şarkıyı durdur").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_PAUSE); say("Durduruldu."); return true }
        if (Regex("müziği (oynat|devam)|şarkıyı oynat").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_PLAY); say("Oynatılıyor."); return true }
        if (Regex("müziği geç|sonraki şarkı|şarkıyı geç").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_NEXT); say("Sonraki şarkı."); return true }
        if (Regex("önceki şarkı").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_PREVIOUS); say("Önceki şarkı."); return true }
        val vp = Regex("^ses(?:i)?\\s*(?:kıs|aç|yükselt|azalt|düşür|ayarla)?\\s*(?:yüzde\\s*)?%?\\s*(\\d{1,3})\\s*(?:yap|yüzde|%)?$").find(s)
        if (vp != null) {
            val p = vp.groupValues[1].toInt().coerceIn(0, 100)
            val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * p / 100f), AudioManager.FLAG_SHOW_UI)
            say("Ses yüzde $p.")
            return true
        }
        if (Regex("^sesi aç$|sesi yükselt").containsMatchIn(s)) { volume(ctx, AudioManager.ADJUST_RAISE); say("Ses açıldı."); return true }
        if (Regex("^sesi kıs$|sesi azalt|sesi düşür").containsMatchIn(s)) { volume(ctx, AudioManager.ADJUST_LOWER); say("Ses kısıldı."); return true }
        if (s.contains("ayar")) {
            val act = when {
                Regex("wi-?fi").containsMatchIn(s) -> Settings.ACTION_WIFI_SETTINGS
                s.contains("bluetooth") -> Settings.ACTION_BLUETOOTH_SETTINGS
                s.contains("ekran") -> Settings.ACTION_DISPLAY_SETTINGS
                s.contains("uçak") -> Settings.ACTION_AIRPLANE_MODE_SETTINGS
                s.contains("konum") -> Settings.ACTION_LOCATION_SOURCE_SETTINGS
                s.contains("ses") -> Settings.ACTION_SOUND_SETTINGS
                else -> null
            }
            if (act != null) return go(ctx, Intent(act), say, "Ayarlar açılıyor.")
        }
        if (s.contains("kamera aç")) return go(ctx, Intent(MediaStore.ACTION_IMAGE_CAPTURE), say, "Kamera açılıyor.")
        val yt = Regex("^youtube(?:'da|da)?\\s+(.+)$").find(s)
        if (yt != null) return go(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(yt.groupValues[1]))), say, "YouTube açılıyor.")
        val yol = Regex("^(.+?)\\s+yol tarifi$").find(s)
        if (yol != null) return go(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + Uri.encode(yol.groupValues[1]))), say, "Yol tarifi açılıyor.")
        val tel = Regex("^([0-9+][0-9 ]{6,})\\s*ara$").find(s)
        if (tel != null) return go(ctx, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel.groupValues[1].replace(" ", ""))), say, "Arama ekranı açılıyor.")
        if (Regex("^(bugün günlerden ne|günlerden ne|bugünün tarihi|tarih|bugün ayın kaçı)\\??$").containsMatchIn(s)) {
            say(SimpleDateFormat("d MMMM yyyy, EEEE", Locale("tr", "TR")).format(Date()) + ".")
            return true
        }
        if (Regex("^depolama( durumu)?$").containsMatchIn(s)) {
            val st = StatFs(Environment.getDataDirectory().path)
            val free = st.availableBytes / 1e9
            val total = st.totalBytes / 1e9
            say(String.format(Locale("tr", "TR"), "Boş alan %.1f GB, toplam %.1f GB.", free, total))
            return true
        }
        val wa = Regex("^(.+?)\\s+whatsapp(?:'tan|tan)?\\s+(.+)$").find(s)
        if (wa != null) {
            val p = phoneOf(ctx, wa.groupValues[1])
            if (p == null) { say("Rehberde bulamadım veya rehber izni yok."); return true }
            return go(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + intl(p) + "?text=" + Uri.encode(wa.groupValues[2]))), say, "WhatsApp açılıyor, göndermek için okuna dokun.")
        }
        val sm = Regex("^(.+?)\\s+(?:mesaj|sms)(?:\\s+(?:at|gönder))?\\s+(.+)$").find(s)
        if (sm != null) {
            val p = phoneOf(ctx, sm.groupValues[1])
            if (p == null) { say("Rehberde bulamadım veya rehber izni yok."); return true }
            return go(ctx, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + p)).putExtra("sms_body", sm.groupValues[2]), say, "Mesaj taslağı açıldı.")
        }
        val ar = Regex("^(.+?)\\s+ara$").find(s)
        if (ar != null) {
            val p = phoneOf(ctx, ar.groupValues[1])
            if (p == null) { say("Rehberde bulamadım veya rehber izni yok."); return true }
            return go(ctx, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + p)), say, "Arama ekranı açıldı.")
        }
        return false
    }
}
