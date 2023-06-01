package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.core.model.MediaContent

data class GalleryContentListItem(
    override val id: String,
    val postInfo: PostInfo,
    val mediaContent: MediaContent
) : IdEntity<String>