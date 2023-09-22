package org.futo.circles.auth.feature.workspace.data_source

import kotlinx.coroutines.delay
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.room.CreateRoomDataSource
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getJoinedRoomIdByTag
import javax.inject.Inject

class ConfigureWorkspaceDataSource @Inject constructor(
    private val createRoomDataSource: CreateRoomDataSource,
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource
) {

    suspend fun perform(room: CirclesRoom) {
        val roomId = createRoomDataSource.createRoom(room)
        room.accountDataKey?.let { key ->
            spacesTreeAccountDataSource.updateSpacesConfigAccountData(key, roomId)
        }
        delay(CREATE_ROOM_DELAY)
    }

    suspend fun validate(room: CirclesRoom): Boolean {
        val accountDataKey = room.accountDataKey ?: return false
        if (spacesTreeAccountDataSource.getRoomIdByKey(accountDataKey) == null) {
            addRecordToAccountDataIfRoomExist(room)
        }
        return getJoinedRoomById(
            spacesTreeAccountDataSource.getRoomIdByKey(accountDataKey) ?: ""
        ) != null
    }

    private suspend fun addRecordToAccountDataIfRoomExist(room: CirclesRoom) {
        val tag = room.tag ?: return
        val key = room.accountDataKey ?: return
        val roomId = getJoinedRoomIdByTag(tag, true) ?: return
        spacesTreeAccountDataSource.updateSpacesConfigAccountData(key, roomId)
    }

    private companion object {
        private const val CREATE_ROOM_DELAY = 1000L
    }

}