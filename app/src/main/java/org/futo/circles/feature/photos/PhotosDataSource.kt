package org.futo.circles.feature.photos

import androidx.lifecycle.map
import org.futo.circles.mapping.toGalleryListItem
import org.futo.circles.model.GALLERY_TYPE
import org.futo.circles.model.GalleryListItem
import org.futo.circles.model.RoomListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class PhotosDataSource {

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