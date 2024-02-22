package org.futo.circles.core.feature.timeline.builder

import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class MultiTimelineBuilder @Inject constructor() : BaseTimelineBuilder() {

    private var currentSnapshotMap: MutableMap<String, List<Post>> = mutableMapOf()
    private var readReceiptMap: MutableMap<String, List<Long>> = mutableMapOf()

    override suspend fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> {
        val roomId = firstOrNull()?.roomId ?: return getAllTimelinesPostsList()
        val room = MatrixSessionProvider.currentSession?.getRoom(roomId)
            ?: return getAllTimelinesPostsList()
        val roomName = room.roomSummary()?.nameOrId()
        val receipts = getReadReceipts(room).also { readReceiptMap[roomId] = it }
        currentSnapshotMap[roomId] = this.map { it.toPost(receipts, roomName) }
        return sortList(getAllTimelinesPostsList(), isThread)
    }

    private fun getAllTimelinesPostsList() = currentSnapshotMap.flatMap { (_, value) -> value }

}