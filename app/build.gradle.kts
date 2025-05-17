plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "me.sensta"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.sensta"
        minSdk = 34
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = false
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            resValue("string", "version", defaultConfig.versionName ?: "1.0.0")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "version", defaultConfig.versionName ?: "1.0.0")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Compose
    implementation(platform(libs.androidx.compose.bom.v20240300))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.activity.compose.v190)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.material.icons.extended)

    // ViewModel & StateFlow
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coil
    implementation(libs.coil.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    // Navigation
    implementation(libs.androidx.navigation.ui)

    // Foundation layout
    implementation("androidx.compose.foundation:foundation-layout:1.7.8")

    // uCrop
    implementation(libs.ucrop)

    // Worker
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.work.runtime.ktx)

    // Google login
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}