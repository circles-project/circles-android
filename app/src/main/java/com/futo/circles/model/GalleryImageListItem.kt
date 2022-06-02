package com.futo.circles.model

import com.futo.circles.core.list.IdEntity

data class GalleryImageListItem(
    override val id: String,
    val imageContent: ImageContent,
    val postInfo: PostInfo
) : IdEntity<String>