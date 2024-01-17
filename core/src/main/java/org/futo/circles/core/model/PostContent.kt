package org.futo.circles.core.model

import android.text.Spanned
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
    val message: String,
    val messageSpanned: Spanned
) : PostContent(PostContentType.TEXT_CONTENT) {

    // to optimize payload calculation (spanned==spanned will return false and trigger unnecessary list update)
    override fun equals(other: Any?): Boolean = this.message == (other as? TextContent)?.message

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + messageSpanned.hashCode()
        return result
    }
}

data class MediaContent(
    override val type: PostContentType,
    val caption: String?,
    val captionSpanned: Spanned?,
    val mediaFileData: MediaFileData,
    val thumbnailFileData: MediaFileData?,
    val thumbHash: String?
) : PostContent(type) {

    override fun equals(other: Any?): Boolean =
        this.type == (other as? MediaContent)?.type &&
                this.caption == (other as? MediaContent)?.caption &&
                this.mediaFileData == (other as? MediaContent)?.mediaFileData &&
                this.thumbnailFileData == (other as? MediaContent)?.thumbnailFileData &&
                this.thumbHash == (other as? MediaContent)?.thumbHash


    fun thumbnailOrFullSize(width: Int) = thumbnailFileData?.let {
        Size(width, (width / it.aspectRatio).toInt())
    } ?: Size(width, (width / mediaFileData.aspectRatio).toInt())


    fun getMediaType(): MediaType =
        if (type == PostContentType.VIDEO_CONTENT) MediaType.Video else MediaType.Image

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (caption?.hashCode() ?: 0)
        result = 31 * result + (captionSpanned?.hashCode() ?: 0)
        result = 31 * result + mediaFileData.hashCode()
        result = 31 * result + (thumbnailFileData?.hashCode() ?: 0)
        result = 31 * result + (thumbHash?.hashCode() ?: 0)
        return result
    }
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

fun PollState.canVote() = this != PollState.Sending && this != PollState.Ended

data class PollOption(
    val optionId: String,
    val optionAnswer: String,
    val voteCount: Int,
    val voteProgress: Int,
    val isMyVote: Boolean,
    val isWinner: Boolean
)