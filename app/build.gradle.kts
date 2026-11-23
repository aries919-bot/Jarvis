plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    dependencies {
        testImplementation("junit:junit:4.13.2")
    }
}
android {
    namespace = "com.cem.jarvis"
    compileSdk = 34
    signingConfigs {
        create("aries") {
            val ks = System.getenv("KEYSTORE_PATH")
            if (!ks.isNullOrBlank()) {
                storeFile = file(ks)
                storePassword = System.getenv("KEYSTORE_PASS")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASS")
            }
        }
    }
    defaultConfig {
        applicationId = "com.cem.jarvis"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    testOptions { unitTests.isReturnDefaultValues = true }
    buildTypes {
        getByName("debug") {
            if (!System.getenv("KEYSTORE_PATH").isNullOrBlank()) signingConfig = signingConfigs.getByName("aries")
        }
    }
}
