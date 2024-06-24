object AppConfig {
    const val compileSdk = 34
    const val minSdk = 24
    const val versionCode = 42
    const val versionName = "1.0.32"

    //output file name
    val archivesName = "circles-v$versionName"

    //build flavours
    const val flavourDimension = "store"
    const val gplayFlavourName = "gplay"
    const val fdroidFlavourName = "fdroid"
}