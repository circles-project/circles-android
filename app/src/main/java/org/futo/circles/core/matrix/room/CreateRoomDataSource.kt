package org.futo.circles.core.matrix.room

import android.content.Context
import android.net.Uri
import org.futo.circles.BuildConfig
import org.futo.circles.model.Circle
import org.futo.circles.model.CirclesRoom
import org.futo.circles.model.Timeline
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.EncryptionEventContent
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.*
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomStateEvent
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.space.CreateSpaceParams

class CreateRoomDataSource(
    private val context: Context,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun createCircleWithTimeline(
        name: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null,
        isKnockingAllowed: Boolean
    ): String {
        val circleId = createRoom(Circle(), name, null, iconUri)
        val timelineId =
            createRoom(Timeline(), name, null, iconUri, inviteIds, allowKnock = isKnockingAllowed)
        session?.getRoom(circleId)
            ?.let { circle -> roomRelationsBuilder.setRelations(timelineId, circle) }
        return circleId
    }

    suspend fun createRoom(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null,
        allowKnock: Boolean = false,
        isPublic: Boolean = false
    ): String {
        val id = session?.roomService()
            ?.createRoom(
                getParams(circlesRoom, name, topic, iconUri, inviteIds, allowKnock, isPublic)
            ) ?: throw Exception("Can not create room")

        circlesRoom.tag?.let { session?.getRoom(id)?.tagsService()?.addTag(it, null) }
        circlesRoom.parentTag?.let { tag ->
            roomRelationsBuilder.findRoomByTag(tag)
                ?.let { room -> roomRelationsBuilder.setRelations(id, room) }
        }
        return id
    }

    private fun getParams(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null,
        allowKnock: Boolean = false,
        isPublic: Boolean = false
    ): CreateRoomParams {
        val params = if (circlesRoom.isSpace()) {
            CreateSpaceParams()
        } else {
            CreateRoomParams().apply {
                visibility = if (isPublic) RoomDirectoryVisibility.PUBLIC
                else RoomDirectoryVisibility.PRIVATE
                guestAccess = GuestAccess.CanJoin
                historyVisibility = RoomHistoryVisibility.SHARED
                initialStates.add(
                    CreateRoomStateEvent(
                        EventType.STATE_ROOM_JOIN_RULES,
                        RoomJoinRulesContent(
                            if (allowKnock) RoomJoinRules.KNOCK.value
                            else RoomJoinRules.INVITE.value
                        ).toContent()
                    )
                )
                powerLevelContentOverride = PowerLevelsContent(invite = Role.Moderator.value)
                enableEncryption()
                overrideEncryptionForTestBuilds(this)
            }
        }.apply {
            circlesRoom.type?.let { this.roomType = it }
        }

        return params.apply {
            this.name = circlesRoom.nameId?.let { context.getString(it) } ?: name
            this.topic = topic
            avatarUri = iconUri
            inviteIds?.let { invitedUserIds.addAll(it) }
        }
    }

    private fun overrideEncryptionForTestBuilds(params: CreateRoomParams) {
        if (!BuildConfig.DEBUG) return
        params.initialStates.add(
            CreateRoomStateEvent(
                type = EventType.STATE_ROOM_ENCRYPTION,
                content = EncryptionEventContent(
                    algorithm = MXCRYPTO_ALGORITHM_MEGOLM,
                    rotationPeriodMs = DEBUG_ROTATION_PERIOD,
                    rotationPeriodMsgs = DEBUG_PERIOD_MSG
                ).toContent()
            )
        )
    }

    companion object {
        private const val DEBUG_ROTATION_PERIOD = 3600000L
        private const val DEBUG_PERIOD_MSG = 10L
    }
}
