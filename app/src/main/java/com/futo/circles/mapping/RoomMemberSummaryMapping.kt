package com.futo.circles.mapping

import com.futo.circles.model.CirclesUserSummary
import com.futo.circles.model.GroupMemberListItem
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.Role

fun RoomMemberSummary.toGroupMemberListItem(role: Role, hasInvitation: Boolean) =
    GroupMemberListItem(
        user = CirclesUserSummary(
            id = userId,
            name = displayName ?: userId,
            avatarUrl = avatarUrl ?: ""
        ),
        role = role,
        hasPendingInvitation = hasInvitation,
        isOptionsOpened = false
    )

