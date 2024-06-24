plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("maven-publish")
}

android {
    namespace = "org.futo.circles.core"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
}

dependencies {
    api("androidx.appcompat:appcompat:1.7.0")
    api("com.google.android.material:material:1.12.0")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("androidx.autofill:autofill:1.1.0")

    // Kotlin
    api("androidx.core:core-ktx:1.13.1")

    // androidx lifecycle
    val lifecycleVersion = "2.8.2"
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-process:$lifecycleVersion")

    // androidx navigation
    api("androidx.navigation:navigation-fragment-ktx:${Versions.androidx_nav_version}")
    api("androidx.navigation:navigation-ui-ktx:${Versions.androidx_nav_version}")

    // Hilt
    implementHilt()

    // Matrix release
    api("org.futo.gitlab.circles:matrix-android-sdk:v1.6.10.43@aar") {
        isTransitive = true
    }

    // Matrix mavenLocal testing
    //api("org.futo.gitlab.circles:matrix-android-sdk:0.1.100")

    // Retrofit2
    val retrofitVersion = "2.11.0"
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // Gson
    api("com.google.code.gson:gson:2.11.0")

    // Worker
    val workVersion = "2.9.0"
    api("androidx.work:work-runtime-ktx:$workVersion")

    // Glide
    val glideVersion = "4.16.0"
    api("com.github.bumptech.glide:glide:$glideVersion")
    ksp("com.github.bumptech.glide:ksp:$glideVersion")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    // Webp animations
    api("com.github.penfeizhou.android.animation:glide-plugin:3.0.1")

    // QR
    api("com.google.zxing:core:3.5.3")

    // Markdown
    val markwonVersion = "4.6.2"
    api("io.noties.markwon:core:$markwonVersion")
    api("io.noties.markwon:linkify:$markwonVersion")
    api("io.noties.markwon:ext-strikethrough:$markwonVersion")
    api("io.noties.markwon:ext-tasklist:$markwonVersion")
    api("io.element.android:wysiwyg:2.37.3")

    // ExoPlayer
    val exoplayerVersion = "1.3.1"
    api("androidx.media3:media3-exoplayer:$exoplayerVersion")
    api("androidx.media3:media3-ui:$exoplayerVersion")

    // Image zoom
    implementation("com.jsibbold:zoomage:1.3.1")


    // UnifiedPush
    implementation("com.github.UnifiedPush:android-connector:2.1.1")

    // Google app update
    gplayImplementation("com.google.android.play:app-update:2.1.0")

    // ChromeTabs
    implementation("androidx.browser:browser:1.8.0")

    // Firebase
    gplayImplementation("com.google.firebase:firebase-crashlytics-ktx:19.0.2")
    gplayImplementation("com.google.firebase:firebase-analytics-ktx:22.0.2")
    gplayImplementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
    gplayImplementation("com.google.android.gms:play-services-base:18.5.0")

    implementTestDep()
}

kapt {
    correctErrorTypes = true
}

afterEvaluate {
    android.libraryVariants.forEach { variant ->
        if (variant.buildType.name != "release") {
            return@forEach
        }
        configurePublishing("core", variant.name, variant.flavorName)
    }
}
