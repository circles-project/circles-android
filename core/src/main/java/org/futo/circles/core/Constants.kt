package org.futo.circles.core

import org.futo.circles.core.provider.MatrixSessionProvider

const val FILE_PROVIDER_AUTHORITY_EXTENSION = ".provider"
const val CREATE_ROOM_DELAY = 1000L
const val SYSTEM_NOTICES_TAG = "m.server_notice"
const val DEFAULT_USER_PREFIX = "@notices:"

const val MediaCaptionFieldKey = "caption"

fun getRageShakeUrl(): String = "https://rageshake.${getCirclesDomain()}/bugreports/submit/"

fun getCirclesDomain(): String {
    if (BuildConfig.DEBUG) return CirclesAppConfig.usServerDomain
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    if (homeServerUrl.contains(CirclesAppConfig.usServerDomain)) return CirclesAppConfig.usServerDomain
    if (homeServerUrl.contains(CirclesAppConfig.euServerDomain)) return CirclesAppConfig.euServerDomain
    return CirclesAppConfig.usServerDomain
}
