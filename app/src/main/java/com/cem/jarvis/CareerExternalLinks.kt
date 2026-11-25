package com.cem.jarvis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/** Opens approved HTTPS source pages in a browser-owned Custom Tab. */
object CareerExternalLinks {
    fun open(
        activity: Activity,
        rawUrl: String,
        onFailure: (String) -> Unit = {}
    ) {
        val validationError = CareerNewsVerificationPolicy.validateHttpsUrl(rawUrl)
        if (validationError != null) {
            onFailure(validationError)
            return
        }
        val uri = Uri.parse(rawUrl.trim())
        try {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
                .launchUrl(activity, uri)
        } catch (customTabError: Exception) {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (browserError: Exception) {
                onFailure(browserError.message ?: customTabError.message ?: "Kaynak açılamadı.")
            }
        }
    }
}
