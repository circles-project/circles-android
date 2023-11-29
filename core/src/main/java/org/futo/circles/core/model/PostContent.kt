package org.futo.circles.core.model

import android.util.Size
import org.matrix.android.sdk.api.session.room.model.message.MessageType

enum class PostContentType(val typeKey: String) {
    TEXT_CONTENT(MessageType.MSGTYPE_TEXT),
    IMAGE_CONTENT(MessageType.MSGTYPE_IMAGE),
    VIDEO_CONTENT(MessageType.MSGTYPE_VIDEO),
    POLL_CONTENT(MessageType.MSGTYPE_POLL_START)
}

sealed class PostContent(open val type: PostContentType) {
    fun isMedia(): Boolean =
        type == PostContentType.IMAGE_CONTENT || type == PostContentType.VIDEO_CONTENT

    fun isPoll(): Boolean = type == PostContentType.POLL_CONTENT
}

data class TextContent(
    val message: CharSequence
) : PostContent(PostContentType.TEXT_CONTENT)

data class MediaContent(
    override val type: PostContentType,
    val caption: CharSequence?,
    val mediaFileData: MediaFileData,
    val thumbnailFileData: MediaFileData?,
    val thumbHash: String?
) : PostContent(type) {

    fun thumbnailOrFullSize(width: Int) = thumbnailFileData?.let {
        Size(width, (width / it.aspectRatio).toInt())
    } ?: Size(width, (width / mediaFileData.aspectRatio).toInt())


    fun getMediaType(): MediaType =
        if (type == PostContentType.VIDEO_CONTENT) MediaType.Video else MediaType.Image
}

data class PollContent(
    val question: String,
    val state: PollState,
    val totalVotes: Int,
    val options: List<PollOption>,
    val isClosedType: Boolean
) : PostContent(PostContentType.POLL_CONTENT)

enum class PollState { Sending, Ready, Voted, Ended }

fun PollState.canEdit() = this == PollState.Sending || this == PollState.Ready

fun PollState.canVote() =
    this != PollState.Sending && this != PollState.Ended && this != PollState.Voted

data class PollOption(
    val optionId: String,
    val optionAnswer: String,
    val voteCount: Int,
    val voteProgress: Int,
    val isMyVote: Boolean,
    val isWinner: Boolean
)