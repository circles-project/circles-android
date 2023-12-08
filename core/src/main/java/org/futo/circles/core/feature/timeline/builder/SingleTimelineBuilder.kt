package org.futo.circles.core.feature.timeline.builder

import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class SingleTimelineBuilder @Inject constructor() : BaseTimelineBuilder() {

    override suspend fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> {
        val room = MatrixSessionProvider.currentSession?.getRoom(firstOrNull()?.roomId ?: "")
            ?: return emptyList()
        val receipts = getReadReceipts(room)
        return sortList(this.map { it.toPost(receipts) }, isThread)
    }

}