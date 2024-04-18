package org.futo.circles.core.model

import org.futo.circles.core.feature.share.BASE_SHARE_URL
import org.matrix.android.sdk.api.session.room.model.Membership

data class RoomUrlData(
    val roomId: String,
    val type: ShareUrlTypeArg
)

fun parseUrlData(url: String): RoomUrlData? {
    val data = if (url.startsWith(BASE_SHARE_URL)) url.removePrefix(BASE_SHARE_URL).split("/")
    else return null

    val typeString = data.getOrNull(0) ?: return null
    val type = shareUrlTypeArgFromType(typeString) ?: return null
    val roomId = data.getOrNull(1) ?: return null
    return RoomUrlData(roomId, type)
}

fun RoomUrlData.toRoomPublicInfo() = RoomPublicInfo(
    id = roomId,
    name = null,
    avatarUrl = null,
    topic = null,
    memberCount = 0,
    membership = Membership.NONE,
    type = type
)