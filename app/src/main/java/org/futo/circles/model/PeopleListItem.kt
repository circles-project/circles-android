package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

enum class PeopleItemType { Header, Friend, Following, Follower, Request, Known, Suggestion, Ignored }
sealed class PeopleListItem(
    open val type: PeopleItemType
) : IdEntity<String>

data class PeopleHeaderItem(
    val titleRes: Int
) : PeopleListItem(PeopleItemType.Header) {
    override val id: String = titleRes.toString()

    companion object {
        val friends = PeopleHeaderItem(R.string.friends)
        val followersUsersHeader = PeopleHeaderItem(R.string.followers)
        val followingUsersHeader = PeopleHeaderItem(R.string.following)
        val knownUsersHeader = PeopleHeaderItem(R.string.known_users)
        val suggestions = PeopleHeaderItem(R.string.suggestions)
        val requests = PeopleHeaderItem(R.string.requests)
        val ignoredUsers = PeopleHeaderItem(R.string.ignored_users)
    }
}

class PeopleUserListItem(
    val user: CirclesUserSummary,
    override val type: PeopleItemType
) : PeopleListItem(type) {
    override val id: String = user.id
}
