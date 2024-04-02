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

    var serverDomains = emptyList<String>()
        private set

    var isMediaBackupEnabled = false
        private set

    fun isGplayFlavor(): Boolean = buildFlavourName.contains("gplay", true)

    data class Initializer(
        private var appId: String? = null,
        private var version: String? = null,
        private var flavour: String? = null,
        private var appName: String? = null,
        private var serverDomains: List<String> = emptyList(),
        private var euDomain: String? = null,
        private var mediaBackupEnabled: Boolean = false
    ) {

        fun buildConfigInfo(appId: String, version: String, flavour: String = "empty") =
            apply {
                this.appId = appId
                this.version = version
                this.flavour = flavour
            }

        fun appName(appName: String) = apply { this.appName = appName }

        fun serverDomains(domains: List<String>) = apply { this.serverDomains = domains }

        fun isMediaBackupEnabled(isEnabled: Boolean) = apply { this.mediaBackupEnabled = isEnabled }


        fun init() {
            CirclesAppConfig.appId = appId?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $appId")

            appVersion = version?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $version")

            buildFlavourName = flavour?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $flavour")

            CirclesAppConfig.appName = appName?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("appName is empty $appName")

            CirclesAppConfig.serverDomains = serverDomains.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal empty server domains")

            isMediaBackupEnabled = mediaBackupEnabled
        }
    }

}