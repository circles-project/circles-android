package org.futo.circles.core.picker.gallery.media

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.onUI
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.picker.gallery.PickGalleryMediaListener
import org.futo.circles.core.picker.helper.MediaPickerHelper
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.TimelineDataSource
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import javax.inject.Inject

@HiltViewModel
class PickMediaItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineDataSource: TimelineDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    private val isVideoAvailable: Boolean =
        savedStateHandle[MediaPickerHelper.IS_VIDEO_AVAILABLE] ?: true

    val galleryItemsLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull { post ->
            (post.content as? MediaContent)?.let {
                if (it.type == PostContentType.VIDEO_CONTENT && !isVideoAvailable) null
                else GalleryContentListItem(post.id, post.postInfo, it)
            }
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
}