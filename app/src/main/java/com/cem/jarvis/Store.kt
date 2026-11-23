package com.cem.jarvis

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class Conv(val id: String, var title: String, var updated: Long, val msgs: MutableList<Pair<String, String>>)

class Store(ctx: Context) {
    private val appContext = ctx.applicationContext
    val prefs: SharedPreferences = ctx.getSharedPreferences("j", Context.MODE_PRIVATE)
    private val cache = ArrayList<Conv>()

    init {
        loadConvs()
    }

    fun str(k: String, def: String = ""): String = prefs.getString(k, def) ?: def

    fun put(k: String, v: String) {
        prefs.edit().putString(k, v).apply()
    }

    fun flag(k: String, def: Boolean): Boolean = prefs.getBoolean(k, def)

    fun setFlag(k: String, v: Boolean) {
        prefs.edit().putBoolean(k, v).apply()
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
            for (k in listOf("key", "groq", "groqModel", "mistral", "mistralModel", "finnhub", "finnhub_enc", "news")) e.remove(k)
            CloudKeyStore.clear(appContext)
        }
        e.apply()
        cache.clear()
    }
}

/**
 * Keeps the Aries Cloud API key encrypted with a non-exportable Android Keystore AES key.
 * Existing plaintext preferences are migrated on first read and removed after encryption succeeds.
 */
object CloudKeyStore {
    private const val PREFS = "j"
    private const val ENCRYPTED_PREF = "bulut_enc"
    private const val LEGACY_PREF = "bulut"
    private const val KEY_ALIAS = "aries_cloud_aes_v1"
    private const val GCM_IV_BYTES = 12

    fun save(context: Context, rawKey: String): Boolean {
        if (rawKey.isBlank()) {
            clear(context)
            return true
        }
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val encrypted = cipher.doFinal(rawKey.trim().toByteArray(Charsets.UTF_8))
            val payload = cipher.iv + encrypted
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(ENCRYPTED_PREF, Base64.encodeToString(payload, Base64.NO_WRAP))
                .remove(LEGACY_PREF)
                .apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun read(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(ENCRYPTED_PREF, "").orEmpty()
        if (saved.isNotBlank()) {
            return try {
                val payload = Base64.decode(saved, Base64.NO_WRAP)
                if (payload.size <= GCM_IV_BYTES) throw IllegalStateException("Şifreli anahtar verisi eksik")
                val iv = payload.copyOfRange(0, GCM_IV_BYTES)
                val encrypted = payload.copyOfRange(GCM_IV_BYTES, payload.size)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
                String(cipher.doFinal(encrypted), Charsets.UTF_8)
            } catch (_: Exception) {
                // A restored backup may contain ciphertext without its device-bound Keystore key.
                prefs.edit().remove(ENCRYPTED_PREF).apply()
                ""
            }
        }

        val legacy = prefs.getString(LEGACY_PREF, "").orEmpty()
        if (legacy.isNotBlank() && save(context, legacy)) return legacy
        return legacy
    }

    fun isEncrypted(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(ENCRYPTED_PREF, "").orEmpty().isNotBlank()

    fun hasLegacyKey(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(LEGACY_PREF, "").orEmpty().isNotBlank()

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(ENCRYPTED_PREF)
            .remove(LEGACY_PREF)
            .apply()
        runCatching {
            KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
                deleteEntry(KEY_ALIAS)
            }
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existing != null) return existing

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }
}