package org.futo.circles.model

data class DMListItemPayload(
    val timestamp: Long?,
    val unreadCount: Int?,
    val userName: String?,
    val avatarUrl: String?,
    val userId: String
)