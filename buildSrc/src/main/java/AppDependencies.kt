import org.gradle.api.artifacts.dsl.DependencyHandler


fun DependencyHandler.implementHilt() {
    add("implementation", "com.google.dagger:hilt-android:${Versions.hilt_version}")
    add("kapt", "com.google.dagger:hilt-compiler:${Versions.hilt_version}")
    add("implementation", "androidx.hilt:hilt-work:1.2.0")
}

fun DependencyHandler.implementTestDep() {
    add("testImplementation", "junit:junit:4.13.2")
    add("androidTestImplementation", "androidx.test.ext:junit:1.1.5")
    add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.5.1")
}


fun DependencyHandler.gplayImplementation(dependency: String) {
    add("gplayImplementation", dependency)
}
