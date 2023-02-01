package org.futo.circles.model

import org.futo.circles.feature.notifications.ProcessedEvent

data class GroupedNotificationEvents(
    val roomEvents: Map<String, List<ProcessedEvent<NotifiableMessageEvent>>>,
    val simpleEvents: List<ProcessedEvent<SimpleNotifiableEvent>>,
    val invitationEvents: List<ProcessedEvent<InviteNotifiableEvent>>
)