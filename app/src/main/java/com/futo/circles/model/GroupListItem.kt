package com.futo.circles.model

import com.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class GroupListItem(
    override val id: String,
    open val info: GroupInfo,
    open val membership: Membership
) : IdEntity<String>

data class JoinedGroupListItem(
    override val id: String,
    override val info: GroupInfo,
    val topic: String,
    val membersCount: Int,
    val timestamp: Long
) : GroupListItem(id, info, Membership.JOIN)

data class InvitedGroupListItem(
    override val id: String,
    override val info: GroupInfo,
    val inviterName: String,
) : GroupListItem(id, info, Membership.INVITE)

data class GroupInfo(
    val title: String,
    val isEncrypted: Boolean,
    val avatarUrl: String,
)