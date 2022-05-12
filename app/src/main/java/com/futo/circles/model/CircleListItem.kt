package com.futo.circles.model

import com.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class CircleListItem(
    override val id: String,
    open val info: CircleInfo,
    open val membership: Membership
) : IdEntity<String>

data class JoinedCircleListItem(
    override val id: String,
    override val info: CircleInfo,
    val followingCount: Int,
    val followedByCount: Int
) : CircleListItem(id, info, Membership.JOIN)

data class InvitedCircleListItem(
    override val id: String,
    override val info: CircleInfo,
    val inviterName: String,
) : CircleListItem(id, info, Membership.INVITE)

data class CircleInfo(
    val title: String,
    val avatarUrl: String
)