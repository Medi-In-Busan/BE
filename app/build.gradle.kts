import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // AGP 9.0+ built-in Kotlin support supersedes org.jetbrains.kotlin.android; do not re-add it.
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// local.properties is git-ignored; secrets are read from here and never hardcoded.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun secret(key: String): String = localProperties.getProperty(key)
    ?: System.getenv(key)
    ?: ""

fun normalizedBaseUrl(value: String): String = value.trim().let { url ->
    if (url.endsWith('/')) url else "$url/"
}

val configuredBackendBaseUrl = secret("MEDIINBUSAN_API_BASE_URL")
    .takeIf(String::isNotBlank)
    ?.let(::normalizedBaseUrl)

val signingPropertiesFile = rootProject.file("keystore.properties")
val signingProperties = Properties().apply {
    if (signingPropertiesFile.exists()) {
        signingPropertiesFile.inputStream().use { load(it) }
    }
}
val releaseSigningConfigured = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
    .all { !signingProperties.getProperty(it).isNullOrBlank() }

android {
    namespace = "com.mediinbusan.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = rootProject.file(signingProperties.getProperty("storeFile"))
                storePassword = signingProperties.getProperty("storePassword")
                keyAlias = signingProperties.getProperty("keyAlias")
                keyPassword = signingProperties.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "com.mediinbusan.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Kakao Map SDK(libK3fAndroid.so)는 arm64-v8a/armeabi-v7a로만 배포된다. x86_64가 1순위 ABI인
        // 기기/에뮬레이터에 설치하면 PackageManager가 x86_64 lib 폴더만 골라 설치해서(다른 ABI는 아예
        // 무시) 카카오 라이브러리가 통째로 빠진다 — arm64-v8a로 강제해 그 폴더가 선택되게 한다.
        ndk {
            abiFilters += "arm64-v8a"
        }

        buildConfigField("String", "TOURISM_API_SERVICE_KEY", "\"${secret("TOURISM_API_SERVICE_KEY")}\"")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${secret("KAKAO_NATIVE_APP_KEY")}\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = secret("KAKAO_NATIVE_APP_KEY")

    }

    buildTypes {
        debug {
            val backendBaseUrl = configuredBackendBaseUrl ?: "http://10.0.2.2:8080/"
            buildConfigField("String", "MEDIINBUSAN_API_BASE_URL", "\"$backendBaseUrl\"")
        }
        release {
            val backendBaseUrl = configuredBackendBaseUrl ?: "https://ownrefrigerator.site/"
            buildConfigField("String", "MEDIINBUSAN_API_BASE_URL", "\"$backendBaseUrl\"")
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

tasks.configureEach {
    if (name == "bundleRelease" || name == "assembleRelease") {
        doFirst {
            check(releaseSigningConfigured) {
                "Release signing is not configured. Create the ignored keystore.properties file first."
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.animation.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)

    // Dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // Persistence
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)

    // Images
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    // feature/documentscan에서 OCR 업로드 전 EXIF 방향을 읽어 재인코딩 전 회전을 보정하는 데 사용.
    implementation(libs.androidx.exifinterface)

    // Map
    implementation(libs.kakao.map.sdk)

    // UI effects — BottomNavBar의 실시간 backdrop blur(glassmorphism)에 사용.
    implementation(libs.haze)
    implementation(libs.haze.materials)
}
