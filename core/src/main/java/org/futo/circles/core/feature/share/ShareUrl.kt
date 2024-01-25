package org.futo.circles.core.feature.share

import org.futo.circles.core.model.ShareUrlTypeArg

const val BASE_SHARE_URL = "https://circles.futo.org/"
const val LEGACY_BASE_SHARE_URL = "https://circu.li/"

fun buildShareRoomUrl(type: ShareUrlTypeArg, roomId: String) =
    BASE_SHARE_URL + type.typeKey + "/$roomId"
