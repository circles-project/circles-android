package org.futo.circles.model

import org.futo.circles.core.feature.share.BASE_SHARE_URL
import org.futo.circles.core.model.ShareUrlTypeArg
import org.futo.circles.core.model.shareUrlTypeArgFromType

data class RoomUrlData(
    val roomId: String,
    val type: ShareUrlTypeArg
)

fun parseUrlData(url: String): RoomUrlData? {
    val data = url.removePrefix(BASE_SHARE_URL).split("/")
    val typeString = data.getOrNull(0) ?: return null
    val type = shareUrlTypeArgFromType(typeString) ?: return null
    val roomId = data.getOrNull(1) ?: return null
    return RoomUrlData(roomId, type)
}