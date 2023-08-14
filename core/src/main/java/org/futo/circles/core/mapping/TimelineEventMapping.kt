package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getPostContentType
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.PostInfo
import org.futo.circles.core.model.ReactionsData
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited

fun TimelineEvent.toPost(readReceipts: List<Long> = emptyList()): Post = Post(
    postInfo = toPostInfo(),
    content = toPostContent(),
    sendState = root.sendState,
    readByCount = getReadByCount(readReceipts),
    repliesCount = root.threadDetails?.numberOfThreads ?: 0
)

private fun TimelineEvent.toPostInfo(): PostInfo = PostInfo(
    id = eventId,
    roomId = roomId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    reactionsData = annotations?.reactionsSummary?.map {
        ReactionsData(it.key, it.count, it.addedByMe)
    } ?: emptyList(),
    isEdited = hasBeenEdited()
)

private fun TimelineEvent.toPostContent(): PostContent = when (getPostContentType()) {
    PostContentType.TEXT_CONTENT -> toTextContent()
    PostContentType.IMAGE_CONTENT -> toMediaContent(MediaType.Image)
    PostContentType.VIDEO_CONTENT -> toMediaContent(MediaType.Video)
    PostContentType.POLL_CONTENT -> toPollContent()
    else -> toTextContent()
}

private fun TimelineEvent.getReadByCount(receipts: List<Long>): Int {
    val eventTime = root.originServerTs ?: System.currentTimeMillis()
    var count = 0
    receipts.forEach { receiptTime -> if (receiptTime >= eventTime) count++ }
    return count
}