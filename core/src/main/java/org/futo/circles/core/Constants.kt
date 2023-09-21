package org.futo.circles.core

import org.futo.circles.core.provider.MatrixSessionProvider

const val FILE_PROVIDER_AUTHORITY_EXTENSION = ".provider"
const val SYSTEM_NOTICES_TAG = "m.server_notice"
const val DEFAULT_USER_PREFIX = "@notices:"

const val MediaCaptionFieldKey = "caption"

const val READ_ONLY_ROLE = -10

fun getRageShakeUrl(): String = "https://rageshake.${getCirclesDomain()}/bugreports/submit/"

fun getCirclesDomain(): String {
    if (BuildConfig.DEBUG) return CirclesAppConfig.usServerDomain
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    if (homeServerUrl.contains(CirclesAppConfig.usServerDomain)) return CirclesAppConfig.usServerDomain
    if (homeServerUrl.contains(CirclesAppConfig.euServerDomain)) return CirclesAppConfig.euServerDomain
    return CirclesAppConfig.usServerDomain
}

const val SHARE_ROOM_URL_PREFIX = "https://circu.li/room/"
const val SHARE_PROFILE_URL_PREFIX = "https://circu.li/profile/"

fun buildShareRoomUrl(roomId: String, roomName: String, topic: String?) =
    SHARE_ROOM_URL_PREFIX + roomId + "/$roomName" + if (topic.isNullOrEmpty()) "" else "/$topic"

fun buildShareProfileUrl(sharedSpaceId: String) =
    MatrixSessionProvider.currentSession?.myUserId?.let { userId ->
        "$SHARE_PROFILE_URL_PREFIX$userId/$sharedSpaceId"
    } ?: ""
