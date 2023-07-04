package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.model.UserListItem
import org.matrix.android.sdk.api.session.user.model.User

fun User.toUserListItem(isSelected: Boolean) = UserListItem(
    user = toCirclesUserSummary(),
    isSelected = isSelected
)

fun User.toCirclesUserSummary() = CirclesUserSummary(
    id = userId,
    name = notEmptyDisplayName(),
    avatarUrl = avatarUrl ?: ""
)