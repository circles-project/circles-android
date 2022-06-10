package com.futo.circles.mapping

import com.futo.circles.model.CirclesUserSummary
import com.futo.circles.model.PeopleUserListItem
import com.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.user.model.User

fun User.toUserListItem(isSelected: Boolean) = UserListItem(
    user = CirclesUserSummary(
        id = userId,
        name = displayName ?: userId,
        avatarUrl = avatarUrl ?: ""
    ),
    isSelected = isSelected
)

fun User.toPeopleUserListItem(isIgnored: Boolean) = PeopleUserListItem(
    user = CirclesUserSummary(
        id = userId,
        name = displayName ?: userId,
        avatarUrl = avatarUrl ?: ""
    ),
    isIgnored = isIgnored
)