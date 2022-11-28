package org.futo.circles.mapping

import org.futo.circles.model.CirclesUserSummary
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.NotJoinedUserListItem
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

fun RoomMemberSummary.toNotJoinedUserListItem(powerLevelsContent: PowerLevelsContent) =
    NotJoinedUserListItem(
        user = toCircleUserSummary(),
        powerLevelsContent = powerLevelsContent,
        membership = membership
    )