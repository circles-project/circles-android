package org.futo.circles.mapping

import org.futo.circles.core.utils.UserUtils
import org.futo.circles.model.CirclesUserSummary
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.user.model.User

fun User.toUserListItem(isSelected: Boolean) = UserListItem(
    user = CirclesUserSummary(
        id = userId,
        name = notEmptyDisplayName(),
        avatarUrl = avatarUrl ?: ""
    ),
    isSelected = isSelected
)

fun User.toPeopleUserListItem(
    profileRoomId: String? = null,
    isFollowedByMe: Boolean = false,
    isIgnored: Boolean = false
) =
    PeopleUserListItem(
        user = CirclesUserSummary(
            id = userId,
            name = notEmptyDisplayName(),
            avatarUrl = avatarUrl ?: ""
        ),
        isIgnored = isIgnored,
        isFollowedByMe = isFollowedByMe,
        profileRoomId = profileRoomId
    )

fun User.notEmptyDisplayName(): String = getName(userId, displayName)

fun SenderInfo.notEmptyDisplayName(): String = getName(userId, displayName)

private fun getName(userId: String, displayName: String?): String {
    val name = displayName?.takeIf { it.isNotEmpty() }
        ?: userId.replace("@", "").substringBefore(":")
    return UserUtils.removeDomainSuffix(name)
}