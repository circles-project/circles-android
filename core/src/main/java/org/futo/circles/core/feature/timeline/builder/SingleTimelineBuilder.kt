package org.futo.circles.core.feature.timeline.builder

import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class SingleTimelineBuilder @Inject constructor() : BaseTimelineBuilder() {

    private var currentSnapshotList: List<Post> = listOf()

    override suspend fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> {
        val roomId = firstOrNull()?.roomId ?: return currentSnapshotList
        val room =
            MatrixSessionProvider.currentSession?.getRoom(roomId) ?: return currentSnapshotList
        val receipts = getReadReceipts(room)
        val roomName = room.roomSummary()?.nameOrId()
        val roomOwnerName = getRoomOwner(roomId)?.notEmptyDisplayName()
        val posts = this.map {
            it.toPost(
                receipts,
                if (isThread) roomName else null,
                if (isThread) roomOwnerName else null
            )
        }
        currentSnapshotList = posts
        return sortList(posts, isThread)
    }

}