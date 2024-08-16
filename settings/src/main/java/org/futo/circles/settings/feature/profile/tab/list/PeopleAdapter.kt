package org.futo.circles.settings.feature.profile.tab.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.settings.model.PeopleHeaderItem
import org.futo.circles.settings.model.PeopleListItem
import org.futo.circles.settings.model.PeopleUserListItem

enum class PeopleItemViewType { Header, User }

class PeopleAdapter(
    private val onUserClicked: (String) -> Unit
) : BaseRvAdapter<PeopleListItem, PeopleViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PeopleHeaderItem -> PeopleItemViewType.Header.ordinal
        is PeopleUserListItem -> PeopleItemViewType.User.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return when (PeopleItemViewType.entries[viewType]) {
            PeopleItemViewType.Header -> PeopleHeaderViewHolder(parent = parent)

            PeopleItemViewType.User -> PeopleUserViewHolder(
                parent,
                onUserClicked = { position -> onUserClicked(getItem(position).id) }
            )
        }
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}