package org.futo.circles.feature.circles.accept_invite

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder

class AcceptCircleInviteDataSource @AssistedInject constructor(
    @Assisted private val roomId: String,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): AcceptCircleInviteDataSource
    }

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun acceptCircleInvite(selectedCircles: List<SelectableRoomListItem>) = createResult {
        session?.roomService()?.joinRoom(roomId)
        selectedCircles.forEach { circle ->
            roomRelationsBuilder.setInvitedCircleRelations(roomId, circle.id)
        }
    }

}