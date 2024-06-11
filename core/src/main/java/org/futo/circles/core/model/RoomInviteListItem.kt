package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary

data class RoomInviteListItem(
    val roomId: String,
    val roomType: CircleRoomTypeArg,
    val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
    val shouldBlurIcon: Boolean,
    val isLoading: Boolean = false
) : IdEntity<String> {
    override val id: String = roomId
}

fun RoomSummary.toRoomInviteListItem(roomType: CircleRoomTypeArg, shouldBlurIcon: Boolean) =
    RoomInviteListItem(
        roomId = roomId,
        info = toRoomInfo(),
        inviterName = getInviterName(),
        isEncrypted = isEncrypted,
        shouldBlurIcon = shouldBlurIcon,
        roomType = roomType
    )

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""