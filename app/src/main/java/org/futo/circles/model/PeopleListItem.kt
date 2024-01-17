package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

enum class PeopleItemType { Header, Connection, Following, Follower, RequestNotification, Others, Suggestion }
sealed class PeopleListItem(
    open val type: PeopleItemType
) : IdEntity<String>

data class PeopleHeaderItem(
    val titleRes: Int
) : PeopleListItem(PeopleItemType.Header) {
    override val id: String = titleRes.toString()

    companion object {
        val connections = PeopleHeaderItem(R.string.my_connections)
        val followersUsersHeader = PeopleHeaderItem(org.futo.circles.core.R.string.my_followers)
        val followingUsersHeader =
            PeopleHeaderItem(org.futo.circles.core.R.string.people_i_m_following)
        val othersHeader = PeopleHeaderItem(org.futo.circles.core.R.string.others)
        val suggestions = PeopleHeaderItem(R.string.suggestions)
    }
}

class PeopleUserListItem(
    val user: CirclesUserSummary,
    override val type: PeopleItemType,
    val isIgnored: Boolean
) : PeopleListItem(type) {
    override val id: String = user.id
}

class PeopleRequestNotificationListItem(
    val requestsCount: Int
) : PeopleListItem(PeopleItemType.RequestNotification) {
    override val id: String = "PeopleRequestNotificationListItem"
}
