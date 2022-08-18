package org.futo.circles.feature.circles.accept_invite

import org.futo.circles.core.matrix.room.RoomRelationsBuilder
import org.futo.circles.extensions.createResult
import org.futo.circles.model.SelectableRoomListItem
import org.futo.circles.provider.MatrixSessionProvider

class AcceptCircleInviteDataSource(
    private val roomId: String,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun acceptCircleInvite(selectedCircles: List<SelectableRoomListItem>) = createResult {
        session?.roomService()?.joinRoom(roomId)
        selectedCircles.forEach { circle ->
            roomRelationsBuilder.setInvitedCircleRelations(roomId, circle.id)
        }
    }

}