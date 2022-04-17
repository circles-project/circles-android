package com.futo.circles.mapping

import com.futo.circles.model.CirclesUserSummary
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.model.InvitedUserListItem
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.Role

fun RoomMemberSummary.toCircleUserSummary() = CirclesUserSummary(
    id = userId,
    name = displayName ?: userId,
    avatarUrl = avatarUrl ?: ""
)

fun RoomMemberSummary.toGroupMemberListItem(
    role: Role,
    isOptionsVisible: Boolean,
    powerLevelsContent: PowerLevelsContent
) = GroupMemberListItem(
    user = toCircleUserSummary(),
    role = role,
    isOptionsOpened = isOptionsVisible,
    powerLevelsContent = powerLevelsContent
)

fun RoomMemberSummary.toInvitedUserListItem(powerLevelsContent: PowerLevelsContent) =
    InvitedUserListItem(
        user = toCircleUserSummary(),
        powerLevelsContent = powerLevelsContent
    )