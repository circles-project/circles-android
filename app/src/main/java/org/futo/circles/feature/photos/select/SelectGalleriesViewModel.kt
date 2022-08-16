package org.futo.circles.feature.photos.select

import androidx.lifecycle.ViewModel
import org.futo.circles.model.SelectableRoomListItem

class SelectGalleriesViewModel(
    private val selectGalleriesDataSource: SelectGalleriesDataSource
) : ViewModel() {

    val galleriesLiveData = selectGalleriesDataSource.galleriesLiveData

    fun toggleGallerySelect(selectableRoomListItem: SelectableRoomListItem) {
        selectGalleriesDataSource.toggleGallerySelect(selectableRoomListItem)
    }

}