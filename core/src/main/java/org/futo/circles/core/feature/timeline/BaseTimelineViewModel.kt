package org.futo.circles.core.feature.timeline

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.toRoomInfo
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.utils.FileUtils

abstract class BaseTimelineViewModel(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    private val baseTimelineDataSource: BaseTimelineDataSource
) : ViewModel() {

    protected val roomId: String = savedStateHandle.getOrThrow("roomId")

    val titleLiveData =
        baseTimelineDataSource.room.getRoomSummaryLive().map {
            it.getOrNull()?.toRoomInfo()?.title ?: ""
        }

    private val prefetchedVideoUriFlow = MutableStateFlow<Map<String, Uri>>(emptyMap())

    val timelineEventsLiveData = combine(
        getTimelineEventFlow(),
        prefetchedVideoUriFlow
    ) { events, videoUris ->
        mapEventsWithVideoUri(context, events, videoUris)
    }.flowOn(Dispatchers.IO).distinctUntilChanged().asLiveData()

    fun getRoomSummaryLive() = baseTimelineDataSource.room.getRoomSummaryLive()

    fun getTimelineEventFlow() = baseTimelineDataSource.getTimelineEventFlow(viewModelScope)


    private fun mapEventsWithVideoUri(
        context: Context,
        events: List<PostListItem>,
        uriMap: Map<String, Uri>
    ): List<PostListItem> = events.map { listItem ->
        val post = (listItem as? Post) ?: return@map listItem
        if (post.content.type != PostContentType.VIDEO_CONTENT) return@map post
        val mediaContent = (post.content as? MediaContent) ?: return@map post
        val uri = uriMap[post.id] ?: run {
            prefetchVideo(context, post.id, mediaContent.mediaFileData)
            return@map post
        }
        post.copy(
            content = mediaContent.copy(
                mediaFileData = mediaContent.mediaFileData.copy(videoUri = uri)
            )
        )
    }

    @Suppress("DeferredResultUnused")
    private fun prefetchVideo(context: Context, postId: String, data: MediaFileData) {
        launchBg {
            async {
                val uri =
                    FileUtils.downloadEncryptedFileToContentUri(context, data) ?: return@async
                prefetchedVideoUriFlow.update {
                    it.toMutableMap().apply { put(postId, uri) }
                }
            }
        }
    }

    override fun onCleared() {
        baseTimelineDataSource.clearTimeline()
        super.onCleared()
    }

    fun loadMore() {
        launchBg { baseTimelineDataSource.loadMore(true) }
    }
}