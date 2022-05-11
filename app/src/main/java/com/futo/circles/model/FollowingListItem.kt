package com.futo.circles.model

import com.futo.circles.core.list.IdEntity

data class FollowingListItem(
    override val id: String,
    val name: String,
    val ownerName: String,
    val avatarUrl: String,
    val updatedTime: Long,
    val isMyTimeline: Boolean
) : IdEntity<String>