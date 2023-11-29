package org.futo.circles.core.mapping

import io.noties.markwon.Markwon
import org.futo.circles.core.extensions.getPostContentType
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.PostInfo
import org.futo.circles.core.model.ReactionsData
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited

fun TimelineEvent.toPost(markwon: Markwon, readReceipts: List<Long> = emptyList()): Post = Post(
    postInfo = toPostInfo(),
    content = toPostContent(markwon),
    sendState = root.sendState,
    readByCount = getReadByCount(readReceipts),
    repliesCount = root.threadDetails?.numberOfThreads ?: 0,
    reactionsData = annotations?.reactionsSummary?.map {
        ReactionsData(it.key, it.count, it.addedByMe)
    } ?: emptyList()
)

private fun TimelineEvent.toPostInfo(): PostInfo = PostInfo(
    id = eventId,
    roomId = roomId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    isEdited = hasBeenEdited()
)

private fun TimelineEvent.toPostContent(markwon: Markwon): PostContent =
    when (getPostContentType()) {
        PostContentType.TEXT_CONTENT -> toTextContent(markwon)
        PostContentType.IMAGE_CONTENT -> toMediaContent(MediaType.Image, markwon)
        PostContentType.VIDEO_CONTENT -> toMediaContent(MediaType.Video, markwon)
        PostContentType.POLL_CONTENT -> toPollContent()
        else -> toTextContent(markwon)
    }

private fun TimelineEvent.getReadByCount(receipts: List<Long>): Int {
    val eventTime = root.originServerTs ?: 0
    var count = 0
    receipts.forEach { receiptTime -> if (receiptTime >= eventTime) count++ }
    return count
}