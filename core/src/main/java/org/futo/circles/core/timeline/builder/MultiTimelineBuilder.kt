package org.futo.circles.core.timeline.builder

import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class MultiTimelineBuilder @Inject constructor() : BaseTimelineBuilder() {

    private var currentSnapshotMap: MutableMap<String, List<Post>> = mutableMapOf()
    private var readReceiptMap: MutableMap<String, List<Long>> = mutableMapOf()

    override fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> {
        val roomId = firstOrNull()?.roomId ?: return emptyList()
        val room = MatrixSessionProvider.currentSession?.getRoom(roomId) ?: return emptyList()
        val receipts = getReadReceipts(room).also { readReceiptMap[roomId] = it }
        currentSnapshotMap[roomId] = this.map { it.toPost(receipts) }
        val fullTimelineEventList = currentSnapshotMap.flatMap { (_, value) -> value }
        return sortList(fullTimelineEventList, isThread)
    }

}