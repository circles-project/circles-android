package org.futo.circles.gallery.feature

import androidx.lifecycle.map
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.gallery.mapping.toGalleryListItem
import org.futo.circles.gallery.model.GalleryListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class PhotosDataSource @Inject constructor() {

    fun getGalleriesLiveData() = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams())
        ?.map { list -> filterGalleries(list) }

    private fun filterGalleries(list: List<RoomSummary>): List<GalleryListItem> {
        return list.mapNotNull { summary ->
            if (summary.roomType == GALLERY_TYPE && summary.membership == Membership.JOIN) {
                summary.toGalleryListItem()
            } else null
        }
    }
}