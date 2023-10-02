package org.futo.circles.gallery.feature

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.mapping.toInvitedGalleryListItem
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.RequestGalleryListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class PhotosDataSource @Inject constructor() {

    fun getGalleriesFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams()).asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { filterGalleries(roomSummaries) }
    }.distinctUntilChanged()


    private fun filterGalleries(list: List<RoomSummary>): List<GalleryListItem> {
        val groups = list.filter { it.roomType == GALLERY_TYPE }
        val joined = groups.mapNotNull { it.takeIf { it.membership == Membership.JOIN } }
        val invites = groups.mapNotNull { it.takeIf { it.membership == Membership.INVITE } }
        val knocks = getKnockRequestToJoinedGroups(joined)
        return mutableListOf<GalleryListItem>().apply {
            addAll(knocks)
            addAll(invites.map { it.toInvitedGalleryListItem() })
            addAll(joined.map { it.toJoinedGalleryListItem() })
        }
    }

    private fun getKnockRequestToJoinedGroups(joined: List<RoomSummary>): List<RequestGalleryListItem> {
        val requests = mutableListOf<RequestGalleryListItem>()

        joined.forEach { groupSummary ->
            val group =
                MatrixSessionProvider.currentSession?.getRoom(groupSummary.roomId) ?: return@forEach

            val knockingMembers =
                group.membershipService().getRoomMembers(roomMemberQueryParams {
                    memberships = listOf(Membership.KNOCK)
                }).takeIf { it.isNotEmpty() } ?: return@forEach


            knockingMembers.forEach { user ->
                requests.add(
                    RequestGalleryListItem(
                        id = groupSummary.roomId,
                        info = groupSummary.toRoomInfo(),
                        requesterName = user.displayName
                            ?: UserUtils.removeDomainSuffix(user.userId),
                        requesterId = user.userId
                    )
                )
            }
        }
        return requests
    }
}
