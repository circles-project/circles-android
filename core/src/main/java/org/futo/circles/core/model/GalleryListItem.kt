package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class GalleryListItem(
    override val id: String,
    open val info: RoomInfo,
    open val membership: Membership
) : IdEntity<String>

data class JoinedGalleryListItem(
    override val id: String,
    override val info: RoomInfo
) : GalleryListItem(id, info, Membership.JOIN)

data class InvitedGalleryListItem(
    override val id: String,
    override val info: RoomInfo,
    val inviterName: String,
) : GalleryListItem(id, info, Membership.INVITE)