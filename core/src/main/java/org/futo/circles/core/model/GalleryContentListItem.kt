package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity

data class GalleryContentListItem(
    override val id: String,
    val postInfo: PostInfo,
    val mediaContent: MediaContent,
    val isSelected: Boolean = false
) : IdEntity<String>