package org.futo.circles.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.futo.circles.core.extensions.getPowerLevelContent
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.model.DmConnected
import org.futo.circles.core.model.DmHasInvite
import org.futo.circles.core.model.DmInviteSent
import org.futo.circles.core.model.DmNotFound
import org.futo.circles.core.model.DmRoomState
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.RoomCategoryFilter
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

const val roomType = "m.room"
const val spaceType = RoomType.SPACE

private val roomTypes = listOf(
    null,
    roomType,
    spaceType,
    GALLERY_TYPE,
    TIMELINE_TYPE,
    GROUP_TYPE
)

val circlesRoomsTypes = listOf(
    GALLERY_TYPE,
    TIMELINE_TYPE,
    GROUP_TYPE
)

fun getRoomsLiveDataWithType(
    type: String,
    membershipFilter: List<Membership> = Membership.activeMemberships()
) = MatrixSessionProvider.getSessionOrThrow().roomService()
    .getRoomSummariesLive(getCirclesRoomTypeFilter(type, membershipFilter))

fun getRoomsWithType(
    type: String,
    membershipFilter: List<Membership> = Membership.activeMemberships()
) = MatrixSessionProvider.getSessionOrThrow().roomService()
    .getRoomSummaries(getCirclesRoomTypeFilter(type, membershipFilter))

private fun getCirclesRoomTypeFilter(type: String, membershipFilter: List<Membership>) =
    roomSummaryQueryParams {
        memberships = membershipFilter
        includeType = listOf(type)
        excludeType = roomTypes - type
        roomCategoryFilter = RoomCategoryFilter.ONLY_ROOMS
    }

fun getGroupsLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    getRoomsLiveDataWithType(GROUP_TYPE, membershipFilter)

fun getGalleriesLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    getRoomsLiveDataWithType(GALLERY_TYPE, membershipFilter)

fun getGalleries(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    getRoomsWithType(GALLERY_TYPE, membershipFilter)

fun getTimelinesLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    getRoomsLiveDataWithType(TIMELINE_TYPE, membershipFilter)

fun getAllDirectMessagesLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    MatrixSessionProvider.getSessionOrThrow().roomService()
        .getRoomSummariesLive(roomSummaryQueryParams {
            memberships = membershipFilter
            roomCategoryFilter = RoomCategoryFilter.ONLY_DM
        })

fun getUserDirectMessagesRoomLiveData(
    userId: String,
    membershipFilter: List<Membership> = Membership.activeMemberships()
): LiveData<RoomSummary?> {
    val roomService = MatrixSessionProvider.getSessionOrThrow().roomService()

    return roomService.getRoomSummariesLive(roomSummaryQueryParams {
        memberships = membershipFilter
        roomCategoryFilter = RoomCategoryFilter.ONLY_DM
    }).map { summaries -> filterActiveDmRoom(userId, summaries) }
}

private fun filterActiveDmRoom(userId: String, summaries: List<RoomSummary>): RoomSummary? {
    val roomService = MatrixSessionProvider.getSessionOrThrow().roomService()
    return summaries.firstOrNull { it.directUserId == userId && (it.joinedMembersCount ?: 0) > 1 }
        ?.let {
            summaries.firstOrNull {
                it.directUserId == userId &&
                        roomService.getRoom(it.roomId)?.membershipService()
                            ?.getRoomMember(it.directUserId ?: "")?.membership?.isActive() == true
            }
        }
}


fun getUserDirectMessagesStateLiveData(
    userId: String,
    membershipFilter: List<Membership> = Membership.activeMemberships()
): LiveData<DmRoomState> =
    getUserDirectMessagesRoomLiveData(userId, membershipFilter).map { directRoomSummary ->
        directRoomSummary?.let {
            if (it.membership == Membership.JOIN) {
                if ((it.joinedMembersCount ?: 0) > 1) DmConnected(it.roomId)
                else DmInviteSent
            } else {
                DmHasInvite(it.roomId)
            }
        } ?: DmNotFound
    }

private fun getAllRoomsAndSpacesFilter(membershipFilter: List<Membership>) =
    roomSummaryQueryParams {
        excludeType = listOf(roomType, null)
        memberships = membershipFilter
    }

fun getAllJoinedCirclesRoomsAndSpaces(session: Session = MatrixSessionProvider.getSessionOrThrow()) =
    session.roomService().getRoomSummaries(getAllRoomsAndSpacesFilter(listOf(Membership.JOIN)))


private fun getAllRoomsFiler(membershipFilter: List<Membership>) = roomSummaryQueryParams {
    excludeType = listOf(roomType, spaceType, null)
    memberships = membershipFilter
}

fun getAllRoomsLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    MatrixSessionProvider.getSessionOrThrow().roomService()
        .getRoomSummariesLive(getAllRoomsFiler(membershipFilter))

fun getAllRooms(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    MatrixSessionProvider.getSessionOrThrow().roomService()
        .getRoomSummaries(getAllRoomsFiler(membershipFilter))

fun getKnocksCount(roomId: String): Int {
    if (getPowerLevelContent(roomId)?.isCurrentUserAbleToInvite() == false) return 0
    return MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
        ?.getRoomMembers(
            roomMemberQueryParams {
                excludeSelf = true
                memberships = listOf(Membership.KNOCK)
            }
        )?.size ?: 0
}


