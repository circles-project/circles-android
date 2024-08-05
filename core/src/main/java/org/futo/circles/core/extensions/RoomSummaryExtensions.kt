package org.futo.circles.core.extensions

import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.model.RoomInfo
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toRoomInfo(): RoomInfo = RoomInfo(nameOrId(), avatarUrl)