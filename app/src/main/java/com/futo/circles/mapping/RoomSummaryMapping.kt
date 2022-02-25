package com.futo.circles.mapping

import com.futo.circles.extensions.nameOrId
import com.futo.circles.model.GroupListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toGroupListItem() = GroupListItem(
    id = roomId,
    title = nameOrId(),
    topic = topic,
    membersCount = joinedMembersCount ?: 0,
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    isEncrypted = isEncrypted,
    avatarUrl = avatarUrl
)