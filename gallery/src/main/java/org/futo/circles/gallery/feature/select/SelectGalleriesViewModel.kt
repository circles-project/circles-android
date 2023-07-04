package org.futo.circles.gallery.feature.select

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.model.SelectableRoomListItem
import javax.inject.Inject

@HiltViewModel
class SelectGalleriesViewModel @Inject constructor(
    private val selectGalleriesDataSource: SelectGalleriesDataSource
) : ViewModel() {

    val galleriesLiveData = selectGalleriesDataSource.galleriesLiveData

    fun toggleGallerySelect(selectableRoomListItem: SelectableRoomListItem) {
        selectGalleriesDataSource.toggleGallerySelect(selectableRoomListItem)
    }

}