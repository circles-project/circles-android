@file:Suppress("DEPRECATION")

import com.android.build.OutputFile
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("base")
}

android {
    compileSdk = AppConfig.compileSdk
    namespace = "org.futo.circles"

    defaultConfig {
        applicationId = "org.futo.circles"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.compileSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        base.archivesName = AppConfig.archivesName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val properties = Properties()
            if (rootProject.file("signing.properties").exists()) {
                properties.load(rootProject.file("signing.properties").inputStream())
            }
            storeFile = file(properties.getProperty("KEY_PATH"))
            storePassword = properties.getProperty("KEY_PASSWORD")
            keyAlias = properties.getProperty("ALIAS_NAME")
            keyPassword = properties.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        named("debug") {
            isMinifyEnabled = false
        }
        named("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions.add(AppConfig.flavourDimension)
    productFlavors {
        create(AppConfig.gplayFlavourName) {
            isDefault = true
            dimension = AppConfig.flavourDimension
        }
        create(AppConfig.fdroidFlavourName) {
            dimension = AppConfig.flavourDimension
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    lint {
        disable.add("Instantiatable")
    }

    val abiCodes = mapOf("armeabi-v7a" to 1, "x86" to 2, "x86_64" to 3, "arm64-v8a" to 4)

    applicationVariants.configureEach {
        val variant = this
        outputs?.forEach { output ->
            if (output is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val baseAbiVersionCode =
                    abiCodes[output.getFilter(OutputFile.ABI)]
                if (baseAbiVersionCode != null)
                    output.versionCodeOverride = variant.versionCode * 100 + baseAbiVersionCode
            }
        }
    }

}

dependencies {
    implementation(project(":core"))
    implementation(project(":auth"))
    implementation(project(":settings"))

    // Emoji
    implementation("com.vanniktech:emoji-google:0.20.0")

    // Log
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Hilt
    implementHilt()

    // Test
    implementTestDep()
}

kapt {
    correctErrorTypes = true
}

if (gradle.startParameter.taskRequests.toString().lowercase().contains(AppConfig.gplayFlavourName)) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}
