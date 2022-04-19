package com.futo.circles.model

data class GroupListItemPayload(
    val topic: String?,
    val isEncrypted: Boolean?,
    val membersCount: Int?,
    val timestamp: Long?,
    val needUpdateFullItem: Boolean
)
