package com.cem.jarvis

/**
 * Builds a small, local-first memory context for the assistant.
 * Personal notes are never added to cloud prompts unless the user explicitly
 * enables the separate sharing switch.
 */
internal object AiMemory {
    private const val MAX_CONTEXT_CHARS = 1200

    fun appendContext(
        systemPrompt: String,
        rawFacts: String,
        memoryEnabled: Boolean,
        cloudSharingEnabled: Boolean
    ): String {
        if (!memoryEnabled || !cloudSharingEnabled || rawFacts.isBlank()) return systemPrompt

        val safeFacts = rawFacts
            .split(';', '\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() && isSafeToShare(it) }
            .distinct()
        if (safeFacts.isEmpty()) return systemPrompt

        val context = safeFacts.joinToString("\n") { "- $it" }.take(MAX_CONTEXT_CHARS)
        if (context.isBlank()) return systemPrompt

        return systemPrompt + "\n\nKULLANICININ AÇIKÇA PAYLAŞMAYI SEÇTİĞİ HAFIZA:\n" +
            context +
            "\nBu notları yalnızca ilgili olduklarında kullan; eksik bilgiyi tahmin etme."
    }

    private fun isSafeToShare(value: String): Boolean {
        val sensitive = listOf(
            Regex("\\b\\d{11,}\\b"),
            Regex("[\\w.+-]+@[\\w-]+\\.[\\w.-]+"),
            Regex("\\+?\\d[\\d ()-]{8,}\\d"),
            Regex("(?i)(şifre|parola|password|api.?key|access.?token|bearer|cvv|cvc)"),
            Regex("(?i)(sk-[a-z0-9_-]{12,}|gsk_[a-z0-9_-]{12,}|AIza[0-9A-Za-z_-]{20,})")
        )
        return sensitive.none { it.containsMatchIn(value) }
    }
}
