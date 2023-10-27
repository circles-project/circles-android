package org.futo.circles.core.feature.share

import org.futo.circles.core.model.ShareUrlTypeArg

private const val BASE_SHARE_URL = "https://circu.li/"

fun buildShareRoomUrl(type: ShareUrlTypeArg, roomId: String) =
    BASE_SHARE_URL + type.typeKey + "/$roomId"
