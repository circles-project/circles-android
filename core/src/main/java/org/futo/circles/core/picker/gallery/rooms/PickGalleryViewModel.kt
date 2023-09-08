package org.futo.circles.core.picker.gallery.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

@HiltViewModel
class PickGalleryViewModel @Inject constructor() : ViewModel() {

    val galleriesLiveData = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams())
        ?.map { list -> filterGalleries(list) }

    private fun filterGalleries(list: List<RoomSummary>): List<JoinedGalleryListItem> {
        return list.mapNotNull { summary ->
            if (summary.roomType == GALLERY_TYPE && summary.membership == Membership.JOIN) {
                summary.toJoinedGalleryListItem()
            } else null
        }
    }
}