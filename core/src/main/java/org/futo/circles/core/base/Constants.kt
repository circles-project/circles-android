package org.futo.circles.core.base

import org.futo.circles.core.provider.MatrixSessionProvider

const val FILE_PROVIDER_AUTHORITY_EXTENSION = ".provider"
const val MediaCaptionFieldKey = "caption"
const val READ_ONLY_ROLE = -10

val PUSHER_APP_ID = "${CirclesAppConfig.appId}.android"

fun getPusherUrl(): String = "https://sygnal.${getCirclesDomain()}/_matrix/push/v1/notify"

const val DEFAULT_PUSH_GATEWAY = "https://matrix.gateway.unifiedpush.org/_matrix/push/v1/notify"

fun getCirclesDomain(): String {
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    return CirclesAppConfig.serverDomains.firstOrNull { homeServerUrl.contains(it) }
        ?: CirclesAppConfig.serverDomains.first()
}
