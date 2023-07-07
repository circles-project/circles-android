package org.futo.circles.feature.timeline.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.timeline.post.PostOptionsDataSource
import org.futo.circles.gallery.feature.gallery.full_screen.media_item.FullScreenMediaDataSource
import javax.inject.Inject

@HiltViewModel
class TimelineMediaPreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaDataSource: FullScreenMediaDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String = savedStateHandle.getOrThrow("eventId")

    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadLiveData = SingleEventLiveData<Unit>()


    fun share() {
        val content = mediaDataSource.getPostContent() ?: return
        launchBg {
            shareLiveData.postValue(postOptionsDataSource.getShareableContent(content))
        }
    }

    fun removeImage() {
        postOptionsDataSource.removeMessage(roomId, eventId)
    }

    fun save() {
        val content = mediaDataSource.getPostContent() ?: return
        launchBg {
            postOptionsDataSource.saveMediaToDevice(content)
            downloadLiveData.postValue(Unit)
        }
    }
}