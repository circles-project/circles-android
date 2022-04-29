package com.futo.circles.mapping

import com.bumptech.glide.request.target.Target
import com.futo.circles.model.*
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.model.message.getFileUrl
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getRelationContent
import org.matrix.android.sdk.api.session.room.timeline.isReply
import org.matrix.android.sdk.internal.crypto.attachments.toElementToDecrypt


fun TimelineEvent.toPost(
    postContentType: PostContentType,
    isRepliesVisible: Boolean = false
): Post =
    if (isReply()) toReplyPost(postContentType) else toRootPost(postContentType, isRepliesVisible)

private fun TimelineEvent.toPostInfo(): PostInfo = PostInfo(
    id = eventId,
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
    }

private fun TimelineEvent.toTextContent(): TextContent = TextContent(
    message = root.getClearContent().toModel<MessageTextContent>()?.body ?: ""
)

private fun TimelineEvent.toImageContent(): ImageContent {
    val messageContent = root.getClearContent().toModel<MessageImageContent>()

    return ImageContent(
        fileName = messageContent?.body ?: "",
        mimeType = messageContent?.mimeType ?: "",
        fileUrl = messageContent?.getFileUrl() ?: "",
        thumbnailUrl = messageContent?.info?.thumbnailFile?.url ?: "",
        width = messageContent?.info?.width ?: Target.SIZE_ORIGINAL,
        height = messageContent?.info?.height ?: Target.SIZE_ORIGINAL,
        elementToDecrypt = messageContent?.encryptedFileInfo?.toElementToDecrypt(),
    )
}
