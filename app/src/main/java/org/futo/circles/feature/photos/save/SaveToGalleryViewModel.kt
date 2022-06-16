package org.futo.circles.feature.photos.save

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.photos.preview.GalleryImageDataSource
import org.futo.circles.model.SelectableRoomListItem

class SaveToGalleryViewModel(
    private val galleryImageDataSource: GalleryImageDataSource,
    private val selectGalleryDataSource: SelectGalleryDataSource
) : ViewModel() {

    val galleriesLiveData = selectGalleryDataSource.galleriesLiveData
    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun saveToGallery() {
        launchBg {
            galleryImageDataSource.getImageItem()?.imageContent?.let { content ->
                selectGalleryDataSource.saveImageToGalleries(content)
            }
            saveResultLiveData.postValue(Response.Success(Unit))
        }
    }

    fun toggleGallerySelect(selectableRoomListItem: SelectableRoomListItem) {
        selectGalleryDataSource.toggleGallerySelect(selectableRoomListItem)
    }

}