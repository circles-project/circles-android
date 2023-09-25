package org.futo.circles.core.room

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.model.Circle
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.model.Timeline
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getSharedCirclesSpaceId
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.getRoom
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
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session by lazy { MatrixSessionProvider.getSessionOrThrow() }

    suspend fun createRoom(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null,
        isPublicCircle: Boolean = false
    ): String {
        val id = session.roomService().createRoom(
            getParams(circlesRoom, name, topic, iconUri, inviteIds)
        )
        circlesRoom.tag?.let { session.getRoom(id)?.tagsService()?.addTag(it, null) }
        circlesRoom.parentTag?.let { tag ->
            roomRelationsBuilder.findRoomByTag(tag)
                ?.let { room -> roomRelationsBuilder.setRelations(id, room) }
        }
        if (circlesRoom is Circle) {
            val timelineId = createCircleTimeline(id, name, iconUri, inviteIds)
            if (isPublicCircle) addToSharedCircles(timelineId)
        }
        return id
    }

    suspend fun createCircleTimeline(
        circleId: String, name: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null
    ): String {
        val timelineId = createRoom(Timeline(), name, null, iconUri, inviteIds)
        session.getRoom(circleId)
            ?.let { circle -> roomRelationsBuilder.setRelations(timelineId, circle) }
        return timelineId
    }

    private fun getParams(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null
    ): CreateRoomParams {
        val params = if (circlesRoom.isSpace()) {
            CreateSpaceParams()
        } else {
            CreateRoomParams().apply {
                visibility = RoomDirectoryVisibility.PRIVATE
                historyVisibility = RoomHistoryVisibility.SHARED
                powerLevelContentOverride = PowerLevelsContent(invite = Role.Moderator.value)
                enableEncryption()
            }
        }.apply {
            circlesRoom.type?.let { this.roomType = it }
            setInviteRules(this, circlesRoom)
        }

        return params.apply {
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

    suspend fun addToSharedCircles(timelineId: String) {
        session.getRoom(getSharedCirclesSpaceId() ?: "")
            ?.let { sharedCirclesSpace ->
                roomRelationsBuilder.setRelations(timelineId, sharedCirclesSpace)
            }
    }

    suspend fun removeFromSharedCircles(timelineId: String) {
        session.getRoom(getSharedCirclesSpaceId() ?: "")
            ?.let { sharedCirclesSpace ->
                roomRelationsBuilder.removeRelations(timelineId, sharedCirclesSpace.roomId)
            }
    }
}
