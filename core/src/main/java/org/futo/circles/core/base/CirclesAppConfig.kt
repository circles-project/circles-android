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

    var usDomain = ""
        private set

    var euDomain = ""
        private set

    var isMediaBackupEnabled = false
        private set

    var changelog = ""
        private set

    fun serverDomains() = listOf(usDomain, euDomain)

    data class Initializer(
        private var appId: String? = null,
        private var versionName: String? = null,
        private var versionCode: Int? = null,
        private var flavour: String? = null,
        private var usDomain: String? = null,
        private var euDomain: String? = null,
        private var mediaBackupEnabled: Boolean = false,
        private var changelog: String? = null
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

        fun serverDomains(usDomain: String, euDomain: String) = apply {
            this.usDomain = usDomain
            this.euDomain = euDomain
        }

        fun isMediaBackupEnabled(isEnabled: Boolean) = apply { this.mediaBackupEnabled = isEnabled }

        fun changeLog(changelog: String) = apply { this.changelog = changelog }
        

        fun init() {
            CirclesAppConfig.appId = appId?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $appId")

            appVersionName = versionName?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal versionName $versionName")

            appVersionCode = versionCode?.takeIf { it != -1 }
                ?: throw IllegalArgumentException("Illegal versionCode $versionCode")

            buildFlavourName = flavour?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal flavour $flavour")

            CirclesAppConfig.usDomain = usDomain?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal empty US server domains")

            CirclesAppConfig.euDomain = euDomain?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal empty EU server domains")

            CirclesAppConfig.changelog = changelog?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("changelog is empty $changelog")

            isMediaBackupEnabled = mediaBackupEnabled
        }
    }

}