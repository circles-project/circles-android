package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.InvitedGalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toRoomInfo() = RoomInfo(
    title = nameOrId(),
    avatarUrl = avatarUrl
)

fun RoomSummary.toSelectableRoomListItem(selected: Boolean = false) = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = selected
)

fun RoomSummary.toJoinedGalleryListItem() = JoinedGalleryListItem(
    id = roomId,
    info = toRoomInfo()
)

fun RoomSummary.toInvitedGalleryListItem(shouldBlurIcon: Boolean) = InvitedGalleryListItem(
    id = roomId,
    info = toRoomInfo(),
    inviterName = getInviterName(),
    shouldBlurIcon = shouldBlurIcon
)

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""