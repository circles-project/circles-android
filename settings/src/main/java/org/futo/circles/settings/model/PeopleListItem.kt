package org.futo.circles.settings.model

import org.futo.circles.core.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary

sealed class PeopleListItem : IdEntity<String>

data class PeopleHeaderItem(
    val titleRes: Int
) : PeopleListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val followingHeader = PeopleHeaderItem(R.string.people_i_m_following)
        val followersHeader = PeopleHeaderItem(R.string.my_followers)
        val suggestionsHeader = PeopleHeaderItem(R.string.other_known_users)
    }
}

class PeopleUserListItem(
    val user: CirclesUserSummary
) : PeopleListItem() {
    override val id: String = user.id
}
