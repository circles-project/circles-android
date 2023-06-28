package org.futo.circles.model

import org.matrix.android.sdk.api.session.room.model.Membership

data class UserPublicInfo(
    val id: String,
    val displayName: String,
    val avatarUrl: String?,
    val sharedSpaceId: String,
    val memberCount: Int,
    val membership: Membership
)