package com.cem.jarvis

import android.content.Intent
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AriesFullAppAuditTest {
    private lateinit var device: UiDevice
    private lateinit var packageName: String
    private lateinit var outputDir: File
    private val screenRecords = JSONArray()
    private val findings = JSONArray()
    private var launchToVisibleMs: Long? = null

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        packageName = context.packageName
        // The first career-module open otherwise schedules a real background scan.
        // Keep emulator UI coverage deterministic and network/API-key independent.
        context.getSharedPreferences("j", android.content.Context.MODE_PRIVATE).edit()
            .putBoolean("career_monitor_auto_default_applied", true)
            .putBoolean("career_monitor_enabled", false)
            .putBoolean("career_monitor_initial_scan_queued", true)
            .apply()
        device = UiDevice.getInstance(instrumentation)
        outputDir = File(context.getExternalFilesDir(null), "AriesQA").apply {
            deleteRecursively()
            mkdirs()
        }
    }

    @Test
    fun launchAndAuditAllReachableApplicationSections() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        assertNotNull("Aries launcher activity could not be resolved", launchIntent)
        launchIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val launchStartedAt = SystemClock.elapsedRealtime()
        context.startActivity(launchIntent)

        assertTrue(
            "Aries did not appear in the foreground after launch",
            device.wait(Until.hasObject(By.pkg(packageName)), 20_000)
        )
        launchToVisibleMs = SystemClock.elapsedRealtime() - launchStartedAt
        SystemClock.sleep(1_200)
        handleRuntimePermissionDialogs()
        captureAndAudit("01-home")

        val menuOpened = clickDescription("Menü") || clickText("Menü")
        recordFinding(
            if (menuOpened) "info" else "error",
            "navigation",
            "home",
            if (menuOpened) "Main menu opened through its accessible label." else "Main menu control was not found by text/content-description."
        )
        if (menuOpened) {
            captureAndAudit("02-sidebar")

            // Voice assistant page was previously constructed but had no visible route.
            val voiceOpened = clickTextContains("Sesli Asistan")
            if (voiceOpened) {
                val voiceVisible = device.wait(Until.hasObject(By.textContains("Son söylenen")), 5_000)
                if (!voiceVisible) {
                    recordFinding("error", "navigation-destination", "voice", "Voice menu entry opened, but the voice screen's 'Son söylenen' section did not appear.")
                }
                SystemClock.sleep(400)
                captureAndAudit("02a-voice-assistant")
                device.pressBack()
                device.wait(Until.hasObject(By.desc("Menü")), 5_000)
            } else {
                recordFinding("error", "navigation", "sidebar", "The new 'Sesli Asistan' menu entry was not reachable.")
            }

            // Re-open the drawer after returning from the voice page.
            if (!device.hasObject(By.desc("Menü"))) {
                recordFinding("error", "navigation", "home", "The home menu control was not available after returning from voice screen.")
            }
            val careerOpened = clickTextContains("İş ve Kariyer")
            if (!careerOpened) {
                recordFinding("error", "navigation", "sidebar", "The İş ve Kariyer entry was not reachable from the main menu.")
                device.pressBack()
            } else {
                val careerVisible = device.wait(Until.hasObject(By.textContains("İş & Kariyer")), 10_000)
                if (!careerVisible) {
                    recordFinding("error", "navigation-destination", "career", "CareerActivity did not show its expected heading.")
                    device.pressBack()
                } else {
                    SystemClock.sleep(700)
                    captureAndAudit("03-career-listings")

                    val careerTabs = listOf(
                        Triple("CV'im", "04-career-cv", "CV profilin"),
                        Triple("Takip", "05-career-tracking", "Takip merkezim"),
                        Triple("Başvuru", "06-career-applications", "Başvurularım"),
                        Triple("Sonuç", "07-career-results", "Başvuru sonuçlarım"),
                        Triple("Haber", "08-career-news", "ARIES  /  HABER & DUYURULAR")
                    )
                    for ((label, fileName, expectedHeading) in careerTabs) {
                        if (clickText(label)) {
                            val destinationVisible = device.wait(Until.hasObject(By.textContains(expectedHeading)), 5_000)
                            if (!destinationVisible) {
                                recordFinding("error", "navigation-destination", fileName, "The '$label' tab responded to a tap but its expected destination heading '$expectedHeading' did not appear.")
                            }
                            SystemClock.sleep(450)
                            captureAndAudit(fileName)
                        } else {
                            recordFinding("error", "navigation", "career", "Career tab '$label' was not found or could not be tapped.")
                        }
                    }
                    if (!clickDescription("Önceki ekrana dön")) device.pressBack()
                    device.wait(Until.hasObject(By.desc("Menü")), 8_000)
                    SystemClock.sleep(500)
                    captureAndAudit("09-home-returned")
                }
            }
        }

        val settingsOpened = clickDescription("Ayarlar")
        if (settingsOpened) {
            SystemClock.sleep(400)
            captureAndAudit("10-main-settings")
            device.pressBack()
            SystemClock.sleep(400)
        } else {
            recordFinding("error", "navigation", "home", "Main settings shortcut was not found by its content description.")
        }

        if (clickDescription("Ekle")) {
            SystemClock.sleep(250)
            val assistantOpened = clickText("Hatırlatmalar ve görevler")
            if (assistantOpened) {
                SystemClock.sleep(500)
                captureAndAudit("11-assistant-tasks-reminders")
                device.pressBack()
                SystemClock.sleep(400)
            } else {
                recordFinding("error", "navigation", "home", "Quick action 'Hatırlatmalar ve görevler' was not found.")
                device.pressBack()
            }
        } else {
            recordFinding("error", "navigation", "home", "Home quick-action control 'Ekle' was not found.")
        }

        if (clickDescription("Ekle")) {
            SystemClock.sleep(250)
            if (clickText("Dosya ekle")) {
                SystemClock.sleep(400)
                captureAndAudit("12-files-documents")
                device.pressBack()
                SystemClock.sleep(400)
            } else {
                recordFinding("error", "navigation", "home", "Quick action 'Dosya ekle' was not found.")
                device.pressBack()
            }
        }

        if (clickDescription("Film ve Dizi")) {
            SystemClock.sleep(1_000)
            captureAndAudit("13-film-series")
            val mediaTabs = listOf(
                "Trendler" to "13a-film-trends",
                "Filmler" to "13b-film-list",
                "Dizileri" to "13c-series-list",
                "Anime" to "13d-anime-list"
            )
            for ((label, fileName) in mediaTabs) {
                if (clickText(label)) {
                    SystemClock.sleep(350)
                    captureAndAudit(fileName)
                } else {
                    recordFinding("error", "navigation", "film-series", "The Film & TV category '$label' was not visible or could not be selected.")
                }
            }
            device.pressBack()
            SystemClock.sleep(500)
        } else {
            recordFinding("error", "navigation", "home", "Film ve Dizi shortcut was not found.")
        }

        recordFinding(
            "info",
            "coverage",
            "main-navigation",
            "Voice screen and audio actions require a separate controlled microphone test; this smoke test does not send speech or trigger real calls."
        )
        writeAuditBundle()
        assertTrue("No screenshots were produced by the UI audit", screenRecords.length() >= 1)
    }

    private fun handleRuntimePermissionDialogs() {
        repeat(8) {
            val allow = device.findObject(
                By.textMatches("(?i)^(allow|while using the app|only this time|izin ver|uygulamayı kullanırken)$")
            )
            if (allow != null) {
                runCatching { allow.click() }
                SystemClock.sleep(350)
            } else {
                val permissionPackages = listOf(
                    "com.android.permissioncontroller",
                    "com.google.android.permissioncontroller",
                    "com.android.packageinstaller"
                )
                val permissionUiVisible = permissionPackages.any { device.hasObject(By.pkg(it)) }
                if (!permissionUiVisible) return
                // On this disposable emulator, dismiss an unrecognised prompt rather than
                // leaving a system dialog blocking the app-navigation audit.
                device.pressBack()
                SystemClock.sleep(250)
            }
        }
    }

    private fun clickDescription(value: String): Boolean = clickObject(By.desc(value))
    private fun clickText(value: String): Boolean = clickObject(By.text(value))
    private fun clickTextContains(value: String): Boolean = clickObject(By.textContains(value))

    private fun clickObject(selector: androidx.test.uiautomator.BySelector): Boolean {
        val item = runCatching { device.wait(Until.findObject(selector), 3_500) }.getOrNull() ?: return false
        return runCatching {
            val bounds = item.visibleBounds
            if (bounds.isEmpty) false else {
                device.click(bounds.centerX(), bounds.centerY())
                device.waitForIdle(2_000)
                true
            }
        }.getOrDefault(false)
    }

    private fun captureAndAudit(screenName: String) {
        device.waitForIdle(2_000)
        SystemClock.sleep(350)
        val screenshot = File(outputDir, "$screenName.png")
        val hierarchy = File(outputDir, "$screenName.xml")
        val screenshotSaved = runCatching { device.takeScreenshot(screenshot) }.getOrDefault(false)
        val hierarchySaved = runCatching {
            device.dumpWindowHierarchy(hierarchy)
            hierarchy.exists() && hierarchy.length() > 0
        }.getOrDefault(false)

        screenRecords.put(
            JSONObject()
                .put("name", screenName)
                .put("screenshot", if (screenshotSaved) screenshot.name else "")
                .put("hierarchy", if (hierarchySaved) hierarchy.name else "")
        )
    }

    private fun recordFinding(severity: String, category: String, screen: String, message: String) {
        findings.put(
            JSONObject()
                .put("severity", severity)
                .put("category", category)
                .put("screen", screen)
                .put("message", message)
        )
    }

    private fun writeAuditBundle() {
        File(outputDir, "ui-audit.json").writeText(
            JSONObject()
                .put("package", packageName)
                .put(
                    "device",
                    JSONObject()
                        .put("sdk", android.os.Build.VERSION.SDK_INT)
                        .put("release", android.os.Build.VERSION.RELEASE)
                        .put("model", android.os.Build.MODEL)
                        .put("density", InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density)
                        .put("screenWidthPx", InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.widthPixels)
                        .put("screenHeightPx", InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.heightPixels)
                )
                .put("metrics", JSONObject().put("launchToVisibleMs", launchToVisibleMs ?: JSONObject.NULL))
                .put("screens", screenRecords)
                .put("findings", findings)
                .toString(2)
        )
        File(outputDir, "visited-screens.txt").writeText(
            (0 until screenRecords.length()).joinToString("\n") {
                screenRecords.getJSONObject(it).getString("name")
            }
        )
    }
}
