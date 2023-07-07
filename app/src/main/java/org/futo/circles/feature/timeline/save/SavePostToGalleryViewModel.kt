package org.futo.circles.feature.timeline.save

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.feature.timeline.preview.TimelineMediaPreviewDataSource
import javax.inject.Inject

@HiltViewModel
class SavePostToGalleryViewModel @Inject constructor(
    private val mediaPreviewDataSource: TimelineMediaPreviewDataSource,
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