package org.futo.circles.gallery.feature.gallery.grid

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.TimelineDataSource
import org.futo.circles.core.timeline.post.PostOptionsDataSource
import org.futo.circles.core.timeline.post.SendMessageDataSource
import org.futo.circles.core.timeline.post.PostContentDataSource
import org.futo.circles.core.model.GalleryContentListItem
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineDataSource: TimelineDataSource,
    private val sendMessageDataSource: SendMessageDataSource,
    private val mediaDataSource: PostContentDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val galleryItemsLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull { post ->
            (post.content as? MediaContent)?.let {
                GalleryContentListItem(post.id, post.postInfo, it)
            }
        }
    }

    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadLiveData = SingleEventLiveData<Unit>()

    fun uploadMedia(uri: Uri, mediaType: MediaType) {
        launchBg {
            sendMessageDataSource.sendMedia(roomId, uri, null, null, mediaType)
        }
    }

    fun share(position: Int) {
        val eventId = galleryItemsLiveData.value?.getOrNull(position)?.id ?: return
        val content = mediaDataSource.getPostContent(roomId, eventId) ?: return
        launchBg {
            shareLiveData.postValue(postOptionsDataSource.getShareableContent(content))
        }
    }

    fun removeImage(position: Int) {
        val eventId = galleryItemsLiveData.value?.getOrNull(position)?.id ?: return
        postOptionsDataSource.removeMessage(roomId, eventId)
    }

    fun save(position: Int) {
        val eventId = galleryItemsLiveData.value?.getOrNull(position)?.id ?: return
        val content = mediaDataSource.getPostContent(roomId, eventId) ?: return
        launchBg {
            postOptionsDataSource.saveMediaToDevice(content)
            downloadLiveData.postValue(Unit)
        }
    }
}