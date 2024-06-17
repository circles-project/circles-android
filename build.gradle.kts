plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

ext {
    set("sdk_version", 34)
    set("min_sdk_version", 24)
    set("androidx_nav_version", "2.7.7")
    set("hilt_version", "2.51.1")
    set("modules_version", "1.0.12")
    set("modules_groupId", "org.futo.gitlab.circles")
}