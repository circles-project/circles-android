package com.futo.circles.model

import com.futo.circles.R
import com.futo.circles.core.list.IdEntity
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
    val isOptionsOpened: Boolean
) : ManageMembersListItem() {
    override val id: String = user.id

    fun getRoleNameResId(): Int = when (role.value) {
        10 -> R.string.can_post
        50 -> R.string.moderator
        100 -> R.string.owner
        else -> R.string.can_view
    }
}

data class InvitedUserListItem(
    val user: CirclesUserSummary
) : ManageMembersListItem() {
    override val id: String = user.id
}