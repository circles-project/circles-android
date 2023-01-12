package org.futo.circles.mapping

import org.futo.circles.core.utils.UserUtils
import org.futo.circles.model.*
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.user.model.User

fun User.toUserListItem(isSelected: Boolean) = UserListItem(
    user = toCirclesUserSummary(),
    isSelected = isSelected
)

fun User.toPeopleSuggestionUserListItem(
    isKnown: Boolean,
    profileRoomId: String?
) = PeopleSuggestionUserListItem(
    user = toCirclesUserSummary(),
    profileRoomId = profileRoomId,
    isKnown = isKnown
)

fun User.toPeopleIgnoredUserListItem() = PeopleIgnoredUserListItem(user = toCirclesUserSummary())

fun User.toPeopleRequestUserListItem() = PeopleRequestUserListItem(user = toCirclesUserSummary())

fun User.toPeopleFollowingUserListItem() =
    PeopleFollowingUserListItem(user = toCirclesUserSummary())

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