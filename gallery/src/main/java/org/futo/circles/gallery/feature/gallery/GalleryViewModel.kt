package org.futo.circles.gallery.feature.gallery

import android.content.Context
import android.net.Uri
import androidx.lifecycle.map
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.onUI
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.picker.PickGalleryMediaListener
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.timeline.BaseTimelineViewModel
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.gallery.model.GalleryContentListItem
import org.futo.circles.model.MediaContent
import org.futo.circles.model.PostContentType

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
            (post.content as? MediaContent)?.let {
                if (it.type == PostContentType.VIDEO_CONTENT && !isVideoAvailable) null
                else GalleryContentListItem(post.id, post.postInfo, it)
            }
        }
    }

    fun uploadMedia(uri: Uri, mediaType: MediaType) {
        launchBg {
            sendMessageDataSource.sendMedia(roomId, uri, null, null, mediaType)
            scrollToTopLiveData.postValue(Unit)
        }
    }

    fun deleteGallery() {
        launchBg { deleteGalleryLiveData.postValue(leaveRoomDataSource.deleteGallery()) }
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
}