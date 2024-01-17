package org.futo.circles.core.feature.room.invite

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.feature.room.RoomRelationsBuilder
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.Group
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@ViewModelScoped
class ManageInviteRequestsDataSource @Inject constructor(
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    suspend fun inviteUsers(scope: CoroutineScope, roomId: String, usersIds: List<String>) =
        createResult { usersIds.map { scope.async { inviteUser(roomId, it) } }.awaitAll() }

    suspend fun inviteUser(roomId: String, userId: String) = createResult {
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
            ?.invite(userId, null)
    }

    suspend fun acceptInvite(roomId: String, roomType: CircleRoomTypeArg) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.joinRoom(roomId)
        when (roomType) {
            CircleRoomTypeArg.Group -> roomRelationsBuilder.setInvitedRoomRelations(roomId, Group())
            CircleRoomTypeArg.Photo -> roomRelationsBuilder.setInvitedRoomRelations(
                roomId,
                Gallery()
            )

            CircleRoomTypeArg.Circle -> throw IllegalArgumentException("Circle has different relations")
        }
    }

    suspend fun rejectInvite(roomId: String) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(roomId)
    }

    suspend fun kickUser(roomId: String, userId: String) = createResult {
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()?.remove(userId)
    }

}