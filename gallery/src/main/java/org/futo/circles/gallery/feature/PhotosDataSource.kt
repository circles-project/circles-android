package org.futo.circles.gallery.feature

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
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

    fun getGalleriesFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams()).asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { filterGalleries(roomSummaries) }
    }.distinctUntilChanged()


    private fun filterGalleries(list: List<RoomSummary>): List<GalleryListItem> {
        val galleries = list.filter { it.roomType == GALLERY_TYPE }
        val joined = galleries.mapNotNull { it.takeIf { it.membership == Membership.JOIN } }
        val invites = galleries.mapNotNull { it.takeIf { it.membership == Membership.INVITE } }
        return mutableListOf<GalleryListItem>().apply {
            addAll(invites.map { it.toInvitedGalleryListItem() })
            addAll(joined.map { it.toJoinedGalleryListItem() })
        }
    }
}
