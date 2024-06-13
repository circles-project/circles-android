package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

sealed class PeopleListItem : IdEntity<String>

data class PeopleCategoryListItem(
    val titleRes: Int,
    val iconRes: Int,
    val typeArg: PeopleCategoryTypeArg,
    val count: Int = 0
) : PeopleListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val followersUsers = PeopleCategoryListItem(
            org.futo.circles.core.R.string.my_followers,
            org.futo.circles.core.R.drawable.ic_round_people,
            PeopleCategoryTypeArg.Followers
        )
        val followingUsers =
            PeopleCategoryListItem(
                org.futo.circles.core.R.string.people_i_m_following,
                org.futo.circles.core.R.drawable.ic_outline_people,
                PeopleCategoryTypeArg.Following
            )
        val others = PeopleCategoryListItem(
            org.futo.circles.core.R.string.other_known_users,
            R.drawable.ic_other_people,
            PeopleCategoryTypeArg.Other

        )
        val ignored = PeopleCategoryListItem(
            R.string.ignored_users,
            R.drawable.ic_ignore,
            PeopleCategoryTypeArg.Ignored
        )
    }
}

class PeopleUserListItem(
    val user: CirclesUserSummary,
    val isIgnored: Boolean
) : PeopleListItem() {
    override val id: String = user.id
}

class PeopleIgnoredUserListItem(
    val user: CirclesUserSummary
) : PeopleListItem() {
    override val id: String = user.id
}
