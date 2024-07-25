package org.futo.circles.gallery.feature.gallery.grid

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.circles.filter.CircleFilterAccountDataManager
import org.futo.circles.core.feature.timeline.BaseTimelineViewModel
import org.futo.circles.core.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.feature.timeline.data_source.TimelineType
import org.futo.circles.core.feature.timeline.post.PostContentDataSource
import org.futo.circles.core.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.GalleryTimelineLoadingListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.model.TimelineLoadingItem
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    timelineDataSourceFactory: BaseTimelineDataSource.Factory,
    private val sendMessageDataSource: SendMessageDataSource,
    private val mediaDataSource: PostContentDataSource,
    private val postOptionsDataSource: PostOptionsDataSource,
    accessLevelDataSource: AccessLevelDataSource,
    circleFilterAccountDataManager: CircleFilterAccountDataManager
) : BaseTimelineViewModel(
    savedStateHandle,
    context,
    timelineDataSourceFactory.create(TimelineType.GALLERY),
    circleFilterAccountDataManager
) {

    val accessLevelLiveData = accessLevelDataSource.accessLevelFlow.asLiveData()

    val galleryItemsLiveData = getTimelineEventFlow().asLiveData().map { list ->
        list.mapNotNull { item ->
            when (item) {
                is Post -> (item.content as? MediaContent)?.let {
                    GalleryContentListItem(item.id, item.postInfo, it)
                }

                is TimelineLoadingItem -> GalleryTimelineLoadingListItem()
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
        launchBg { postOptionsDataSource.removeMessage(roomId, eventId) }
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