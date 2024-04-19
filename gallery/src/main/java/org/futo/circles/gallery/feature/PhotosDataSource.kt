package org.futo.circles.gallery.feature

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.model.GalleryHeaderItem
import org.futo.circles.core.model.GalleryInvitesNotificationListItem
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getGalleriesLiveData
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class PhotosDataSource @Inject constructor() {

    fun getGalleriesFlow() = combine(
        getGalleriesLiveData().asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { buildList(roomSummaries) }
    }.distinctUntilChanged()


    private fun buildList(galleries: List<RoomSummary>): List<GalleryListItem> {
        val joined = galleries.mapNotNull {
            if (it.membership == Membership.JOIN) it.toJoinedGalleryListItem()
            else null
        }
        val sharedGalleries = joined.filter { !it.isMyGallery() }
        val invitesCount = galleries.filter { it.membership == Membership.INVITE }.size

        val displayList = mutableListOf<GalleryListItem>().apply {
            if (invitesCount > 0)
                add(GalleryInvitesNotificationListItem(invitesCount))

            addSection(
                GalleryHeaderItem.myGalleriesHeader,
                joined - sharedGalleries.toSet()
            )
            addSection(
                GalleryHeaderItem.sharedGalleriesHeader,
                sharedGalleries
            )
        }
        return displayList
    }

    private fun MutableList<GalleryListItem>.addSection(
        title: GalleryHeaderItem,
        items: List<GalleryListItem>
    ) {
        if (items.isNotEmpty()) {
            add(title)
            addAll(items)
        }
    }
}
