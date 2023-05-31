package org.futo.circles.gallery.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.core.model.RoomInfo

data class GalleryListItem(
    override val id: String,
    val info: RoomInfo
) : IdEntity<String>