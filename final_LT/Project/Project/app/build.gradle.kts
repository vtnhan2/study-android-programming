plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.secrets.gradle.plugin)
}

android {
    namespace = "com.example.motoshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.motoshop"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    // Cấu hình Secrets Gradle Plugin
    // Phương án: Mặc định sẽ dùng Key cung cấp sẵn nếu không tìm thấy trong local.properties
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

secrets {
    // Tệp này chứa key mặc định 
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gson)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
    implementation(libs.okhttp)
    implementation(libs.mpandroidchart)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage) // Đảm bảo dòng này đúng với libs.versions.toml

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
