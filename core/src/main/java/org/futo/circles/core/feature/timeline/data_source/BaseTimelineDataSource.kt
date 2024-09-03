package org.futo.circles.core.feature.timeline.data_source

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
import kotlinx.coroutines.withContext
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.core.model.TimelineTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.api.session.room.timeline.isEdition
import javax.inject.Inject

abstract class BaseTimelineDataSource(
    private val preferencesProvider: PreferencesProvider
) {

    class Factory @Inject constructor(
        private val preferencesProvider: PreferencesProvider
    ) {
        fun create(
            timelineType: TimelineTypeArg,
            roomId: String?,
            threadEventId: String?
        ): BaseTimelineDataSource = when (timelineType) {
            TimelineTypeArg.ALL_CIRCLES -> MultiTimelinesDataSource(preferencesProvider)

            else -> {
                if (roomId == null) throw IllegalArgumentException(
                    "Single room timeline must have roomId"
                )
                if (timelineType == TimelineTypeArg.THREAD && threadEventId == null) throw IllegalArgumentException(
                    "Thread timeline type must have threadEventId"
                )
                SingleTimelineDataSource(preferencesProvider, timelineType, roomId, threadEventId)
            }
        }
    }

    protected val session = MatrixSessionProvider.getSessionOrThrow()

    protected abstract val listDirection: Timeline.Direction

    private val supportedTimelineEvens: List<String> =
        listOf(EventType.MESSAGE, EventType.POLL_START.stable, EventType.POLL_START.unstable)

    private val pageLoadingFlow = MutableStateFlow(false)

    fun getTimelineEventFlow(viewModelScope: CoroutineScope): Flow<List<PostListItem>> = combine(
        pageLoadingFlow,
        getPostEventsFlow(viewModelScope)
    ) { isLoading, events ->
        if (isLoading) events + TimelineLoadingItem()
        else events
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    abstract fun clearTimeline()

    abstract suspend fun loadMore(showLoader: Boolean)

    protected abstract fun startTimeline(
        viewModelScope: CoroutineScope,
        listener: Timeline.Listener
    )

    protected abstract fun onRestartTimeline(timelineId: String, throwable: Throwable)

    protected abstract suspend fun processSnapshot(
        snapshot: List<TimelineEvent>,
        roomId: String,
    ): List<Post>

    protected abstract fun filterTimelineEvents(snapshot: List<TimelineEvent>): List<TimelineEvent>

    protected fun createAndStartNewTimeline(
        room: Room,
        listener: Timeline.Listener,
        threadEventId: String? = null
    ) =
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

    protected suspend fun loadNextPage(showLoader: Boolean, timeline: Timeline) {
        if (timeline.hasMoreToLoad(listDirection)) {
            pageLoadingFlow.update { showLoader }
            var snapshot = timeline.awaitPaginate(listDirection, MESSAGES_PER_PAGE)
            var postsLoadedCount = filterTimelineEvents(snapshot).size
            while (postsLoadedCount < MIN_MESSAGES_ON_PAGE && timeline.hasMoreToLoad(listDirection)) {
                snapshot = timeline.awaitPaginate(listDirection, MESSAGES_PER_PAGE)
                postsLoadedCount = filterTimelineEvents(snapshot).size
            }
            timeline.postCurrentSnapshot()
        }
        pageLoadingFlow.update { false }
    }


    protected fun sortList(list: List<Post>) =
        if (listDirection == Timeline.Direction.FORWARDS) list.sortedBy { it.postInfo.timestamp }
        else list.sortedByDescending { it.postInfo.timestamp }


    protected fun TimelineEvent.isSupportedEvent() =
        if (preferencesProvider.isDeveloperModeEnabled()) true
        else root.getClearType() in supportedTimelineEvens


    protected fun TimelineEvent.isNotRemovedEvent() = !isEdition() && !root.isRedacted()


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

            override fun onNewTimelineEvents(eventIds: List<String>) {
                super.onNewTimelineEvents(eventIds)
                pageLoadingFlow.update { false }
            }
        }
        startTimeline(viewModelScope, listener)
        awaitClose()
    }
        .flowOn(Dispatchers.IO)
        .mapLatest { (roomId, snapshot) -> this.buildList(roomId, snapshot) }
        .distinctUntilChanged()


    private suspend fun buildList(
        roomId: String,
        snapshot: List<TimelineEvent>
    ): List<Post> =
        withContext(Dispatchers.IO) {
            val filteredEvents = filterTimelineEvents(snapshot)
            processSnapshot(filteredEvents, roomId)
        }

    companion object {
        private const val MESSAGES_PER_PAGE = 20
        private const val MIN_MESSAGES_ON_PAGE = 10
    }
}