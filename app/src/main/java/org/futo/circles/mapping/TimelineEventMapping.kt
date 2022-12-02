package org.futo.circles.mapping

import android.content.Context
import org.futo.circles.core.picker.MediaType
import org.futo.circles.extensions.getReadByCountForEvent
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getRelationContent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited
import org.matrix.android.sdk.api.session.room.timeline.isReply

fun TimelineEvent.toPost(
    context: Context,
    postContentType: PostContentType,
    lastReadEventTime: Long = 0L,
    isRepliesVisible: Boolean = false
): Post =
    if (isReply()) toReplyPost(postContentType, lastReadEventTime, context)
    else toRootPost(postContentType, isRepliesVisible, lastReadEventTime, context)

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

private fun TimelineEvent.toRootPost(
    postContentType: PostContentType,
    isRepliesVisible: Boolean,
    lastReadEventTime: Long,
    context: Context
) =
    RootPost(
        postInfo = toPostInfo(),
        content = toPostContent(postContentType, context),
        isRepliesVisible = isRepliesVisible,
        sendState = root.sendState,
        readInfo = toReadInfo(lastReadEventTime)
    )

private fun TimelineEvent.toReplyPost(
    postContentType: PostContentType,
    lastReadEventTime: Long,
    context: Context
) =
    ReplyPost(
        postInfo = toPostInfo(),
        content = toPostContent(postContentType, context),
        replyToId = getRelationContent()?.inReplyTo?.eventId ?: "",
        readInfo = toReadInfo(lastReadEventTime),
        sendState = root.sendState
    )

private fun TimelineEvent.toPostContent(
    postContentType: PostContentType,
    context: Context
): PostContent =
    when (postContentType) {
        PostContentType.TEXT_CONTENT -> toTextContent(context)
        PostContentType.IMAGE_CONTENT -> toMediaContent(MediaType.Image, context)
        PostContentType.VIDEO_CONTENT -> toMediaContent(MediaType.Video, context)
        PostContentType.POLL_CONTENT -> toPollContent()
    }