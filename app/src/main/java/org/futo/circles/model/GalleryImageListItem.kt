package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class GalleryImageListItem(
    override val id: String,
    val imageContent: ImageContent,
    val postInfo: PostInfo
) : IdEntity<String>