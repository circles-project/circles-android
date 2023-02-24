package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class GalleryListItem(
    override val id: String,
    private val info: RoomInfo
) : IdEntity<String>