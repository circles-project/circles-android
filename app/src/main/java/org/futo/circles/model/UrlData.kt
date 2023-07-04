package org.futo.circles.model

import org.futo.circles.base.SHARE_PROFILE_URL_PREFIX
import org.futo.circles.base.SHARE_ROOM_URL_PREFIX

sealed class UrlData(val knockRoomId: String)

data class RoomUrlData(
    val roomId: String,
    val roomName: String,
    val topic: String?
) : UrlData(roomId)

data class UserUrlData(
    val userId: String,
    val sharedSpaceId: String
) : UrlData(sharedSpaceId)

fun parseUrlData(url: String): UrlData? =
    if (url.startsWith(SHARE_ROOM_URL_PREFIX)) createRoomUrlData(url)
    else if (url.startsWith(SHARE_PROFILE_URL_PREFIX)) createUserUrlData(url)
    else null

private fun createRoomUrlData(url: String): RoomUrlData? {
    val data = url.removePrefix(SHARE_ROOM_URL_PREFIX).split("/")
    val roomId = data.getOrNull(0) ?: return null
    val roomName = data.getOrNull(1) ?: return null
    val topic = data.getOrNull(2)
    return RoomUrlData(roomId, roomName, topic)
}

private fun createUserUrlData(url: String): UserUrlData? {
    val data = url.removePrefix(SHARE_PROFILE_URL_PREFIX).split("/")
    val userId = data.getOrNull(0) ?: return null
    val sharedSpaceId = data.getOrNull(1) ?: return null
    return UserUrlData(userId, sharedSpaceId)
}