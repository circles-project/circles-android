package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity

sealed class GalleryListItem(
    override val id: String
) : IdEntity<String>

data class JoinedGalleryListItem(
    override val id: String,
    val info: RoomInfo
) : GalleryListItem(id)

data class GalleryInvitesNotificationListItem(
    val invitesCount: Int
) : GalleryListItem("GroupInvitesNotificationListItem")