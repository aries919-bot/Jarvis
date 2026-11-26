package com.cem.jarvis

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class Conv(val id: String, var title: String, var updated: Long, val msgs: MutableList<Pair<String, String>>)

class Store(ctx: Context) {
    private val app: Context = ctx.applicationContext
    private val SECRET = setOf("key", "bulut", "groq", "mistral", "finnhub")
    val prefs: SharedPreferences = app.getSharedPreferences("j", Context.MODE_PRIVATE)
    private val cache = ArrayList<Conv>()

    init { migrateSecrets(); loadConvs() }

    fun str(k: String, def: String = ""): String {
        if (k in SECRET) {
            val v = SecureStore.get(app, k)
            if (v.isNotEmpty()) return v
        }
        return prefs.getString(k, def) ?: def
    }

    fun put(k: String, v: String) {
        if (k in SECRET) {
            if (v.isEmpty()) {
                SecureStore.remove(app, k)
                prefs.edit().remove(k).apply()
                return
            }
            if (SecureStore.put(app, k, v)) {
                prefs.edit().remove(k).apply()
                return
            }
        }
        prefs.edit().putString(k, v).apply()
    }

    private fun migrateSecrets() {
        for (k in SECRET) {
            val old = prefs.getString(k, "") ?: ""
            if (old.isNotBlank() && SecureStore.put(app, k, old)) prefs.edit().remove(k).apply()
        }
    }

    fun flag(k: String, def: Boolean): Boolean = prefs.getBoolean(k, def)

    fun setFlag(k: String, v: Boolean) {
        prefs.edit().putBoolean(k, v).apply()
        if (k == "histOn" && !v) prefs.edit().remove("convs").apply()
    }

    fun convs(): List<Conv> = cache.sortedByDescending { it.updated }

    fun conv(id: String): Conv? = cache.firstOrNull { it.id == id }

    fun newConv(): Conv {
        val now = System.currentTimeMillis()
        val c = Conv(now.toString(), "Yeni sohbet", now, ArrayList())
        cache.add(c)
        save()
        return c
    }

    fun current(): Conv {
        val found = conv(str("cur"))
        if (found != null) return found
        val latest = cache.maxByOrNull { it.updated }
        return latest ?: newConv()
    }

    fun setCurrent(c: Conv) {
        put("cur", c.id)
    }

    fun addMsg(c: Conv, role: String, text: String) {
        c.msgs.add(Pair(role, text))
        while (c.msgs.size > 200) c.msgs.removeAt(0)
        c.updated = System.currentTimeMillis()
        if (role == "user" && c.title == "Yeni sohbet") c.title = text.take(32)
        save()
    }

    fun deleteConv(id: String) {
        cache.removeAll { it.id == id }
        save()
    }

    fun clearConvs() {
        cache.clear()
        put("cur", "")
        save()
    }

    private fun save() {
        if (!flag("histOn", true)) return
        val arr = JSONArray()
        for (c in cache) {
            val msgs = JSONArray()
            for ((r, t) in c.msgs) msgs.put(JSONObject().put("r", r).put("t", t))
            arr.put(JSONObject().put("id", c.id).put("title", c.title).put("updated", c.updated).put("msgs", msgs))
        }
        put("convs", arr.toString())
    }

    private fun loadConvs() {
        val raw = str("convs", "")
        if (raw.isBlank()) return
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val msgs = ArrayList<Pair<String, String>>()
                val ma = o.getJSONArray("msgs")
                for (j in 0 until ma.length()) {
                    val m = ma.getJSONObject(j)
                    msgs.add(Pair(m.getString("r"), m.getString("t")))
                }
                cache.add(Conv(o.getString("id"), o.getString("title"), o.getLong("updated"), msgs))
            }
        } catch (e: Exception) {
            cache.clear()
        }
    }

    fun facts(): String = str("facts")

    fun addFact(f: String) {
        val old = facts()
        put("facts", (if (old.isBlank()) f.trim() else old + "; " + f.trim()).takeLast(1500))
    }

    fun clearFacts() {
        prefs.edit().remove("facts").apply()
    }

    fun tasks(): JSONArray = try {
        JSONArray(str("tasks", "[]"))
    } catch (e: Exception) {
        JSONArray()
    }

    fun saveTasks(arr: JSONArray) {
        put("tasks", arr.toString())
    }

    fun watch(): List<String> = str("watch").split(",").map { it.trim() }.filter { it.isNotEmpty() }

    fun addWatch(sym: String) {
        val list = watch().toMutableList()
        if (!list.contains(sym)) list.add(sym)
        put("watch", list.joinToString(","))
    }

    fun exportJson(): String = JSONObject()
        .put("facts", facts())
        .put("tasks", tasks())
        .put("watch", str("watch"))
        .toString()

    fun importJson(text: String): Boolean {
        return try {
            val o = JSONObject(text)
            val nf = o.optString("facts", "")
            if (nf.isNotBlank()) addFact(nf)
            val inc = o.optJSONArray("tasks")
            if (inc != null) {
                val arr = tasks()
                for (i in 0 until inc.length()) arr.put(inc.getJSONObject(i))
                saveTasks(arr)
            }
            val w = o.optString("watch", "")
            for (sym in w.split(",")) if (sym.isNotBlank()) addWatch(sym.trim())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun clearAllData(keepKeys: Boolean) {
        val e = prefs.edit()
        for (k in listOf("convs", "cur", "facts", "tasks", "watch", "hist", "app_files")) e.remove(k)
        if (!keepKeys) {
            for (k in SECRET) SecureStore.remove(app, k)
            for (k in listOf("key", "bulut", "groq", "groqModel", "mistral", "mistralModel", "finnhub", "news")) e.remove(k)
        }
        e.apply()
        cache.clear()
    }
}