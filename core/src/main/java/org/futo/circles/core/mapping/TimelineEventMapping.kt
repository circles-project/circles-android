package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getReadByCountForEvent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.PostInfo
import org.futo.circles.core.model.PostReadInfo
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited

fun TimelineEvent.toPost(
    postContentType: PostContentType,
    lastReadEventTime: Long = 0L
): Post = Post(
    postInfo = toPostInfo(),
    content = toPostContent(postContentType),
    sendState = root.sendState,
    readInfo = toReadInfo(lastReadEventTime),
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

private fun TimelineEvent.toReadInfo(lastReadEventTime: Long): PostReadInfo = PostReadInfo(
    shouldIndicateAsNew = (root.originServerTs ?: 0L) > lastReadEventTime,
    readByCount = MatrixSessionProvider.currentSession?.getRoom(roomId)
        ?.getReadByCountForEvent(eventId) ?: 0
)


private fun TimelineEvent.toPostContent(
    postContentType: PostContentType
): PostContent =
    when (postContentType) {
        PostContentType.TEXT_CONTENT -> toTextContent()
        PostContentType.IMAGE_CONTENT -> toMediaContent(MediaType.Image)
        PostContentType.VIDEO_CONTENT -> toMediaContent(MediaType.Video)
        PostContentType.POLL_CONTENT -> toPollContent()
    }