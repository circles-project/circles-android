package org.futo.circles.core.feature.timeline

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.toRoomInfo
import org.futo.circles.core.feature.circles.filter.CircleFilterAccountDataManager
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
    private val baseTimelineDataSource: BaseTimelineDataSource,
    private val filterAccountDataManager: CircleFilterAccountDataManager
) : ViewModel() {

    protected val roomId: String = savedStateHandle.getOrThrow("roomId")
    protected val timelineId: String? = savedStateHandle["timelineId"]

    val titleLiveData =
        baseTimelineDataSource.room.getRoomSummaryLive().map {
            it.getOrNull()?.toRoomInfo(timelineId != null)?.title ?: ""
        }

    val isFilterActiveLiveData = MutableLiveData(false)
    private val prefetchedVideoUriFlow = MutableStateFlow<Map<String, Uri>>(emptyMap())

    val timelineEventsLiveData = combine(
        baseTimelineDataSource.getTimelineEventFlow(viewModelScope),
        getFilterFlow(),
        prefetchedVideoUriFlow
    ) { events, selectedRoomIds, videoUris ->
        val filteredEvents = applyTimelinesFilter(events, selectedRoomIds)
        mapEventsWithVideoUri(context, filteredEvents, videoUris)
    }.flowOn(Dispatchers.IO).distinctUntilChanged().asLiveData()

    private fun getFilterFlow(): Flow<Set<String>> {
        timelineId ?: return MutableStateFlow(emptySet())

        return filterAccountDataManager.getCircleFilterLive(roomId)?.map { optionalEvent ->
            filterAccountDataManager.getEventContentAsSet(
                optionalEvent.getOrNull()?.content,
                roomId
            )
        }?.asFlow() ?: MutableStateFlow(emptySet())
    }

    private fun applyTimelinesFilter(
        events: List<PostListItem>,
        selectedRoomIds: Set<String>
    ): List<PostListItem> {
        val isActive = isFilterActive(selectedRoomIds)
        isFilterActiveLiveData.postValue(isActive)
        return if (isActive) events.filter { selectedRoomIds.contains((it as? Post)?.postInfo?.roomId) }
        else events
    }

    private fun isFilterActive(selectedRoomIds: Set<String>): Boolean {
        timelineId ?: return false
        if (selectedRoomIds.isEmpty()) return false
        return selectedRoomIds.size != filterAccountDataManager.getAllTimelinesIds(roomId).size
    }

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