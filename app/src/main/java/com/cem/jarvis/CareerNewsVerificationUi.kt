package com.cem.jarvis

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/** Small, explicit user-driven evidence/review UI for an existing Haber card. */
class CareerNewsVerificationUi(
    private val activity: Activity,
    private val store: CareerNewsVerificationStore
) {
    fun createActionButton(
        headline: String,
        publisher: String,
        sourceUrl: String,
        collectedAtMillis: Long,
        firstSeenAtMillis: Long
    ): Button = Button(activity).apply {
        text = "İddia ve kanıtları incele"
        minHeight = dp(48)
        setOnClickListener {
            val record = store.getOrCreate(
                headline = headline,
                publisher = publisher,
                originalSourceUrl = sourceUrl,
                collectedAtMillis = collectedAtMillis,
                firstSeenAtMillis = firstSeenAtMillis
            )
            if (record == null) {
                toast("Doğrulama kaydı oluşturulamadı. Kaynak HTTPS olmalı ve yerel arşiv yazılabilmeli.")
            } else {
                showRecord(record)
            }
        }
    }

    private fun showRecord(record: CareerNewsVerificationRecord) {
        val body = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
        }
        val scroll = ScrollView(activity).apply { addView(body) }
        val dialog = AlertDialog.Builder(activity)
            .setTitle("Haber doğrulama")
            .setView(scroll)
            .setPositiveButton("Kapat", null)
            .create()

        addText(body, record.headline, bold = true, size = 17)
        addText(body, "Yayıncı: " + record.publisher)
        addText(body, "Asıl kaynak: " + record.originalSourceUrl)
        addText(body, "Yayın tarihi: " + (record.publishedAt ?: "Kaynakta belirtilmemiş"))
        addText(body, "Aries toplama zamanı: " + record.collectedAt)
        addText(body, "H1 kaydında ilk görülme: " + record.firstSeenAt)
        addText(body, "Genel durum: " + newsVerdictLabel(record.verdict()), bold = true)
        addText(
            body,
            "Bu durum otomatik AI hükmü değildir. Doğrulandı/Çürütüldü için izinli birincil kaynak kanıtı ve gerekçeli insan incelemesi gerekir."
        )
        addButton(body, "Yeni iddia ekle") { promptNewClaim(record, dialog) }

        if (record.claims.isEmpty()) {
            addText(body, "Henüz iddia eklenmedi. Haber başlığını değil, tek tek doğrulanabilir ifadeleri ekle.")
        }

        record.claims.forEach { claim ->
            addText(body, "İDDİA · " + claim.text, bold = true, size = 15)
            addText(body, "Durum: " + claimVerdictLabel(claim.verdict()), bold = true)
            if (claim.reviewDecision != null) {
                addText(body, "İnsan incelemesi: " + claim.reviewDecision.name +
                    " · " + claim.reviewedAt + " · " + claim.reviewNote)
            }
            claim.evidence.forEachIndexed { index, evidence ->
                addText(
                    body,
                    "Kanıt " + (index + 1) + " · " + sourceTypeLabel(evidence.sourceType) +
                        " · " + stanceLabel(evidence.stance),
                    bold = true
                )
                addText(body, evidence.publisher + " · " + evidence.sourceUrl)
                addText(body, "Alıntı: " + evidence.excerpt)
                addText(body, "Kaynak yayın tarihi: " + (evidence.publishedAt ?: "Belirtilmemiş") +
                    " · Aries kontrolü: " + evidence.checkedAt)
            }
            val actions = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }
            addButton(actions, "Bu iddiaya kanıt ekle") { promptEvidence(record, claim.id, dialog) }
            addButton(actions, "İnsan incelemesi yap") { promptReview(record, claim.id, dialog) }
            addButton(actions, "İddiayı düzelt") { promptCorrection(record, claim.id, dialog) }
            body.addView(actions)
        }

        if (record.correctionHistory.isNotEmpty()) {
            addText(body, "DÜZELTME GEÇMİŞİ", bold = true, size = 15)
            record.correctionHistory.asReversed().forEach { correction ->
                addText(body, correction.previousText + " → " + correction.revisedText, bold = true)
                addText(body, correction.correctedAt + " · " + correction.explanation)
                addText(body, "Düzeltme kaynağı: " + correction.sourceUrl)
                addText(body, "Önceki kanıt adedi: " + correction.previousEvidence.size +
                    " · Önceki inceleme: " + (correction.previousReviewDecision?.name ?: "yok"))
            }
        }
        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.94f).toInt(),
                (activity.resources.displayMetrics.heightPixels * 0.82f).toInt()
            )
        }
        dialog.show()
    }

    private fun promptNewClaim(record: CareerNewsVerificationRecord, parent: AlertDialog) {
        val input = editText("Tek bir doğrulanabilir iddia yaz", multiline = true)
        AlertDialog.Builder(activity)
            .setTitle("İddia ekle")
            .setMessage("Örnek: Kurum 12 teknisyen alacağını duyurdu. Birden fazla iddiayı tek cümlede birleştirme.")
            .setView(input)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("Ekle") { _, _ ->
                val value = input.text.toString().trim()
                val updated = record.addClaim(CareerNewsClaim(UUID.randomUUID().toString(), value))
                if (updated == null) {
                    toast("İddia boş, çok uzun veya kayıtla uyumsuz.")
                } else {
                    saveAndRefresh(updated, parent)
                }
            }
            .show()
    }

    private fun promptEvidence(
        record: CareerNewsVerificationRecord,
        claimId: String,
        parent: AlertDialog
    ) {
        val fields = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), 0)
        }
        val publisher = editText("Kaynağı yayımlayan kurum")
        val url = editText("Kaynağın HTTPS adresi")
        val excerpt = editText("İlgili kısa kanıt alıntısı", multiline = true)
        val publishedAt = editText("Kaynak yayın tarihi (UTC ISO-8601, yoksa boş bırak)")
        val typeSpinner = spinner(
            listOf("Birincil resmî kurum/işveren", "Bağımsız haber kuruluşu", "İkincil kaynak", "Kaynak türü bilinmiyor")
        )
        val stanceSpinner = spinner(listOf("İddiayı destekliyor", "İddiayla çelişiyor", "Yalnız bağlam sağlıyor"))
        addText(fields, "Kaynak türü")
        fields.addView(typeSpinner)
        addText(fields, "Kanıtın yönü")
        fields.addView(stanceSpinner)
        fields.addView(publisher)
        fields.addView(url)
        fields.addView(excerpt)
        fields.addView(publishedAt)
        AlertDialog.Builder(activity)
            .setTitle("Kanıt ekle")
            .setMessage("Birincil kaynak seçilirse bağlantı kurum/işveren izin listesinde olmalı. Arama snippet'i tek başına birincil kanıt değildir.")
            .setView(fields)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("Kanıtı kaydet") { _, _ ->
                val rawExcerpt = excerpt.text.toString().trim()
                val safeExcerpt = CareerNewsVerificationPolicy.sanitizeEvidenceExcerpt(rawExcerpt)
                if (safeExcerpt != rawExcerpt) toast("E-posta/telefon/IBAN/kimlik numarası desenleri alıntıdan maskelendi.")
                val sourceType = CareerEvidenceSourceType.values()[typeSpinner.selectedItemPosition]
                val stance = CareerEvidenceStance.values()[stanceSpinner.selectedItemPosition]
                val evidence = CareerNewsEvidence(
                    id = UUID.randomUUID().toString(),
                    publisher = publisher.text.toString().trim(),
                    sourceUrl = url.text.toString().trim(),
                    sourceType = sourceType,
                    stance = stance,
                    excerpt = safeExcerpt,
                    publishedAt = publishedAt.text.toString().trim().takeIf { it.isNotBlank() },
                    checkedAt = utcNow()
                )
                val updated = record.addEvidence(claimId, evidence)
                if (updated == null) {
                    toast("Kanıt kaydedilmedi. HTTPS, tarih, alıntı ve birincil kaynak izin listesini kontrol et.")
                } else {
                    saveAndRefresh(updated, parent)
                }
            }
            .show()
    }

    private fun promptReview(
        record: CareerNewsVerificationRecord,
        claimId: String,
        parent: AlertDialog
    ) {
        val labels = arrayOf("Destekleniyor · CONFIRM", "Çürütülüyor · REFUTE", "Sonuçsuz · INCONCLUSIVE")
        val decisions = CareerHumanReviewDecision.values()
        AlertDialog.Builder(activity)
            .setTitle("İnsan incelemesi")
            .setMessage("Karar gerekçe ve UTC zamanıyla saklanır. Kanıt yoksa veya birincil kaynak uygun değilse doğrulanmış sonucu oluşmaz.")
            .setItems(labels) { _, which ->
                val note = editText("Neden bu kararı verdiğini açıkla", multiline = true)
                AlertDialog.Builder(activity)
                    .setTitle("İnceleme gerekçesi")
                    .setView(note)
                    .setNegativeButton("Vazgeç", null)
                    .setPositiveButton("Kararı kaydet") { _, _ ->
                        val updated = record.reviewClaim(
                            claimId,
                            decisions[which],
                            utcNow(),
                            note.text.toString()
                        )
                        if (updated == null) {
                            toast("İnceleme kaydedilmedi. Gerekçe boş olamaz.")
                        } else {
                            saveAndRefresh(updated, parent)
                        }
                    }
                    .show()
            }
            .setNegativeButton("Vazgeç", null)
            .show()
    }

    private fun promptCorrection(
        record: CareerNewsVerificationRecord,
        claimId: String,
        parent: AlertDialog
    ) {
        val claim = record.claims.firstOrNull { it.id == claimId } ?: return
        val fields = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), 0)
        }
        val revisedText = editText("Düzeltilmiş tekil iddia", multiline = true).apply {
            setText(claim.text)
        }
        val explanation = editText("Düzeltme gerekçesi", multiline = true)
        val sourceUrl = editText("Düzeltmeyi destekleyen HTTPS kaynak")
        fields.addView(revisedText)
        fields.addView(explanation)
        fields.addView(sourceUrl)
        AlertDialog.Builder(activity)
            .setTitle("İddiayı düzelt")
            .setMessage("Eski metin, kanıtlar ve insan incelemesi silinmez; düzeltme geçmişine taşınır. Yeni iddia yeniden kanıt ve inceleme bekler.")
            .setView(fields)
            .setNegativeButton("Vazgeç", null)
            .setPositiveButton("Düzeltmeyi kaydet") { _, _ ->
                val updated = record.correctClaim(
                    revisedClaim = claim.copy(text = revisedText.text.toString().trim()),
                    correctedAt = utcNow(),
                    explanation = explanation.text.toString(),
                    sourceUrl = sourceUrl.text.toString()
                )
                if (updated == null) {
                    toast("Düzeltme kaydedilmedi. Metin değişmeli; açıklama ve geçerli HTTPS kaynağı gerekli.")
                } else {
                    saveAndRefresh(updated, parent)
                }
            }
            .show()
    }

    private fun saveAndRefresh(
        updated: CareerNewsVerificationRecord,
        parent: AlertDialog
    ) {
        if (!store.save(updated)) {
            toast("Yerel kayıt kaydedilemedi. Arşiv bozuk veya güvenli kapasite sınırına ulaştı; mevcut kayıt korunuyor.")
            return
        }
        parent.dismiss()
        showRecord(updated)
    }

    private fun spinner(values: List<String>): Spinner = Spinner(activity).apply {
        adapter = ArrayAdapter(
            activity,
            android.R.layout.simple_spinner_dropdown_item,
            values
        )
    }

    private fun editText(hintText: String, multiline: Boolean = false): EditText =
        EditText(activity).apply {
            hint = hintText
            textSize = 14f
            if (multiline) {
                minLines = 2
                maxLines = 5
                setSingleLine(false)
                gravity = android.view.Gravity.TOP
            }
        }

    private fun addText(
        parent: LinearLayout,
        value: String,
        bold: Boolean = false,
        size: Int = 13
    ) {
        parent.addView(TextView(activity).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(0xFF26384D.toInt())
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(5), 0, dp(5))
        })
    }

    private fun addButton(parent: LinearLayout, title: String, action: () -> Unit) {
        parent.addView(Button(activity).apply {
            text = title
            minHeight = dp(48)
            setOnClickListener { action() }
        }, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(3); bottomMargin = dp(3) })
    }

    private fun claimVerdictLabel(verdict: CareerClaimVerdict): String = when (verdict) {
        CareerClaimVerdict.NEEDS_EVIDENCE -> "Kanıt bekleniyor"
        CareerClaimVerdict.CONTEXT_MISSING -> "Bağlam eksik"
        CareerClaimVerdict.SUPPORTS_PENDING_REVIEW -> "Destekleyici birincil kanıt var · insan incelemesi bekliyor"
        CareerClaimVerdict.CONTRADICTION_PENDING_REVIEW -> "Çelişen birincil kanıt var · insan incelemesi bekliyor"
        CareerClaimVerdict.CONFLICTING_EVIDENCE -> "Çelişkili kanıt"
        CareerClaimVerdict.VERIFIED -> "Doğrulandı"
        CareerClaimVerdict.REFUTED -> "Çürütüldü"
    }

    private fun newsVerdictLabel(verdict: CareerNewsVerdict): String = when (verdict) {
        CareerNewsVerdict.NEEDS_CLAIMS -> "İddia bekleniyor"
        CareerNewsVerdict.NEEDS_EVIDENCE -> "Kanıt bekleniyor"
        CareerNewsVerdict.CONTEXT_MISSING -> "Bağlam eksik"
        CareerNewsVerdict.REVIEW_PENDING -> "İnsan incelemesi bekliyor"
        CareerNewsVerdict.CONFLICTING_EVIDENCE -> "Çelişkili kanıt"
        CareerNewsVerdict.VERIFIED -> "Doğrulandı"
        CareerNewsVerdict.REFUTED -> "Çürütüldü"
        CareerNewsVerdict.PARTIALLY_VERIFIED -> "Kısmen doğrulandı"
    }

    private fun sourceTypeLabel(value: CareerEvidenceSourceType): String = when (value) {
        CareerEvidenceSourceType.PRIMARY_SOURCE -> "Birincil resmî kaynak"
        CareerEvidenceSourceType.INDEPENDENT_REPORTING -> "Bağımsız haber"
        CareerEvidenceSourceType.SECONDARY -> "İkincil kaynak"
        CareerEvidenceSourceType.UNKNOWN -> "Bilinmeyen kaynak"
    }

    private fun stanceLabel(value: CareerEvidenceStance): String = when (value) {
        CareerEvidenceStance.SUPPORTS -> "Destekler"
        CareerEvidenceStance.CONTRADICTS -> "Çelişir"
        CareerEvidenceStance.CONTEXT_ONLY -> "Bağlam"
    }

    private fun utcNow(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

    private fun dp(value: Int): Int =
        (value * activity.resources.displayMetrics.density).toInt()

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}
