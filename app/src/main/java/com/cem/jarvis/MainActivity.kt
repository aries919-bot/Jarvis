package com.cem.jarvis

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.Gravity
import android.text.InputType
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private lateinit var store: Store
    private var tts: TextToSpeech? = null
    private var lastSpoken = ""
    private var lastPos = 0
    private var voiceTurn = false
    private var barge: SpeechRecognizer? = null
    private var ttsReady = false
    private var pendingSay: String? = null
    private var rec: SpeechRecognizer? = null
    private var continuous = false
    private var awaitingCommand = false
    private var listeningNow = false
    private var manualSession = false // DÜZELTİLDİ
    private var thinkingView: View? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private val tr = Locale("tr", "TR")
    private val pages = ArrayList<View>()
    private val navBtns = ArrayList<Button>()
    private val micBtns = ArrayList<Button>()
    private val toggleBtns = HashMap<String, Button>()
    private val keyFields = HashMap<String, EditText>()
    private val NO_NET = "İnternet yok. Bu özellik internet gerektiriyor; yerel komutlar çalışıyor."

    private val PICK_FILE_REQUEST = 1001
    private lateinit var fileListView: LinearLayout

    private val flagDefs = mapOf("heyJarvis" to false, "aiOn" to true, "histOn" to true, "memOn" to true)
    private var pendingFact: String? = null
    private val flagTitles = mapOf(
        "heyJarvis" to "Hey Jarvis",
        "aiOn" to "Yapay zekâ",
        "histOn" to "Sohbet kaydı",
        "memOn" to "Hafıza"
    )

    private val SYS = """
Adın Aries. Türkçe konuşan bir telefon asistanısın. Kullanıcının cümlesini aşağıdaki kalıplardan BİRİne çevir; kalıp uymuyorsa sohbet olarak yanıtla.
Kalıplar (command alanı):
- "<isim> whatsapp <metin>", "<isim> mesaj <metin>", "<isim> ara" (cümlede whatsapp veya mesaj geçiyorsa asla "ara" kalıbını kullanma)
- "müziği durdur", "müziği oynat", "müziği geç", "önceki şarkı", "sesi aç", "sesi kıs", "sesi <0-100> yap"
- "<şarkı adı> çal" (çal, aç, oynat, dinlet, koy, dinleyelim gibi her söylenişi bu kalıba çevir; ad yoksa "müziği oynat"; şarkı için asla "<uygulama adı> aç" yazma)
- "<uygulama>'dan <şarkı adı> çal" (spotify, youtube müzik, deezer gibi bir uygulama adı söylenirse bu kalıbı aynen kullan)
- "<başlık> yarın 15:00 takvime ekle" (takvim, toplantı, randevu; saat yoksa sohbet olarak saati sor)
- İki iş varsa "ve" ile ayır: "07:30 alarm kur ve <şarkı adı> çal"
- Komut gerçekten çalışmadan "oynatılıyor", "kuruldu" gibi sözler yazma
- "<ss>:<dd> alarm kur", "<n> dakika zamanlayıcı", "<n> dakika sonra <metin> hatırlat"
- "youtube <arama metni>", "<uygulama adı> aç"
- "saat kaç", "batarya durumu", "depolama", "görev ekle: <metin>"
- "<yer> yol tarifi", "fener aç", "fener kapat", "kamera aç"
- "<ayar adı> ayarlarını aç" (wifi, bluetooth, ekran, ses, uçak modu, konum)
- "araştır <konu>", "hesapla <ifade>"
Kural: Gerçek dünya bilgisi isteyen her soruda (kişi, yer, sayı, tarih, fiyat, haber, skor, "kaç", "kim", "ne zaman" gibi) command'a "araştır <soru>" yaz. Yalnızca sohbet, fikir, yaratıcı yazı veya genel mantık için reply ile doğrudan cevap ver. Bir işlemi yaptığını asla söyleme; kalıp yoksa "Bunu yapamıyorum." de.
Yanıtı yalnızca şu JSON olarak ver: {"command":"...","reply":"..."}
Kalıpla yapılabilen bir istek varsa command'ı doldur, reply'ı boş bırak. Aksi halde command boş olsun, reply kısa ve doğal Türkçe bir cevap olsun (en fazla 3 cümle). Yıldız, başlık gibi biçimlendirme kullanma; cevap sesli okunacak. Sıcak ve samimi ol.
""".trimIndent()

    private val ABOUT = """
Aries telefonda çalışır. Sohbetler telefonda, hafıza hem telefonda hem Aries Bulut'ta saklanır. Hafıza komutları: hatırla: x (sormadan kaydeder), notlarım, notlarda ara: x, unut: x, unut hepsi, profilim: x. Kart, şifre, kimlik kaydedilmez. gizli: ile başlayan mesaj buluta kaydedilmez.
İnternetsiz: saat, tarih, batarya, depolama, alarm, zamanlayıcı, hatırlatmalar, takvim etkinliği, arama (onaylı), WhatsApp ve SMS taslağı, müzik ve ses, uygulama açma, ayar sayfaları, fener, kamera, hesap makinesi, yol tarifi, görevler, hafıza, dosya yönetimi.
İnternetle: yapay zekâ sohbeti, internet araştırması (kaynaklı), çeviri, haber, borsa.
Not: MIUI pil optimizasyonu nedeniyle uygulama tam kapalıyken 'Hey Jarvis' uykudan uyandırması işletim sistemi tarafından kısıtlanmıştır.
""".trimIndent()

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            getSharedPreferences("j", MODE_PRIVATE).edit()
                .putString("crash", android.util.Log.getStackTraceString(e)).commit()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        val crash = getSharedPreferences("j", MODE_PRIVATE).getString("crash", null)
        if (crash != null) {
            showCrash(crash)
            return
        }

        store = Store(this)
        buildUi()
        renderConv(store.current())
        showPage(0)
        setOrb(0)

        tts = TextToSpeech(this) { status -> onTtsInit(status) }
        val perms = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
        )
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        requestPermissions(perms.toTypedArray(), 1)

        Reminders.restoreAll(this)
        AriesNotifService.gizliSay = { t -> runOnUiThread { sayGizli(t) } }
        refreshStatus()
        refreshTasks()
        refreshReminders()
        refreshDevice()
        sayGizli(greeting())
    }

    override fun onResume() {
        super.onResume()
        try { if (AriesNotifService.instance == null) android.service.notification.NotificationListenerService.requestRebind(android.content.ComponentName(this, AriesNotifService::class.java)) } catch (e: Exception) {}
        if (!::store.isInitialized) return
        refreshStatus()
        refreshTasks()
        refreshReminders()
        if (store.flag("heyJarvis", false) && hasMic()) listen()
    }

    override fun onPause() {
        super.onPause()
        rec?.cancel()
        uiHandler.removeCallbacksAndMessages(null)
        stopBarge()
        listeningNow = false
        setMicUi(false)
        setOrb(0)
        keepScreen(false)
    }

    override fun onDestroy() {
        uiHandler.removeCallbacksAndMessages(null)
        rec?.destroy()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (chatOpen && kbOpen()) { hideKeyboard(); return }
        if (::sidePanel.isInitialized && sidePanel.visibility == View.VISIBLE) {
            closeSide()
            return
        }
        if (chatOpen && pages.isNotEmpty() && pages[0].visibility == View.VISIBLE) {
            closeChat()
            return
        }
        if (pages.isNotEmpty() && pages[0].visibility != View.VISIBLE) {
            showPage(0)
            return
        }
        super.onBackPressed()
    }

    private fun showCrash(msg: String) {
        getSharedPreferences("j", MODE_PRIVATE).edit().remove("crash").commit()
        val sv = ScrollView(this)
        sv.setBackgroundColor(Color.BLACK)
        val tv = TextView(this)
        tv.text = msg
        tv.textSize = 12f
        tv.setTextColor(Color.WHITE)
        tv.setPadding(30, 80, 30, 30)
        tv.setTextIsSelectable(true)
        sv.addView(tv)
        setContentView(sv)
    }

    private lateinit var holo: HoloView
    private lateinit var wave: WaveView
    private lateinit var holoV: HoloView
    private lateinit var waveV: WaveView
    private lateinit var orbStatus: TextView
    private lateinit var voiceStatus: TextView
    private lateinit var clockTv: TextView
    private lateinit var dateTv: TextView
    private lateinit var chatBox: LinearLayout
    private lateinit var chatScroll: ScrollView
    private lateinit var composer: EditText
    private lateinit var voiceText: TextView
    private lateinit var contBtn: Button
    private lateinit var wakeBtn: Button
    private lateinit var scrim: View
    private lateinit var sidePanel: LinearLayout
    private lateinit var sideList: LinearLayout
    private lateinit var taskBox: LinearLayout
    private lateinit var assistInput: EditText
    private lateinit var reminderBox: TextView
    private lateinit var deviceView: TextView
    private lateinit var newsView: TextView
    private lateinit var stockView: TextView
    private lateinit var testOut: TextView
    private lateinit var orderBtn: Button
    private lateinit var homeRoot: View
    private lateinit var homeMid: FrameLayout
    private lateinit var homeMic: FrameLayout
    private lateinit var speakBtn: FrameLayout
    private var chatOpen = false
    private var lastKb = false
    private var muted = false
    private var orbState = 0
    private val ORB_DP = 300
    private val ORB_MIN = 0.18f

    private fun buildUi() {
        val root = FrameLayout(this)
        root.setBackgroundColor(Color.parseColor(Pal.BG))

        val main = Ui.column(this)
        main.setPadding(dp(18), dp(40), dp(18), dp(10))
        val frame = FrameLayout(this)
        pages.add(buildHome())
        pages.add(buildVoice())
        pages.add(buildAssist())
        pages.add(buildFilesPage())
        pages.add(buildSettings())
        for (p in pages) frame.addView(p, FrameLayout.LayoutParams(-1, -1))
        main.addView(frame, LinearLayout.LayoutParams(-1, 0, 1f))

        root.addView(main, FrameLayout.LayoutParams(-1, -1))

        scrim = View(this)
        scrim.setBackgroundColor(Color.parseColor("#AA000000"))
        scrim.visibility = View.GONE
        scrim.setOnClickListener { closeSide() }
        root.addView(scrim, FrameLayout.LayoutParams(-1, -1))

        sidePanel = buildSidebar()
        sidePanel.visibility = View.GONE
        val w = resources.displayMetrics.widthPixels * 8 / 10
        root.addView(sidePanel, FrameLayout.LayoutParams(w, -1))
        setContentView(root)
    }

    private fun showPage(i: Int) {
        for (k in pages.indices) {
            pages[k].visibility = if (k == i) View.VISIBLE else View.GONE
            if (k < navBtns.size) navBtns[k].setTextColor(Color.parseColor(if (k == i) Pal.WHITE else Pal.MUTED))
        }
        if (i == 0) refreshStatus()
        if (i == 2) {
            refreshTasks()
            refreshReminders()
            refreshDevice()
        }
        if (i == 3) loadInternalFileList()
        if (i == 4) refreshToggles()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun add(col: LinearLayout, v: View) {
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.topMargin = dp(12)
        col.addView(v, lp)
    }

    private fun scrollOf(v: View): ScrollView {
        val s = ScrollView(this)
        s.addView(v)
        return s
    }

    private fun chip(text: String, onClick: () -> Unit): Button {
        val b = Ui.button(this, text, onClick)
        b.textSize = 12f
        val lp = LinearLayout.LayoutParams(-2, -2)
        lp.setMargins(dp(3), 0, dp(3), 0)
        b.layoutParams = lp
        return b
    }

    private fun buildHome(): View {
        val rtx = intent?.getStringExtra("remind_text"); if (!rtx.isNullOrEmpty()) { intent?.removeExtra("remind_text"); window.decorView.postDelayed({ say("Hatırlatma: " + rtx) }, 1000) }
        val col = Ui.column(this)
        col.isFocusable = true
        col.isFocusableInTouchMode = true
        homeRoot = col

        // Üst çubuk: menü, başlık + durum, bluetooth, ayarlar
        val top = Ui.row(this)
        top.addView(Ui.iconBtn(this, "jmenu", "Menü", 48, 24, Pal.TEXT) { openSide() })
        val titleCol = Ui.column(this)
        val title = Ui.text(this, "A.R.I.E.S", 22f, Pal.WHITE, true)
        title.setTypeface(title.typeface, android.graphics.Typeface.BOLD)
        title.letterSpacing = 0.12f
        title.gravity = Gravity.CENTER_HORIZONTAL
        titleCol.addView(title, LinearLayout.LayoutParams(-1, -2))
        orbStatus = Ui.text(this, "Hazır", 12f, Pal.MUTED)
        orbStatus.gravity = Gravity.CENTER_HORIZONTAL
        titleCol.addView(orbStatus, LinearLayout.LayoutParams(-1, -2))
        top.addView(titleCol, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(Ui.iconBtn(this, "jbt", "Bluetooth", 48, 22, Pal.TEXT) {
            showOutputPicker()
        })
        val filmBtn = Ui.text(this, "▶", 20f, Pal.TEXT)
        filmBtn.gravity = Gravity.CENTER
        filmBtn.contentDescription = "Film ve Dizi"
        filmBtn.setOnClickListener { startActivity(android.content.Intent(this, com.cem.jarvis.media.FilmDiziActivity::class.java)) }
        top.addView(filmBtn, LinearLayout.LayoutParams(dp(48), dp(48)))
        top.addView(Ui.iconBtn(this, "jgear", "Ayarlar", 48, 22, Pal.TEXT) { showPage(4) })
        col.addView(top, LinearLayout.LayoutParams(-1, -2))

        // Ekranda gösterilmeyen ama kodun başka yerlerinin kullandığı görünümler
        clockTv = Ui.text(this, "", 18f)
        dateTv = Ui.text(this, "", Tok.T_CAPTION, Pal.MUTED)
        wave = WaveView(this)

        // Orta alan: sohbet (gizli) + küre
        homeMid = FrameLayout(this)
        chatBox = Ui.column(this)
        chatScroll = ScrollView(this)
        chatScroll.addView(chatBox)
        chatScroll.descendantFocusability = android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS
        chatScroll.visibility = View.GONE
        chatScroll.alpha = 0f
        homeMid.addView(chatScroll, FrameLayout.LayoutParams(-1, -1))
        holo = HoloView(this)
        homeMid.addView(holo, FrameLayout.LayoutParams(dp(ORB_DP), dp(ORB_DP), Gravity.CENTER))
        homeMid.addOnLayoutChangeListener { _, _, tp, _, bt, _, otp, _, obt ->
            if (chatOpen && (bt - tp) != (obt - otp)) holo.post {
                val kb = kbOpen()
                lastKb = kb; placeOrb()
            }
        }
        attachOrbGesture()
        val greetBox = Ui.column(this)
        greetBox.gravity = Gravity.CENTER_HORIZONTAL
        val greetMain = Ui.text(this, greeting().removeSuffix("."), 20f, Pal.WHITE)
        greetMain.gravity = Gravity.CENTER_HORIZONTAL
        greetMain.setTypeface(greetMain.typeface, android.graphics.Typeface.BOLD)
        val greetHint = Ui.text(this, "Sabah özetini iste", 12f, Pal.MUTED)
        greetHint.gravity = Gravity.CENTER_HORIZONTAL
        greetBox.addView(greetMain)
        greetBox.addView(greetHint)
        homeMid.addView(greetBox, FrameLayout.LayoutParams(-1, -2, Gravity.CENTER))
        homeGreet = greetBox
        greetBox.post {
            var ty = holo.top + holo.height + dp(12) - greetBox.top
            val panelTop = homePanel?.top ?: homeMid.height
            val maxTy = panelTop - dp(8) - greetBox.height - greetBox.top
            if (ty > maxTy) ty = maxTy
            greetBox.translationY = ty.toFloat()
        }
        val chatStat = Ui.text(this, "", 12f, Pal.MUTED)
        chatStat.gravity = Gravity.CENTER_HORIZONTAL
        val csLp = FrameLayout.LayoutParams(-1, -2, Gravity.TOP or Gravity.CENTER_HORIZONTAL)
        csLp.topMargin = dp((ORB_DP * ORB_MIN).toInt() + 8)
        homeMid.addView(chatStat, csLp)
        chatStat.visibility = View.GONE
        chatOrbStatus = chatStat
        val panel = android.widget.LinearLayout(this)
        panel.orientation = android.widget.LinearLayout.VERTICAL
        panel.gravity = Gravity.CENTER_HORIZONTAL
        val pc = android.graphics.Color.parseColor(Pal.CARD2); val pd = android.graphics.Color.rgb((android.graphics.Color.red(pc) * 0.6f).toInt(), (android.graphics.Color.green(pc) * 0.6f).toInt(), (android.graphics.Color.blue(pc) * 0.6f).toInt()); val pbg = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(pc, pd)); pbg.cornerRadius = dp(20).toFloat(); pbg.setStroke(dp(1), android.graphics.Color.parseColor(Pal.LINE))
        panel.background = pbg
        panel.setPadding(dp(24), dp(14), dp(24), dp(14)); panel.elevation = dp(10).toFloat()
        val pt1 = android.widget.TextView(this); pt1.textSize = 20f; pt1.setTextColor(android.graphics.Color.parseColor(Pal.WHITE)); pt1.gravity = Gravity.CENTER_HORIZONTAL; pt1.maxLines = 2
        val pt2 = android.widget.TextView(this); pt2.textSize = 12f; pt2.setTextColor(android.graphics.Color.parseColor(Pal.MUTED)); pt2.gravity = Gravity.CENTER_HORIZONTAL
        panel.addView(pt1); panel.addView(pt2)
        val tr = java.util.Locale("tr", "TR")
        val upd = object : Runnable { override fun run() {
            try {
                val up = Reminders.upcoming(this@MainActivity)
                if (up.isNotEmpty()) {
                    val r0 = up[0]; val mins = (r0.time - System.currentTimeMillis()) / 60000L
                    val rel = if (mins < 60) mins.toString() + " dk sonra" else if (mins < 1440) (mins / 60).toString() + " sa " + (mins % 60).toString() + " dk sonra" else java.text.SimpleDateFormat("d MMM HH:mm", tr).format(java.util.Date(r0.time))
                    pt1.text = r0.text
                    pt2.text = rel + (if (up.size > 1) " · +" + (up.size - 1).toString() + " hatırlatma daha" else "")
                } else {
                    pt1.text = java.text.SimpleDateFormat("HH:mm", tr).format(java.util.Date())
                    pt2.text = java.text.SimpleDateFormat("d MMMM EEEE", tr).format(java.util.Date())
                }
            } catch (e: Exception) { }
            if (panel.isAttachedToWindow) panel.postDelayed(this, 30000)
        } }
        panel.setOnLongClickListener { openChat(); AriesNotifService.oku(this) { t -> say(t) }; true }
        panel.setOnClickListener {
            openChat()
            val up = Reminders.upcoming(this)
            say(if (up.isEmpty()) "Yaklaşan hatırlatma yok." else "Yaklaşan hatırlatmalar:\n" + up.take(8).joinToString("\n") { x -> java.text.SimpleDateFormat("d MMM HH:mm", tr).format(java.util.Date(x.time)) + " - " + x.text })
        }
        val plp = FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL); plp.bottomMargin = dp(14)
        homeMid.addView(panel, plp)
        homePanel = panel
        panel.post(upd)
        col.addView(homeMid, LinearLayout.LayoutParams(-1, 0, 1f))

        // Alt sıra: artı, yazı kutusu, hoparlör, mikrofon / gönder
        val bar = Ui.row(this)
        bar.setPadding(0, dp(8), 0, 0)
        bar.addView(Ui.iconBtn(this, "jplus", "Ekle", 48, 22, Pal.TEXT, Pal.CARD2, Pal.LINE) { plusMenu() })
        composer = Ui.input(this, "Aries'e yaz...")
        composer.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        composer.setHorizontallyScrolling(false)
        composer.minLines = 1
        composer.maxLines = 8
        composer.imeOptions = EditorInfo.IME_ACTION_SEND
        composer.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEND) { sendComposer(); true } else false
        }
        composer.setOnFocusChangeListener { _, has -> if (has) openChat() }
        composer.setOnClickListener { openChat() }
        composer.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) { refreshHomeMic() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        val clp = LinearLayout.LayoutParams(0, -2, 1f)
        clp.setMargins(dp(8), 0, dp(8), 0)
        bar.addView(composer, clp)
        speakBtn = Ui.iconBtn(this, "jspeaker", "Ses", 48, 22, Pal.TEXT, Pal.CARD2, Pal.LINE) { speakerTap() }
        bar.addView(speakBtn)
        homeMic = Ui.iconBtn(this, "jmic", "Mikrofon", 48, 22, "#000000", Pal.WHITE, null) { micOrSend() }
        val mlp = LinearLayout.LayoutParams(dp(48), dp(48))
        mlp.leftMargin = dp(8)
        homeMic.layoutParams = mlp
        bar.addView(homeMic)
        col.addView(bar, LinearLayout.LayoutParams(-1, -2))
        return col
    }

    // Küreyi aşağı çekince sohbet açılır, yukarı itince kapanır.
    private var homePanel: View? = null
    private var homeGreet: View? = null
    private var chatOrbStatus: TextView? = null

    private fun attachOrbGesture() {
        var downY = 0f
        holo.setOnTouchListener { v, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downY = e.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = e.rawY - downY
                    if (!chatOpen && dy > 0f) holo.translationY = minOf(dy * 0.5f, dp(160).toFloat())
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val dy = e.rawY - downY
                    if (!chatOpen) {
                        if (dy > dp(70)) openChat()
                        else holo.animate().translationY(0f).setDuration(180).start()
                    } else if (dy < -dp(40)) {
                        closeChat()
                    }
                    if (e.actionMasked == MotionEvent.ACTION_UP && Math.abs(dy) < dp(8)) v.performClick()
                    true
                }
                else -> true
            }
        }
    }

    private fun openChat() {
        if (chatOpen || !::homeMid.isInitialized) return
        chatOpen = true
        val clp = chatScroll.layoutParams as FrameLayout.LayoutParams
        clp.bottomMargin = 0
        clp.topMargin = dp((ORB_DP * ORB_MIN).toInt() + 32)
        chatScroll.requestLayout()
        chatScroll.visibility = View.VISIBLE
        chatScroll.bringToFront()
        chatScroll.animate().alpha(1f).setDuration(220).start()
        homePanel?.visibility = View.GONE
        homeGreet?.visibility = View.GONE
        holo.visibility = View.VISIBLE
        holo.bringToFront()
        chatOrbStatus?.visibility = View.VISIBLE
        chatOrbStatus?.bringToFront()
        placeOrb(true)
    }

    private fun closeChat() {
        if (!chatOpen || !::homeMid.isInitialized) return
        chatOpen = false
        hideKeyboard()
        homePanel?.visibility = View.VISIBLE
        homeGreet?.visibility = View.VISIBLE
        chatScroll.animate().alpha(0f).setDuration(180).withEndAction {
            if (!chatOpen) chatScroll.visibility = View.GONE
        }.start()
        val clp = chatScroll.layoutParams as FrameLayout.LayoutParams
        clp.topMargin = 0
        chatScroll.requestLayout()
        chatOrbStatus?.visibility = View.GONE
        holo.visibility = View.VISIBLE
        holo.bringToFront()
        holo.dotScale = 1f
        holo.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(0f).setDuration(260).start()
    }

    private fun kbOpen(): Boolean {
        val r = android.graphics.Rect()
        window.decorView.getWindowVisibleDisplayFrame(r)
        val full = window.decorView.rootView.height
        return full > 0 && full - r.bottom > full * 0.15
    }

    override fun onNewIntent(intent: Intent?) { super.onNewIntent(intent); setIntent(intent); val rtx = intent?.getStringExtra("remind_text"); if (!rtx.isNullOrEmpty()) { intent?.removeExtra("remind_text"); say("Hatırlatma: " + rtx) } }

    private fun placeOrb(animate: Boolean = false) {
        if (!chatOpen || !::homeMid.isInitialized) return
        chatScroll.post { chatScroll.scrollTo(0, chatBox.height) }
        holo.post {
            if (!chatOpen) return@post
            val mini = ORB_MIN
            holo.pivotX = holo.width / 2f
            holo.pivotY = holo.height / 2f
            val cy = holo.top + holo.height / 2f
            val ty = dp(4) - (cy - holo.width * mini / 2f)
            if (animate) {
                holo.animate().alpha(1f).scaleX(mini).scaleY(mini).translationY(ty).setDuration(260).start()
            } else {
                holo.animate().cancel()
                holo.alpha = 1f
                holo.scaleX = mini
                holo.scaleY = mini
                holo.translationY = ty
            }
        }
    }

    @android.annotation.TargetApi(31)
    private fun showOutputPicker() {
        val am = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 7)
            say("Bluetooth izni ver, sonra düğmeye tekrar bas.")
            return
        }
        val names = ArrayList<String>()
        val acts = ArrayList<() -> Unit>()
        names.add("Telefon hoparlörü")
        if (sdk >= 31) {
            val devs = am.availableCommunicationDevices
            val spk = devs.firstOrNull { it.type == android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
            acts.add { if (spk != null && am.setCommunicationDevice(spk)) say("Ses telefon hoparlörüne geçti.") else say("Hoparlöre geçilemedi.") }
            val bts = devs.filter { it.type == android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO || it.type == android.media.AudioDeviceInfo.TYPE_BLE_HEADSET || it.type == android.media.AudioDeviceInfo.TYPE_BLE_SPEAKER }
            if (bts.isEmpty()) { names.add("Bluetooth bağlı değil"); acts.add { say("Bağlı Bluetooth cihazı yok.") } }
            for (d in bts) {
                names.add("Bluetooth: " + d.productName)
                acts.add { if (am.setCommunicationDevice(d)) say("Ses Bluetooth'a geçti: " + d.productName + ".") else say("Bluetooth'a geçilemedi.") }
            }
        } else {
            acts.add { am.isSpeakerphoneOn = true; say("Ses telefon hoparlörüne geçti.") }
            names.add("Bluetooth")
            acts.add { am.isSpeakerphoneOn = false; say("Ses Bluetooth'a döndü.") }
        }
        if (sdk >= 31) { names.add("Varsayılana dön (Bluetooth)"); acts.add { am.clearCommunicationDevice(); say("Ses varsayılan çıkışa döndü.") } }
        names.add("Bluetooth ayarlarını aç")
        acts.add { safeStart(Intent(Settings.ACTION_BLUETOOTH_SETTINGS), "Bluetooth ayarları açılamadı.") }
        val box = android.widget.LinearLayout(this); box.orientation = android.widget.LinearLayout.VERTICAL; box.background = Ui.rounded(Pal.CARD, Pal.LINE, dp(Tok.R_CARD + 6).toFloat()); box.setPadding(dp(22), dp(18), dp(22), dp(10))
        val dlg = android.app.AlertDialog.Builder(this).setView(box).create()
        val ttl = android.widget.TextView(this); ttl.text = "Ses çıkışı"; ttl.setTextColor(android.graphics.Color.parseColor(Pal.WHITE)); ttl.textSize = Tok.T_TITLE; ttl.setPadding(0, 0, 0, dp(6)); box.addView(ttl)
        for (i in names.indices) { val tv = android.widget.TextView(this); tv.text = names[i]; tv.setTextColor(android.graphics.Color.parseColor(Pal.TEXT)); tv.textSize = 16f; tv.minHeight = dp(Tok.MIN_TOUCH_DP); tv.gravity = Gravity.CENTER_VERTICAL; tv.setPadding(0, dp(10), 0, dp(10)); tv.setOnClickListener { dlg.dismiss(); acts[i]() }; box.addView(tv) }
        dlg.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)); dlg.show()
    }

    private fun hideKeyboard() {
        if (!::composer.isInitialized) return
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(composer.windowToken, 0)
        composer.clearFocus()
        if (::homeRoot.isInitialized) homeRoot.requestFocus()
    }

    private fun plusMenu() {
        AlertDialog.Builder(this)
            .setItems(arrayOf("Dosya ekle", "Yeni sohbet", "Hatırlatıcılar ve görevler")) { _, i ->
                when (i) {
                    0 -> showPage(3)
                    1 -> newChat()
                    else -> showPage(2)
                }
            }
            .show()
    }

    // Mikrofon düğmesi: yazı varsa gönderir, yoksa sesli sohbeti açar/kapatır.
    private fun micOrSend() {
        if (composer.text.toString().isNotBlank()) {
            sendComposer()
            return
        }
        if (orbState == 2) { tts?.stop(); setOrb(0) } else if (listeningNow) rec?.stopListening() else if (continuous) stopVoice() else { closeChat(); startVoice() }
    }

    private fun startVoice() {
        if (!hasMic()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }
        continuous = false
        refreshToggles()
        refreshHomeMic()
        listen(true)
    }

    private fun stopVoice() {
        continuous = false
        awaitingCommand = false
        uiHandler.removeCallbacksAndMessages(null)
        rec?.cancel()
        listeningNow = false
        tts?.stop()
        setMicUi(false)
        setOrb(0)
        refreshToggles()
    }

    // Hoparlör düğmesi: konuşurken çarpıdır ve hemen susturur; değilken sesli cevabı açıp kapatır.
    private fun speakerTap() {
        if (orbState == 2) {
            tts?.stop()
            setOrb(if (listeningNow) 1 else 0)
            if (loopActive()) restartLater(300)
        } else {
            muted = !muted
            setOrb(orbState)
        }
    }

    private fun refreshHomeMic() {
        if (!::homeMic.isInitialized) return
        val hasText = ::composer.isInitialized && composer.text.toString().isNotBlank()
        Ui.swapIcon(homeMic, if (hasText) "jsend" else if (continuous) "jstop" else "jmic", "#000000")
    }

    private fun pulseOrb(x: Float) {
        if (::holo.isInitialized && x > holo.level) holo.level = x
        if (::holoV.isInitialized && x > holoV.level) holoV.level = x
    }

    private fun prefill(text: String) {
        composer.setText(text)
        composer.setSelection(text.length)
        composer.requestFocus()
    }

    private fun sendComposer() {
        val t = composer.text.toString().trim()
        if (t.isEmpty()) return
        composer.setText("")
        openChat()
        handle(t)
    }

    private fun buildVoice(): View {
        val col = Ui.column(this)
        holoV = HoloView(this)
        col.addView(holoV, LinearLayout.LayoutParams(-1, dp(230)))
        waveV = WaveView(this)
        col.addView(waveV, LinearLayout.LayoutParams(-1, dp(50)))
        voiceStatus = Ui.text(this, "Hazır", 15f, Pal.MUTED)
        voiceStatus.gravity = Gravity.CENTER_HORIZONTAL
        add(col, voiceStatus)
        voiceText = Ui.card(this, "Son söylenen: -")
        add(col, voiceText)

        val mic = Ui.button(this, "🎤  Konuş") { micTap() }
        mic.tag = "🎤  Konuş"
        mic.textSize = 18f
        micBtns.add(mic)
        add(col, mic)

        val r = Ui.row(this)
        contBtn = Ui.button(this, contLabel()) { toggleContinuous() }
        wakeBtn = Ui.button(this, flagLabel("heyJarvis")) { setWake(!store.flag("heyJarvis", false)) }
        r.addView(contBtn, LinearLayout.LayoutParams(0, -2, 1f))
        r.addView(wakeBtn, LinearLayout.LayoutParams(0, -2, 1f))
        add(col, r)

        add(col, Ui.button(this, "Durdur (ses ve dinleme)") { stopAll() })
        add(col, Ui.card(
            this,
            "Örnekler: saat kaç, 5 artı 3 çarpı 2 kaç eder, Ayşe ara, Ayşe whatsapp geç kalacağım, yarın saat 9'da ilaç hatırlat, Kızılay yol tarifi, fener aç, wifi ayarlarını aç, araştır: ..., İngilizceye çevir: ..., hatırla: ..."
        ))
        return scrollOf(col)
    }

    private fun buildAssist(): View {
        val col = Ui.column(this)
        add(col, Ui.text(this, "Asistan", 20f, Pal.WHITE))

        add(col, Ui.text(this, "Cihaz", 14f, Pal.MUTED))
        deviceView = Ui.card(this, "")
        add(col, deviceView)

        add(col, Ui.text(this, "Görevler", 14f, Pal.MUTED))
        taskBox = Ui.column(this)
        add(col, taskBox)
        val row = Ui.row(this)
        assistInput = Ui.input(this, "Yeni görev yaz...")
        row.addView(assistInput, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(Ui.button(this, "Ekle") { addTaskFromUi() })
        add(col, row)
        add(col, Ui.button(this, "Tamamlananları temizle") {
            confirm("Tamamlanan görevler silinsin mi?", { removeDoneTasks() }, { })
        })

        add(col, Ui.text(this, "Hatırlatmalar", 14f, Pal.MUTED))
        reminderBox = Ui.card(this, "")
        add(col, reminderBox)
        add(col, Ui.button(this, "Bütün hatırlatmaları iptal et") {
            confirm("Bütün hatırlatmalar iptal edilsin mi?", {
                Reminders.cancelAll(this)
                refreshReminders()
                say("Hatırlatmaları iptal ettim.")
            }, { })
        })

        add(col, Ui.text(this, "Haberler", 14f, Pal.MUTED))
        newsView = Ui.card(this, "Henüz haber çekilmedi.")
        add(col, newsView)
        add(col, Ui.button(this, "Haberleri yenile") {
            if (isOnline()) fetchNews(newsTopic(), false) else setNewsText(NO_NET)
        })

        add(col, Ui.text(this, "Borsa", 14f, Pal.MUTED))
        stockView = Ui.card(this, "İzlenen hisse yok.")
        add(col, stockView)
        add(col, Ui.button(this, "Fiyatları yenile") {
            if (isOnline()) fetchWatchlist(false) else setStockText(NO_NET)
        })
        return scrollOf(col)
    }

    private fun buildFilesPage(): View {
        val col = Ui.column(this)
        add(col, Ui.text(this, "Dosyalar ve Belgeler", 20f, Pal.WHITE))
        
        val row = Ui.row(this)
        row.addView(Ui.button(this, "+ Dosya Seç ve Kaydet") { openAndroidFilePicker() }, LinearLayout.LayoutParams(-1, -2))
        add(col, row)

        add(col, Ui.text(this, "Kayıtlı Dosyalarım", 14f, Pal.MUTED))
        fileListView = Ui.column(this)
        add(col, fileListView)
        return scrollOf(col)
    }

    private fun openAndroidFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        try {
            startActivityForResult(intent, PICK_FILE_REQUEST)
        } catch (e: Exception) {
            say("Dosya seçici açılamadı.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    val fileName = uri.lastPathSegment ?: "Dosya"
                    saveFileRecord(fileName, uri.toString())
                    loadInternalFileList()
                    say("Dosya eklendi: $fileName")
                } catch (e: Exception) {
                    say("Dosya izin hatası.")
                }
            }
        }
    }

    private fun saveFileRecord(name: String, uriStr: String) {
        val arr = JSONArray(store.str("app_files", "[]"))
        arr.put(JSONObject().put("name", name).put("uri", uriStr))
        store.put("app_files", arr.toString())
    }

    private fun loadInternalFileList() {
        if (!::fileListView.isInitialized) return
        fileListView.removeAllViews()
        val arr = JSONArray(store.str("app_files", "[]"))
        if (arr.length() == 0) {
            fileListView.addView(Ui.text(this, "Henüz kayıtlı dosya yok.", 13f, Pal.MUTED))
            return
        }
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val name = o.getString("name")
            val uriStr = o.getString("uri")

            val row = Ui.row(this)
            val openBtn = Ui.button(this, "📄 $name") {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)))
                } catch (e: Exception) {
                    say("Dosya açılamadı.")
                }
            }
            val delBtn = Ui.button(this, "Sil") {
                deleteFileRecord(i)
            }
            row.addView(openBtn, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(delBtn, LinearLayout.LayoutParams(-2, -2))
            add(fileListView, row)
        }
    }

    private fun deleteFileRecord(index: Int) {
        val arr = JSONArray(store.str("app_files", "[]"))
        val out = JSONArray()
        for (i in 0 until arr.length()) {
            if (i != index) out.put(arr.getJSONObject(i))
        }
        store.put("app_files", out.toString())
        loadInternalFileList()
    }

    private fun buildSettings(): View {
        val col = Ui.column(this)
        add(col, Ui.text(this, "Ayarlar", 20f, Pal.WHITE))

        add(col, Ui.text(this, "API Anahtarları", 14f, Pal.MUTED))
        keyField(col, "Aries Bulut Anahtarı", "bulut")
        add(col, Ui.button(this, "Kaydet") { saveSettings() })

        add(col, Ui.text(this, "Bildirim erişimi", 14f, Pal.MUTED))
        add(col, Ui.button(this, "Bildirim erişimi ver") { safeStart(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), "Bildirim erişimi ekranı açılamadı.") })

        add(col, Ui.text(this, "Hakkında", 14f, Pal.MUTED))
        add(col, Ui.card(this, ABOUT))
        return scrollOf(col)
    }

    private fun renderFacts(box: LinearLayout) {
        box.removeAllViews()
        val list = store.facts().split("; ").map { it.trim() }.filter { it.isNotEmpty() }
        if (list.isEmpty()) {
            add(box, Ui.card(this, "Kayıtlı not yok."))
            return
        }
        for ((i, f) in list.withIndex()) {
            add(box, Ui.button(this, f + "  ✕") {
                confirm("Silinsin mi: '" + f + "'?", {
                    store.put("facts", list.filterIndexed { j, _ -> j != i }.joinToString("; "))
                    renderFacts(box)
                }, {})
            })
        }
    }

    private fun keyField(col: LinearLayout, hintText: String, prefKey: String) {
        val e = Ui.input(this, hintText)
        e.setSingleLine()
        e.setText(store.str(prefKey))
        e.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        keyFields[prefKey] = e
        val row = Ui.row(this)
        row.addView(e, LinearLayout.LayoutParams(0, -2, 1f))
        var shown = false
        val eye = Ui.button(this, "Göster") { }
        eye.setOnClickListener {
            shown = !shown
            e.transformationMethod = if (shown) null else android.text.method.PasswordTransformationMethod.getInstance()
            e.setSelection(e.text.length)
            eye.text = if (shown) "Gizle" else "Göster"
        }
        row.addView(eye, LinearLayout.LayoutParams(-2, -2))
        add(col, row)
    }

    private fun saveSettings() {
        for ((k, v) in keyFields) store.put(k, v.text.toString().trim())
        say("Ayarlar kaydedildi.")
    }

    private fun flagLabel(flag: String): String =
        (flagTitles[flag] ?: flag) + ": " + (if (store.flag(flag, flagDefs[flag] ?: true)) "AÇIK" else "KAPALI")

    private fun flagButton(flag: String): Button {
        val b = Ui.button(this, flagLabel(flag)) {}
        b.setOnClickListener {
            val now = !store.flag(flag, flagDefs[flag] ?: true)
            if (flag == "heyJarvis") {
                setWake(now)
            } else {
                store.setFlag(flag, now)
                refreshToggles()
            }
        }
        toggleBtns[flag] = b
        return b
    }

    private fun refreshToggles() {
        if (::contBtn.isInitialized) contBtn.text = contLabel()
        if (::wakeBtn.isInitialized) wakeBtn.text = flagLabel("heyJarvis")
        for ((flag, b) in toggleBtns) b.text = flagLabel(flag)
        if (::orderBtn.isInitialized) orderBtn.text = "Yapay zekâ sırası: " + orderLabel()
    }

    private fun newsTopic(): String = store.str("news", "Türkiye").ifBlank { "Türkiye" }

    private fun buildSidebar(): LinearLayout {
        val p = Ui.column(this)
        p.setBackgroundColor(Color.parseColor(Pal.CARD))
        p.setPadding(dp(18), dp(44), dp(18), dp(18))
        val navPairs = listOf(listOf("Ayarlar" to 4))
        for (pair in navPairs) {
            val r = Ui.row(this)
            for ((name, idx) in pair) {
                val nb = Ui.button(this, name) { closeSide(); showPage(idx) }
                val nlp = LinearLayout.LayoutParams(0, -2, 1f)
                nlp.setMargins(dp(2), dp(2), dp(2), dp(2))
                r.addView(nb, nlp)
            }
            p.addView(r, LinearLayout.LayoutParams(-1, -2))
        }
        add(p, Ui.button(this, "🎙  Sesli Asistan") {
            closeSide()
            showPage(1)
        })
        add(p, Ui.button(this, "💼  İş ve Kariyer") {
            closeSide()
            startActivity(Intent(this, CareerActivity::class.java))
        })
        val ttl = Ui.text(this, "Sohbetler", 20f, Pal.WHITE)
        ttl.setPadding(0, dp(14), 0, 0)
        p.addView(ttl)
        add(p, Ui.button(this, "+ Yeni sohbet") { newChat() })
        val sc = ScrollView(this)
        sideList = Ui.column(this)
        sc.addView(sideList)
        p.addView(sc, LinearLayout.LayoutParams(-1, 0, 1f))
        add(p, Ui.button(this, "Panoya kopyala") { exportConv() })
        return p
    }

    private fun openSide() {
        refreshSideList()
        scrim.visibility = View.VISIBLE
        sidePanel.visibility = View.VISIBLE
    }

    private fun closeSide() {
        scrim.visibility = View.GONE
        sidePanel.visibility = View.GONE
    }

    private fun refreshSideList() {
        sideList.removeAllViews()
        val current = store.current().id
        for (c in store.convs()) {
            val date = SimpleDateFormat("d MMM HH:mm", tr).format(Date(c.updated))
            val marker = if (c.id == current) "● " else ""
            val b = Ui.button(this, marker + c.title + "\n" + date) { openConv(c) }
            b.setOnLongClickListener {
                confirm("\"${c.title}\" silinsin mi?", {
                    store.deleteConv(c.id)
                    if (c.id == current) newChat() else refreshSideList()
                }, {})
                true
            }
            val lp = LinearLayout.LayoutParams(-1, -2)
            lp.topMargin = dp(8)
            sideList.addView(b, lp)
        }
    }

    private fun newChat() {
        val c = store.newConv()
        store.setCurrent(c)
        renderConv(c)
        closeSide()
        showPage(0)
    }

    private fun openConv(c: Conv) {
        store.setCurrent(c)
        renderConv(c)
        closeSide()
        showPage(0)
    }

    private fun exportConv() {
        val c = store.current()
        val text = c.msgs.joinToString("\n\n") { (r, t) ->
            (if (r == "user") "Sen" else "Aries") + ": " + t
        }
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("jarvis-sohbet", text))
        say("Sohbet panoya kopyalandı.")
    }

    private fun renderConv(c: Conv) {
        if (!::chatBox.isInitialized) return
        chatBox.removeAllViews()
        val lastAries = c.msgs.indexOfLast { it.first != "user" }
        for ((i, m) in c.msgs.withIndex()) {
            val (r, t) = m
            chatBox.addView(Ui.line(this, labelOf(r), t, emptyList(), { speakNow(t) }, { v -> oyGonder(v, t) }, emptyList(), null, i == lastAries))
        }
        chatScroll.post { chatScroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun oyGonder(v: Int, t: String) {
        val tk = store.str("bulut")
        if (tk.isBlank()) return
        Thread { Ai.oyVer(tk, v, t) }.start()
    }

    private fun labelOf(role: String): String = if (role == "user") "Sen" else "J"

    private fun addBubble(who: String, text: String, srcs: List<Pair<String, String>> = emptyList(), ai: Boolean = false) {
        if (!::chatBox.isInitialized) return
        clearSuggest()
        val aries = who == "J" && text != "Düşünüyorum…"
        val oneri = aries && ai
        val onSuggest: ((String) -> Unit)? = if (oneri) { s -> suggestTap(s) } else null
        chatBox.addView(Ui.line(this, who, text, srcs, { speakNow(text) }, { v -> oyGonder(v, text) },
            if (oneri) listOf("Başka bir fikir", "Daha kısa anlat") else emptyList(), onSuggest, aries))
        chatScroll.post { chatScroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun clearSuggest() {
        if (!::chatBox.isInitialized) return
        for (tag in listOf("oneri", "aksiyon")) {
            while (true) {
                val old = chatBox.findViewWithTag<View>(tag) ?: break
                (old.parent as? android.view.ViewGroup)?.removeView(old)
            }
        }
    }

    private fun suggestTap(s: String) {
        composer.setText("")
        openChat()
        handle(s)
    }

    private fun showThinking() {
        hideThinking()
        if (!::chatBox.isInitialized) return
        if (::orbStatus.isInitialized) orbStatus.text = "Düşünüyor…"
        chatOrbStatus?.text = "Düşünüyor…"
        val v = Ui.line(this, "J", "Düşünüyorum…")
        thinkingView = v
        chatBox.addView(v)
        chatScroll.post { chatScroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun orbThink(st: Int) {
        if (::holo.isInitialized) holo.state = st
        if (::holoV.isInitialized) holoV.state = st
    }

    private fun hideThinking() {
        if (thinkingView != null) setOrb(orbState)
        val v = thinkingView ?: return
        if (::chatBox.isInitialized) chatBox.removeView(v)
        thinkingView = null
    }

    private fun say(display: String, spoken: String = display, srcs: List<Pair<String, String>> = emptyList(), ai: Boolean = false) {
        addBubble("J", display, srcs, ai)
        store.addMsg(store.current(), "assistant", display)
        speakNow(spoken)
    }

    private fun sayGizli(metin: String) {
        addBubble("J", metin, emptyList(), false)
        speakNow(metin)
    }

    private fun setOrb(state: Int) {
        orbState = state
        if (!::holo.isInitialized || !::holoV.isInitialized || !::wave.isInitialized || !::waveV.isInitialized) return
        holo.state = state
        wave.state = state
        holoV.state = state
        waveV.state = state
        val txt = when (state) {
            1 -> "Dinliyor…"
            2 -> "Konuşuyor…"
            else -> "Hazır"
        }
        orbStatus.text = txt
        voiceStatus.text = txt
        chatOrbStatus?.text = when (state) {
            1 -> "Dinliyor…"
            2 -> "Aries konuşuyor"
            else -> ""
        }
        if (::speakBtn.isInitialized) {
            if (state == 2) Ui.swapIcon(speakBtn, "jclose", Pal.RED) else Ui.swapIcon(speakBtn, "jspeaker", Pal.TEXT)
            speakBtn.alpha = if (muted && state != 2) 0.4f else 1f
        }
        refreshHomeMic()
    }

    private fun onTtsInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val engine = tts ?: return
        engine.setLanguage(tr)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) { runOnUiThread { setOrb(2); if (voiceTurn) startBarge() } }
            override fun onDone(id: String?) { runOnUiThread { setOrb(0); stopBarge(); voiceTurn = false; keepScreen(false); afterSpeech() } }
            override fun onError(id: String?) { runOnUiThread { setOrb(0); stopBarge(); voiceTurn = false; keepScreen(false); afterSpeech() } }
            @android.annotation.SuppressLint("NewApi")
            override fun onRangeStart(id: String?, start: Int, end: Int, frame: Int) { lastPos = start; runOnUiThread { pulseOrb(0.9f) } }
        })
        ttsReady = true
        pendingSay?.let { speakNow(it); pendingSay = null }
    }

    private fun speakNow(t: String) {
        if (muted) {
            afterSpeech()
            return
        }
        val engine = tts
        if (!ttsReady || engine == null) {
            pendingSay = t
            return
        }
        lastSpoken = t; lastPos = 0
        keepScreen(true)
        engine.speak(t, TextToSpeech.QUEUE_FLUSH, null, "j")
    }

    private fun stopBarge() {
        val b = barge
        barge = null
        try { b?.cancel(); b?.destroy() } catch (e: Exception) {}
    }

    private fun bargeHit(res: Bundle?, b: SpeechRecognizer): Boolean {
        val t = res?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.lowercase(tr) ?: return false
        if (!Regex("(^| )(sus|dur|bekle|yeter)( |$)").containsMatchIn(t)) return false
        if (barge !== b) return true
        stopBarge()
        tts?.stop()
        setOrb(0)
        voiceTurn = false
        uiHandler.postDelayed({ if (orbState != 2 && !listeningNow && hasMic()) listen(true) }, 500)
        return true
    }

    private fun startBarge() {
        if (!hasMic() || !SpeechRecognizer.isRecognitionAvailable(this)) return
        stopBarge()
        val b = SpeechRecognizer.createSpeechRecognizer(this)
        barge = b
        b.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(x: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(t: Int, p: Bundle?) {}
            override fun onPartialResults(p: Bundle?) { bargeHit(p, b) }
            override fun onResults(res: Bundle?) {
                if (!bargeHit(res, b) && orbState == 2 && barge === b) uiHandler.postDelayed({ if (orbState == 2 && barge === b) startBarge() }, 300)
            }
            override fun onError(err: Int) {
                if (orbState == 2 && barge === b && err != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) uiHandler.postDelayed({ if (orbState == 2 && barge === b) startBarge() }, 500)
            }
        })
        b.startListening(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
                .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        )
    }

    private fun loopActive(): Boolean = continuous || awaitingCommand || store.flag("heyJarvis", false)

    private fun afterSpeech() {
        if (loopActive()) restartLater(400)
    }

    private fun restartLater(ms: Long) {
        uiHandler.postDelayed({ if (loopActive() && hasMic()) listen() }, ms)
    }

    private fun hasMic(): Boolean =
        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private fun setContinuous(on: Boolean) {
        continuous = on
        if (!on) rec?.cancel()
        refreshToggles()
        refreshHomeMic()
    }

    private fun contLabel(): String = if (continuous) "Sürekli dinleme: AÇIK" else "Sürekli dinleme: KAPALI"

    private fun toggleContinuous() {
        if (continuous) {
            setContinuous(false)
            say("Sürekli dinleme kapandı.")
        } else {
            setContinuous(true)
            say("Sürekli dinleme açıldı. Konuşabilirsiniz.")
        }
    }

    private fun setWake(on: Boolean) {
        store.setFlag("heyJarvis", on)
        if (!on) {
            rec?.cancel()
            awaitingCommand = false
            refreshToggles()
            return
        }
        if (!hasMic()) {
            store.setFlag("heyJarvis", false)
            refreshToggles()
            say("Hey Jarvis için mikrofon izni gerekli.")
            return
        }
        refreshToggles()
        say("Hey Jarvis açık. 'Jarvis' diyerek çağırabilirsiniz.")
    }

    private fun stopAll() {
        tts?.stop()
        rec?.cancel()
        continuous = false
        awaitingCommand = false
        store.setFlag("heyJarvis", false)
        setOrb(0)
        refreshToggles()
        say("Durdurdum.")
    }

    private fun micTap() {
        if (listeningNow) {
            rec?.stopListening()
            return
        }
        if (continuous) setContinuous(false)
        listen(true)
    }

    private fun setMicUi(on: Boolean) {
        for (b in micBtns) {
            b.text = if (on) "⏹ Gönder" else (b.tag as? String ?: "🎤")
        }
        refreshHomeMic()
        keepScreen(on)
    }

    private fun keepScreen(on: Boolean) {
        if (on) window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun listen(manual: Boolean = false) {
        if (!hasMic()) return
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return
        rec?.destroy()
        val r = SpeechRecognizer.createSpeechRecognizer(this)
        rec = r
        manualSession = manual
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p: Bundle?) { listeningNow = true; setMicUi(true); setOrb(1) }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) { pulseOrb(((v + 2f) / 12f).coerceIn(0f, 1f) * 0.8f) }
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(p: Bundle?) {
                val part = p?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (manualSession && part != null && ::composer.isInitialized) composer.setText(part)
            }
            override fun onEvent(t: Int, p: Bundle?) {}
            override fun onError(err: Int) {
                listeningNow = false
                setMicUi(false)
                setOrb(0)
                if (loopActive() && err != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                    restartLater(if (continuous) 700 else 1500)
                }
            }
            override fun onResults(res: Bundle?) {
                listeningNow = false
                setMicUi(false)
                setOrb(0)
                val text = res?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (::composer.isInitialized) composer.setText("")
                if (text.isNullOrBlank()) {
                    if (!manualSession && loopActive()) restartLater(400)
                    return
                }
                voiceTurn = false
                if (::voiceText.isInitialized) voiceText.text = "Son söylenen: $text"
                if (continuous || awaitingCommand) {
                    awaitingCommand = false
                    handle(text)
                    return
                }
                if (store.flag("heyJarvis", false)) {
                    wakeFilter(text)
                    return
                }
                handle(text)
            }
        })
        r.startListening(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
                .putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        )
    }

    private fun wakeFilter(text: String) {
        val m = Regex("\\b(jarvis|jarvıs)\\b[,:]?\\s*(.*)").find(text.lowercase(tr))
        if (m == null) {
            restartLater(400)
            return
        }
        val rest = m.groupValues[2].trim()
        if (rest.isBlank()) {
            awaitingCommand = true
            say("Efendim?")
            return
        }
        handle(rest)
    }

    private fun confirm(msg: String, onYes: () -> Unit, onNo: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("Evet") { _, _ -> onYes() }
            .setNegativeButton("Hayır") { _, _ -> onNo() }
            .setCancelable(false)
            .show()
    }

    private fun handle(raw: String, fromAi: Boolean = false) {
        val c = store.current()
        if (!fromAi) {
            addBubble("Sen", raw)
            store.addMsg(c, "user", raw)
        }
        val s = raw.lowercase(tr).trim().removeSuffix(" lütfen").trim()
        try {
            if (localCommand(raw, s)) return
            if (fromAi) {
                say("Bu isteği yerine getiremedim.")
                return
            }
            if (!store.flag("aiOn", true)) {
                say("Yapay zekâ kapalı. Yerel komutlar çalışıyor.")
                return
            }
            if (isOnline() && hasAiKey()) {
                askAi(raw)
            } else {
                val hits = searchFacts(raw)
                say("Bunu internetsiz anlayamadım." + (if (hits.isNotEmpty()) " Notlarda: " + hits.joinToString(". ") else ""))
            }
        } catch (e: Exception) {
            say("Hata oluştu.")
        }
    }

    private fun safeStart(i: Intent, failMsg: String): Boolean {
        return try {
            startActivity(i)
            true
        } catch (e: ActivityNotFoundException) {
            say(failMsg)
            false
        }
    }

    private fun localCommand(raw: String, s: String): Boolean {
        if (Regex("^(yardım|ne yapabilirsin|komutlar)$").containsMatchIn(s)) {
            say("Saat, tarih, batarya, depolama, alarm, zamanlayıcı, hatırlatma, takvim, arama, mesaj, müzik, ayarlar, fener, kamera, hesaplama, görevler ve dosyalar.")
            return true
        }

        if (Regex("^(sus|sustur|dur|yeter|bekle|bir dakika|dur biraz|bekle biraz)$").containsMatchIn(s)) {
            tts?.stop(); setOrb(0)
            return true
        }
        if (Regex("^(devam et|devam|devam et lütfen|oradan devam et)$").containsMatchIn(s)) {
            val rest = if (lastPos in 0 until lastSpoken.length) lastSpoken.substring(lastPos) else ""
            if (rest.isNotBlank()) { speakNow(rest); return true }
            say("Devam edecek bir şey yok.")
            return true
        }
        if (Reminders.tryHandle(this, s) { msg -> say(msg) }) {
            refreshReminders()
            return true
        }

        if (Commands.tryHandle(this, s) { msg -> say(msg) }) return true
        val exprVal = evalMath(s)
        if (exprVal != null) {
            say("Sonuç: " + formatNumber(exprVal))
            return true
        }

        if (Regex("^(?:bunu )?hatırla[:,]?\\s+.+|^unut\\s*(?::\\s*|\\s+).+|^notlarım[.!?]*$|^notlarda ara\\s*:?\\s+.+|^profilim\\s*:\\s*.+|^profilim[.!?]*$", RegexOption.IGNORE_CASE).containsMatchIn(raw.trim())) return memCloud(raw)

        if (Regex("^saat kaç(ti)?$").containsMatchIn(s)) {
            val c = Calendar.getInstance()
            say("Saat %02d:%02d.".format(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)))
            return true
        }

        if (Regex("^(batarya|şarj)( durumu)?$").containsMatchIn(s)) {
            say(batteryText())
            return true
        }

        if (Regex("fener aç").containsMatchIn(s)) {
            say(if (torch(true)) "Fener açıldı." else "Fener açılamadı.")
            return true
        }
        if (Regex("fener kapat").containsMatchIn(s)) {
            say(if (torch(false)) "Fener kapatıldı." else "Fener kapatılamadı.")
            return true
        }

        val task = Regex("^görev ekle[:,]?\\s+(.+)$", RegexOption.IGNORE_CASE).find(raw.trim())
        if (task != null) {
            addTask(task.groupValues[1])
            say("Görevi ekledim.")
            return true
        }

        val flm = Regex("^(film|dizi) (ara|bul)[:,]? +(.+)$").find(s); if (flm != null) { val tk = store.str("bulut"); if (tk.isBlank()) { say("Aries Bulut anahtarı yok."); return true }; showThinking(); thread { try { val r = Ai.film(tk, flm.groupValues[3]); runOnUiThread { hideThinking(); say(r) } } catch (e: Exception) { runOnUiThread { hideThinking(); say(Ai.friendly(e)) } } }; return true }
        val res = Regex("^araştır[:,]?\\s+(.+)$").find(s)
        if (res != null) {
            doResearch(res.groupValues[1])
            return true
        }

        val calc = Regex("^hesapla[:,]?\\s+(.+)$").find(s)
        if (calc != null) {
            val v = evalMath(calc.groupValues[1])
            say(if (v != null) "Sonuç: " + formatNumber(v) else "Bu işlemi hesaplayamadım.")
            return true
        }

        val tra = Regex("^(.+?)(?:ye|ya|e|a)\\s+çevir[:,]?\\s+(.+)$").find(s)
        if (tra != null) {
            doTranslate(tra.groupValues[1], tra.groupValues[2])
            return true
        }

        if (Regex("^(haberler|haberleri (oku|getir|göster))$").containsMatchIn(s)) {
            if (isOnline()) fetchNews(newsTopic(), true) else say(NO_NET)
            return true
        }

        if (s.endsWith(" aç")) {
            openApp(s.removeSuffix(" aç"))
            return true
        }

        return false
    }

    private fun hasAiKey(): Boolean = store.str("bulut").isNotBlank() || Ai.ALL.any { store.str(keyName(it)).isNotBlank() }
    private fun keyName(who: String): String = if (who == "gemini") "key" else who
    private fun providerKeys(): Map<String, String> = Ai.ALL.associateWith { store.str(keyName(it)) }
    private fun providerModels(): Map<String, String> = mapOf(
        "gemini" to Ai.GEMINI_MODEL,
        "groq" to store.str("groqModel"),
        "mistral" to store.str("mistralModel")
    )
    private fun providerOrder(): List<String> = Ai.ALL

    private fun needsLive(t: String): Boolean {
        val l = t.lowercase(java.util.Locale("tr", "TR"))
        val keys = listOf("altın", "dolar", "euro", "sterlin", "borsa", "bitcoin", "kripto", "hava durumu", "haber", "son dakika", "maç", "skor", "güncel", "fiyat", "kur ")
        return keys.any { l.contains(it) } || l.contains("?") || listOf("kim ", "kimdir", "nedir", "kaç ", "hangi", "nerede", "ne zaman", "neden", "kaçta", "var mı", "ne kadar", "kazandı", "yendi", "sezon", "gol").any { l.contains(it) }
    }

    private fun askAi(text: String) {
        val c = store.current()
        val hist = c.msgs.filter { it.first == "user" || it.first == "assistant" }.takeLast(20)
        showThinking()
        thread {
            try {
                uiHandler.post { orbThink(4) }
                val raw = Ai.chat(listOf("bulut") + providerOrder(), providerKeys() + ("bulut" to store.str("bulut")), providerModels(), SYS, hist)
                val (cmd, reply) = Ai.parseReply(raw)
                runOnUiThread {
                    hideThinking()
                    if (cmd.isNotBlank()) handle(cmd, true)
                    else say(reply.ifEmpty { "Anlaşılamadı." }, ai = true)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    hideThinking()
                    say(Ai.friendly(e))
                }
            }
        }
    }

    private fun doResearch(q: String) {
        if (!store.flag("aiOn", true)) { say("Yapay zekâ kapalı."); return }
        if (!isOnline() || !hasAiKey()) { say(NO_NET); return }
        showThinking()
        thread {
            try {
                uiHandler.post { orbThink(5) }
                var hz = "ok"; val tk = store.str("bulut"); val a = (if (tk.isNotBlank()) try { Ai.hizli(tk, q) } catch (e: Exception) { hz = e.message ?: "hata"; null } else { hz = "anahtar yok"; null }) ?: Ai.research(listOf("bulut") + providerOrder(), providerKeys() + ("bulut" to store.str("bulut")), providerModels(), q)
                runOnUiThread {
                    hideThinking()
                    say(a.text.ifEmpty { "Sonuç bulamadım." }, a.text, a.sources.take(5).map { it.title to it.url }, ai = true)
                }
            } catch (e: Exception) {
                runOnUiThread { hideThinking(); say(Ai.friendly(e)) }
            }
        }
    }

    private fun doTranslate(lang: String, text: String) {
        if (!store.flag("aiOn", true)) { say("Yapay zekâ kapalı."); return }
        if (!isOnline() || !hasAiKey()) { say(NO_NET); return }
        showThinking()
        thread {
            try {
                uiHandler.post { orbThink(6) }
                val r = Ai.translate(providerOrder(), providerKeys(), providerModels(), lang, text)
                runOnUiThread { hideThinking(); say(r, ai = true) }
            } catch (e: Exception) {
                runOnUiThread { hideThinking(); say(Ai.friendly(e)) }
            }
        }
    }

    private fun testProvider(who: String) {
        val key = store.str(keyName(who))
        if (key.isBlank()) { showTest("Anahtar boş."); return }
        showTest("Test ediliyor...")
        thread {
            val result = try { "Başarılı: " + Ai.ping(who, key, providerModels()[who].orEmpty()).take(50) }
            catch (e: Exception) { "Başarısız: " + Ai.friendly(e) }
            runOnUiThread { showTest(result) }
        }
    }

    private fun showTest(t: String) { if (::testOut.isInitialized) testOut.text = t }

    private fun orderLabel(): String = store.str("order", "auto")

    private fun pickOrder() {
        val values = arrayOf("auto", "gemini", "groq", "mistral")
        store.put("order", values[0])
        refreshToggles()
    }

    private fun setNewsText(t: String) { if (::newsView.isInitialized) newsView.text = t }
    private fun setStockText(t: String) { if (::stockView.isInitialized) stockView.text = t }

    private fun fetchNews(topic: String, speak: Boolean) {
        setNewsText("Haberler alınıyor...")
        thread {
            try {
                val titles = Net.newsTitles(topic)
                val shown = titles.take(5).joinToString("\n\n") { "• $it" }
                runOnUiThread {
                    setNewsText(shown)
                    if (speak) say(shown)
                }
            } catch (e: Exception) {
                runOnUiThread { setNewsText("Haberler alınamadı.") }
            }
        }
    }

    private fun fetchWatchlist(speak: Boolean) {
        val list = store.watch()
        val token = store.str("finnhub")
        if (token.isBlank() || list.isEmpty()) return
        thread {
            val lines = list.map { sym -> try { Net.quote(sym, token) } catch (e: Exception) { "$sym: hata" } }
            runOnUiThread { setStockText(lines.joinToString("\n")) }
        }
    }

    private fun refreshTasks() {
        if (!::taskBox.isInitialized) return
        taskBox.removeAllViews()
        val arr = store.tasks()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            taskBox.addView(Ui.button(this, (if (o.getBoolean("d")) "✓ " else "○ ") + o.getString("t")) { toggleTask(i) })
        }
    }

    private fun addTask(text: String) {
        val arr = store.tasks()
        arr.put(JSONObject().put("t", text.trim()).put("d", false))
        store.saveTasks(arr)
        refreshTasks()
    }

    private fun addTaskFromUi() {
        val text = assistInput.text.toString().trim()
        if (text.isNotEmpty()) { addTask(text); assistInput.setText("") }
    }

    private fun toggleTask(i: Int) {
        val arr = store.tasks()
        val o = arr.getJSONObject(i)
        o.put("d", !o.getBoolean("d"))
        store.saveTasks(arr)
        refreshTasks()
    }

    private fun removeDoneTasks() {
        val arr = store.tasks()
        val out = JSONArray()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            if (!o.getBoolean("d")) out.put(o)
        }
        store.saveTasks(out)
        refreshTasks()
    }

    private fun refreshReminders() {
        if (!::reminderBox.isInitialized) return
        val list = Reminders.upcoming(this)
        reminderBox.text = if (list.isEmpty()) "Yaklaşan hatırlatma yok."
        else list.take(5).joinToString("\n") { Reminders.describe(it.time) + " - " + it.text }
    }

    private fun refreshDevice() {
        if (::deviceView.isInitialized) deviceView.text = batteryText() + "\n" + storageLine()
    }

    private fun refreshStatus() {
        if (::clockTv.isInitialized) {
            val now = Date()
            clockTv.text = SimpleDateFormat("HH:mm", tr).format(now)
            dateTv.text = SimpleDateFormat("d MMMM yyyy, EEE", tr).format(now)
        }
    }

    private fun exportMemory() {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("jarvis", store.exportJson()))
        say("Hafıza panoya kopyalandı.")
    }

    private fun memCloud(raw: String): Boolean {
        val t = raw.trim()
        val tk = store.str("bulut")
        val fact = Regex("^(?:bunu )?hatırla[:,]?\\s+(.+)$", RegexOption.IGNORE_CASE).find(t)?.groupValues?.get(1)?.trim()
        val unutN = Regex("^unut\\s*(?::\\s*|\\s+)(\\d+)$", RegexOption.IGNORE_CASE).find(t)
        if (unutN != null) {
            val n = unutN.groupValues[1].toIntOrNull() ?: 0
            val list = store.facts().split("; ").map { it.trim() }.filter { it.isNotEmpty() }
            if (n < 1 || n > list.size) {
                say(if (list.isEmpty()) "Kayıtlı not yok." else "$n numaralı not yok, toplam " + list.size + " not var.")
            } else {
                store.put("facts", list.filterIndexed { j, _ -> j != n - 1 }.joinToString("; "))
                say("$n numaralı notu sildim: " + list[n - 1])
            }
            return true
        }
        if (tk.isBlank() || !isOnline()) {
            if (fact != null) {
                if (Regex("\\d{11,}|şifre|parola|cvv", RegexOption.IGNORE_CASE).containsMatchIn(fact)) say("Kart, şifre ya da kimlik gibi görünüyor, kaydetmedim.")
                else { store.addFact(fact); say("Not ettim (yalnızca telefonda): " + fact) }
            } else if (Regex("^notlarım", RegexOption.IGNORE_CASE).containsMatchIn(t)) {
                say(store.facts().ifBlank { "Kayıtlı notun yok." })
            } else say("Bu komut için internet ve Aries Bulut anahtarı gerekir.")
            return true
        }
        showThinking()
        thread {
            try {
                val ans = Ai.chat(listOf("bulut"), mapOf("bulut" to tk), emptyMap(), "", listOf(Pair("user", t)))
                runOnUiThread {
                    hideThinking()
                    if (fact != null && ans.startsWith("Not ettim")) store.addFact(fact)
                    if (ans.startsWith("Bütün notları sildim")) store.clearFacts()
                    say(ans)
                }
            } catch (e: Exception) {
                runOnUiThread { hideThinking(); say(Ai.friendly(e)) }
            }
        }
        return true
    }

    private fun searchFacts(query: String): List<String> {
        val words = query.lowercase(tr).split(Regex("[^a-zçğıöşü0-9]+")).filter { it.length >= 3 }
        if (words.isEmpty()) return emptyList()
        return store.facts().split(Regex("[;.\\n]"))
            .map { it.trim() }
            .filter { seg -> seg.isNotEmpty() && words.any { w -> seg.lowercase(tr).contains(w.take(4)) } }
            .take(3)
    }

    private fun torch(on: Boolean): Boolean {
        return try {
            val cm = getSystemService(CAMERA_SERVICE) as CameraManager
            val id = cm.cameraIdList.firstOrNull { cm.getCameraCharacteristics(it).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true }
            if (id != null) { cm.setTorchMode(id, on); true } else false
        } catch (e: Exception) { false }
    }

    private fun openApp(name: String) {
        val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val hit = packageManager.queryIntentActivities(i, 0).firstOrNull { it.loadLabel(packageManager).toString().lowercase(tr).contains(name) }
        val launch = hit?.let { packageManager.getLaunchIntentForPackage(it.activityInfo.packageName) }
        if (launch != null) safeStart(launch, "Açılamadı.") else say("$name bulunamadı.")
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun batteryInfo(): Pair<Int, Boolean>? {
        val i = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return null
        val level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        if (level < 0 || scale <= 0) return null
        return Pair(level * 100 / scale, status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL || status == BatteryManager.BATTERY_STATUS_FULL)
    }

    private fun batteryText(): String {
        val bat = batteryInfo() ?: return "Batarya okunamadı."
        return "Batarya: %d%% %s".format(bat.first, if (bat.second) "(Şarjda)" else "")
    }

    private fun storageLine(): String = try {
        val st = StatFs(filesDir.absolutePath)
        "Depolama: %.1f GB boş".format(Locale.US, st.availableBytes / 1e9)
    } catch (e: Exception) { "Depolama: bilinmiyor" }

    private fun greeting(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (h) {
            in 5..11 -> "Günaydın, Cem."
            in 12..17 -> "İyi günler, Cem."
            in 18..22 -> "İyi akşamlar, Cem."
            else -> "İyi geceler, Cem."
        }
    }
}