package com.futo.circles.feature.photos

import com.futo.circles.core.rooms.data_source.RoomsDataSource
import com.futo.circles.mapping.toGalleryListItem
import com.futo.circles.model.GALLERY_TYPE
import com.futo.circles.model.RoomListItem
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