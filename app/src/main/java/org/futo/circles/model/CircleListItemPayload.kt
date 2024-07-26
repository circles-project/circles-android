package org.futo.circles.model

data class CircleListItemPayload(
    val followersCount: Int?,
    val unreadCount: Int?,
    val knocksCount: Int?,
    val timestamp: Long?,
    val needUpdateFullItem: Boolean
)