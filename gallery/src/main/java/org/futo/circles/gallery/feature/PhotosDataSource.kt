package org.futo.circles.gallery.feature

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GalleryInvitesNotificationListItem
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class PhotosDataSource @Inject constructor() {

    fun getGalleriesFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams { excludeType = listOf(RoomType.SPACE) })
            .asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { buildList(roomSummaries) }
    }.distinctUntilChanged()


    private fun buildList(list: List<RoomSummary>): List<GalleryListItem> {
        val galleries = list.filter { it.roomType == GALLERY_TYPE }
        val joined = galleries.mapNotNull { it.takeIf { it.membership == Membership.JOIN } }
        val invitesCount =
            galleries.mapNotNull { it.takeIf { it.membership == Membership.INVITE } }.size
        return mutableListOf<GalleryListItem>().apply {
            if (invitesCount > 0)
                add(GalleryInvitesNotificationListItem(invitesCount))

            addAll(joined.map { it.toJoinedGalleryListItem() })
        }
    }
}
