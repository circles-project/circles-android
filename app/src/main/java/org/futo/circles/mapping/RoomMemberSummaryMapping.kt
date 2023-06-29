package org.futo.circles.mapping

import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.model.BannedMemberListItem
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.InvitedMemberListItem
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

fun RoomMemberSummary.toInvitedMemberListItem(
    isOptionsVisible: Boolean,
    powerLevelsContent: PowerLevelsContent
) = InvitedMemberListItem(
    user = toCircleUserSummary(),
    isOptionsOpened = isOptionsVisible,
    powerLevelsContent = powerLevelsContent
)

fun RoomMemberSummary.toBannedUserListItem(powerLevelsContent: PowerLevelsContent) =
    BannedMemberListItem(user = toCircleUserSummary(), powerLevelsContent = powerLevelsContent)