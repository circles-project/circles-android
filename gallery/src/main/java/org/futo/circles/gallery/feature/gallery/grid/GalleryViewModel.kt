package org.futo.circles.gallery.feature.gallery.grid

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.onUI
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.picker.DeviceMediaPickerHelper
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.TimelineDataSource
import org.futo.circles.core.timeline.post.PostOptionsDataSource
import org.futo.circles.core.timeline.post.SendMessageDataSource
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.gallery.feature.gallery.full_screen.media_item.FullScreenMediaDataSource
import org.futo.circles.gallery.feature.pick.PickGalleryMediaListener
import org.futo.circles.gallery.model.GalleryContentListItem
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineDataSource: TimelineDataSource,
    private val sendMessageDataSource: SendMessageDataSource,
    private val mediaDataSource: FullScreenMediaDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val isVideoAvailable: Boolean =
        savedStateHandle[DeviceMediaPickerHelper.IS_VIDEO_AVAILABLE] ?: true

    val galleryItemsLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull { post ->
            (post.content as? MediaContent)?.let {
                if (it.type == PostContentType.VIDEO_CONTENT && !isVideoAvailable) null
                else GalleryContentListItem(post.id, post.postInfo, it)
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

    fun selectMediaForPicker(
        context: Context,
        item: GalleryContentListItem,
        listener: PickGalleryMediaListener
    ) = launchBg {
        onMediaSelected(
            context, item.mediaContent.mediaFileData, listener, item.mediaContent.getMediaType()
        )
    }

    private suspend fun onMediaSelected(
        context: Context,
        mediaFileData: MediaFileData?,
        listener: PickGalleryMediaListener,
        mediaType: MediaType
    ) {
        val content = mediaFileData ?: return
        val uri = downloadEncryptedFileToContentUri(context, content) ?: return
        onUI { listener.onMediaSelected(uri, mediaType) }
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