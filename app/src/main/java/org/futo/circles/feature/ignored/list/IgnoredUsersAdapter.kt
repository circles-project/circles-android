package org.futo.circles.feature.ignored.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.model.PeopleUserListItem

class IgnoredUsersAdapter(
    private val onUnIgnore: (String) -> Unit,
) : BaseRvAdapter<CirclesUserSummary, IgnoredUsersViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IgnoredUsersViewHolder {
        return IgnoredUsersViewHolder(
            parent,
            onUnIgnore = { position -> onUnIgnore(getItem(position).id) }
        )
    }

    override fun onBindViewHolder(holder: IgnoredUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}