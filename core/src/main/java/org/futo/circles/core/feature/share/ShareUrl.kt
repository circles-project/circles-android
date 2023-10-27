package org.futo.circles.core.feature.share

import org.futo.circles.core.provider.MatrixSessionProvider

private const val BASE_SHARE_URL = "https://circu.li/"
const val SHARE_ROOM_URL_PREFIX = "https://circu.li/room/"
const val SHARE_PROFILE_URL_PREFIX = "https://circu.li/profile/"

fun buildShareRoomUrl(roomId: String, roomName: String, topic: String?) =
    SHARE_ROOM_URL_PREFIX + roomId + "/$roomName" + if (topic.isNullOrEmpty()) "" else "/$topic"

fun buildShareProfileUrl(sharedSpaceId: String) =
    MatrixSessionProvider.currentSession?.myUserId?.let { userId ->
        "$SHARE_PROFILE_URL_PREFIX$userId/$sharedSpaceId"
    } ?: ""