package org.futo.circles.gallery.feature

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.mapping.toInvitedGalleryListItem
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class PhotosDataSource @Inject constructor() {

    private val roomIdsToUnblurProfile = MutableStateFlow<Set<String>>(emptySet())

    fun getGalleriesFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams()).asFlow(),
        MatrixSessionProvider.getSessionOrThrow().getKnownUsersFlow(),
        roomIdsToUnblurProfile,
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, knownUsers, roomIdsToUnblur, _ ->
        withContext(Dispatchers.IO) {
            filterGalleries(roomSummaries, knownUsers.map { it.userId }.toSet(), roomIdsToUnblur)
        }
    }.distinctUntilChanged()


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
                    shouldBlurIconFor(it.roomId, it.inviterId, knownUsersIds, roomIdsToUnblur)
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

    fun unblurProfileImageFor(id: String) {
        roomIdsToUnblurProfile.update { set -> set.toMutableSet().apply { add(id) } }
    }
}
