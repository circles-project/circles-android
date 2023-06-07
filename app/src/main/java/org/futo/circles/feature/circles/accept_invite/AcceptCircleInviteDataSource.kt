package org.futo.circles.feature.circles.accept_invite

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import org.futo.circles.core.model.SelectableRoomListItem
import javax.inject.Inject

class AcceptCircleInviteDataSource @Inject constructor(
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