package org.futo.circles.core.utils

import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.RoomCategoryFilter
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

const val roomType = "m.room"
const val spaceType = RoomType.SPACE

private val roomTypes = listOf(
    roomType,
    spaceType,
    GALLERY_TYPE,
    TIMELINE_TYPE,
    GROUP_TYPE
)

private fun getRoomsLiveDataWithType(
    type: String,
    membershipFilter: List<Membership> = Membership.activeMemberships()
) = MatrixSessionProvider.getSessionOrThrow().roomService()
    .getRoomSummariesLive(getCirclesRoomTypeFilter(type, membershipFilter))

private fun getRoomsWithType(
    type: String,
    membershipFilter: List<Membership> = Membership.activeMemberships()
) = MatrixSessionProvider.getSessionOrThrow().roomService()
    .getRoomSummaries(getCirclesRoomTypeFilter(type, membershipFilter))

private fun getCirclesRoomTypeFilter(type:String, membershipFilter: List<Membership>) =
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

fun getSpacesLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    getRoomsLiveDataWithType(spaceType, membershipFilter)

fun getTimelinesLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    getRoomsLiveDataWithType(TIMELINE_TYPE, membershipFilter)


