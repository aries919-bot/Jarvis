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

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        packageName = context.packageName
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
        context.startActivity(launchIntent)

        assertTrue(
            "Aries did not appear in the foreground after launch",
            device.wait(Until.hasObject(By.pkg(packageName)), 20_000)
        )
        SystemClock.sleep(1_200)
        handleRuntimePermissionDialogs()
        captureAndAudit("01-home")

        val menuOpened = clickDescription("Menü") || clickText("Menü")
        recordFinding(
            if (menuOpened) "info" else "warning",
            "navigation",
            "home",
            if (menuOpened) "Main menu opened through its accessible label." else "Main menu control was not found by text/content-description."
        )
        if (menuOpened) {
            captureAndAudit("02-sidebar")
            val careerOpened = clickTextContains("İş ve Kariyer")
            assertTrue("The İş ve Kariyer entry was not reachable from the main menu", careerOpened)
            assertTrue(
                "CareerActivity did not become visible",
                device.wait(Until.hasObject(By.textContains("İş & Kariyer")), 10_000)
            )
            SystemClock.sleep(700)
            captureAndAudit("03-career-listings")

            val careerTabs = listOf(
                "CV'im" to "04-career-cv",
                "Takip" to "05-career-tracking",
                "Başvuru" to "06-career-applications",
                "Sonuç" to "07-career-results",
                "Haber" to "08-career-news"
            )
            for ((label, fileName) in careerTabs) {
                if (clickText(label)) {
                    SystemClock.sleep(450)
                    captureAndAudit(fileName)
                } else {
                    recordFinding("warning", "navigation", "career", "Career tab '$label' was not found or could not be tapped.")
                }
            }
            if (!clickDescription("Önceki ekrana dön")) device.pressBack()
            device.wait(Until.hasObject(By.desc("Menü")), 8_000)
            SystemClock.sleep(500)
            captureAndAudit("09-home-returned")
        }

        val settingsOpened = clickDescription("Ayarlar")
        if (settingsOpened) {
            SystemClock.sleep(400)
            captureAndAudit("10-main-settings")
            device.pressBack()
            SystemClock.sleep(400)
        } else {
            recordFinding("warning", "navigation", "home", "Main settings shortcut was not found by its content description.")
        }

        if (clickDescription("Ekle")) {
            SystemClock.sleep(250)
            val assistantOpened = clickText("Hatırlatıcılar ve görevler")
            if (assistantOpened) {
                SystemClock.sleep(500)
                captureAndAudit("11-assistant-tasks-reminders")
                device.pressBack()
                SystemClock.sleep(400)
            } else {
                recordFinding("warning", "navigation", "home", "Quick action 'Hatırlatmalar ve görevler' was not found.")
                device.pressBack()
            }
        } else {
            recordFinding("warning", "navigation", "home", "Home quick-action control 'Ekle' was not found.")
        }

        if (clickDescription("Ekle")) {
            SystemClock.sleep(250)
            if (clickText("Dosya ekle")) {
                SystemClock.sleep(400)
                captureAndAudit("12-files-documents")
                device.pressBack()
                SystemClock.sleep(400)
            } else {
                recordFinding("warning", "navigation", "home", "Quick action 'Dosya ekle' was not found.")
                device.pressBack()
            }
        }

        if (clickDescription("Film ve Dizi")) {
            SystemClock.sleep(1_000)
            captureAndAudit("13-film-series")
            device.pressBack()
            SystemClock.sleep(500)
        } else {
            recordFinding("warning", "navigation", "home", "Film ve Dizi shortcut was not found.")
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
                if (!device.hasObject(By.pkg("com.android.permissioncontroller"))) return
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

        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        val clickableObjects = runCatching { device.findObjects(By.clickable(true)) }.getOrDefault(emptyList())
        var smallTargets = 0
        var unlabeledTargets = 0
        for (node in clickableObjects) {
            val bounds = runCatching { node.visibleBounds }.getOrNull() ?: continue
            if (bounds.isEmpty) continue
            val widthDp = bounds.width() / density
            val heightDp = bounds.height() / density
            val label = (runCatching { node.text }.getOrNull().orEmpty()).trim()
            val description = (runCatching { node.contentDescription }.getOrNull()?.toString().orEmpty()).trim()
            if (widthDp < 48f || heightDp < 48f) {
                smallTargets++
                findings.put(
                    JSONObject()
                        .put("severity", "warning")
                        .put("category", "touch-target")
                        .put("screen", screenName)
                        .put("message", "Clickable target is smaller than 48dp (" + widthDp.toInt() + "x" + heightDp.toInt() + "dp).")
                        .put("label", label.ifBlank { description })
                )
            }
            if (label.isBlank() && description.isBlank()) {
                unlabeledTargets++
                findings.put(
                    JSONObject()
                        .put("severity", "warning")
                        .put("category", "accessibility-label")
                        .put("screen", screenName)
                        .put("message", "Clickable element has no text or content description.")
                )
            }
        }

        screenRecords.put(
            JSONObject()
                .put("name", screenName)
                .put("screenshot", if (screenshotSaved) screenshot.name else "")
                .put("hierarchy", if (hierarchySaved) hierarchy.name else "")
                .put("clickableCount", clickableObjects.size)
                .put("smallTouchTargets", smallTargets)
                .put("unlabeledTargets", unlabeledTargets)
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
                .put("screens", screenRecords)
                .put("findings", findings)
                .toString(2)
        )
        File(outputDir, "visited-screens.txt").writeText(
            (0 until screenRecords.length()).joinToString("\\n") {
                screenRecords.getJSONObject(it).getString("name")
            }
        )
    }
}
