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

sealed class PeopleUserListItem(
    open val user: CirclesUserSummary
) : PeopleListItem()

data class PeopleSuggestionUserListItem(
    override val user: CirclesUserSummary,
    val profileRoomId: String?,
    val isKnown: Boolean
) : PeopleUserListItem(user) {
    override val id: String = user.id
}

data class PeopleFollowingUserListItem(
    override val user: CirclesUserSummary
) : PeopleUserListItem(user) {
    override val id: String = user.id
}

data class PeopleIgnoredUserListItem(
    override val user: CirclesUserSummary
) : PeopleUserListItem(user) {
    override val id: String = user.id
}

data class PeopleRequestUserListItem(
    override val user: CirclesUserSummary
) : PeopleUserListItem(user) {
    override val id: String = user.id
}