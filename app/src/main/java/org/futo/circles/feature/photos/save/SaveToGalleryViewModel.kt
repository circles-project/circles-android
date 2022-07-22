package org.futo.circles.feature.photos.save

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.photos.preview.MediaPreviewDataSource
import org.futo.circles.model.SelectableRoomListItem

class SaveToGalleryViewModel(
    private val mediaPreviewDataSource: MediaPreviewDataSource,
    private val selectGalleryDataSource: SelectGalleryDataSource
) : ViewModel() {

    val galleriesLiveData = selectGalleryDataSource.galleriesLiveData
    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun saveToGallery() {
        launchBg {
            mediaPreviewDataSource.getImageItem()?.imageContent?.let { content ->
                selectGalleryDataSource.saveMediaToGalleries(content)
            }
            saveResultLiveData.postValue(Response.Success(Unit))
        }
    }

    fun toggleGallerySelect(selectableRoomListItem: SelectableRoomListItem) {
        selectGalleryDataSource.toggleGallerySelect(selectableRoomListItem)
    }

}