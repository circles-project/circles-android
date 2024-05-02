package org.futo.circles.core.model

import org.futo.circles.core.feature.notifications.ProcessedEvent

data class GroupedNotificationEvents(
    val roomEvents: Map<String, List<ProcessedEvent<NotifiableMessageEvent>>>,
    val invitationEvents: List<ProcessedEvent<InviteNotifiableEvent>>
)