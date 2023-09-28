package org.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import javax.inject.Inject

@ViewModelScoped
class AcceptCircleInviteDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun acceptCircleInvite(selectedCircles: List<SelectableRoomListItem>) = createResult {
        session?.roomService()?.joinRoom(roomId)
        selectedCircles.forEach { circle ->
            roomRelationsBuilder.setInvitedRoomRelations(roomId, circle.id)
        }
    }

}