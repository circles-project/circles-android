package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.list.IdEntity

sealed class PeopleListItem : IdEntity<String>

data class PeopleHeaderItem(
    val titleRes: Int
) : PeopleListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val followingUsersHeader = PeopleHeaderItem(R.string.following_users)
        val knownUsersHeader = PeopleHeaderItem(R.string.known_users)
        val suggestions = PeopleHeaderItem(R.string.suggestions)
        val requests = PeopleHeaderItem(R.string.requests)
        val ignoredUsers = PeopleHeaderItem(R.string.ignored_users)
    }
}

data class PeopleSuggestionUserListItem(
    val user: CirclesUserSummary,
    val profileRoomId: String?,
    val isKnown: Boolean
) : PeopleListItem() {
    override val id: String = user.id
    fun canFollow() = profileRoomId != null
}

data class PeopleFollowingUserListItem(
    val user: CirclesUserSummary
) : PeopleListItem() {
    override val id: String = user.id
}

data class PeopleIgnoredUserListItem(
    val user: CirclesUserSummary
) : PeopleListItem() {
    override val id: String = user.id
}

data class PeopleRequestUserListItem(
    val user: CirclesUserSummary
) : PeopleListItem() {
    override val id: String = user.id
}