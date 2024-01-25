package org.futo.circles.model

import org.futo.circles.core.feature.share.BASE_SHARE_URL
import org.futo.circles.core.feature.share.LEGACY_BASE_SHARE_URL
import org.futo.circles.core.model.ShareUrlTypeArg
import org.futo.circles.core.model.shareUrlTypeArgFromType

data class RoomUrlData(
    val roomId: String,
    val type: ShareUrlTypeArg
)

fun parseUrlData(url: String): RoomUrlData? {
    val data = if (url.startsWith(BASE_SHARE_URL)) url.removePrefix(BASE_SHARE_URL).split("/")
    else if (url.startsWith(LEGACY_BASE_SHARE_URL)) url.removePrefix(LEGACY_BASE_SHARE_URL)
        .split("/")
    else return null

    val typeString = data.getOrNull(0) ?: return null
    val type = shareUrlTypeArgFromType(typeString) ?: return null
    val roomId = data.getOrNull(1) ?: return null
    return RoomUrlData(roomId, type)
}