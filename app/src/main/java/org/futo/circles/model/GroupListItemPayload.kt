package org.futo.circles.model

data class GroupListItemPayload(
    val topic: String?,
    val isEncrypted: Boolean?,
    val membersCount: Int?,
    val timestamp: Long?,
    val unreadCount: Int?,
    val knocksCount: Int?,
    val needUpdateFullItem: Boolean
)