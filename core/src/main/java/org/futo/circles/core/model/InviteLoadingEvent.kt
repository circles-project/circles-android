package org.futo.circles.core.model


data class InviteLoadingEvent(
    val userId: String = "",
    val roomName: String = "",
    val isLoading: Boolean = true
)