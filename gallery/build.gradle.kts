plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("maven-publish")
}

android {
    namespace = "org.futo.circles.gallery"
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
    implementation(project(":core"))

    // Hilt
    implementHilt()

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
        configurePublishing("gallery", variant.name, variant.flavorName)
    }
}