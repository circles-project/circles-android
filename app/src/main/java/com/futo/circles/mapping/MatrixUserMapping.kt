package com.futo.circles.mapping

import com.futo.circles.model.RoomMemberListItem
import org.matrix.android.sdk.api.session.user.model.User

fun User.toRoomMember() = RoomMemberListItem(
    id = userId,
    name = displayName ?: userId,
    avatarUrl = avatarUrl ?: ""
)