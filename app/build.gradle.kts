import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.mrunk.wearhr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mrunk.wearhr"
        minSdk = 30        // Wear OS 3+ (passt auch für Wear OS 5)
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging.resources.excludes += "META-INF/{AL2.0,LGPL2.1}"

    buildFeatures { viewBinding = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    // Network security: enforce TLS (can be relaxed for local testing)
    defaultConfig {
        manifestPlaceholders["networkSecurityConfig"] = "@xml/network_security_config"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Wear OS UI helpers (optional, for round screens)
    implementation("androidx.wear:wear:1.3.0")

    // Health Services client (Wear OS) - Version corrected
    implementation("androidx.health:health-services-client:1.1.0-alpha01")

    // For .await() on ListenableFuture
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")

    // OkHttp WebSocket
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}