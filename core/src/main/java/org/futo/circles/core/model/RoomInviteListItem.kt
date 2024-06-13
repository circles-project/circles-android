package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary

sealed class RoomRequestListItem : IdEntity<String>

data class RoomRequestHeaderItem(
    val titleRes: Int
) : RoomRequestListItem() {
    override val id: String = titleRes.toString()
}

data class RoomInviteListItem(
    val roomId: String,
    val roomType: CircleRoomTypeArg,
    val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
    val shouldBlurIcon: Boolean,
    val isLoading: Boolean = false
) : RoomRequestListItem() {
    override val id: String = roomId
}

data class KnockRequestListItem(
    val requesterId: String,
    val requesterName: String,
    val requesterAvatarUrl: String?,
    val message: String?,
    val isLoading: Boolean = false
) : RoomRequestListItem() {
    override val id: String = requesterId
}

fun KnockRequestListItem.toCircleUser() = CirclesUserSummary(
    requesterId, requesterName, requesterAvatarUrl ?: ""
)