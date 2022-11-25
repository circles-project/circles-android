package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class GalleryContentListItem(
    override val id: String,
    val postInfo: PostInfo,
    val mediaContent: MediaContent
) : IdEntity<String>