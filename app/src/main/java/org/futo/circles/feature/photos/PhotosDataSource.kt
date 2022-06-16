package org.futo.circles.feature.photos

import org.futo.circles.core.rooms.data_source.RoomsDataSource
import org.futo.circles.mapping.toGalleryListItem
import org.futo.circles.model.GALLERY_TYPE
import org.futo.circles.model.RoomListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class PhotosDataSource : RoomsDataSource() {

    override fun filterRooms(list: List<RoomSummary>): List<RoomListItem> {
        return list.mapNotNull { summary ->
            if (summary.roomType == GALLERY_TYPE && summary.membership == Membership.JOIN) {
                summary.toGalleryListItem()
            } else null
        }
    }
}