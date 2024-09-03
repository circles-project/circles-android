package org.futo.circles.auth.feature.workspace

import android.net.Uri
import org.futo.circles.auth.model.WorkspaceTask
import org.futo.circles.core.feature.room.RoomRelationsBuilder
import org.futo.circles.core.feature.room.create.CreateRoomDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getAllJoinedCirclesRoomsAndSpaces
import org.futo.circles.core.utils.getJoinedRoomById
import org.matrix.android.sdk.api.session.room.Room
import javax.inject.Inject

class ConfigureWorkspaceDataSource @Inject constructor(
    private val createRoomDataSource: CreateRoomDataSource,
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    suspend fun performCreateOrFix(task: WorkspaceTask) {
        var roomId = addIdToAccountDataIfRoomExistWithTag(task.room)
        if (roomId == null) roomId = getJoinedRoomIdFromAccountData(task.room)
        if (roomId == null) createRoomWithAccountDataRecordIfNeed(task.room, task.name, task.uri)
        else validateAndFixRelationInNeeded(roomId, task.room)
    }

    private suspend fun validateAndFixRelationInNeeded(roomId: String, room: CirclesRoom) {
        try {
            getJoinedRoomById(roomId)?.let {
                validateRelations(room.parentAccountDataKey, it, room.accountDataKey)
            }
        } catch (_: Exception) {
            val parentRoomId =
                room.parentAccountDataKey?.let { spacesTreeAccountDataSource.getRoomIdByKey(it) }
            parentRoomId?.let { roomRelationsBuilder.setRelations(roomId, parentRoomId) }
        }
    }

    fun validate(room: CirclesRoom) {
        val accountDataKey = room.accountDataKey ?: return
        val roomId = spacesTreeAccountDataSource.getRoomIdByKey(accountDataKey)
            ?: throw IllegalArgumentException("No account data record for key $accountDataKey")
        val joinedRoom = getJoinedRoomById(roomId)
            ?: throw IllegalArgumentException("No joined room for id $roomId found")
        validateRelations(room.parentAccountDataKey, joinedRoom, accountDataKey)
    }

    private fun validateRelations(
        parentAccountDataKey: String?,
        joinedRoom: Room,
        accountDataKey: String?
    ) {
        val parentKey = parentAccountDataKey ?: return
        val parentRoomId =
            spacesTreeAccountDataSource.getRoomIdByKey(parentKey) ?: throw IllegalArgumentException(
                "No account data record for parent with key $parentKey"
            )
        val joinedParentRoom = getJoinedRoomById(parentRoomId)
            ?: throw IllegalArgumentException("No joined parent room for id $parentKey found")

        val childHasRelationToParent = joinedRoom.asSpace()
            ?.spaceSummary()?.spaceParents?.mapNotNull { it.parentId }
            ?.contains(parentRoomId) == true

        if (!childHasRelationToParent)
            throw IllegalArgumentException("Missing child to parent relations")

        val parentHasRelationToChild = joinedParentRoom.asSpace()
            ?.spaceSummary()?.spaceChildren?.map { it.childRoomId }
            ?.contains(joinedRoom.roomId) == true

        if (!parentHasRelationToChild)
            throw IllegalArgumentException("Missing parent to child relations")
    }

    private suspend fun addIdToAccountDataIfRoomExistWithTag(room: CirclesRoom): String? {
        val tag = room.getTag() ?: return null
        val key = room.accountDataKey ?: return null
        val roomId = getJoinedRoomIdByTag(tag) ?: return null
        spacesTreeAccountDataSource.updateSpacesConfigAccountData(key, roomId)
        return roomId
    }

    private fun getJoinedRoomIdByTag(tag: String): String? {
        val session = MatrixSessionProvider.currentSession ?: return null
        return getAllJoinedCirclesRoomsAndSpaces(session).firstOrNull { it.hasTag(tag) }?.roomId
    }

    private fun getJoinedRoomIdFromAccountData(room: CirclesRoom): String? {
        val key = room.accountDataKey ?: return null
        val roomId = spacesTreeAccountDataSource.getRoomIdByKey(key) ?: return null
        getJoinedRoomById(roomId) ?: return null
        return roomId
    }

    private suspend fun createRoomWithAccountDataRecordIfNeed(
        room: CirclesRoom,
        name: String? = null,
        uri: Uri? = null
    ): String {
        val roomId = createRoomDataSource.createRoom(room, name = name, iconUri = uri)
        room.accountDataKey?.let { key ->
            spacesTreeAccountDataSource.updateSpacesConfigAccountData(key, roomId)
        }
        return roomId
    }
}