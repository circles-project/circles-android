package org.futo.circles.feature.people.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleUserListItem

private enum class PeopleListViewType { Header, User }

class PeopleAdapter(
    private val onUserClicked: (PeopleUserListItem) -> Unit,
    private val onFollow: (PeopleUserListItem) -> Unit
) : BaseRvAdapter<PeopleListItem, PeopleViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PeopleHeaderItem -> PeopleListViewType.Header.ordinal
        is PeopleUserListItem -> PeopleListViewType.User.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return when (PeopleListViewType.values()[viewType]) {
            PeopleListViewType.Header -> PeopleHeaderViewHolder(parent)
            PeopleListViewType.User -> PeopleUserViewHolder(
                parent,
                onUserClicked = { position -> onUserClicked(getItem(position) as PeopleUserListItem) },
                onFollow = { position -> onFollow(getItem(position) as PeopleUserListItem) }
            )
        }
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}