package org.futo.circles.model

data class GroupListItemPayload(
    val title: String?,
    val avatarUrl: String?,
    val membersCount: Int?,
    val unreadCount: Int?
)