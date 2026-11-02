plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

import java.util.Properties

room {
    // Room ghi schema JSON ra đây mỗi lần build. Nhờ vậy khi nâng version sau này
    // có dữ liệu so sánh để viết Migration, thay vì đoán mò.
    schemaDirectory("$projectDir/schemas")
}

// FR4 - đọc API key (KHÔNG commit các file này).
// Thứ tự ưu tiên: ai.properties > local.properties > biến môi trường GEMINI_API_KEY.
// Vì sao có ai.properties: Android Studio ghi đè local.properties mỗi lần sync
// ("YOUR CHANGES WILL BE ERASED") nên key dán vào đó sẽ bị xoá mất. ai.properties
// không bị Android Studio chạm tới.
// Thiếu key thì BuildConfig rỗng và app chạy FakeAiClient.
val aiProperties = Properties().apply {
    listOf("ai.properties", "local.properties").forEach { name ->
        val f = rootProject.file(name)
        if (f.exists()) {
            f.inputStream().use { stream ->
                Properties().apply { load(stream) }.forEach { (k, v) -> putIfAbsent(k, v) }
            }
        }
    }
}

fun geminiApiKey(): String =
    aiProperties.getProperty("GEMINI_API_KEY")?.trim()?.takeIf { it.isNotEmpty() }
        ?: System.getenv("GEMINI_API_KEY")?.trim().orEmpty()

fun geminiModel(): String =
    aiProperties.getProperty("GEMINI_MODEL")?.trim()?.takeIf { it.isNotEmpty() }
        ?: "gemini-3.8-flash"

android {
    namespace = "com.aistudy.aistudy"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.aistudy.aistudy"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"${geminiApiKey()}\"")
        buildConfigField("String", "GEMINI_MODEL", "\"${geminiModel()}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.security.crypto)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.json)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}