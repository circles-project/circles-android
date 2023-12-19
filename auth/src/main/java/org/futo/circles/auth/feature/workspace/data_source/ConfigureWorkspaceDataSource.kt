package org.futo.circles.auth.feature.workspace.data_source

import kotlinx.coroutines.delay
import org.futo.circles.core.feature.room.RoomRelationsBuilder
import org.futo.circles.core.feature.room.create.CreateRoomDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.model.SharedCirclesSpace
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class ConfigureWorkspaceDataSource @Inject constructor(
    private val createRoomDataSource: CreateRoomDataSource,
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    suspend fun performCreateOrFix(room: CirclesRoom) {
        var roomId = addIdToAccountDataIfRoomExistWithTag(room)
        if (roomId == null) roomId = getJoinedRoomIdFromAccountData(room)
        if (roomId == null) createRoomWithAccountDataRecordIfNeed(room)
        else validateAndFixRelationInNeeded(roomId, room)
    }

    private suspend fun validateAndFixRelationInNeeded(roomId: String, room: CirclesRoom) {
        try {
            getJoinedRoomById(roomId)?.let { validateRelations(room.parentAccountDataKey, it) }
        } catch (_: Exception) {
            val parentRoomId =
                room.parentAccountDataKey?.let { spacesTreeAccountDataSource.getRoomIdByKey(it) }
            parentRoomId?.let { roomRelationsBuilder.setRelations(roomId, parentRoomId) }
            removeSharedCirclesToMyCirclesRelationIfNeeded(room, roomId)
        }
    }

    fun validate(room: CirclesRoom) {
        val accountDataKey = room.accountDataKey ?: return
        val roomId = spacesTreeAccountDataSource.getRoomIdByKey(accountDataKey)
            ?: throw IllegalArgumentException("No account data record for key $accountDataKey")
        val joinedRoom = getJoinedRoomById(roomId)
            ?: throw IllegalArgumentException("No joined room for id $roomId found")
        validateRelations(room.parentAccountDataKey, joinedRoom)
    }

    private fun validateRelations(parentAccountDataKey: String?, joinedRoom: Room) {
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
        return session.roomService().getRoomSummaries(roomSummaryQueryParams {
            excludeType = null
            memberships = listOf(Membership.JOIN)
        }).firstOrNull { it.hasTag(tag) }?.roomId
    }

    private fun getJoinedRoomIdFromAccountData(room: CirclesRoom): String? {
        val key = room.accountDataKey ?: return null
        val roomId = spacesTreeAccountDataSource.getRoomIdByKey(key) ?: return null
        getJoinedRoomById(roomId) ?: return null
        return roomId
    }

    private suspend fun createRoomWithAccountDataRecordIfNeed(room: CirclesRoom): String {
        val roomId = createRoomDataSource.createRoom(room)
        room.accountDataKey?.let { key ->
            spacesTreeAccountDataSource.updateSpacesConfigAccountData(key, roomId)
        }
        delay(CREATE_ROOM_DELAY)
        return roomId
    }

    //part of Shared Circles from My Circles to Root migration
    private suspend fun removeSharedCirclesToMyCirclesRelationIfNeeded(
        circlesRoom: CirclesRoom,
        roomId: String
    ) {
        if (circlesRoom !is SharedCirclesSpace) return
        val myCirclesSpaceId =
            spacesTreeAccountDataSource.getRoomIdByKey(CIRCLES_SPACE_ACCOUNT_DATA_KEY) ?: return
        roomRelationsBuilder.removeRelations(roomId, myCirclesSpaceId)

    }

    private companion object {
        private const val CREATE_ROOM_DELAY = 1000L
    }

}