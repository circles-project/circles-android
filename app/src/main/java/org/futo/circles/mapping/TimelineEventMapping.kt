package org.futo.circles.mapping

import org.futo.circles.model.*
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getRelationContent
import org.matrix.android.sdk.api.session.room.timeline.isReply


fun TimelineEvent.toPost(
    postContentType: PostContentType,
    isRepliesVisible: Boolean = false
): Post =
    if (isReply()) toReplyPost(postContentType) else toRootPost(postContentType, isRepliesVisible)

private fun TimelineEvent.toPostInfo(): PostInfo = PostInfo(
    id = eventId,
    roomId = roomId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    reactionsData = annotations?.reactionsSummary?.map {
        ReactionsData(it.key, it.count, it.addedByMe)
    } ?: emptyList()
)

private fun TimelineEvent.toRootPost(postContentType: PostContentType, isRepliesVisible: Boolean) =
    RootPost(
        postInfo = toPostInfo(),
        content = toPostContent(postContentType),
        isRepliesVisible = isRepliesVisible,
    )

private fun TimelineEvent.toReplyPost(postContentType: PostContentType) = ReplyPost(
    postInfo = toPostInfo(),
    content = toPostContent(postContentType),
    replyToId = getRelationContent()?.inReplyTo?.eventId ?: "",
)

private fun TimelineEvent.toPostContent(postContentType: PostContentType): PostContent =
    when (postContentType) {
        PostContentType.TEXT_CONTENT -> toTextContent()
        PostContentType.IMAGE_CONTENT -> toImageContent()
        PostContentType.VIDEO_CONTENT -> toVideoContent()
        PostContentType.POLL_CONTENT -> toPollContent()
    }