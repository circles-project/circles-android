package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class FollowingListItem(
    override val id: String,
    val name: String,
    val ownerName: String,
    val avatarUrl: String,
    val updatedTime: Long,
    val isMyTimeline: Boolean,
    val followInCirclesCount: Int
) : IdEntity<String>