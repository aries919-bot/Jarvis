package com.cem.jarvis

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class AriesNotifService : NotificationListenerService() {
    companion object {
        @Volatile var instance: AriesNotifService? = null
        val items: MutableList<String> = java.util.Collections.synchronizedList(ArrayList<String>())
    }

    override fun onListenerConnected() { instance = this }
    override fun onListenerDisconnected() { instance = null }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return
        val e = sbn.notification.extras
        val title = e.getCharSequence("android.title")?.toString() ?: ""
        val text = e.getCharSequence("android.text")?.toString() ?: ""
        if (title.isBlank() && text.isBlank()) return
        items.removeAll { it.startsWith(sbn.packageName + "|" + title + "|" + text + "|") }
        items.add(0, sbn.packageName + "|" + title + "|" + text + "|" + sbn.postTime)
        while (items.size > 30) items.removeAt(items.size - 1)
    }
}
