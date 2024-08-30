package org.futo.circles.feature.timeline.save

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.timeline.post.PostContentDataSource
import org.futo.circles.core.model.SelectableRoomListItem
import javax.inject.Inject

@HiltViewModel
class SavePostToGalleryViewModel @Inject constructor(
    private val postContentDataSource: PostContentDataSource,
    private val savePostToGalleryDataSource: SavePostToGalleryDataSource
) : ViewModel() {

    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun saveToGallery(
        selectedGalleries: List<SelectableRoomListItem>,
        roomId: String,
        eventId: String
    ) {
        launchBg {
            postContentDataSource.getPostContent(roomId, eventId)?.let { content ->
                savePostToGalleryDataSource.saveMediaToGalleries(content, selectedGalleries)
            }
            saveResultLiveData.postValue(Response.Success(Unit))
        }
    }
}