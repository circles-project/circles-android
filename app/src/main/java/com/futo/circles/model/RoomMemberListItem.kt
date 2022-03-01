package com.futo.circles.model

import com.futo.circles.base.IdEntity

data class RoomMemberListItem(
    override val id: String,
    val name: String,
    val avatarUrl: String,
    val isSelected: Boolean = false
) : IdEntity<String>