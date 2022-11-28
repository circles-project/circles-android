package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.extensions.getCurrentUserPowerLevel
import org.futo.circles.extensions.isCurrentUserAbleToBan
import org.futo.circles.extensions.isCurrentUserAbleToChangeSettings
import org.futo.circles.extensions.isCurrentUserAbleToKick
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

sealed class ManageMembersListItem : IdEntity<String>

data class ManageMembersHeaderListItem(
    val name: String
) : ManageMembersListItem() {
    override val id: String = name
}

data class GroupMemberListItem(
    val user: CirclesUserSummary,
    val role: Role,
    val powerLevelsContent: PowerLevelsContent,
    val isOptionsOpened: Boolean
) : ManageMembersListItem() {
    override val id: String = user.id

    private val isMyUser = MatrixSessionProvider.currentSession?.myUserId == user.id

    val isOptionsAvailable = (powerLevelsContent.isCurrentUserAbleToChangeSettings() ||
            powerLevelsContent.isCurrentUserAbleToBan() ||
            powerLevelsContent.isCurrentUserAbleToKick()) &&
            !isMyUser &&
            powerLevelsContent.getCurrentUserPowerLevel() > role.value

}

data class NotJoinedUserListItem(
    val user: CirclesUserSummary,
    val powerLevelsContent: PowerLevelsContent,
    val membership: Membership
) : ManageMembersListItem() {
    override val id: String = user.id
}