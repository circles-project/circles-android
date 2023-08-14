package org.futo.circles.core.timeline.builder

import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class MultiTimelineBuilder @Inject constructor() : BaseTimelineBuilder() {

    private var currentSnapshotMap: MutableMap<String, List<Post>> = mutableMapOf()

    override fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> {
        val roomId = firstOrNull()?.roomId ?: return emptyList()
        currentSnapshotMap[roomId] = this.map { it.toPost() }
        val fullTimelineEventList = currentSnapshotMap.flatMap { (_, value) -> value }
        return sortList(fullTimelineEventList, isThread)
    }

}