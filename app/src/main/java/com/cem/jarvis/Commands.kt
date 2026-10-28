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
        return false
    }
}
