package com.cem.jarvis

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.*

class CareerActivity : Activity() {
    private lateinit var cv: EditText
    private lateinit var role: EditText
    private lateinit var place: EditText
    private lateinit var results: LinearLayout
    private lateinit var search: Button
    private val prefs by lazy { getSharedPreferences("j", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(11, 13, 20))
        }
        root.addView(Button(this).apply { text = "‹ Geri"; setOnClickListener { finish() } })
        root.addView(TextView(this).apply { text = "İş ve Kariyer"; textSize = 22f; setTextColor(Color.WHITE) })
        cv = EditText(this).apply { hint = "CV metnini yapıştır"; setText(prefs.getString("career_cv", "")); minLines = 5 }
        root.addView(cv)
        role = EditText(this).apply { hint = "Pozisyon"; setText(prefs.getString("career_role", "Elektrik teknisyeni")) }
        root.addView(role)
        place = EditText(this).apply { hint = "Şehir / bölge"; setText(prefs.getString("career_place", "Türkiye geneli")) }
        root.addView(place)
        search = Button(this).apply { text = "Ücretsiz ilan ara"; setOnClickListener { searchJobs() } }
        root.addView(search)
        val scroll = ScrollView(this)
        results = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(results)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun searchJobs() {
        val q = role.text.toString() + " " + place.text.toString() + " elektrik iş ilanı 2026"
        prefs.edit().putString("career_cv", cv.text.toString()).apply()
        search.isEnabled = false
        Thread {
            val hits = try { Net.webSearch(q) } catch (e: Exception) { emptyList() }
            runOnUiThread {
                search.isEnabled = true
                results.removeAllViews()
                hits.forEach { hit ->
                    val row = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                    row.addView(TextView(this).apply { text = hit.title + "\n" + hit.snippet; setTextColor(Color.WHITE) })
                    row.addView(Button(this).apply { text = "Aç"; setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(hit.url))) } })
                    results.addView(row)
                }
            }
        }.start()
    }
}