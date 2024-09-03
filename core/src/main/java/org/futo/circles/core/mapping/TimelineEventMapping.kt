package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getPostContentType
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.PostInfo
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.core.model.toOtherEventContent
import org.futo.circles.core.model.toTextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited

fun TimelineEvent.toPost(
    timelineName: String? = null,
    timelineOwnerName: String? = null
): Post = Post(
    postInfo = toPostInfo(),
    content = toPostContent(),
    repliesCount = root.threadDetails?.numberOfThreads ?: 0,
    reactionsData = annotations?.reactionsSummary?.map {
        ReactionsData(it.key, it.count, it.addedByMe)
    } ?: emptyList(),
    timelineName = timelineName,
    timelineOwnerName = timelineOwnerName
)

fun TimelineEvent.toPostInfo(): PostInfo = PostInfo(
    id = eventId,
    roomId = roomId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    isEdited = hasBeenEdited(),
    editTimestamp = annotations?.editSummary?.lastEditTs
)

private fun TimelineEvent.toPostContent(): PostContent =
    when (getPostContentType()) {
        PostContentType.TEXT_CONTENT -> toTextContent()
        PostContentType.IMAGE_CONTENT -> toMediaContent(MediaType.Image)
        PostContentType.VIDEO_CONTENT -> toMediaContent(MediaType.Video)
        PostContentType.POLL_CONTENT -> toPollContent()
        else -> toOtherEventContent()
    }