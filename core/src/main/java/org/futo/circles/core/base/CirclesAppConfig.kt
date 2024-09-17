package org.futo.circles.core.base

object CirclesAppConfig {

    var appId = ""
        private set

    var appVersionName = ""
        private set

    var appVersionCode = -1
        private set

    var buildFlavourName = ""
        private set


    var isMediaBackupEnabled = false
        private set


    data class Initializer(
        private var appId: String? = null,
        private var versionName: String? = null,
        private var versionCode: Int? = null,
        private var flavour: String? = null,
        private var mediaBackupEnabled: Boolean = false
    ) {

        fun buildConfigInfo(
            appId: String,
            versionName: String,
            versionCode: Int,
            flavour: String
        ) = apply {
            this.appId = appId
            this.versionName = versionName
            this.versionCode = versionCode
            this.flavour = flavour
        }


        fun isMediaBackupEnabled(isEnabled: Boolean) = apply { this.mediaBackupEnabled = isEnabled }


        fun init() {
            CirclesAppConfig.appId = appId?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $appId")

            appVersionName = versionName?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal versionName $versionName")

            appVersionCode = versionCode?.takeIf { it != -1 }
                ?: throw IllegalArgumentException("Illegal versionCode $versionCode")

            buildFlavourName = flavour?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal flavour $flavour")

            isMediaBackupEnabled = mediaBackupEnabled
        }
    }

}