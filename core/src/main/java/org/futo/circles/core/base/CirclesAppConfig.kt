package org.futo.circles.core.base

object CirclesAppConfig {

    var appId = ""
        private set

    var appVersion = ""
        private set

    var buildFlavourName = ""
        private set

    var appName = ""
        private set

    var usServerDomain = ""
        private set

    var euServerDomain = ""
        private set

    var isMediaBackupEnabled = false
        private set
    var isRageshakeEnabled = false
        private set

    data class Initializer(
        private var appId: String? = null,
        private var version: String? = null,
        private var flavour: String? = null,
        private var appName: String? = null,
        private var usDomain: String? = null,
        private var euDomain: String? = null,
        private var subscriptionEnabled: Boolean = false,
        private var mediaBackupEnabled: Boolean = false,
        private var rageshakeEnabled: Boolean = false
    ) {

        fun buildConfigInfo(appId: String, version: String, flavour: String = "empty") =
            apply {
                this.appId = appId
                this.version = version
                this.flavour = flavour
            }

        fun appName(appName: String) = apply { this.appName = appName }

        fun usDomain(domain: String) = apply { this.usDomain = domain }

        fun euDomain(domain: String) = apply { this.euDomain = domain }

        fun isMediaBackupEnabled(isEnabled: Boolean) = apply { this.mediaBackupEnabled = isEnabled }

        fun isRageshakeEnabled(isEnabled: Boolean) = apply { this.rageshakeEnabled = isEnabled }


        fun init() {
            CirclesAppConfig.appId = appId?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $appId")

            appVersion = version?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $version")

            buildFlavourName = flavour?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $flavour")

            CirclesAppConfig.appName = appName?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("appName is empty $appName")

            usServerDomain = usDomain?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal US server domain $usDomain")

            euServerDomain = euDomain?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal EU server domain $euDomain")

            isMediaBackupEnabled = mediaBackupEnabled
            isRageshakeEnabled = rageshakeEnabled
        }
    }

}