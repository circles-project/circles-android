@file:Suppress("DEPRECATION")
import com.android.build.OutputFile
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = rootProject.extra["sdk_version"] as Int
    namespace = "org.futo.circles"

    defaultConfig {
        applicationId = "org.futo.circles"
        minSdk = rootProject.extra["min_sdk_version"] as Int
        targetSdk = rootProject.extra["sdk_version"] as Int
        versionCode = 40
        versionName = "1.0.30"
        archivesName = "circles-v$versionName"

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

    val flavorDimensionName = "store"
    flavorDimensions.add(flavorDimensionName)
    productFlavors {
        create("gplay") {
            dimension = flavorDimensionName
        }
        create("fdroid") {
            dimension = flavorDimensionName
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
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")
    kapt("com.google.dagger:hilt-compiler:${rootProject.extra["hilt_version"]}")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
    correctErrorTypes = true
}

if (gradle.startParameter.taskRequests.toString().lowercase().contains("gplay")) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}
