package org.futo.circles.feature.timeline.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.timeline.post.PostContentDataSource
import org.futo.circles.core.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.core.model.ShareableContent
import javax.inject.Inject

@HiltViewModel
class TimelineMediaPreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaDataSource: PostContentDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String = savedStateHandle.getOrThrow("eventId")

    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadLiveData = SingleEventLiveData<Unit>()


    fun share() {
        val content = mediaDataSource.getPostContent(roomId, eventId) ?: return
        launchBg {
            postOptionsDataSource.getShareableContent(content)?.let {
                shareLiveData.postValue(it)
            }
        }
    }

    fun removeImage() {
        launchBg { postOptionsDataSource.removeMessage(roomId, eventId) }
    }

    fun save() {
        val content = mediaDataSource.getPostContent(roomId, eventId) ?: return
        launchBg {
            postOptionsDataSource.saveMediaToDevice(content)
            downloadLiveData.postValue(Unit)
        }
    }
}