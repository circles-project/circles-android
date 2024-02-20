package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

enum class PeopleItemType { Category, Connection, Following, Follower, RequestNotification, Others, Suggestion }
sealed class PeopleListItem(
    open val type: PeopleItemType
) : IdEntity<String>

data class PeopleCategoryListItem(
    val titleRes: Int,
    val iconRes: Int,
    val count: Int = 0
) : PeopleListItem(PeopleItemType.Category) {
    override val id: String = titleRes.toString()

    companion object {
        val connections = PeopleCategoryListItem(
            R.string.my_connections,
            R.drawable.ic_connection
        )
        val followersUsers = PeopleCategoryListItem(
            org.futo.circles.core.R.string.my_followers,
            org.futo.circles.core.R.drawable.ic_round_people
        )
        val followingUsers =
            PeopleCategoryListItem(
                org.futo.circles.core.R.string.people_i_m_following,
                org.futo.circles.core.R.drawable.ic_outline_people
            )
        val others = PeopleCategoryListItem(
            org.futo.circles.core.R.string.other_known_users,
            R.drawable.ic_other_people

        )
        val ignored = PeopleCategoryListItem(
            R.string.ignored_users,
            R.drawable.ic_ignore
        )
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
