package com.cem.jarvis

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ============================================================
// Zamanlı hatırlatmalar. Tamamen telefonda çalışır, internet gerektirmez.
// setAlarmClock: Doze modunda da tetiklenir, ek "tam alarm" izni gerektirmez.
// ============================================================
object Reminders {

    const val CHANNEL = "jarvis_hatirlatma"
    private const val PREF = "jarvis_reminders"
    private const val KEY = "list"
    private val tr = Locale("tr", "TR")

    class Item(val id: Int, val time: Long, val text: String)
    class Parsed(val time: Long, val text: String)

    // ---------- Depolama ----------

    private fun load(ctx: Context): MutableList<Item> {
        val out = ArrayList<Item>()
        try {
            val raw = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(Item(o.getInt("id"), o.getLong("time"), o.getString("text")))
            }
        } catch (e: Exception) {
            // Bozuk kayıt: boş liste ile devam et.
        }
        return out
    }

    private fun save(ctx: Context, list: List<Item>) {
        val arr = JSONArray()
        for (item in list) {
            arr.put(JSONObject().put("id", item.id).put("time", item.time).put("text", item.text))
        }
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).commit()
    }

    fun upcoming(ctx: Context): List<Item> {
        val now = System.currentTimeMillis()
        return load(ctx).filter { it.time > now }.sortedBy { it.time }
    }

    // ---------- Alarm kurma ----------

    private fun firePending(ctx: Context, id: Int, text: String): PendingIntent {
        val i = Intent(ctx, ReminderReceiver::class.java)
        i.putExtra("id", id)
        i.putExtra("text", text)
        return PendingIntent.getBroadcast(
            ctx, id, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun openAppPending(ctx: Context, rid: Int = 0, rtext: String = ""): PendingIntent {
        val i = Intent(ctx, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP; i.putExtra("remind_text", rtext)
        return PendingIntent.getActivity(
            ctx, rid, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun canScheduleExactAlarms(ctx: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return am.canScheduleExactAlarms()
    }

    private fun schedule(ctx: Context, item: Item): Boolean {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return false
        am.setAlarmClock(
            AlarmManager.AlarmClockInfo(item.time, openAppPending(ctx)),
            firePending(ctx, item.id, item.text)
        )
        return true
    }

    fun add(ctx: Context, time: Long, text: String): Item {
        val list = load(ctx)
        var id = (System.currentTimeMillis() % 1000000000L).toInt()
        while (list.any { it.id == id }) id++
        val item = Item(id, time, text)
        if (!canScheduleExactAlarms(ctx)) {
            throw SecurityException("Android kesin alarm izni kapalı.")
        }
        list.add(item)
        save(ctx, list)
        val scheduled = try {
            schedule(ctx, item)
        } catch (e: SecurityException) {
            list.removeAll { it.id == item.id }
            save(ctx, list)
            throw e
        }
        if (!scheduled) {
            list.removeAll { it.id == item.id }
            save(ctx, list)
            throw SecurityException("Android kesin alarm izni kapalı.")
        }
        return item
    }

    // Zamanı gelen hatırlatmayı listeden çıkarır.
    fun remove(ctx: Context, id: Int) {
        val list = load(ctx)
        list.removeAll { it.id == id }
        save(ctx, list)
    }

    fun cancelAll(ctx: Context) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (item in load(ctx)) am.cancel(firePending(ctx, item.id, item.text))
        save(ctx, ArrayList())
    }

    // Açılışta ya da telefon yeniden başlayınca: gelecekteki alarmları tekrar kur,
    // zamanı geçenleri bildirim olarak göster.
    fun restoreAll(ctx: Context) {
        val now = System.currentTimeMillis()
        val keep = ArrayList<Item>()
        for (item in load(ctx)) {
            if (item.time > now) {
                // İzin sonradan kapatılmışsa kaydı koru; sessizce silme.
                try {
                    schedule(ctx, item)
                } catch (_: SecurityException) {
                    // Kayıt korunur; izin yeniden açıldığında geri yüklenebilir.
                }
                keep.add(item)
            } else {
                notifyNow(ctx, item.id, "Kaçırılan hatırlatma: " + item.text)
            }
        }
        save(ctx, keep)
    }

    // ---------- Bildirim ----------

    fun notificationsEnabled(ctx: Context): Boolean {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.areNotificationsEnabled()
    }

    fun notifyNow(ctx: Context, id: Int, text: String) {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(CHANNEL, "Jarvis hatırlatmaları", NotificationManager.IMPORTANCE_HIGH)
            val attrs = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
            ch.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), attrs)
            ch.enableVibration(true)
            nm.createNotificationChannel(ch)
        }
        val b = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(ctx, CHANNEL) else Notification.Builder(ctx)
        b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        b.setContentTitle("Jarvis")
        b.setContentText(text)
        b.setStyle(Notification.BigTextStyle().bigText(text))
        b.setAutoCancel(true)
        b.setCategory(Notification.CATEGORY_ALARM)
        b.setContentIntent(openAppPending(ctx, id, text))
        if (Build.VERSION.SDK_INT < 26) {
            b.setDefaults(Notification.DEFAULT_ALL)
            b.setPriority(Notification.PRIORITY_HIGH)
        }
        nm.notify(id, b.build())
    }

    // ---------- Komut girişi: MainActivity'den çağrılır ----------
    // true dönerse komut burada işlendi demektir.
    fun tryHandle(ctx: Context, raw: String, say: (String) -> Unit): Boolean {
        val s = raw.lowercase(tr).trim()
        if (s.contains("hatırlatmalarım") || s.contains("hatırlatmaları oku")) {
            val l = upcoming(ctx)
            if (l.isEmpty()) {
                say("Kayıtlı hatırlatman yok.")
            } else {
                val sb = StringBuilder("Hatırlatmaların: ")
                for (item in l.take(5)) sb.append(describe(item.time)).append(", ").append(item.text).append(". ")
                say(sb.toString())
            }
            return true
        }
        if (s.contains("hatırlatmaları sil") || s.contains("hatırlatmaları iptal")) {
            cancelAll(ctx)
            say("Bütün hatırlatmaları iptal ettim.")
            return true
        }
        if (!s.contains("hatırlat")) return false
        val p = parse(s, Calendar.getInstance())
        if (p == null) {
            say("Saati anlayamadım. Mesela yarın saat 15.30'da doktor, diye söyle.")
            return true
        }
        val item = try {
            add(ctx, p.time, p.text)
        } catch (e: Exception) {
            if (e is SecurityException) {
                say("Android kesin alarm izni kapalı. Aries'in hatırlatma kurabilmesi için uygulama ayarlarından Alarmlar ve hatırlatmalar iznini aç.")
            } else {
                say("Hatırlatmayı kuramadım: " + (e.message ?: "bilinmeyen hata"))
            }
            return true
        }
        if (upcoming(ctx).none { it.id == item.id }) {
            say("Hatırlatma kaydedilemedi, tekrar dener misin?")
            return true
        }
        val what = if (item.text == "hatırlatma") "" else ": " + item.text
        say("Tamam, " + describe(item.time) + " hatırlatacağım" + what + "." + warnIfDisabled(ctx))
        return true
    }

    // Bildirim izni kapalıysa açıkça söyler; sessizce yutmaz.
    fun warnIfDisabled(ctx: Context): String =
        if (notificationsEnabled(ctx)) "" else
            " Dikkat: Jarvis için bildirimler kapalı, hatırlatma görünmez. Ayarlardan bildirimleri aç."

    fun describe(time: Long): String {
        val now = Calendar.getInstance()
        val c = Calendar.getInstance()
        c.timeInMillis = time
        val hm = SimpleDateFormat("HH:mm", tr).format(c.time)
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val diffDays = ((time - today.timeInMillis) / 86400000L).toInt()
        val mins = (time - now.timeInMillis) / 60000L
        if (mins >= 0 && mins < 60) return mins.toString() + " dakika sonra"
        if (diffDays == 0) return "Bugün saat " + hm
        if (diffDays == 1) return "Yarın saat " + hm
        return SimpleDateFormat("d MMMM EEEE", tr).format(c.time) + " saat " + hm
    }

    // ============================================================
    // Türkçe zaman çözümleyici (küçük harfe çevrilmiş metin bekler)
    // ============================================================

    private const val W = "[a-zçğıöşü]"
    private const val LB = "(?<![a-zçğıöşü0-9])"
    private const val NUMW = "(?:\\d+|(?:(?:on|yirmi|otuz|kırk|elli|altmış)\\s+)?(?:bir|iki|üç|dört|beş|altı|yedi|sekiz|dokuz)|on|yirmi|otuz|kırk|elli|altmış)"
    private const val SUF = "(?:['’]?(?:da|de|ta|te|ya|ye|a|e)(?!" + W + "))?"

    private val REL = Regex(LB + "((" + NUMW + ")(?:\\s*buçu[kğ]" + W + "*)?|yarım|çeyrek)\\s*(dakika|dk|saat|saniye|sn)\\s*(?:sonra|sonrasında|içinde)")
    private val ABS_A = Regex("(?<![0-9])(?:saat\\s*)?(\\d{1,2})\\s*[:.]\\s*(\\d{2})(?![0-9])" + SUF)
    private val ABS_B = Regex(LB + "saat\\s*(" + NUMW + ")(?:\\s*(buçu[kğ]" + W + "*))?" + SUF)
    private val ABS_D = Regex("(?<![0-9:.])(\\d{1,2})['’](?:da|de|ta|te)(?!" + W + ")")
    private val ABS_C = Regex(LB + "(" + NUMW + ")\\s*buçu[kğ]" + W + "*" + SUF)
    private val D_YARIN = Regex(LB + "yarın(?!" + W + ")")
    private val D_BUGUN = Regex(LB + "bugün(?!" + W + ")")
    private val D_OBUR = Regex(LB + "(?:öbür|ertesi)\\s+gün(?!" + W + ")")
    private val D_WEEK = Regex(LB + "(pazartesi|salı|çarşamba|perşembe|cumartesi|cuma|pazar)" + W + "*(?:\\s+günü)?")
    private val P_PM = Regex(LB + "(akşam|öğleden sonra|gece)" + W + "*")
    private val P_AM = Regex(LB + "sabah" + W + "*")

    private val JUNK = listOf(
        Regex(LB + "hatırlat" + W + "*"),
        Regex(LB + "görev\\s+ekle" + W + "*"),
        Regex(LB + "görev" + W + "*"),
        Regex(LB + "bana(?!" + W + ")"),
        Regex(LB + "lütfen(?!" + W + ")"),
        Regex(LB + "ekle(?!" + W + ")"),
        Regex(LB + "saat(?!" + W + ")"),
        Regex(LB + "beni(?!" + W + ")")
    )

    private val UNITS = mapOf(
        "bir" to 1, "iki" to 2, "üç" to 3, "dört" to 4, "beş" to 5,
        "altı" to 6, "yedi" to 7, "sekiz" to 8, "dokuz" to 9
    )
    private val TENS = mapOf(
        "on" to 10, "yirmi" to 20, "otuz" to 30, "kırk" to 40, "elli" to 50, "altmış" to 60
    )
    private val WEEKDAYS = mapOf(
        "pazar" to 1, "pazartesi" to 2, "salı" to 3, "çarşamba" to 4,
        "perşembe" to 5, "cuma" to 6, "cumartesi" to 7
    )

    private fun num(raw: String): Int? {
        val s = raw.trim()
        if (s.isEmpty()) return null
        if (s.all { it.isDigit() }) return s.toIntOrNull()
        var v = 0
        for (t in s.split(Regex("\\s+"))) {
            val u = UNITS[t]
            val d = TENS[t]
            if (u != null) v += u else if (d != null) v += d else return null
        }
        return v
    }

    private fun cut(s: String, m: MatchResult): String =
        s.substring(0, m.range.first) + " " + s.substring(m.range.last + 1)

    fun parse(s: String, now: Calendar): Parsed? {
        var text = s
        var addMinutes: Double? = null
        var h = -1
        var m = 0

        // 1) Göreli süre: "10 dakika sonra", "yarım saat içinde"
        val r = REL.find(s)
        if (r != null) {
            val w = r.groupValues[1]
            val x: Double? = when (w) {
                "yarım" -> 0.5
                "çeyrek" -> 0.25
                else -> num(r.groupValues[2])?.toDouble()?.let { if (w.contains("buçu")) it + 0.5 else it }
            }
            if (x != null) {
                val mult = when (r.groupValues[3]) {
                    "dakika", "dk" -> 1.0
                    "saat" -> 60.0
                    else -> 1.0 / 60.0
                }
                addMinutes = x * mult
                text = cut(text, r)
            }
        }

        // 2) Mutlak saat: "15.30", "saat 9", "yedi buçuk", "15'te"
        if (addMinutes == null) {
            val a = ABS_A.find(s)
            val b = ABS_B.find(s)
            val d = ABS_D.find(s)
            val c = ABS_C.find(s)
            if (a != null && a.groupValues[1].toInt() <= 23 && a.groupValues[2].toInt() <= 59) {
                h = a.groupValues[1].toInt()
                m = a.groupValues[2].toInt()
                text = cut(text, a)
            } else if (b != null && (num(b.groupValues[1]) ?: 99) <= 24) {
                h = num(b.groupValues[1])!! % 24
                m = if (b.groupValues[2].isNotEmpty()) 30 else 0
                text = cut(text, b)
            } else if (d != null && d.groupValues[1].toInt() in 1..24) {
                h = d.groupValues[1].toInt() % 24
                m = 0
                text = cut(text, d)
            } else if (c != null && (num(c.groupValues[1]) ?: 99) in 1..12) {
                h = num(c.groupValues[1])!!
                m = 30
                text = cut(text, c)
            }
        }

        if (addMinutes == null && h < 0) return null

        // 3) Gün: yarın, bugün, öbür gün, haftanın günü
        var dayOffset = -1
        var weekday = -1
        var found = D_YARIN.find(text)
        if (found != null) {
            dayOffset = 1
            text = cut(text, found)
        }
        if (dayOffset < 0) {
            found = D_BUGUN.find(text)
            if (found != null) {
                dayOffset = 0
                text = cut(text, found)
            }
        }
        if (dayOffset < 0) {
            found = D_OBUR.find(text)
            if (found != null) {
                dayOffset = 2
                text = cut(text, found)
            }
        }
        if (dayOffset < 0) {
            val wk = D_WEEK.find(text)
            if (wk != null) {
                weekday = WEEKDAYS[wk.groupValues[1]] ?: -1
                text = cut(text, wk)
            }
        }

        // 4) Öğle / akşam / sabah
        var pm = false
        var explicitPart = false
        val pmMatch = P_PM.find(text)
        if (pmMatch != null) {
            pm = true
            explicitPart = true
            text = cut(text, pmMatch)
        } else {
            val amMatch = P_AM.find(text)
            if (amMatch != null) {
                explicitPart = true
                text = cut(text, amMatch)
            }
        }

        val whenCal = now.clone() as Calendar
        if (addMinutes != null) {
            whenCal.add(Calendar.SECOND, Math.round(addMinutes * 60.0).toInt())
        } else {
            if (pm && h in 1..11) h += 12
            if (pm && h == 12 && pmMatch?.groupValues?.get(1) != "öğleden sonra") h = 0
            whenCal.set(Calendar.SECOND, 0)
            whenCal.set(Calendar.MILLISECOND, 0)
            whenCal.set(Calendar.HOUR_OF_DAY, h)
            whenCal.set(Calendar.MINUTE, m)
            if (dayOffset >= 0) {
                whenCal.add(Calendar.DAY_OF_YEAR, dayOffset)
            } else if (weekday > 0) {
                val diff = (weekday - now.get(Calendar.DAY_OF_WEEK) + 7) % 7
                whenCal.add(Calendar.DAY_OF_YEAR, diff)
                if (whenCal.timeInMillis <= now.timeInMillis) whenCal.add(Calendar.DAY_OF_YEAR, 7)
            } else if (whenCal.timeInMillis <= now.timeInMillis) {
                // Saat geçtiyse: öğleden sonra ihtimali varsa 12 saat ileri al, yoksa yarına.
                val alt = whenCal.clone() as Calendar
                alt.add(Calendar.HOUR_OF_DAY, 12)
                if (!explicitPart && h < 12 && alt.timeInMillis > now.timeInMillis) {
                    whenCal.timeInMillis = alt.timeInMillis
                } else {
                    whenCal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        // 5) Geriye kalan metin = hatırlatma içeriği
        for (j in JUNK) text = j.replace(text, " ")
        text = text.replace(Regex("[:,;.\\-–]+"), " ").replace(Regex("\\s+"), " ").trim()
        if (text.isEmpty()) text = "hatırlatma"
        return Parsed(whenCal.timeInMillis, text)
    }
}

// ============================================================
// Alarm zamanı gelince çalışır: bildirim gösterir, kaydı siler.
// ============================================================
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val id = intent.getIntExtra("id", 0)
        val text = intent.getStringExtra("text") ?: "Hatırlatma"
        Reminders.notifyNow(ctx, id, "Hatırlatma: " + text)
        Reminders.remove(ctx, id)
    }
}

// ============================================================
// Telefon yeniden açılınca ya da uygulama güncellenince hatırlatmaları geri yükler.
// ============================================================
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Reminders.restoreAll(ctx)
    }
}
