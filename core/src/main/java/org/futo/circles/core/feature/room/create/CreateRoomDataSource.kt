package org.futo.circles.core.feature.room.create

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.feature.room.RoomRelationsBuilder
import org.futo.circles.core.feature.room.create.CreateRoomProgressStage.CreateRoom
import org.futo.circles.core.feature.room.create.CreateRoomProgressStage.SetParentRelations
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.model.CHATS_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomJoinRulesContent
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomStateEvent
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.space.CreateSpaceParams
import javax.inject.Inject

class CreateRoomDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val roomRelationsBuilder: RoomRelationsBuilder,
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource
) {

    private val session by lazy { MatrixSessionProvider.getSessionOrThrow() }

    suspend fun createDmRoom(otherUserId: String): String {
        val roomId =
            MatrixSessionProvider.getSessionOrThrow().roomService().createDirectRoom(otherUserId)
        val parentId = spacesTreeAccountDataSource.getRoomIdByKey(CHATS_SPACE_ACCOUNT_DATA_KEY)
        parentId?.let { roomRelationsBuilder.setRelations(roomId, it) }
        return roomId
    }

    suspend fun createRoom(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null,
        defaultUserPowerLevel: Int = AccessLevel.User.levelValue,
        progressObserver: CreateRoomProgressListener? = null
    ): String {
        progressObserver?.onProgressUpdated(CreateRoom)
        val id = session.roomService().createRoom(
            getParams(circlesRoom, name, topic, iconUri, inviteIds, defaultUserPowerLevel)
        )
        circlesRoom.parentAccountDataKey?.let { key ->
            progressObserver?.onProgressUpdated(SetParentRelations)
            val parentId = spacesTreeAccountDataSource.getRoomIdByKey(key)
            parentId?.let { roomRelationsBuilder.setRelations(id, it) }
        }
        return id
    }

    private fun getParams(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null,
        defaultUserPowerLevel: Int = AccessLevel.User.levelValue
    ): CreateRoomParams {
        val params = if (circlesRoom.isSpace()) {
            CreateSpaceParams()
        } else {
            CreateRoomParams().apply {
                visibility = RoomDirectoryVisibility.PRIVATE
                historyVisibility = RoomHistoryVisibility.SHARED
                powerLevelContentOverride = PowerLevelsContent(
                    invite = Role.Moderator.value,
                    usersDefault = defaultUserPowerLevel
                )
                enableEncryption()
            }
        }
        return params.apply {
            circlesRoom.type?.let { this.roomType = it }
            setInviteRules(this, circlesRoom)
            this.name = circlesRoom.nameId?.let { context.getString(it) } ?: name
            this.topic = topic
            avatarUri = iconUri
            inviteIds?.let { invitedUserIds.addAll(it) }
        }
    }

    private fun setInviteRules(params: CreateRoomParams, circlesRoom: CirclesRoom) {
        circlesRoom.joinRules?.let { params.guestAccess = GuestAccess.CanJoin }

        params.initialStates.add(
            CreateRoomStateEvent(
                EventType.STATE_ROOM_JOIN_RULES,
                RoomJoinRulesContent(
                    circlesRoom.joinRules?.value ?: RoomJoinRules.INVITE.value
                ).toContent()
            )
        )
    }
}
