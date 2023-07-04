package org.futo.circles.base

import org.futo.circles.BuildConfig
import org.futo.circles.core.getCirclesDomain
import org.futo.circles.core.provider.MatrixSessionProvider


const val PUSHER_APP_ID = "${BuildConfig.APPLICATION_ID}.android"

const val READ_ONLY_ROLE = -10

fun getPusherUrl(): String = "https://sygnal.${getCirclesDomain()}/_matrix/push/v1/notify"

const val DEFAULT_PUSH_GATEWAY = "https://matrix.gateway.unifiedpush.org/_matrix/push/v1/notify"

const val SHARE_ROOM_URL_PREFIX = "https://circu.li/room/"
const val SHARE_PROFILE_URL_PREFIX = "https://circu.li/profile/"

fun buildShareRoomUrl(roomId: String, roomName: String, topic: String?) =
    SHARE_ROOM_URL_PREFIX + roomId + "/$roomName" + if (topic.isNullOrEmpty()) "" else "/$topic"

fun buildShareProfileUrl(sharedSpaceId: String) =
    SHARE_PROFILE_URL_PREFIX + MatrixSessionProvider.currentSession?.myUserId + "/" + sharedSpaceId

