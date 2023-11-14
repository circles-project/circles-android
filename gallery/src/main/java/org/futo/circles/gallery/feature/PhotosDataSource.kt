package org.futo.circles.gallery.feature

import org.futo.circles.core.feature.room.RoomListHelper
import org.futo.circles.core.mapping.toInvitedGalleryListItem
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GalleryListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class PhotosDataSource @Inject constructor(
    private val roomListHelper: RoomListHelper
) {

    fun getGalleriesFlow() = roomListHelper.getRoomsFlow(::filterGalleries)

    private fun filterGalleries(
        list: List<RoomSummary>,
        knownUsersIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): List<GalleryListItem> {
        val galleries = list.filter { it.roomType == GALLERY_TYPE }
        val joined = galleries.mapNotNull { it.takeIf { it.membership == Membership.JOIN } }
        val invites = galleries.mapNotNull { it.takeIf { it.membership == Membership.INVITE } }
        return mutableListOf<GalleryListItem>().apply {
            addAll(invites.map {
                it.toInvitedGalleryListItem(
                    roomListHelper.shouldBlurIconFor(it, knownUsersIds, roomIdsToUnblur)
                )
            })
            addAll(joined.map { it.toJoinedGalleryListItem() })
        }
    }

    private fun shouldBlurIconFor(
        roomId: String,
        inviterId: String?,
        knownUserIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): Boolean {
        val isKnownUser = knownUserIds.contains(inviterId)
        val isRoomUnbluredByUser = roomIdsToUnblur.contains(roomId)
        return !isKnownUser && !isRoomUnbluredByUser
    }

    fun unblurProfileImageFor(id: String) = roomListHelper.unblurProfileImageFor(id)
}
