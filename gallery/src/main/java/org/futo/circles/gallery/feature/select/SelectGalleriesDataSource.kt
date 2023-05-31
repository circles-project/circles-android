package org.futo.circles.gallery.feature.select

import androidx.lifecycle.MutableLiveData
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.gallery.model.SelectableRoomListItem
import org.futo.circles.mapping.toSelectableRoomListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SelectGalleriesDataSource {

    private val session by lazy { MatrixSessionProvider.currentSession }

    val galleriesLiveData = MutableLiveData(getInitialGalleriesList())

    private fun getInitialGalleriesList(): List<SelectableRoomListItem> =
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams {
            excludeType = null
        })?.mapNotNull { summary ->
            if (summary.roomType == GALLERY_TYPE && summary.membership == Membership.JOIN)
                summary.toSelectableRoomListItem()
            else null
        } ?: emptyList()


    fun toggleGallerySelect(gallery: SelectableRoomListItem) {
        val newList = galleriesLiveData.value?.toMutableList()?.map {
            if (it.id == gallery.id) it.copy(isSelected = !it.isSelected) else it
        }
        galleriesLiveData.postValue(newList)
    }
}