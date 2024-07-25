package org.futo.circles.core.model

sealed class DmRoomState

data class DmConnected(val roomId: String) : DmRoomState()
data class DmHasInvite(val roomId: String) : DmRoomState()
data object DmInviteSent : DmRoomState()
data object DmNotFound : DmRoomState()