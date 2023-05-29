package org.futo.circles.mapping

import org.futo.circles.model.CirclesUserSummary
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
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

fun User.notEmptyDisplayName(): String = getName(userId, displayName)

fun SenderInfo.notEmptyDisplayName(): String = getName(userId, displayName)

private fun getName(userId: String, displayName: String?): String {
    val name = displayName?.takeIf { it.isNotEmpty() }
        ?: userId.replace("@", "").substringBefore(":")
    return UserUtils.removeDomainSuffix(name)
}