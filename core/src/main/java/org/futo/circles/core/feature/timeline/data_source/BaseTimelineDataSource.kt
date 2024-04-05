package org.futo.circles.core.feature.timeline.data_source

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.feature.timeline.builder.BaseTimelineBuilder
import org.futo.circles.core.feature.timeline.builder.MultiTimelineBuilder
import org.futo.circles.core.feature.timeline.builder.SingleTimelineBuilder
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import javax.inject.Inject


abstract class BaseTimelineDataSource(
    savedStateHandle: SavedStateHandle,
    private val timelineBuilder: BaseTimelineBuilder
) {

    class Factory @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val preferencesProvider: PreferencesProvider
    ) {
        fun create(isMultiTimelines: Boolean): BaseTimelineDataSource =
            if (isMultiTimelines) MultiTimelinesDataSource(
                savedStateHandle,
                MultiTimelineBuilder(preferencesProvider)
            )
            else SingleTimelineDataSource(
                savedStateHandle,
                SingleTimelineBuilder(preferencesProvider)
            )
    }

    protected val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val threadEventId: String? = savedStateHandle["threadEventId"]
    protected val session = MatrixSessionProvider.getSessionOrThrow()

    val room = session.getRoom(roomId) ?: throw IllegalArgumentException("room is not found")

    private val isThread: Boolean = threadEventId != null
    private val listDirection =
        if (isThread) Timeline.Direction.FORWARDS else Timeline.Direction.BACKWARDS

    private val pageLoadingFlow = MutableStateFlow(false)

    fun getTimelineEventFlow(viewModelScope: CoroutineScope): Flow<List<PostListItem>> = combine(
        pageLoadingFlow,
        getPostEventsFlow(viewModelScope)
    ) { isLoading, events ->
        if (isLoading) {
            mutableListOf<PostListItem>().apply {
                addAll(events)
                add(TimelineLoadingItem())
            }
        } else events
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getPostEventsFlow(viewModelScope: CoroutineScope): Flow<List<Post>> = callbackFlow {
        val listener = object : Timeline.Listener {
            override fun onTimelineUpdated(
                roomId: String,
                timelineId: String,
                snapshot: List<TimelineEvent>
            ) {
                if (snapshot.isNotEmpty()) trySend(roomId to snapshot)
            }

            override fun onTimelineFailure(timelineId: String, throwable: Throwable) {
                onRestartTimeline(timelineId, throwable)
            }
        }
        startTimeline(viewModelScope, listener)
        awaitClose()
    }.flowOn(Dispatchers.IO)
        .mapLatest { (roomId, snapshot) -> timelineBuilder.build(roomId, snapshot, isThread) }
        .distinctUntilChanged()

    protected abstract fun startTimeline(
        viewModelScope: CoroutineScope,
        listener: Timeline.Listener
    )

    protected abstract fun onRestartTimeline(timelineId: String, throwable: Throwable)
    abstract fun clearTimeline()
    protected abstract suspend fun loadMore()

    suspend fun loadNextPostsPage() {
        loadMore()
        pageLoadingFlow.update { false }
    }

    protected fun createAndStartNewTimeline(room: Room, listener: Timeline.Listener) =
        room.timelineService()
            .createTimeline(
                null,
                TimelineSettings(initialSize = MESSAGES_PER_PAGE, rootThreadEventId = threadEventId)
            )
            .apply {
                addListener(listener)
                start(threadEventId)
            }

    protected fun closeTimeline(timeline: Timeline) {
        timeline.removeAllListeners()
        timeline.dispose()
    }

    protected suspend fun loadNextPage(timeline: Timeline) {
        if (timeline.hasMoreToLoad(listDirection)) {
            pageLoadingFlow.update { true }
            Log.d("MyLog", "load root")
            var snapshot = timeline.awaitPaginate(listDirection, MESSAGES_PER_PAGE)
            var postsLoadedCount = timelineBuilder.filterTimelineEvents(snapshot, isThread).size

            Log.d("MyLog", "count $postsLoadedCount")
            while (postsLoadedCount < MIN_MESSAGES_ON_PAGE && timeline.hasMoreToLoad(listDirection)) {
                Log.d("MyLog", "load next")
                snapshot = timeline.awaitPaginate(listDirection, MESSAGES_PER_PAGE)
                postsLoadedCount = timelineBuilder.filterTimelineEvents(snapshot, isThread).size
                Log.d("MyLog", "count $postsLoadedCount")
            }
            timeline.postCurrentSnapshot()
        }
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 20
        private const val MIN_MESSAGES_ON_PAGE = 10
    }
}