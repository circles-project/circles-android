package org.futo.circles.feature.photos.save

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.feature.photos.preview.MediaPreviewDataSource
import org.futo.circles.model.SelectableRoomListItem

class SavePostToGalleryViewModel(
    private val mediaPreviewDataSource: MediaPreviewDataSource,
    private val savePostToGalleryDataSource: SavePostToGalleryDataSource
) : ViewModel() {


    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun saveToGallery(selectedGalleries: List<SelectableRoomListItem>) {
        launchBg {
            mediaPreviewDataSource.getPostContent()?.let { content ->
                savePostToGalleryDataSource.saveMediaToGalleries(content, selectedGalleries)
            }
            saveResultLiveData.postValue(Response.Success(Unit))
        }
    }

}