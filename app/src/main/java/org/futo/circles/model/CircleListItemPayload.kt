package org.futo.circles.model

data class CircleListItemPayload(
    val followersCount: Int?,
    val followedByCount: Int?,
    val unreadCount: Int?,
    val needUpdateFullItem: Boolean
)