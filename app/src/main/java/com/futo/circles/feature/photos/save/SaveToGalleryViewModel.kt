package com.futo.circles.feature.photos.save

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.photos.preview.GalleryImageDataSource
import com.futo.circles.model.SelectableRoomListItem

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