package com.cem.jarvis

/**
 * Keeps crash diagnostics readable while removing likely credentials and private paths.
 * Pure Kotlin so the redaction rules can be verified by local JVM unit tests.
 */
internal object CrashTraceSanitizer {
    private val appPathPattern =
        Regex("""(?i)/data/(?:user/[^\s)]+|data/[^\s)]+)""")
    private val knownKeyPrefixPattern =
        Regex("""(?<![A-Za-z0-9_-])(?:AIza|sk-|gsk_)[A-Za-z0-9_-]{16,}""")
    private val longKeyPattern =
        Regex("""(?=[A-Za-z0-9_-]*\d)[A-Za-z0-9_-]{24,}""")
    private val emailPattern =
        Regex("""[\w.+-]+@[\w-]+\.[\w.-]+""")

    fun sanitizeCrashStackTrace(stackTrace: String): String {
        return stackTrace
            .replace(appPathPattern, "[APP_PATH]")
            .replace(knownKeyPrefixPattern, "[REDACTED_KEY]")
            .replace(longKeyPattern, "[REDACTED_KEY]")
            .replace(emailPattern, "[REDACTED_EMAIL]")
    }
}
