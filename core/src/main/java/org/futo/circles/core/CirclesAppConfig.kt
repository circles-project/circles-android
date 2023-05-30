package org.futo.circles.core

object CirclesAppConfig {

    var appId = ""
        private set

    var usServerDomain = ""
        private set

    var euServerDomain = ""
        private set

    var isSubscriptionsEnabled = false
        private set

    data class Initializer(
        private var appId: String? = null,
        private var debugUsDomain: String? = null,
        private var releaseUsDomain: String? = null,
        private var debugEuDomain: String? = null,
        private var releaseEuDomain: String? = null,
        private var subscriptionEnabled: Boolean = false
    ) {

        fun appId(appId: String) = apply { this.appId = appId }
        fun usDomain(debugDomain: String, releaseDomain: String) = apply {
            this.debugUsDomain = debugDomain
            this.releaseUsDomain = releaseDomain
        }

        fun euDomain(debugDomain: String, releaseDomain: String) = apply {
            this.debugEuDomain = debugDomain
            this.releaseEuDomain = releaseDomain
        }

        fun isSubscriptionEnabled(isEnabled: Boolean) =
            apply { this.subscriptionEnabled = isEnabled }

        fun init() {
            CirclesAppConfig.appId = appId?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Illegal appId $appId")

            usServerDomain = if (BuildConfig.DEBUG)
                debugUsDomain?.takeIf { it.isNotEmpty() }
                    ?: throw IllegalArgumentException("Illegal US server domain $debugUsDomain")
            else
                releaseUsDomain?.takeIf { it.isNotEmpty() }
                    ?: throw IllegalArgumentException("Illegal US server domain $releaseUsDomain")

            euServerDomain = if (BuildConfig.DEBUG)
                debugEuDomain?.takeIf { it.isNotEmpty() }
                    ?: throw IllegalArgumentException("Illegal EU server domain $debugEuDomain")
            else
                releaseEuDomain?.takeIf { it.isNotEmpty() }
                    ?: throw IllegalArgumentException("Illegal EU server domain $releaseEuDomain")

            isSubscriptionsEnabled = subscriptionEnabled
        }
    }

}