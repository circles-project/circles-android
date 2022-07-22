package org.futo.circles.feature.photos.preview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.feature.timeline.post.share.ShareableContent

class MediaPreviewViewModel(
    private val roomId: String,
    private val eventId: String,
    mediaPreviewDataSource: MediaPreviewDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : ViewModel() {

    val mediaContentLiveData = MutableLiveData(mediaPreviewDataSource.getPostContent())
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadLiveData = SingleEventLiveData<Unit>()

    fun share() {
        val content = mediaContentLiveData.value ?: return
        launchBg {
            shareLiveData.postValue(postOptionsDataSource.getShareableContent(content))
        }
    }

    fun removeImage() {
        postOptionsDataSource.removeMessage(roomId, eventId)
    }

    fun save() {
        val content = mediaContentLiveData.value ?: return
        launchBg {
            postOptionsDataSource.saveMediaToDevice(content)
            downloadLiveData.postValue(Unit)
        }
    }
}