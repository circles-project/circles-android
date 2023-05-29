package org.futo.circles.mapping

import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.model.CirclesUserSummary
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.user.model.User

fun User.toUserListItem(isSelected: Boolean) = UserListItem(
    user = toCirclesUserSummary(),
    isSelected = isSelected
)

fun User.toPeopleUserListItem(type: PeopleItemType) =
    PeopleUserListItem(toCirclesUserSummary(), type)

fun User.toCirclesUserSummary() = CirclesUserSummary(
    id = userId,
    name = notEmptyDisplayName(),
    avatarUrl = avatarUrl ?: ""
)
