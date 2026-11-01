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
    const val BG = "#0B0D12"       // ekran zemini (saf siyah)
    const val CARD = "#12151C"     // kart zemini
    const val CARD2 = "#1A1E28"    // düğme ve giriş kutusu zemini
    const val LINE = "#2A3040"     // ince kenar çizgisi
    const val TEXT = "#EDEFF5"     // yazı
    const val MUTED = "#8B93A7"    // pasif yazı
    const val WHITE = "#FFFFFF"    // dinleme / vurgu
    const val GREEN = "#34D399"    // başarılı işlem
    const val RED = "#FF6B6B"      // uyarı / durdur
    const val ACCENT = "#C4B5FD"   // lavanta vurgu (tek vurgu rengi)
    const val BUBBLE_ME = "#1E1B2E"// kullanıcı balonu zemini (koyu lavanta alt tonu)
}

// ============================================================
// Küre: 0 bekleme (sönük gri), 1 dinliyor (beyaz), 2 konuşuyor (parlak beyaz), 3 başarılı (yeşil)
// Parlama için BlurMaskFilter yerine üst üste yarı saydam çizgiler kullanılır
// (donanım hızlandırmada her sürümde aynı çalışır).
// ============================================================
class HoloView(c: Context) : View(c) {
    // 0 bekleme, 1 dinleme, 2 konuşma, 3 başarılı, 4 düşünme, 5 araştırma, 6 bağlanma
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
    // 0..1: ses şiddeti
    var level: Float = 0f
    var dotScale: Float = 1f

    private val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val lineP = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.3f
        color = Color.WHITE
    }
    private var t = 0f
    private var sc = 0.9f
    private val n: Int
    private val px: FloatArray
    private val py: FloatArray
    private val pz: FloatArray
    private val pr: IntArray

    init {
        val xs = ArrayList<Float>()
        val ys = ArrayList<Float>()
        val zs = ArrayList<Float>()
        val rs = ArrayList<Int>()
        val rows = 15
        for (ri in 0 until rows) {
            val lat = -Math.PI / 2 + Math.PI * (ri + 0.5) / rows
            val y = Math.sin(lat)
            val rr = Math.cos(lat)
            val cnt = Math.max(5, Math.round(30 * rr).toInt())
            for (j in 0 until cnt) {
                val lon = 2 * Math.PI * j / cnt + (ri % 2) * Math.PI / cnt
                xs.add((Math.cos(lon) * rr).toFloat())
                ys.add(y.toFloat())
                zs.add((Math.sin(lon) * rr).toFloat())
                rs.add(ri)
            }
        }
        n = xs.size
        px = xs.toFloatArray()
        py = ys.toFloatArray()
        pz = zs.toFloatArray()
        pr = rs.toIntArray()
    }

    private fun hash(i: Int): Double {
        val v = Math.sin(i * 12.9898) * 43758.5453
        return v - Math.floor(v)
    }

    private fun put(cv: Canvas, x: Float, y: Float, size: Float, a: Int, col: Int) {
        dot.color = col
        dot.alpha = a.coerceIn(0, 255)
        cv.drawCircle(x, y, size, dot)
    }

    override fun onDraw(cv: Canvas) {
        if (animated) {
            t += when (state) {
                2 -> 0.045f
                1 -> 0.04f
                5 -> 0.035f
                4 -> 0.03f
                6 -> 0.03f
                else -> 0.02f
            }
        }
        val cx = width / 2f
        val cy = height / 2f
        val r = minOf(width, height) / 2f - 4f
        if (r <= 8f) return
        val target = when (state) {
            0 -> 0.86f
            1 -> 1.0f
            2 -> 0.94f
            3 -> 1.04f
            4 -> 0.9f
            5 -> 1.0f
            6 -> 0.92f
            else -> 0.9f
        }
        sc += (target - sc) * 0.06f
        if (level > 0.01f) level *= 0.88f else level = 0f
        val lv = level.toDouble()
        val u = r * 0.82f * sc * (1f + level * 0.1f)
        val col = if (state == 3) Color.parseColor(Pal.GREEN) else Color.WHITE
        val ds = maxOf(1.3f, r * 0.009f) * dotScale
        val rot = t * 0.5
        val cr = Math.cos(rot)
        val sr = Math.sin(rot)
        val tilt = 0.35
        val ct = Math.cos(tilt)
        val st = Math.sin(tilt)

        if (state == 99) {
            for (k in 0 until 5) {
                val phi = k * Math.PI / 5
                for (j in 0 until 16) {
                    val a = t * (1.0 + k * 0.25) + j * 2 * Math.PI / 16
                    val ox = Math.cos(a)
                    val oy = Math.sin(a) * Math.cos(phi)
                    val oz = Math.sin(a) * Math.sin(phi)
                    val ry = t * 0.3
                    val xr = ox * Math.cos(ry) + oz * Math.sin(ry)
                    val zr = -ox * Math.sin(ry) + oz * Math.cos(ry)
                    val rad = 1.0 + lv * 0.2
                    val dep = ((zr + 1) / 2).coerceIn(0.0, 1.0)
                    put(cv, cx + (xr * rad * u).toFloat(), cy + (oy * rad * u).toFloat(), ds * (0.6f + 0.9f * dep.toFloat()), (60 + 195 * dep).toInt(), col)
                }
            }
        } else if (state == 6) {
            val m = 30
            val qx = FloatArray(m)
            val qy = FloatArray(m)
            val qz = FloatArray(m)
            for (k in 0 until m) {
                val i = k * n / m
                val x0 = px[i].toDouble()
                val y0 = py[i].toDouble()
                val z0 = pz[i].toDouble()
                val xr = x0 * cr + z0 * sr
                val zr = -x0 * sr + z0 * cr
                qx[k] = xr.toFloat()
                qy[k] = (y0 * ct - zr * st).toFloat()
                qz[k] = (y0 * st + zr * ct).toFloat()
            }
            val prog = (t * 0.3) % 1.0
            for (k in 0 until m) {
                for (l in k + 1 until m) {
                    val dx = qx[k] - qx[l]
                    val dy = qy[k] - qy[l]
                    val dz = qz[k] - qz[l]
                    if (dx * dx + dy * dy + dz * dz < 0.8f && hash(k * m + l) < prog * 1.15) {
                        val dep = ((qz[k] + qz[l]) / 4f + 0.5f).coerceIn(0f, 1f)
                        lineP.alpha = (40 + 160 * dep).toInt()
                        cv.drawLine(cx + qx[k] * u, cy + qy[k] * u, cx + qx[l] * u, cy + qy[l] * u, lineP)
                    }
                }
            }
            for (k in 0 until m) {
                val dep = ((qz[k] + 1f) / 2f).coerceIn(0f, 1f)
                put(cv, cx + qx[k] * u, cy + qy[k] * u, ds * (1f + 1.2f * dep), (90 + 165 * dep).toInt(), col)
            }
        } else {
            val scan = (t * 1.6) % (2 * Math.PI) - Math.PI
            val ph = (t * 0.5) % 1.0
            val scr = maxOf(0.0, 1.0 - ph * 2.2)
            for (i in 0 until n) {
                var x0 = px[i].toDouble()
                var y0 = py[i].toDouble()
                var z0 = pz[i].toDouble()
                var rad = 1.0
                var boost = 0.0
                var vis = 1.0
                val lon = Math.atan2(z0, x0)
                if (state == 0) {
                    rad = 1.0 + 0.03 * Math.sin(t * 3.0)
                } else if (state == 1) {
                    rad = 1.0 + (0.04 + lv * 0.35) * Math.sin(pr[i] * 0.9 - t * 6.0)
                } else if (state == 4) {
                    val w = 0.45 * Math.sin(lon * 2 + t * 2.5)
                    boost = maxOf(0.0, 1.0 - Math.abs(y0 - w) / 0.35)
                    vis = 0.15 + 0.85 * boost
                    y0 += 0.12 * Math.sin(lon * 3 - t * 3.0)
                } else if (state == 5) {
                    var dl = Math.abs(lon - scan)
                    if (dl > Math.PI) dl = 2 * Math.PI - dl
                    boost = maxOf(0.0, 1.0 - dl / 0.4)
                } else if (state == 2) {
                    val wv = Math.sin(pr[i] * 0.7 - t * 7.0)
                    rad = 1.0 + (0.04 + lv * 0.3) * wv
                    boost = maxOf(0.0, wv) * 0.7
                } else if (state == 3) {
                    x0 += scr * 0.6 * (hash(i) - 0.5)
                    y0 += scr * 0.6 * (hash(i + 500) - 0.5)
                    z0 += scr * 0.6 * (hash(i + 900) - 0.5)
                }
                val xr = x0 * cr + z0 * sr
                val zr = -x0 * sr + z0 * cr
                val yt = y0 * ct - zr * st
                val zt = y0 * st + zr * ct
                val dep = ((zt + 1) / 2).coerceIn(0.0, 1.0)
                val a = ((35 + 190 * dep) * vis + 120 * boost).toInt()
                val sz = ds * (0.55f + 0.9f * dep.toFloat() + 0.8f * boost.toFloat())
                put(cv, cx + (xr * rad * u).toFloat(), cy + (yt * rad * u).toFloat(), sz, a, col)
            }
        }
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
    fun line(ctx: Context, who: String, text: String, sources: List<Pair<String, String>> = emptyList(), onSpeak: (() -> Unit)? = null, onVote: ((Int) -> Unit)? = null, suggestions: List<String> = emptyList(), onSuggest: ((String) -> Unit)? = null): View {
        val me = who == "Sen"
        val sys = who == "Sistem"
        val outer = android.widget.LinearLayout(ctx)
        outer.orientation = android.widget.LinearLayout.VERTICAL
        outer.setPadding(dp(ctx, 4), dp(ctx, 6), dp(ctx, 4), dp(ctx, 6))
        val box = android.widget.LinearLayout(ctx)
        box.orientation = android.widget.LinearLayout.VERTICAL
        val lp = android.widget.LinearLayout.LayoutParams(-2, -2)
        lp.gravity = if (me) android.view.Gravity.END else android.view.Gravity.START
        val name = when { me -> "Cem"; who == "J" -> "Aries"; else -> null }
        if (name != null) {
            val nv = TextView(ctx)
            nv.text = name
            nv.textSize = 11f
            nv.setTextColor(Color.parseColor(if (me) Pal.ACCENT else Pal.MUTED))
            nv.setPadding(dp(ctx, 6), dp(ctx, 0), dp(ctx, 6), dp(ctx, 3))
            val nlp = android.widget.LinearLayout.LayoutParams(-2, -2)
            nlp.gravity = lp.gravity
            outer.addView(nv, nlp)
        }
        val tv = TextView(ctx)
        tv.text = text
        tv.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
        tv.textSize = if (sys) 14f else 18f
        tv.setLineSpacing(0f, 1.4f)
        tv.setOnClickListener { val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager; cm.setPrimaryClip(android.content.ClipData.newPlainText("Aries", text)); android.widget.Toast.makeText(ctx, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show() }
        tv.setTextIsSelectable(true)
        tv.maxWidth = (ctx.resources.displayMetrics.widthPixels * 0.84f).toInt()
        if (sys) {
            tv.setTextColor(Color.parseColor("#B4B9D0"))
            lp.gravity = android.view.Gravity.CENTER_HORIZONTAL
        } else {
            tv.setTextColor(Color.parseColor("#FFFFFF"))
            val bg = android.graphics.drawable.GradientDrawable()
            bg.cornerRadius = dp(ctx, 18).toFloat()
            bg.setColor(Color.parseColor(if (me) Pal.BUBBLE_ME else Pal.CARD2))
            bg.setStroke(dp(ctx, 1), Color.parseColor(if (me) Pal.ACCENT else Pal.LINE))
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
                a.setTextColor(Color.parseColor("#B4B9D0"))
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
                sv.setBackgroundColor(Color.parseColor("#12151C"))
                sv.addView(big)
                android.app.AlertDialog.Builder(ctx).setView(sv).setPositiveButton("Kapat", null).show()
            })
            if (onSpeak != null) row.addView(act("Oku") { onSpeak() })
            if (onVote != null) {
                val vk = text.hashCode()
                val gri = "#B4B9D0"
                val up = icon(ctx, "jup", 18, if (oyDurum[vk] == 1) Pal.WHITE else gri)
                val dn = icon(ctx, "jdown", 18, if (oyDurum[vk] == 0) Pal.WHITE else gri)
                up.setPadding(dp(ctx, 10), dp(ctx, 8), dp(ctx, 10), dp(ctx, 6))
                dn.setPadding(dp(ctx, 10), dp(ctx, 8), dp(ctx, 10), dp(ctx, 6))
                up.setOnClickListener { oyDurum[vk] = 1; up.setColorFilter(Color.parseColor(Pal.WHITE)); dn.setColorFilter(Color.parseColor(gri)); onVote(1) }
                dn.setOnClickListener { oyDurum[vk] = 0; dn.setColorFilter(Color.parseColor(Pal.WHITE)); up.setColorFilter(Color.parseColor(gri)); onVote(0) }
                row.addView(up)
                row.addView(dn)
            }
            if (sources.isNotEmpty()) row.addView(act("Kaynak (" + sources.size + ")") {
                val names = sources.map { it.first }.toTypedArray()
                android.app.AlertDialog.Builder(ctx).setTitle("Kaynaklar").setItems(names) { _, w ->
                    try { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(sources[w].second))) } catch (e: Exception) {}
                }.show()
            })
            box.addView(row)
            if (suggestions.isNotEmpty() && onSuggest != null) {
                val srow = android.widget.LinearLayout(ctx)
                srow.orientation = android.widget.LinearLayout.HORIZONTAL
                srow.tag = "oneri"
                for (s in suggestions) {
                    val b = TextView(ctx)
                    b.text = s
                    b.textSize = 12f
                    b.setTextColor(Color.parseColor(Pal.ACCENT))
                    val sbg = android.graphics.drawable.GradientDrawable()
                    sbg.cornerRadius = dp(ctx, 14).toFloat()
                    sbg.setColor(Color.parseColor(Pal.CARD2))
                    sbg.setStroke(dp(ctx, 1), Color.parseColor(Pal.LINE))
                    b.background = sbg
                    b.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6))
                    b.setOnClickListener { onSuggest(s) }
                    val mlp = android.widget.LinearLayout.LayoutParams(-2, -2)
                    mlp.rightMargin = dp(ctx, 6)
                    srow.addView(b, mlp)
                }
                val slp = android.widget.LinearLayout.LayoutParams(-2, -2)
                slp.topMargin = dp(ctx, 4)
                box.addView(srow, slp)
            }
        }
        return outer
    }
}

private val oyDurum = HashMap<Int, Int>()
