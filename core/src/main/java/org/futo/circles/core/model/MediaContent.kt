package org.futo.circles.core.model

import android.text.Spanned
import android.util.Size
import org.futo.circles.core.utils.MediaUtils

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


    fun calculateThumbnailSize(viewWidth: Int): Size {
        val data = thumbnailFileData ?: mediaFileData
        return MediaUtils.getThumbSizeWithLimits(viewWidth, Size(data.width, data.height))
    }

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