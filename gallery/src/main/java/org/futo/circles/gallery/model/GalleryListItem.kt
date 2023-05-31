package org.futo.circles.gallery.model

import org.futo.circles.core.list.IdEntity

data class GalleryListItem(
    override val id: String,
    val info: RoomInfo
) : IdEntity<String>