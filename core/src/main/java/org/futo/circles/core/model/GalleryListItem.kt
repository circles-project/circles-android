package org.futo.circles.core.model

import org.futo.circles.core.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.user.model.User

sealed class GalleryListItem : IdEntity<String>

data class GalleryHeaderItem(
    val titleRes: Int
) : GalleryListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val myGalleriesHeader = GalleryHeaderItem(R.string.my_galleries)
        val sharedGalleriesHeader = GalleryHeaderItem(R.string.shared_galleries)
    }
}

data class JoinedGalleryListItem(
    override val id: String,
    val info: RoomInfo,
    val roomOwner: User?
) : GalleryListItem() {
    fun isMyGallery(): Boolean =
        roomOwner?.let { it.userId == MatrixSessionProvider.currentSession?.myUserId } ?: true
}

data class GalleryInvitesNotificationListItem(
    val invitesCount: Int
) : GalleryListItem() {
    override val id: String = "GroupInvitesNotificationListItem"
}