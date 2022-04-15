package com.futo.circles.model

import com.futo.circles.core.list.IdEntity
import com.futo.circles.extensions.isCurrentUserAbleToBan
import com.futo.circles.extensions.isCurrentUserAbleToChangeSettings
import com.futo.circles.extensions.isCurrentUserAbleToInvite
import com.futo.circles.provider.MatrixSessionProvider
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
            powerLevelsContent.isCurrentUserAbleToInvite()) && !isMyUser

}

data class InvitedUserListItem(
    val user: CirclesUserSummary,
    val powerLevelsContent: PowerLevelsContent
) : ManageMembersListItem() {
    override val id: String = user.id
}