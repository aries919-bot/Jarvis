package com.cem.jarvis

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class CareerNewsVerificationActivity : Activity() {
    private lateinit var store: CareerNewsVerificationStore
    private lateinit var content: LinearLayout
    private lateinit var status: TextView
    private lateinit var record: CareerNewsVerificationRecord
    private val padding by lazy { (resources.displayMetrics.density * 16).toInt() }

    companion object {
        const val EXTRA_STORY_ID = "news_story_id"
        const val EXTRA_TITLE = "news_title"
        const val EXTRA_URL = "news_url"
        const val EXTRA_PUBLISHER = "news_publisher"
        const val EXTRA_PUBLISHED_AT = "news_published_at"
        const val EXTRA_COLLECTED_AT = "news_collected_at"
        const val EXTRA_FIRST_SEEN_AT = "news_first_seen_at"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = CareerNewsVerificationStore(SharedPreferencesCareerNewsStorage(this))

        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty().trim()
        val sourceUrl = intent.getStringExtra(EXTRA_URL).orEmpty().trim()
        if (title.isBlank() || CareerNewsVerificationPolicy.validateHttpsUrl(sourceUrl) != null) {
            Toast.makeText(this, "Haber başlığı veya HTTPS kaynak bağlantısı geçersiz.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
            ?.takeIf { it.isNotBlank() }
            ?: CareerNewsVerificationRecord.stableStoryId(sourceUrl, title)

        record = store.load(storyId) ?: CareerNewsVerificationRecord(
            storyId = storyId,
            headline = title,
            publisher = intent.getStringExtra(EXTRA_PUBLISHER).orEmpty().ifBlank {
                runCatching { URI(sourceUrl).host.orEmpty() }.getOrDefault("Bilinmeyen kaynak")
            },
            originalSourceUrl = sourceUrl,
            publishedAt = intent.getStringExtra(EXTRA_PUBLISHED_AT)
                ?.takeIf(CareerNewsVerificationPolicy::isUtcTimestamp),
            collectedAt = intent.getStringExtra(EXTRA_COLLECTED_AT)
                ?.takeIf(CareerNewsVerificationPolicy::isUtcTimestamp),
            firstSeenAt = intent.getStringExtra(EXTRA_FIRST_SEEN_AT)
                ?.takeIf(CareerNewsVerificationPolicy::isUtcTimestamp)
        )

        val saved = store.save(record)
        if (!saved.saved) {
            Toast.makeText(this, saved.reason ?: "Haber doğrulama kaydı açılamadı.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        render()
    }

    private fun dp(value: Int): Int = (resources.displayMetrics.density * value).toInt()

    private fun text(value: String, size: Float = 14f, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(0xff203047.toInt())
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(4))
        }

    private fun button(value: String, action: () -> Unit): Button =
        Button(this).apply {
            text = value
            isAllCaps = false
            setOnClickListener { action() }
        }

    private fun panel(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(14), dp(12), dp(14), dp(12))
        setBackgroundColor(0xfff1f5f9.toInt())
    }

    private fun field(hintText: String, multiline: Boolean = false): EditText =
        EditText(this).apply {
            hint = hintText
            textSize = 14f
            if (multiline) {
                minLines = 3
                maxLines = 6
                gravity = Gravity.TOP
            } else {
                singleLine = true
            }
        }

    private fun render() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(12))
            setBackgroundColor(0xfff8fafc.toInt())
        }
        root.addView(button("‹  Haber arşivine dön") { finish() })
        root.addView(text("HABER / İDDİA DOĞRULAMA", 11f, true))
        root.addView(text(record.headline, 20f, true))
        root.addView(text(record.publisher + " · " + record.originalSourceUrl, 12f))
        root.addView(button("Asıl kaynak sayfasını aç") { openExternal(record.originalSourceUrl) })
        root.addView(text(
            "Yayın tarihi: " + (record.publishedAt ?: "Kaynakta belirtilmemiş") +
                "\nAries'in toplama zamanı: " + (record.collectedAt ?: "Bilinmiyor") +
                "\nİlk görülme zamanı: " + (record.firstSeenAt ?: "Eski kayıtta bilinmiyor"),
            11f
        ))
        status = text("", 14f, true)
        root.addView(status)
        root.addView(text(
            "Aries tek başına haber doğrulamaz. İddia için asıl kaynak, destekleyen/çelişen kanıt ve açık insan incelemesi gerekir.",
            12f
        ))
        root.addView(button("＋  İddia ekle") { addClaimDialog() })
        root.addView(text("İDDİALAR VE KANITLAR", 11f, true))

        if (record.claims.isEmpty()) {
            root.addView(panel().apply {
                addView(text("Henüz ayrı bir iddia kaydedilmedi.", 14f, true))
                addView(text("Haber başlığını kopyalamak yerine, kaynakla karşılaştırılabilecek tek bir olgusal cümle ekle.", 12f))
            })
        } else {
            record.claims.forEach { claim ->
                val card = panel()
                card.addView(text(claim.text, 15f, true))
                card.addView(text(
                    verdictLabel(claim.verdict()) + " · " + claim.evidence.size + " kanıt",
                    12f, true
                ))
                claim.evidence.forEach { evidence ->
                    card.addView(text(
                        evidence.publisher + " · " + sourceTypeLabel(evidence.sourceType) +
                            " · " + stanceLabel(evidence.stance),
                        11f, true
                    ))
                    card.addView(text(evidence.excerpt, 12f))
                    card.addView(button("Kanıt kaynağını aç") { openExternal(evidence.sourceUrl) })
                }
                val actions = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                actions.addView(button("＋ Kanıt ekle") { addEvidenceDialog(claim.id) })
                actions.addView(button("İnsan incelemesi") { reviewDialog(claim.id) })
                actions.addView(button("İddiayı düzelt") { correctClaimDialog(claim.id) })
                card.addView(actions)
                root.addView(card)
            }
        }

        root.addView(text("DÜZELTME GEÇMİŞİ", 11f, true))
        if (record.correctionHistory.isEmpty()) {
            root.addView(text("Henüz düzeltme kaydı yok.", 12f))
        } else {
            record.correctionHistory.asReversed().forEach { correction ->
                root.addView(panel().apply {
                    addView(text(correction.previousText + "\n↓\n" + correction.revisedText, 13f, true))
                    addView(text(
                        correction.correctedAt + " · " + verdictLabel(correction.previousVerdict) +
                            " → " + verdictLabel(correction.newVerdict),
                        11f
                    ))
                    addView(text(correction.explanation, 12f))
                    addView(button("Düzeltme kaynağını aç") { openExternal(correction.sourceUrl) })
                })
            }
        }

        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
        status.text = "Haber durumu: " + newsVerdictLabel(record.verdict())
    }

    private fun persist(updated: CareerNewsVerificationRecord): Boolean {
        val result = store.save(updated)
        if (!result.saved) {
            Toast.makeText(this, result.reason ?: "Kayıt saklanamadı.", Toast.LENGTH_LONG).show()
            return false
        }
        record = updated
        render()
        return true
    }

    private fun addClaimDialog() {
        val input = field("Tek ve doğrulanabilir iddia cümlesi yaz", true)
        val dialog = AlertDialog.Builder(this)
            .setTitle("İddia ekle")
            .setView(input)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("Kaydet", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = input.text.toString().trim()
                val updated = record.addClaim(CareerNewsClaim(
                    id = UUID.randomUUID().toString(),
                    text = value
                ))
                if (updated == null) {
                    Toast.makeText(this, "İddia boş, kişisel veri içeriyor veya kayıt sınırını aşıyor.", Toast.LENGTH_LONG).show()
                } else if (persist(updated)) {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun addEvidenceDialog(claimId: String) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), 0)
        }
        val url = field("HTTPS kaynak bağlantısı")
        val publisher = field("Kaynak kurum / yayıncı")
        val excerpt = field("Kaynakta görülen kısa kanıt alıntısı", true)
        val sourceType = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@CareerNewsVerificationActivity,
                android.R.layout.simple_spinner_dropdown_item,
                CareerEvidenceSourceType.values().map(::sourceTypeLabel)
            )
        }
        val stance = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@CareerNewsVerificationActivity,
                android.R.layout.simple_spinner_dropdown_item,
                CareerEvidenceStance.values().map(::stanceLabel)
            )
        }
        layout.addView(text("Kaynak türü", 12f, true))
        layout.addView(sourceType)
        layout.addView(text("İddia ile ilişkisi", 12f, true))
        layout.addView(stance)
        layout.addView(url)
        layout.addView(publisher)
        layout.addView(excerpt)
        layout.addView(text(
            "Birincil kaynak seçimi yalnız izinli/resmî kurum veya işveren alanlarında kabul edilir. Haber sitesi ve iş panosu birincil kaynak sayılmaz.",
            11f
        ))
        val dialog = AlertDialog.Builder(this)
            .setTitle("Kanıt ekle")
            .setView(layout)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("Kanıtı kaydet", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val item = CareerNewsEvidence(
                    id = UUID.randomUUID().toString(),
                    publisher = publisher.text.toString().trim(),
                    sourceUrl = url.text.toString().trim(),
                    sourceType = CareerEvidenceSourceType.values()[sourceType.selectedItemPosition],
                    stance = CareerEvidenceStance.values()[stance.selectedItemPosition],
                    excerpt = excerpt.text.toString().trim(),
                    publishedAt = null,
                    checkedAt = utcNow()
                )
                val updated = record.addEvidence(claimId, item)
                if (updated == null) {
                    Toast.makeText(this, "Kanıt URL'si, alıntı veya kaynak türü geçersiz. Tarih uydurulmadı.", Toast.LENGTH_LONG).show()
                } else if (persist(updated)) {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun reviewDialog(claimId: String) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), 0)
        }
        val note = field("Asıl kaynakla karşılaştırma notu", true)
        val checked = CheckBox(this).apply {
            text = "Kaynağı açtım ve iddiayı kaynak metniyle karşılaştırdım"
        }
        val decision = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@CareerNewsVerificationActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Kararsız", "Doğrulandı", "Çürütüldü")
            )
        }
        layout.addView(decision)
        layout.addView(note)
        layout.addView(checked)
        val dialog = AlertDialog.Builder(this)
            .setTitle("İnsan incelemesi")
            .setView(layout)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("İncelemeyi kaydet", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (!checked.isChecked || note.text.toString().trim().isBlank()) {
                    Toast.makeText(this, "Kaynağı karşılaştırıp kısa inceleme notu yazmalısın.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val choice = when (decision.selectedItemPosition) {
                    1 -> CareerHumanReviewDecision.CONFIRM
                    2 -> CareerHumanReviewDecision.REFUTE
                    else -> CareerHumanReviewDecision.INCONCLUSIVE
                }
                val updated = record.reviewClaim(claimId, choice, utcNow(), note.text.toString())
                if (updated == null) {
                    Toast.makeText(this, "İnsan incelemesi kaydedilemedi; birincil kanıt ve zaman koşullarını kontrol et.", Toast.LENGTH_LONG).show()
                } else if (persist(updated)) {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun correctClaimDialog(claimId: String) {
        val claim = record.claims.firstOrNull { it.id == claimId } ?: return
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), 0)
        }
        val revisedText = field("Düzeltilmiş tek iddia cümlesi", true).apply { setText(claim.text) }
        val sourceUrl = field("Düzeltmeyi destekleyen HTTPS kaynak")
        val explanation = field("Düzeltme nedeni", true)
        layout.addView(revisedText)
        layout.addView(sourceUrl)
        layout.addView(explanation)
        layout.addView(text(
            "Düzeltme eski kanıt ve insan kararını arşivde saklar; yeni iddia için kanıt ve inceleme yeniden gerekir.",
            11f
        ))
        val dialog = AlertDialog.Builder(this)
            .setTitle("İddiayı düzelt")
            .setView(layout)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("Düzeltmeyi kaydet", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val revised = claim.copy(text = revisedText.text.toString().trim())
                val updated = record.correctClaim(
                    revisedClaim = revised,
                    correctedAt = utcNow(),
                    explanation = explanation.text.toString().trim(),
                    sourceUrl = sourceUrl.text.toString().trim()
                )
                if (updated == null) {
                    Toast.makeText(this, "Düzeltme metni, kaynak URL'si veya açıklaması geçersiz.", Toast.LENGTH_LONG).show()
                } else if (persist(updated)) {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun openExternal(url: String) {
        if (CareerNewsVerificationPolicy.validateHttpsUrl(url) != null) {
            Toast.makeText(this, "Güvenli HTTPS kaynak bağlantısı değil.", Toast.LENGTH_LONG).show()
            return
        }
        runCatching {
            CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(this, Uri.parse(url))
        }.onFailure {
            Toast.makeText(this, "Kaynak açılamadı.", Toast.LENGTH_LONG).show()
        }
    }

    private fun utcNow(): String = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        Locale.US
    ).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

    private fun sourceTypeLabel(value: CareerEvidenceSourceType): String = when (value) {
        CareerEvidenceSourceType.PRIMARY_SOURCE -> "Birincil / resmî kaynak"
        CareerEvidenceSourceType.INDEPENDENT_REPORTING -> "Bağımsız haber"
        CareerEvidenceSourceType.SECONDARY -> "İkincil kaynak"
        CareerEvidenceSourceType.UNKNOWN -> "Kaynak türü bilinmiyor"
    }

    private fun stanceLabel(value: CareerEvidenceStance): String = when (value) {
        CareerEvidenceStance.SUPPORTS -> "Destekliyor"
        CareerEvidenceStance.CONTRADICTS -> "Çelişiyor"
        CareerEvidenceStance.CONTEXT_ONLY -> "Yalnız bağlam"
    }

    private fun verdictLabel(value: CareerClaimVerdict): String = when (value) {
        CareerClaimVerdict.NEEDS_EVIDENCE -> "Kanıt bekliyor"
        CareerClaimVerdict.CONTEXT_MISSING -> "Bağlam eksik"
        CareerClaimVerdict.SUPPORTS_PENDING_REVIEW -> "İnsan incelemesi bekliyor"
        CareerClaimVerdict.CONTRADICTION_PENDING_REVIEW -> "Karşıt kanıt incelemesi bekliyor"
        CareerClaimVerdict.CONFLICTING_EVIDENCE -> "Çelişkili kanıt"
        CareerClaimVerdict.VERIFIED -> "Doğrulandı"
        CareerClaimVerdict.REFUTED -> "Çürütüldü"
    }

    private fun newsVerdictLabel(value: CareerNewsVerdict): String = when (value) {
        CareerNewsVerdict.NEEDS_CLAIMS -> "İddia eklenmedi"
        CareerNewsVerdict.NEEDS_EVIDENCE -> "Kanıt bekliyor"
        CareerNewsVerdict.CONTEXT_MISSING -> "Bağlam eksik"
        CareerNewsVerdict.REVIEW_PENDING -> "İnsan incelemesi bekliyor"
        CareerNewsVerdict.CONFLICTING_EVIDENCE -> "Çelişkili kanıt"
        CareerNewsVerdict.VERIFIED -> "Doğrulandı"
        CareerNewsVerdict.REFUTED -> "Çürütüldü"
        CareerNewsVerdict.PARTIALLY_VERIFIED -> "Kısmen doğrulandı"
    }
}
