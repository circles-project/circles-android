plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("maven-publish")
}

android {
    namespace = "org.futo.circles.auth"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            cmake {
                cFlags += "-std=c11"
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        named("debug") {
            isMinifyEnabled = false
        }
        named("release") {
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
        jvmTarget = "17"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation(project(":core"))

    // Password strength
    implementation("com.nulab-inc:zxcvbn:1.9.0")

    // Subscriptions
    gplayImplementation("com.android.billingclient:billing-ktx:7.0.0")

    // PasswordManager
    val credentialsVersion = "1.3.0-beta02"
    gplayImplementation("androidx.credentials:credentials:$credentialsVersion")
    gplayImplementation("androidx.credentials:credentials-play-services-auth:$credentialsVersion")

    // Hilt
    implementHilt()

    // Test dependencies
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
        publishing.publications.create(variant.name, MavenPublication::class) {
            groupId = Versions.modules_groupId
            artifactId = "auth_${variant.flavorName}"
            version = Versions.modules_version

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                fun addDependency(dep: Dependency, scope: String) {
                    if (dep.group == null || dep.name == "unspecified" || dep.version == "unspecified") return
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", dep.group)
                    dependencyNode.appendNode("artifactId", dep.name)
                    dependencyNode.appendNode("version", dep.version)
                    dependencyNode.appendNode("scope", scope)
                }
                configurations.getByName("api").dependencies.forEach { dep -> addDependency(dep, "compile") }
                configurations.getByName("implementation").dependencies.forEach { dep ->
                    addDependency(dep, "runtime")
                }
                if (variant.flavorName == "gplay") {
                    configurations.getByName("gplayApi").dependencies.forEach { dep ->
                        addDependency(dep, "compile")
                    }
                    configurations.getByName("gplayImplementation").dependencies.forEach { dep ->
                        addDependency(dep, "runtime")
                    }
                } else if (variant.flavorName == "fdroid") {
                    configurations.getByName("fdroidApi").dependencies.forEach { dep ->
                        addDependency(dep, "compile")
                    }
                    configurations.getByName("fdroidImplementation").dependencies.forEach { dep ->
                        addDependency(dep, "runtime")
                    }
                }
            }
        }
    }
}
