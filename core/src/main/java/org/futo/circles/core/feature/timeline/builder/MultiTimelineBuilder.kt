package org.futo.circles.core.feature.timeline.builder

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class MultiTimelineBuilder @Inject constructor(
    @ApplicationContext context: Context
) : BaseTimelineBuilder() {

    private var currentSnapshotMap: MutableMap<String, List<Post>> = mutableMapOf()
    private var readReceiptMap: MutableMap<String, List<Long>> = mutableMapOf()
    private val markwon = MarkdownParser.markwonBuilder(context)

    override fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> {
        val roomId = firstOrNull()?.roomId ?: return emptyList()
        val room = MatrixSessionProvider.currentSession?.getRoom(roomId) ?: return emptyList()
        val receipts = getReadReceipts(room).also { readReceiptMap[roomId] = it }
        currentSnapshotMap[roomId] = this.map { it.toPost(markwon, receipts) }
        val fullTimelineEventList = currentSnapshotMap.flatMap { (_, value) -> value }
        return sortList(fullTimelineEventList, isThread)
    }

}