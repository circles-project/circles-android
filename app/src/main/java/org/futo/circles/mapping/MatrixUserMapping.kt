package org.futo.circles.mapping

import org.futo.circles.model.CirclesUserSummary
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.user.model.User

fun User.toUserListItem(isSelected: Boolean) = UserListItem(
    user = CirclesUserSummary(
        id = userId,
        name = notEmptyDisplayName(),
        avatarUrl = avatarUrl ?: ""
    ),
    isSelected = isSelected
)

fun User.toPeopleUserListItem(isIgnored: Boolean) = PeopleUserListItem(
    user = CirclesUserSummary(
        id = userId,
        name = notEmptyDisplayName(),
        avatarUrl = avatarUrl ?: ""
    ),
    isIgnored = isIgnored
)

fun User.notEmptyDisplayName() =
    displayName?.takeIf { it.isNotEmpty() }
        ?: userId.replace("@", "").substringBefore(":")