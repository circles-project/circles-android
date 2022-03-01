package com.futo.circles.model

import com.futo.circles.R
import com.futo.circles.base.IdEntity

sealed class InviteMemberListItem : IdEntity<String>

data class HeaderItem(
    val titleRes: Int
) : InviteMemberListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val knownUsersHeader = HeaderItem(R.string.known_users)
        val suggestionHeader = HeaderItem(R.string.suggestion)
    }
}

data class CirclesUser(
    override val id: String,
    val name: String,
    val avatarUrl: String,
    val isSelected: Boolean = false
) : InviteMemberListItem()

data class NoResultsItem(
    val titleRes: Int = R.string.no_results
) : InviteMemberListItem() {
    override val id: String = titleRes.toString()
}