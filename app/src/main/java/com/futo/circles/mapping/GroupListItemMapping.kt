package com.futo.circles.mapping

import com.futo.circles.model.GroupInfo
import com.futo.circles.model.InvitedGroupListItem
import com.futo.circles.model.JoinedGroupListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toGroupInfo() = GroupInfo(
    title = nameOrId(),
    isEncrypted = isEncrypted,
    avatarUrl = avatarUrl
)

fun RoomSummary.toJoinedGroupListItem() = JoinedGroupListItem(
    id = roomId,
    info = toGroupInfo(),
    topic = topic,
    membersCount = joinedMembersCount ?: 0,
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis()
)

fun RoomSummary.toInviteGroupListItem() = InvitedGroupListItem(
    id = roomId,
    info = toGroupInfo(),
    inviterName = MatrixSessionProvider.currentSession?.getUser(inviterId ?: "")?.displayName ?: ""
)
