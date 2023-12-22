package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

enum class PeopleItemType { Header, Friend, Following, Follower, RequestNotification, Known, Suggestion }
sealed class PeopleListItem(
    open val type: PeopleItemType
) : IdEntity<String>

data class PeopleHeaderItem(
    val titleRes: Int
) : PeopleListItem(PeopleItemType.Header) {
    override val id: String = titleRes.toString()

    companion object {
        val friends = PeopleHeaderItem(org.futo.circles.auth.R.string.friends)
        val followersUsersHeader = PeopleHeaderItem(R.string.followers)
        val followingUsersHeader = PeopleHeaderItem(R.string.following)
        val knownUsersHeader = PeopleHeaderItem(R.string.known_users)
        val suggestions = PeopleHeaderItem(R.string.suggestions)
    }
}

class PeopleUserListItem(
    val user: CirclesUserSummary,
    override val type: PeopleItemType
) : PeopleListItem(type) {
    override val id: String = user.id
}

class PeopleRequestNotificationListItem(
    val requestsCount: Int
) : PeopleListItem(PeopleItemType.RequestNotification) {
    override val id: String = "PeopleRequestNotificationListItem"
}
