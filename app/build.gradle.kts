plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.viktorger.zulip_client.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.viktorger.zulip_client.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        val baseUrlFieldName = "BASE_URL"
        val baseUrl = "\"https://your_base_url.com\""

        debug {
            versionNameSuffix = "-DEBUG"
            buildConfigField("String", baseUrlFieldName, baseUrl)
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", baseUrlFieldName, baseUrl)
        }
        create("debug-test") {
            initWith(getByName("debug"))
            isDebuggable = true

            val fakeBaseUrl = "\"http://localhost:8080/\""
            buildConfigField("String", baseUrlFieldName, fakeBaseUrl)
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
        }
        animationsDisabled = true
    }
    testBuildType = "debug-test"
}

dependencies {
    implementation(libs.cicerone)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.shimmer)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.glide)
    implementation(libs.logging.interceptor)

    // DI
    implementation(libs.dagger)
    implementation(libs.androidx.fragment.testing)
    ksp(libs.dagger.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.emoji2.emojipicker)

    testImplementation(libs.kotlinx.coroutines.test)

    // Android Test Rules
    implementation(libs.androidx.rules)

    // JUnit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    // Kotest
    testImplementation(libs.kotest.junit)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.property)

    // Kaspresso
    androidTestImplementation(libs.kaspresso)

    // Espresso Intents
    androidTestImplementation(libs.androidx.espresso.intents)

    // Hamcrest Matchers
    androidTestImplementation(libs.hamcrest)

    // Mockk
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)

    // Wiremock
    debugImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.httpclient.android)
    androidTestImplementation(libs.wiremock) {
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
    }
}