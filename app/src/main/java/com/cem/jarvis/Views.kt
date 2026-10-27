package com.cem.jarvis

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

// ============================================================
// RENKLER: Tema değiştirmek istediğinde yalnızca bu bloğu düzenle.
// ============================================================
object Pal {
    const val BG = "#000000"       // ekran zemini (saf siyah)
    const val CARD = "#0A0A0A"     // kart zemini
    const val CARD2 = "#131313"    // düğme ve giriş kutusu zemini
    const val LINE = "#262626"     // ince kenar çizgisi
    const val TEXT = "#F2F2F2"     // yazı
    const val MUTED = "#8C8C8C"    // pasif yazı
    const val WHITE = "#FFFFFF"    // dinleme / vurgu
    const val GREEN = "#34D399"    // başarılı işlem
    const val RED = "#FF6B6B"      // uyarı / durdur
}

// ============================================================
// Küre: 0 bekleme (sönük gri), 1 dinliyor (beyaz), 2 konuşuyor (parlak beyaz), 3 başarılı (yeşil)
// Parlama için BlurMaskFilter yerine üst üste yarı saydam çizgiler kullanılır
// (donanım hızlandırmada her sürümde aynı çalışır).
// ============================================================
class HoloView(c: Context) : View(c) {
    var state: Int = 0
        set(v) {
            field = v
            invalidate()
        }
    var animated: Boolean = true
        set(v) {
            field = v
            invalidate()
        }

    // 0..1: ses şiddeti. Küre bu değere göre hafifçe kabarır, kendiliğinden söner.
    var level: Float = 0f

    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val box = RectF()
    private var t = 0f

    private var cacheKey = ""
    private var halo: RadialGradient? = null
    private var body: RadialGradient? = null
    private var core: RadialGradient? = null
    private var sweep: SweepGradient? = null

    private fun withAlpha(col: Int, a: Int): Int = Color.argb(a, Color.red(col), Color.green(col), Color.blue(col))

    private fun build(cx: Float, cy: Float, r: Float, main: Int, accent: Int) {
        val key = "${width}x$height:$state"
        if (key == cacheKey) return
        cacheKey = key
        halo = RadialGradient(
            cx, cy, r,
            intArrayOf(withAlpha(main, 0), withAlpha(main, 95), withAlpha(accent, 45), withAlpha(main, 0)),
            floatArrayOf(0.50f, 0.74f, 0.88f, 1f),
            Shader.TileMode.CLAMP
        )
        body = RadialGradient(
            cx, cy, r * 0.74f,
            intArrayOf(withAlpha(accent, 70), withAlpha(Color.parseColor(Pal.BG), 235)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        core = RadialGradient(
            cx, cy, r * 0.40f,
            intArrayOf(Color.WHITE, withAlpha(main, 235), withAlpha(main, 130), withAlpha(main, 0)),
            floatArrayOf(0f, 0.30f, 0.65f, 1f),
            Shader.TileMode.CLAMP
        )
        sweep = SweepGradient(
            cx, cy,
            intArrayOf(main, accent, Color.WHITE, main),
            null
        )
    }

    private fun arcs(cv: Canvas, cx: Float, cy: Float, rad: Float, w: Float, n: Int, sw: Float, rot: Float, alpha: Int) {
        box.set(cx - rad, cy - rad, cx + rad, cy + rad)
        ring.strokeWidth = w
        ring.alpha = alpha
        for (i in 0 until n) cv.drawArc(box, rot + i * 360f / n, sw, false, ring)
    }

    override fun onDraw(cv: Canvas) {
        if (animated) t += if (state == 2) 3.0f else if (state == 1) 2.0f else 0.7f
        val cx = width / 2f
        val cy = height / 2f
        val r = minOf(width, height) / 2f - 2f
        if (r <= 8f) return
        val main = when (state) {
            1 -> Color.parseColor("#FFFFFF")
            2 -> Color.parseColor("#FFFFFF")
            3 -> Color.parseColor(Pal.GREEN)
            else -> Color.parseColor("#9AA0A6")
        }
        val accent = when (state) {
            1 -> Color.parseColor("#CFCFCF")
            2 -> Color.parseColor("#BDBDBD")
            3 -> Color.parseColor("#10B981")
            else -> Color.parseColor("#5F6368")
        }
        build(cx, cy, r, main, accent)
        if (level > 0.01f) level *= 0.88f else level = 0f
        val pulse = 1f + (if (state == 2 || state == 3) 0.06f else 0.02f) * Math.sin(t / 8.0).toFloat() + level * 0.12f

        // dış parlama
        fill.shader = halo
        fill.alpha = 255
        cv.drawCircle(cx, cy, r * pulse, fill)

        // gövde
        fill.shader = body
        cv.drawCircle(cx, cy, r * 0.74f, fill)

        // kalın ışık halkası (üç geçiş = yumuşak parlama)
        ring.shader = sweep
        cv.save()
        cv.rotate(t * 0.5f, cx, cy)
        box.set(cx - r * 0.62f, cy - r * 0.62f, cx + r * 0.62f, cy + r * 0.62f)
        ring.strokeWidth = r * 0.15f
        ring.alpha = 40
        cv.drawOval(box, ring)
        ring.strokeWidth = r * 0.09f
        ring.alpha = 95
        cv.drawOval(box, ring)
        ring.strokeWidth = r * 0.035f
        ring.alpha = 255
        cv.drawOval(box, ring)
        cv.restore()
        ring.shader = null

        // ince çentik halkası
        ring.color = main
        arcs(cv, cx, cy, r * 0.76f, 1.5f, 60, 2f, -t * 0.2f, 150)

        // dönen yaylar
        ring.color = accent
        arcs(cv, cx, cy, r * 0.90f, 2.5f, 3, 38f, -t * 0.9f, 200)
        ring.color = main
        arcs(cv, cx, cy, r * 0.50f, 2f, 2, 70f, t * 1.3f, 230)

        // iç çekirdek
        fill.shader = core
        cv.drawCircle(cx, cy, r * 0.40f * pulse, fill)
        fill.shader = null

        // yörüngedeki parçacıklar
        fill.color = Color.WHITE
        for (i in 0 until 14) {
            val a = Math.toRadians((t * (0.25 + (i % 3) * 0.12) + i * 360.0 / 14))
            val rr = r * (0.88f + 0.06f * Math.sin((t / 12.0) + i).toFloat())
            val x = cx + (rr * Math.cos(a)).toFloat()
            val y = cy + (rr * Math.sin(a)).toFloat()
            fill.alpha = 90 + (i % 4) * 40
            cv.drawCircle(x, y, 1.2f + (i % 3) * 0.7f, fill)
        }
        fill.alpha = 255

        if (animated) postInvalidateOnAnimation()
    }
}

// Ses dalgası: dinlerken ve konuşurken hareket eden çubuklar
class WaveView(c: Context) : View(c) {
    var state = 0
    private val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private var t = 0f

    override fun onDraw(cv: Canvas) {
        t += if (state == 0) 0.05f else 0.25f
        val bars = 28
        val gap = width.toFloat() / bars
        val amp = when (state) {
            2 -> 1f
            1 -> 0.6f
            3 -> 0.3f
            else -> 0.12f
        }
        p.color = when (state) {
            1, 2 -> Color.parseColor(Pal.WHITE)
            3 -> Color.parseColor(Pal.GREEN)
            else -> Color.parseColor("#5F6368")
        }
        val mid = height / 2f
        for (i in 0 until bars) {
            val wobble = 0.4f + 0.6f * Math.abs(Math.sin((t + i * 0.6).toDouble())).toFloat()
            val h = (height * 0.5f) * amp * wobble + 3f
            val x = i * gap + gap / 2f
            cv.drawRoundRect(x - gap * 0.2f, mid - h, x + gap * 0.2f, mid + h, 4f, 4f, p)
        }
        postInvalidateOnAnimation()
    }
}

// Ortak arayüz yardımcıları. Tüm renkler Pal'dan gelir.
object Ui {
    fun dp(ctx: Context, v: Int): Int = (v * ctx.resources.displayMetrics.density + 0.5f).toInt()

    // radius piksel cinsindendir (eski çağrılarla uyumlu)
    fun rounded(fill: String, stroke: String?, radius: Float): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(Color.parseColor(fill))
        g.cornerRadius = radius
        if (stroke != null) g.setStroke(2, Color.parseColor(stroke))
        return g
    }

    fun column(ctx: Context): LinearLayout {
        val l = LinearLayout(ctx)
        l.orientation = LinearLayout.VERTICAL
        return l
    }

    fun row(ctx: Context): LinearLayout {
        val l = LinearLayout(ctx)
        l.orientation = LinearLayout.HORIZONTAL
        l.gravity = Gravity.CENTER_VERTICAL
        return l
    }

    fun text(ctx: Context, s: String, size: Float = 15f, color: String = Pal.TEXT, bold: Boolean = false): TextView {
        val t = TextView(ctx)
        t.text = s
        t.textSize = size
        t.setTextColor(Color.parseColor(color))
        if (bold) t.setTypeface(t.typeface, android.graphics.Typeface.BOLD)
        return t
    }

    fun section(ctx: Context, s: String): TextView {
        val t = text(ctx, s.uppercase(java.util.Locale("tr", "TR")), 11f, Pal.MUTED, true)
        t.letterSpacing = 0.12f
        t.setPadding(dp(ctx, 4), dp(ctx, 6), 0, 0)
        return t
    }

    fun card(ctx: Context, s: String): TextView {
        val t = TextView(ctx)
        t.text = s
        t.textSize = 14f
        t.setTextColor(Color.parseColor(Pal.TEXT))
        t.background = rounded(Pal.CARD, Pal.LINE, dp(ctx, 18).toFloat())
        t.setPadding(dp(ctx, 16), dp(ctx, 14), dp(ctx, 16), dp(ctx, 14))
        t.setTextIsSelectable(true)
        return t
    }

    fun button(ctx: Context, s: String, onClick: () -> Unit): Button {
        val b = Button(ctx)
        b.text = s
        b.isAllCaps = false
        b.textSize = 14f
        b.stateListAnimator = null
        b.minHeight = 0
        b.minimumHeight = dp(ctx, 46)
        b.setTextColor(Color.parseColor(Pal.TEXT))
        b.background = rounded(Pal.CARD2, Pal.LINE, dp(ctx, 16).toFloat())
        b.setOnClickListener { onClick() }
        return b
    }

    fun primary(ctx: Context, s: String, onClick: () -> Unit): Button {
        val b = button(ctx, s, onClick)
        b.setTextColor(Color.BLACK)
        b.background = rounded(Pal.WHITE, null, dp(ctx, 16).toFloat())
        return b
    }

    fun input(ctx: Context, hint: String): EditText {
        val e = EditText(ctx)
        e.hint = hint
        e.textSize = 15f
        e.setTextColor(Color.parseColor(Pal.TEXT))
        e.setHintTextColor(Color.parseColor(Pal.MUTED))
        e.background = rounded(Pal.CARD2, Pal.LINE, dp(ctx, 16).toFloat())
        e.setPadding(dp(ctx, 14), dp(ctx, 12), dp(ctx, 14), dp(ctx, 12))
        return e
    }

    // ---------- Vektör ikonlar (res/drawable/ic_*.xml) ----------
    private val resCache = HashMap<String, Int>()

    private fun iconRes(ctx: Context, name: String): Int =
        resCache.getOrPut(name) { ctx.resources.getIdentifier("ic_$name", "drawable", ctx.packageName) }

    fun icon(ctx: Context, name: String, sizeDp: Int = 22, color: String = Pal.TEXT): ImageView {
        val iv = ImageView(ctx)
        val id = iconRes(ctx, name)
        if (id != 0) iv.setImageResource(id)
        iv.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        iv.scaleType = ImageView.ScaleType.FIT_CENTER
        iv.layoutParams = LinearLayout.LayoutParams(dp(ctx, sizeDp), dp(ctx, sizeDp))
        return iv
    }

    // Yuvarlak (isteğe bağlı dolgulu) ikon düğmesi. İçindeki ikon swapIcon ile değiştirilir.
    fun iconBtn(
        ctx: Context,
        name: String,
        desc: String,
        sizeDp: Int = 42,
        iconDp: Int = 22,
        color: String = Pal.TEXT,
        bg: String? = null,
        stroke: String? = null,
        onClick: () -> Unit
    ): FrameLayout {
        val f = FrameLayout(ctx)
        val iv = icon(ctx, name, iconDp, color)
        f.addView(iv, FrameLayout.LayoutParams(dp(ctx, iconDp), dp(ctx, iconDp), Gravity.CENTER))
        if (bg != null) {
            val g = GradientDrawable()
            g.shape = GradientDrawable.OVAL
            g.setColor(Color.parseColor(bg))
            if (stroke != null) g.setStroke(dp(ctx, 1), Color.parseColor(stroke))
            f.background = g
        }
        f.isClickable = true
        f.contentDescription = desc
        f.setOnClickListener { onClick() }
        f.layoutParams = LinearLayout.LayoutParams(dp(ctx, sizeDp), dp(ctx, sizeDp))
        return f
    }

    fun swapIcon(btn: FrameLayout, name: String, color: String) {
        val iv = btn.getChildAt(0) as ImageView
        val id = iconRes(btn.context, name)
        if (id != 0) iv.setImageResource(id)
        iv.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
    }

    fun badge(ctx: Context, s: String, color: String): TextView {
        val t = text(ctx, s, 11f, color, true)
        t.background = rounded(Pal.CARD2, color, dp(ctx, 10).toFloat())
        t.setPadding(dp(ctx, 8), dp(ctx, 3), dp(ctx, 8), dp(ctx, 3))
        return t
    }

    fun chip(ctx: Context, s: String, active: Boolean, onClick: () -> Unit): TextView {
        val t = text(ctx, s, 13f, if (active) "#000000" else Pal.TEXT)
        t.background = rounded(if (active) Pal.WHITE else Pal.CARD2, if (active) null else Pal.LINE, dp(ctx, 18).toFloat())
        t.setPadding(dp(ctx, 14), dp(ctx, 8), dp(ctx, 14), dp(ctx, 8))
        t.isClickable = true
        t.setOnClickListener { onClick() }
        val lp = LinearLayout.LayoutParams(-2, -2)
        lp.setMargins(0, 0, dp(ctx, 8), 0)
        t.layoutParams = lp
        return t
    }

    // Kart satırı: solda ikon karosu, ortada başlık/alt yazı, sağda özel görünüm ya da ok.
    fun listRow(ctx: Context, ico: String, title: String, sub: String?, trailing: View?, onClick: (() -> Unit)?): LinearLayout {
        val r = LinearLayout(ctx)
        r.orientation = LinearLayout.HORIZONTAL
        r.gravity = Gravity.CENTER_VERTICAL
        r.background = rounded(Pal.CARD, Pal.LINE, dp(ctx, 18).toFloat())
        r.setPadding(dp(ctx, 14), dp(ctx, 12), dp(ctx, 12), dp(ctx, 12))
        val tile = FrameLayout(ctx)
        tile.background = rounded(Pal.CARD2, Pal.LINE, dp(ctx, 12).toFloat())
        tile.addView(icon(ctx, ico, 20, Pal.WHITE), FrameLayout.LayoutParams(dp(ctx, 20), dp(ctx, 20), Gravity.CENTER))
        r.addView(tile, LinearLayout.LayoutParams(dp(ctx, 40), dp(ctx, 40)))
        val col = column(ctx)
        col.setPadding(dp(ctx, 12), 0, dp(ctx, 8), 0)
        val tt = text(ctx, title, 15f)
        tt.maxLines = 2
        col.addView(tt)
        if (!sub.isNullOrBlank()) {
            val st = text(ctx, sub, 12f, Pal.MUTED)
            st.maxLines = 2
            col.addView(st)
        }
        r.addView(col, LinearLayout.LayoutParams(0, -2, 1f))
        if (trailing != null) r.addView(trailing)
        else if (onClick != null) r.addView(icon(ctx, "chevron", 18, Pal.MUTED))
        if (onClick != null) {
            r.isClickable = true
            r.setOnClickListener { onClick() }
        }
        return r
    }

    // Anahtarlı satır. Switch, satırın tag'ine değil dönen Pair'e konur; dinleyici Switch.tag'dedir.
    fun switchRow(ctx: Context, title: String, sub: String?, checked: Boolean, onChange: (Boolean) -> Unit): Pair<LinearLayout, Switch> {
        val r = LinearLayout(ctx)
        r.orientation = LinearLayout.HORIZONTAL
        r.gravity = Gravity.CENTER_VERTICAL
        r.background = rounded(Pal.CARD, Pal.LINE, dp(ctx, 18).toFloat())
        r.setPadding(dp(ctx, 16), dp(ctx, 12), dp(ctx, 12), dp(ctx, 12))
        val col = column(ctx)
        col.addView(text(ctx, title, 15f))
        if (!sub.isNullOrBlank()) col.addView(text(ctx, sub, 12f, Pal.MUTED))
        r.addView(col, LinearLayout.LayoutParams(0, -2, 1f))
        val sw = Switch(ctx)
        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        sw.thumbTintList = ColorStateList(states, intArrayOf(Color.parseColor(Pal.GREEN), Color.parseColor(Pal.MUTED)))
        sw.trackTintList = ColorStateList(states, intArrayOf(Color.parseColor("#14614A"), Color.parseColor(Pal.CARD2)))
        sw.isChecked = checked
        val l = CompoundButton.OnCheckedChangeListener { _, b -> onChange(b) }
        sw.setOnCheckedChangeListener(l)
        sw.tag = l
        r.addView(sw)
        return Pair(r, sw)
    }

    // Sade sohbet satırı (balonsuz, uçtan uca): "Sen" silik, Jarvis parlak, "Sistem" küçük ve gri.
    fun line(ctx: Context, who: String, text: String, sources: List<Pair<String, String>> = emptyList(), onSpeak: (() -> Unit)? = null): View {
        val me = who == "Sen"
        val sys = who == "Sistem"
        val outer = android.widget.LinearLayout(ctx)
        outer.orientation = android.widget.LinearLayout.VERTICAL
        outer.setPadding(dp(ctx, 4), dp(ctx, 6), dp(ctx, 4), dp(ctx, 6))
        val box = android.widget.LinearLayout(ctx)
        box.orientation = android.widget.LinearLayout.VERTICAL
        val lp = android.widget.LinearLayout.LayoutParams(-2, -2)
        lp.gravity = if (me) android.view.Gravity.END else android.view.Gravity.START
        val tv = TextView(ctx)
        tv.text = text
        tv.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
        tv.textSize = if (sys) 13f else 16f
        tv.setLineSpacing(0f, 1.2f)
        tv.setTextIsSelectable(true)
        tv.maxWidth = (ctx.resources.displayMetrics.widthPixels * 0.84f).toInt()
        if (sys) {
            tv.setTextColor(Color.parseColor("#8A8FA8"))
            lp.gravity = android.view.Gravity.CENTER_HORIZONTAL
        } else {
            tv.setTextColor(Color.parseColor("#F2F2F7"))
            val bg = android.graphics.drawable.GradientDrawable()
            bg.cornerRadius = dp(ctx, 18).toFloat()
            bg.setColor(Color.parseColor(if (me) "#3B3F8C" else "#1C1C24"))
            tv.background = bg
            tv.setPadding(dp(ctx, 14), dp(ctx, 10), dp(ctx, 14), dp(ctx, 10))
        }
        box.addView(tv)
        outer.addView(box, lp)
        if (who == "J" && text != "Düşünüyorum…") {
            fun act(label: String, run: () -> Unit): TextView {
                val a = TextView(ctx)
                a.text = label
                a.textSize = 12f
                a.setTextColor(Color.parseColor("#8A8FA8"))
                a.setPadding(dp(ctx, 6), dp(ctx, 8), dp(ctx, 12), dp(ctx, 6))
                a.setOnClickListener { run() }
                return a
            }
            val row = android.widget.LinearLayout(ctx)
            row.orientation = android.widget.LinearLayout.HORIZONTAL
            row.addView(act("Kopyala") {
                val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                cm.setPrimaryClip(android.content.ClipData.newPlainText("Aries", text))
                android.widget.Toast.makeText(ctx, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
            })
            row.addView(act("Paylaş") {
                val i = android.content.Intent(android.content.Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(android.content.Intent.EXTRA_TEXT, text)
                ctx.startActivity(android.content.Intent.createChooser(i, "Paylaş"))
            })
            row.addView(act("Büyüt") {
                val big = TextView(ctx)
                big.text = text
                big.textSize = 26f
                big.setTextColor(Color.WHITE)
                big.setPadding(dp(ctx, 20), dp(ctx, 20), dp(ctx, 20), dp(ctx, 20))
                big.setTextIsSelectable(true)
                val sv = android.widget.ScrollView(ctx)
                sv.setBackgroundColor(Color.parseColor("#101014"))
                sv.addView(big)
                android.app.AlertDialog.Builder(ctx).setView(sv).setPositiveButton("Kapat", null).show()
            })
            if (onSpeak != null) row.addView(act("Oku") { onSpeak() })
            if (sources.isNotEmpty()) row.addView(act("Kaynak (" + sources.size + ")") {
                val names = sources.map { it.first }.toTypedArray()
                android.app.AlertDialog.Builder(ctx).setTitle("Kaynaklar").setItems(names) { _, w ->
                    try { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(sources[w].second))) } catch (e: Exception) {}
                }.show()
            })
            box.addView(row)
        }
        return outer
    }
}
