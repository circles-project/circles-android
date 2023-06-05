package org.futo.circles.gallery.feature.select

import androidx.lifecycle.ViewModel
import org.futo.circles.core.model.SelectableRoomListItem

class SelectGalleriesViewModel(
    private val selectGalleriesDataSource: SelectGalleriesDataSource
) : ViewModel() {

    val galleriesLiveData = selectGalleriesDataSource.galleriesLiveData

    fun toggleGallerySelect(selectableRoomListItem: SelectableRoomListItem) {
        selectGalleriesDataSource.toggleGallerySelect(selectableRoomListItem)
    }

}