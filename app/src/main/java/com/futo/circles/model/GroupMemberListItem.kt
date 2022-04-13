package com.futo.circles.model

import com.futo.circles.R
import com.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.powerlevels.Role

data class GroupMemberListItem(
    val user: CirclesUserSummary,
    val role: Role,
    val hasPendingInvitation: Boolean,
    val isOptionsOpened: Boolean
) : IdEntity<String> {
    override val id: String = user.id

    fun getRoleNameResId(): Int = if (hasPendingInvitation) R.string.pending_invitation else
        when (role) {
            Role.Admin -> R.string.admin
            Role.Moderator -> R.string.moderator
            else -> R.string.user
        }
}