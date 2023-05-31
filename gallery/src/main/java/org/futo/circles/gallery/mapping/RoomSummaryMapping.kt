package org.futo.circles.gallery.mapping

import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.gallery.model.GalleryListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toGalleryListItem() = GalleryListItem(
    id = roomId,
    info = toRoomInfo()
)