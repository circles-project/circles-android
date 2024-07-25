package org.futo.circles.core.model


sealed class PostContent(open val type: PostContentType) {
    fun isMedia(): Boolean =
        type == PostContentType.IMAGE_CONTENT || type == PostContentType.VIDEO_CONTENT

    fun isPoll(): Boolean = type == PostContentType.POLL_CONTENT

    fun isText(): Boolean = type == PostContentType.TEXT_CONTENT
}









