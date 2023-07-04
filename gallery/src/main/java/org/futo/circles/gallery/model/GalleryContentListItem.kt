package org.futo.circles.gallery.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PostInfo

data class GalleryContentListItem(
    override val id: String,
    val postInfo: PostInfo,
    val mediaContent: MediaContent
) : IdEntity<String>