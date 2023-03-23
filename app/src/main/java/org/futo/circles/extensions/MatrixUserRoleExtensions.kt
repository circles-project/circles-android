package org.futo.circles.extensions

import org.futo.circles.R
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getStateEvent
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.powerlevels.Role

fun Role.getRoleNameResId(): Int = when (this) {
    Role.Admin -> R.string.admin
    Role.Moderator -> R.string.moderator
    Role.Default -> R.string.user
    else -> R.string.read_only
}

fun PowerLevelsContent.isCurrentUserAbleToPost(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAllowedToSend(userId, false, EventType.MESSAGE)
}

fun PowerLevelsContent.isCurrentUserOnlyAdmin(roomId: String): Boolean {
    val isAdmin = isCurrentUserAdmin()
    val roomOwnersCount = getRoomOwners(roomId).size
    return isAdmin && roomOwnersCount == 1
}

fun PowerLevelsContent.isCurrentUserAdmin(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).getUserRole(userId) == Role.Admin
}

fun PowerLevelsContent.isCurrentUserAbleToInvite(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToInvite(userId)
}

fun PowerLevelsContent.isCurrentUserAbleToChangeSettings(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToRedact(userId)
}

fun PowerLevelsContent.isCurrentUserAbleToChangeLevelFor(otherUserId: String): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    val helper = PowerLevelsHelper(this)
    val myAccessLevel = helper.getUserPowerLevelValue(userId)
    val otherUserLevel = helper.getUserPowerLevelValue(otherUserId)
    return myAccessLevel >= otherUserLevel && myAccessLevel != Role.Default.value
}

fun PowerLevelsContent.isCurrentUserAbleToBan(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToBan(userId)
}

fun PowerLevelsContent.isCurrentUserAbleToKick(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToKick(userId)
}

fun PowerLevelsContent.getUserPowerLevel(userId: String): Int {
    return PowerLevelsHelper(this).getUserPowerLevelValue(userId)
}

fun PowerLevelsContent.getCurrentUserPowerLevel(): Int {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return Role.Default.value
    return PowerLevelsHelper(this).getUserPowerLevelValue(userId)
}

fun getPowerLevelContent(roomId: String): PowerLevelsContent? {
    val session = MatrixSessionProvider.currentSession ?: return null
    val room = session.getRoom(roomId) ?: return null
    return room.getStateEvent(
        EventType.STATE_ROOM_POWER_LEVELS,
        QueryStringValue.IsEmpty
    )?.content.toModel<PowerLevelsContent>()
}

fun getCurrentUserPowerLevel(roomId: String): Int {
    val session = MatrixSessionProvider.currentSession ?: return Role.Default.value
    val powerLevelsContent = getPowerLevelContent(roomId) ?: return Role.Default.value
    return PowerLevelsHelper(powerLevelsContent).getUserPowerLevelValue(session.myUserId)
}

fun getRoomOwners(roomId: String): List<RoomMemberSummary> {
    val room = MatrixSessionProvider.currentSession?.getRoom(roomId) ?: return emptyList()
    val powerLevelsContent = getPowerLevelContent(roomId) ?: return emptyList()
    return room.membershipService().getRoomMembers(roomMemberQueryParams())
        .filter { roomMemberSummary ->
            powerLevelsContent.getUserPowerLevel(roomMemberSummary.userId) == Role.Admin.value &&
                    roomMemberSummary.membership.isActive()
        }
}