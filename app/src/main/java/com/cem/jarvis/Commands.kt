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

    private fun playing(c: android.media.session.MediaController?): Boolean =
        c?.playbackState?.state == android.media.session.PlaybackState.STATE_PLAYING

    private fun mcOf(ctx: Context, pkg: String?): android.media.session.MediaController? = try {
        val msm = ctx.getSystemService(Context.MEDIA_SESSION_SERVICE) as android.media.session.MediaSessionManager
        val l = msm.getActiveSessions(android.content.ComponentName(ctx, AriesNotifService::class.java)).filter { pkg == null || it.packageName == pkg }
        l.firstOrNull { playing(it) } ?: l.firstOrNull()
    } catch (e: Exception) { null }

    private fun mediaVerify(ctx: Context, want: Boolean, okMsg: String, failMsg: String, say: (String) -> Unit) {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val c = mcOf(ctx, null)
            if (c == null) say("Çalan medya oturumu görünmüyor.")
            else if (playing(c) == want) {
                val t = c.metadata?.getString(android.media.MediaMetadata.METADATA_KEY_TITLE).orEmpty()
                say(if (want && t.isNotBlank()) okMsg.trimEnd('.') + ": " + t + "." else okMsg)
            } else say(failMsg)
        }, 1200)
    }

    private fun musicVerify(ctx: Context, pkg: String, aname: String, q: String, say: (String) -> Unit) {
        val h = android.os.Handler(android.os.Looper.getMainLooper())
        say("Gönderildi: " + q + " (" + aname + "). Kontrol ediyorum.")
        h.postDelayed({ try { val c = mcOf(ctx, pkg); if (c != null && !playing(c)) c.transportControls.playFromSearch(q, android.os.Bundle()) } catch (e: Exception) { } }, 2500)
        h.postDelayed({ try { if (!playing(mcOf(ctx, pkg))) media(ctx, KeyEvent.KEYCODE_MEDIA_PLAY) } catch (e: Exception) { } }, 6000)
        h.postDelayed({
            val c = mcOf(ctx, pkg)
            if (playing(c)) {
                val t = c?.metadata?.getString(android.media.MediaMetadata.METADATA_KEY_TITLE).orEmpty()
                say("Çalıyor" + (if (t.isNotBlank()) ": " + t else "") + " (" + aname + ").")
            } else if (c == null) say("Çaldığını doğrulayamadım, medya oturumu görünmüyor. Çalmazsa uygulamadan seç.")
            else say("Çalmadı. " + aname + " içinden şarkıyı elle seç.")
        }, 9000)
    }

    private fun volume(ctx: Context, dir: Int) {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, dir, AudioManager.FLAG_SHOW_UI)
    }

    private fun parseTime(s: String): Pair<Int, Int>? {
        val a = Regex("(?<!\\d)(\\d{1,2})\\s*[:.]\\s*(\\d{2})(?!\\d)").find(s)
        val b = Regex("(?<!\\d)(\\d{3,4})(?!\\d)").find(s)
        val c = Regex("saat\\s*(\\d{1,2})(?!\\d)").find(s)
        val d = Regex("(?<!\\d)(\\d{1,2})\\s*['’]?\\s*(?:de|da|te|ta|ye|ya|e|a)\\b").find(s)
        var h: Int
        var m: Int
        if (a != null) { h = a.groupValues[1].toInt(); m = a.groupValues[2].toInt() }
        else if (b != null) { val v = b.groupValues[1].toInt(); h = v / 100; m = v % 100 }
        else if (c != null) { h = c.groupValues[1].toInt(); m = 0 }
        else if (d != null) { h = d.groupValues[1].toInt(); m = 0 }
        else return null
        if (s.contains("buçuk")) m = 30
        if (h < 12 && Regex("akşam|öğleden sonra|ikindi").containsMatchIn(s)) h += 12
        if (h > 23 || m > 59) return null
        return Pair(h, m)
    }

    private fun callIntent(ctx: Context, num: String): Intent =
        Intent(if (ctx.checkSelfPermission(android.Manifest.permission.CALL_PHONE) == android.content.pm.PackageManager.PERMISSION_GRANTED) Intent.ACTION_CALL else Intent.ACTION_DIAL, Uri.parse("tel:" + num))

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
        val parts = s.split(Regex("\\s+(?:ardından|ayrıca|bir de)\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.size < 2) return tryOne(ctx, s, say)
        if (!tryOne(ctx, parts[0], say)) return tryOne(ctx, s, say)
        for (p in parts.drop(1)) { if (!tryOne(ctx, p, say)) say("Şunu anlayamadım: " + p) }
        return true
    }

    private fun tryOne(ctx: Context, s: String, say: (String) -> Unit): Boolean {
        if (s.contains("alarm") && Regex("kur|ayarla|koy|ekle|yap").containsMatchIn(s)) {
            val t = parseTime(s) ?: return false
            val yarin = s.contains("yarın") || s.contains("yarin")
            val i = Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, t.first)
                .putExtra(AlarmClock.EXTRA_MINUTES, t.second)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            if (yarin) i.putIntegerArrayListExtra(AlarmClock.EXTRA_DAYS, arrayListOf(java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 1) }.get(java.util.Calendar.DAY_OF_WEEK)))
            return go(ctx, i, say, "Alarm kuruldu: %02d:%02d%s.".format(t.first, t.second, if (yarin) " (yarın)" else ""))
        }
        if (Regex("zamanlayıcı|sayaç|geri sayım").containsMatchIn(s)) {
            val tms = Regex("(\\d+)\\s*(saniye|sn|dakika|dk|saat)").findAll(s).toList()
            val half = s.contains("yarım saat")
            if (tms.isEmpty() && !half) return false
            
            var secs = 0
            for (x in tms) secs += x.groupValues[1].toInt() * when (x.groupValues[2]) { "saniye", "sn" -> 1; "dakika", "dk" -> 60; else -> 3600 }
            if (half) secs += 1800
            val i = Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_LENGTH, secs)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            return go(ctx, i, say, "Zamanlayıcı kuruldu: " + (if (secs >= 60) (secs / 60).toString() + " dakika" else secs.toString() + " saniye") + ".")
        }
        if (Regex("müziği (durdur|duraklat)|şarkıyı durdur").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_PAUSE); mediaVerify(ctx, false, "Durduruldu.", "Durdurma tuşunu gönderdim ama hâlâ çalıyor görünüyor.", say); return true }
        if (Regex("müziği (oynat|devam)|şarkıyı oynat").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_PLAY); mediaVerify(ctx, true, "Oynatılıyor.", "Oynatma tuşunu gönderdim ama çalan bir şey görünmüyor.", say); return true }
        if (Regex("müziği geç|sonraki şarkı|şarkıyı geç").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_NEXT); say("Sonraki şarkı."); return true }
        if (Regex("önceki şarkı").containsMatchIn(s)) { media(ctx, KeyEvent.KEYCODE_MEDIA_PREVIOUS); say("Önceki şarkı."); return true }
        if (Regex("takvim|etkinlik|toplantı|randevu|buluşma").containsMatchIn(s) && Regex("ekle|kaydet|oluştur|koy|ayarla").containsMatchIn(s)) {
            val tt = parseTime(s)
            if (tt == null) { say("Saat kaçta? Örnek: yarın 15:00 toplantı ekle."); return true }
            val cal = java.util.Calendar.getInstance()
            if (s.contains("yarın")) cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            if (Regex("öbür gün|ertesi gün").containsMatchIn(s)) cal.add(java.util.Calendar.DAY_OF_YEAR, 2)
            var explicitDate = false
            val ayl = listOf("ocak","şubat","mart","nisan","mayıs","haziran","temmuz","ağustos","eylül","ekim","kasım","aralık")
            val dm = Regex("(\\d{1,2})\\s+(ocak|şubat|mart|nisan|mayıs|haziran|temmuz|ağustos|eylül|ekim|kasım|aralık)").find(s)
            val gs = Regex("(\\d{1,3})\\s+gün\\s+sonra").find(s)
            if (dm != null) {
                cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                cal.set(java.util.Calendar.MONTH, ayl.indexOf(dm.groupValues[2]))
                cal.set(java.util.Calendar.DAY_OF_MONTH, dm.groupValues[1].toInt().coerceIn(1, 31))
                explicitDate = true
            } else if (gs != null) {
                cal.add(java.util.Calendar.DAY_OF_YEAR, gs.groupValues[1].toInt())
                explicitDate = true
            }
            cal.set(java.util.Calendar.HOUR_OF_DAY, tt.first); cal.set(java.util.Calendar.MINUTE, tt.second); cal.set(java.util.Calendar.SECOND, 0); if (cal.timeInMillis < System.currentTimeMillis()) cal.add(if (dm != null) java.util.Calendar.YEAR else java.util.Calendar.DAY_OF_YEAR, 1)
            var title = s.replace(Regex("(\\d{1,2}\\s+(?:ocak|şubat|mart|nisan|mayıs|haziran|temmuz|ağustos|eylül|ekim|kasım|aralık))|(\\d+\\s+gün\\s+sonra)|(\\d{1,2}\\s*[:.]\\s*\\d{2})|(saat\\s*\\d{1,2})|(\\d{1,2}\\s*['’]?\\s*(de|da|te|ta|ye|ya)\\b)|\\b(bugün|yarın|öbür gün|ertesi gün|akşam|sabah|öğleden sonra|buçuk|takvime|takvim|ekle|kaydet|oluştur|koy|ayarla|bir|etkinlik|olarak|lütfen)\\b"), " ").replace(Regex("\\s+"), " ").trim()
            if (title.isEmpty()) title = "Etkinlik"
            title = title.replaceFirstChar { it.uppercase() }
            val ci = Intent(Intent.ACTION_INSERT)
                .setData(android.provider.CalendarContract.Events.CONTENT_URI)
                .putExtra(android.provider.CalendarContract.Events.TITLE, title)
                .putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.timeInMillis)
                .putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, cal.timeInMillis + 3600000L)
            val fmt = SimpleDateFormat("d MMMM EEEE HH:mm", Locale("tr", "TR")).format(cal.time)
            var rem = ""
            try { Reminders.add(ctx, cal.timeInMillis, "Zamanı geldi: " + title); val hm = SimpleDateFormat("HH:mm", Locale("tr", "TR")).format(cal.time); val t0 = cal.timeInMillis; val m8 = cal.clone() as java.util.Calendar; m8.set(java.util.Calendar.HOUR_OF_DAY, 8); m8.set(java.util.Calendar.MINUTE, 0); val pre = listOf(Pair(t0 - 86400000L, "Yarın " + hm + ": " + title), Pair(m8.timeInMillis, "Bugün " + hm + ": " + title), Pair(t0 - 10800000L, "3 saat sonra " + hm + ": " + title), Pair(t0 - 1800000L, "30 dk sonra " + hm + ": " + title)); var kn = 0; for (p in pre) { if (p.first > System.currentTimeMillis() + 60000L && p.first < t0) { Reminders.add(ctx, p.first, p.second); kn++ } }; rem = " Aries hatırlatması kuruldu" + (if (kn > 0) " (+" + kn + " ön uyarı)" else ", ön uyarı yok (saat çok yakın)") + "." } catch (e: Exception) { }
            return go(ctx, ci, say, "Takvim taslağı açıldı: " + title + ", " + fmt + "." + rem)
        }
        val mv = Regex("(çal|çalar mısın|oynat|dinlet|koy|aç|dinleyelim|dinlemek istiyorum)$").find(s)
        if (mv != null && Regex("şarkı|müzik|parça|çal$|oynat$|dinlet$|dinleyelim|dinlemek|spotify|deezer|youtube müzik|youtube music").containsMatchIn(s) && !Regex("^youtube(?! müzik| music)|ayar|kamera|fener|wifi|bluetooth|uygulama").containsMatchIn(s)) {
            var q = s.substring(0, mv.range.first)
            val apps = linkedMapOf("youtube müzik" to "com.google.android.apps.youtube.music", "youtube music" to "com.google.android.apps.youtube.music", "spotify" to "com.spotify.music", "deezer" to "deezer.android.app", "yt müzik" to "com.google.android.apps.youtube.music", "yt music" to "com.google.android.apps.youtube.music", "youtube muzik" to "com.google.android.apps.youtube.music", "ytm" to "com.google.android.apps.youtube.music")
            val names = mapOf("com.google.android.apps.youtube.music" to "YouTube Music", "com.spotify.music" to "Spotify", "deezer.android.app" to "Deezer")
            val sp = ctx.getSharedPreferences("aries_music", Context.MODE_PRIVATE)
            var named: String? = null
            for ((k, v) in apps) {
                val r = Regex(k + "(?:['’]?(?:dan|den|tan|ten|da|de|ta|te))?(?=\\s|$)")
                if (r.containsMatchIn(q)) { named = v; q = q.replace(r, " "); break }
            }
            q = q.replace(Regex("\\b(bana|bir|biraz|lütfen|şarkısını|şarkıyı|şarkısı|şarkı|müziği|müzik|parçasını|parçayı|parça)\\b"), " ").replace(Regex("\\s+"), " ").trim()
            val def = sp.getString("app", "com.google.android.apps.youtube.music")!!
            val pkg = named ?: def
            if (named != null) {
                if (named == def) sp.edit().remove("cand").putInt("cnt", 0).apply()
                else {
                    val c = if (sp.getString("cand", "") == named) sp.getInt("cnt", 0) + 1 else 1
                    if (c >= 3) sp.edit().putString("app", named).remove("cand").putInt("cnt", 0).apply()
                    else sp.edit().putString("cand", named).putInt("cnt", c).apply()
                }
            }
            val aname = names[pkg] ?: "müzik uygulaması"
            if (q.isEmpty() && named != null) { val li = ctx.packageManager.getLaunchIntentForPackage(pkg); if (li != null) { li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); ctx.startActivity(li); say(aname + " açıldı."); return true } }
            if (q.isEmpty()) { media(ctx, KeyEvent.KEYCODE_MEDIA_PLAY); say("Oynatma tuşu gönderildi. Çalan bir şey yoksa şarkı adını söyle."); return true }
            val pi = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)
                .putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio")
                .putExtra(android.app.SearchManager.QUERY, q).putExtra(MediaStore.EXTRA_MEDIA_TITLE, q)
                .setPackage(pkg)
            pi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try { ctx.startActivity(pi); musicVerify(ctx, pkg, aname, q, say) }
            catch (e: Exception) { return go(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(q))), say, "Müzik uygulaması açılamadı, YouTube'da aranıyor: " + q + ".") }
            return true
        }
        val vp = Regex("^ses(?:i)?\\s*(?:kıs|aç|yükselt|azalt|düşür|ayarla)?\\s*(?:yüzde\\s*)?%?\\s*(\\d{1,3})\\s*(?:yap|yüzde|%)?$").find(s)
        if (vp != null) {
            val p = vp.groupValues[1].toInt().coerceIn(0, 100)
            val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * p / 100f), AudioManager.FLAG_SHOW_UI)
            say("Ses yüzde $p.")
            return true
        }
        if (Regex("^sesi (biraz |çok )?aç$|sesi yükselt|sesi artır|sesi arttır").containsMatchIn(s)) { volume(ctx, AudioManager.ADJUST_RAISE); say("Ses açıldı."); return true }
        if (Regex("^sesi (biraz |çok )?kıs$|sesi azalt|sesi düşür|sesi alçalt").containsMatchIn(s)) { volume(ctx, AudioManager.ADJUST_LOWER); say("Ses kısıldı."); return true }
        if (s.contains("bildirim") && s.contains("oku")) { AriesNotifService.oku(ctx, say); return true }
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
        if (tel != null) return go(ctx, callIntent(ctx, tel.groupValues[1].replace(" ", "")), say, "Arıyorum.")
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
        if (ar != null && !s.contains("whatsapp") && !s.contains("mesaj") && !s.contains("sms")) {
            val p = phoneOf(ctx, ar.groupValues[1])
            if (p == null) { say("Rehberde bulamadım veya rehber izni yok."); return true }
            return go(ctx, callIntent(ctx, p), say, "Arıyorum.")
        }
        return false
    }
}
