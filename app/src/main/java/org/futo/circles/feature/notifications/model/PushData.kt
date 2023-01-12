package org.futo.circles.feature.notifications.model

data class PushData(
    val eventId: String?,
    val roomId: String?,
    val unread: Int?,
)
