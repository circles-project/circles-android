package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

sealed class DMListItem(
    override val id: String
) : IdEntity<String>

data class JoinedDMsListItem(
    override val id: String,
    val user: CirclesUserSummary,
    val timestamp: Long,
    val unreadCount: Int
) : DMListItem(id)

data class DMsInvitesNotificationListItem(
    val invitesCount: Int
) : DMListItem("DirectMessagesInvitesNotificationListItem")