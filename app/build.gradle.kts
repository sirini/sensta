plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
}

// Firebase 프로젝트 설정이 있는 빌드에서만 Google Services 리소스를 생성한다.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "me.sensta"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.sensta"
        minSdk = 26
        targetSdk = 36
        versionCode = 29
        versionName = "2.1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = false
        resValues = true
    }

    signingConfigs {
        val storePath = providers.gradleProperty("SENSTA_STORE_FILE")
            .orElse(providers.environmentVariable("SENSTA_STORE_FILE"))
            .orNull
        val storePasswordValue = providers.gradleProperty("SENSTA_STORE_PASSWORD")
            .orElse(providers.environmentVariable("SENSTA_STORE_PASSWORD"))
            .orNull
        val keyAliasValue = providers.gradleProperty("SENSTA_KEY_ALIAS")
            .orElse(providers.environmentVariable("SENSTA_KEY_ALIAS"))
            .orNull
        val keyPasswordValue = providers.gradleProperty("SENSTA_KEY_PASSWORD")
            .orElse(providers.environmentVariable("SENSTA_KEY_PASSWORD"))
            .orNull

        // 업로드 키와 비밀번호는 저장소가 아닌 사용자 Gradle 속성이나 환경변수로만 받는다.
        if (listOf(storePath, storePasswordValue, keyAliasValue, keyPasswordValue).all { it != null }) {
            create("release") {
                storeFile = file(requireNotNull(storePath))
                storePassword = requireNotNull(storePasswordValue)
                keyAlias = requireNotNull(keyAliasValue)
                keyPassword = requireNotNull(keyPasswordValue)
            }
        }

        // QA는 Firebase에 등록된 개발용 키를 지정할 수 있고, 없으면 기본 debug 키를 쓴다.
        providers.gradleProperty("SENSTA_QA_STORE_FILE")
            .orElse(providers.environmentVariable("SENSTA_QA_STORE_FILE"))
            .orNull
            ?.let { qaStorePath ->
                create("qa") {
                    storeFile = file(qaStorePath)
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }
            }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            resValue("string", "version", defaultConfig.versionName ?: "1.0.0")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "version", defaultConfig.versionName ?: "1.0.0")
        }
        create("qa") {
            initWith(getByName("release"))
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-qa"
            isDebuggable = false
            signingConfig = signingConfigs.findByName("qa") ?: signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose.v190)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Compose
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
    ksp(libs.hilt.android.compiler)

    // Navigation
    implementation(libs.androidx.navigation.ui)

    // Foundation layout
    implementation(libs.androidx.foundation.layout)

    // uCrop
    implementation(libs.ucrop)
    implementation(libs.androidx.exifinterface)

    // Worker
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.work.runtime.ktx)

    // Google login
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Firebase KTX 모듈은 BoM 34부터 제외되었으므로 기본 모듈을 사용한다.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.installations)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
