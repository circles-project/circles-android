package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.feature.timeline.builder.BaseTimelineBuilder
import org.futo.circles.core.feature.timeline.builder.MultiTimelineBuilder
import org.futo.circles.core.feature.timeline.builder.SingleTimelineBuilder
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
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

    class Factory @Inject constructor(private val savedStateHandle: SavedStateHandle) {
        fun create(isMultiTimelines: Boolean): BaseTimelineDataSource =
            if (isMultiTimelines) MultiTimelinesDataSource(savedStateHandle, MultiTimelineBuilder())
            else SingleTimelineDataSource(savedStateHandle, SingleTimelineBuilder())
    }

    protected val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val threadEventId: String? = savedStateHandle["threadEventId"]
    protected val session = MatrixSessionProvider.getSessionOrThrow()

    val room = session.getRoom(roomId) ?: throw IllegalArgumentException("room is not found")

    private val isThread: Boolean = threadEventId != null
    private val listDirection =
        if (isThread) Timeline.Direction.FORWARDS else Timeline.Direction.BACKWARDS


    fun getTimelineEventFlow(): Flow<List<Post>> = callbackFlow {
        val listener = object : Timeline.Listener {
            override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
                if (snapshot.isNotEmpty()) trySend(snapshot)
            }

            override fun onTimelineFailure(timelineId: String, throwable: Throwable) {
                onTimelineFailure(timelineId, throwable)
            }
        }
        startTimeline(listener)
        awaitClose()
    }.flowOn(Dispatchers.IO)
        .mapLatest {
            timelineBuilder.build(it, isThread)
        }
        .distinctUntilChanged()

    protected abstract fun startTimeline(listener: Timeline.Listener)

    protected abstract fun onTimelineFailure(timelineId: String, throwable: Throwable)
    abstract fun clearTimeline()
    abstract fun loadMore(): Boolean

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

    protected fun loadNextPage(timeline: Timeline): Boolean {
        val hasMore = timeline.hasMoreToLoad(listDirection)
        if (hasMore) timeline.paginate(listDirection, MESSAGES_PER_PAGE)
        return hasMore
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 50
    }
}