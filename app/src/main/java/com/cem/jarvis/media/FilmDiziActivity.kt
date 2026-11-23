package com.cem.jarvis.media

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cem.jarvis.Pal

class FilmDiziActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Pal.lightMode = getSharedPreferences("j", MODE_PRIVATE).getString("theme", "dark") == "light"

        val surface = android.graphics.Color.parseColor(Pal.BG)
        window.statusBarColor = surface
        window.navigationBarColor = surface
        var flags = 0
        if (Pal.lightMode) {
            flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= 26) flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        window.decorView.systemUiVisibility = flags
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(surface))

        setContent { MediaRoot(onExit = { finish() }) }
    }
}
