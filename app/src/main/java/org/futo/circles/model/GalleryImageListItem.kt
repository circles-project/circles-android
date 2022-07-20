package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

sealed class GalleryContentListItem(
    override val id: String,
    open val postInfo: PostInfo,
    val type: PostContentType
) : IdEntity<String>

data class GalleryImageListItem(
    override val id: String,
    val imageContent: ImageContent,
    override val postInfo: PostInfo
) : GalleryContentListItem(id, postInfo, imageContent.type)

data class GalleryVideoListItem(
    override val id: String,
    val videoContent: VideoContent,
    override val postInfo: PostInfo
) : GalleryContentListItem(id, postInfo, videoContent.type)