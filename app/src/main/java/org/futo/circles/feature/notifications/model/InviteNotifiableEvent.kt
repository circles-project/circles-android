package org.futo.circles.feature.notifications.model

data class InviteNotifiableEvent(
        val matrixID: String?,
        override val eventId: String,
        override val editedEventId: String?,
        override val canBeReplaced: Boolean,
        val roomId: String,
        val roomName: String?,
        val noisy: Boolean,
        val title: String,
        val description: String,
        val type: String?,
        val timestamp: Long,
        val soundName: String?,
        override val isRedacted: Boolean = false,
        override val isUpdated: Boolean = false
) : NotifiableEvent
