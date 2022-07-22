package org.futo.circles.feature.photos.gallery

import android.content.Context
import android.net.Uri
import androidx.lifecycle.map
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.picker.PickGalleryMediaListener
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.onUI
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.timeline.BaseTimelineViewModel
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.model.*

class GalleryViewModel(
    private val roomId: String,
    private val isVideoAvailable: Boolean,
    timelineDataSource: TimelineDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource,
    private val sendMessageDataSource: SendMessageDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val scrollToTopLiveData = SingleEventLiveData<Unit>()
    val deleteGalleryLiveData = SingleEventLiveData<Response<Unit?>>()
    val galleryItemsLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull { post ->
            when (val content = post.content) {
                is ImageContent -> GalleryImageListItem(post.id, content, post.postInfo)
                is VideoContent ->
                    if (isVideoAvailable)
                        GalleryVideoListItem(post.id, content, post.postInfo)
                    else null
                else -> null
            }
        }
    }

    fun uploadMedia(uri: Uri, mediaType: MediaType) {
        sendMessageDataSource.sendMedia(roomId, uri, null, mediaType)
        scrollToTopLiveData.postValue(Unit)
    }

    fun deleteGallery() {
        launchBg { deleteGalleryLiveData.postValue(leaveRoomDataSource.deleteGallery()) }
    }

    fun selectMediaForPicker(
        context: Context,
        item: GalleryContentListItem,
        listener: PickGalleryMediaListener
    ) = launchBg {
        when (item.type) {
            PostContentType.IMAGE_CONTENT -> {
                val data = (item as? GalleryImageListItem)?.imageContent?.mediaContentData
                onMediaSelected(context, data, listener, MediaType.Image)
            }
            PostContentType.VIDEO_CONTENT -> {
                val data = (item as? GalleryVideoListItem)?.videoContent?.mediaContentData
                onMediaSelected(context, data, listener, MediaType.Video)
            }
            else -> {}
        }
    }

    private suspend fun onMediaSelected(
        context: Context,
        mediaContentData: MediaContentData?,
        listener: PickGalleryMediaListener,
        mediaType: MediaType
    ) {
        val content = mediaContentData ?: return
        val uri = downloadEncryptedFileToContentUri(context, content) ?: return
        onUI { listener.onMediaSelected(uri, mediaType) }
    }
}