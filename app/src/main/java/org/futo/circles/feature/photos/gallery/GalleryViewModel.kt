package org.futo.circles.feature.photos.gallery

import android.content.Context
import android.net.Uri
import androidx.lifecycle.map
import com.bumptech.glide.Glide
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.getUri
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.photos.preview.GalleryImageDataSource
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.timeline.BaseTimelineViewModel
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.model.GalleryImageListItem
import org.futo.circles.model.ImageContent

class GalleryViewModel(
    private val roomId: String,
    timelineDataSource: TimelineDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource,
    private val sendMessageDataSource: SendMessageDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val scrollToTopLiveData = SingleEventLiveData<Unit>()
    val selectedImageUri = SingleEventLiveData<Response<Uri>>()
    val deleteGalleryLiveData = SingleEventLiveData<Response<Unit?>>()
    val galleryImagesLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull { post ->
            (post.content as? ImageContent)?.let {
                GalleryImageListItem(post.id, it, post.postInfo)
            }
        }
    }

    fun uploadImage(uri: Uri) {
        sendMessageDataSource.sendImage(roomId, uri, null)
        scrollToTopLiveData.postValue(Unit)
    }

    fun deleteGallery() {
        launchBg { deleteGalleryLiveData.postValue(leaveRoomDataSource.deleteGallery()) }
    }

    fun getImageUri(context: Context, postId: String) = launchBg {
        GalleryImageDataSource(roomId, postId).getImageItem()?.imageContent?.let {
            val uri = Glide.with(context).asFile().load(it).submit().get().getUri(context)
            selectedImageUri.postValue(Response.Success(uri))
        }
    }
}