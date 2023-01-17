package org.futo.circles.feature.notifications.model

import java.io.Serializable


sealed interface NotifiableEvent : Serializable {
    val eventId: String
    val editedEventId: String?
    val canBeReplaced: Boolean
    val isRedacted: Boolean
    val isUpdated: Boolean
}
