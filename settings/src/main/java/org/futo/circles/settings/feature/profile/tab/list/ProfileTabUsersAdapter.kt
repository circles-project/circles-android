package org.futo.circles.settings.feature.profile.tab.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CirclesUserSummary


class ProfileTabUsersAdapter(
    private val onUserClicked: (String) -> Unit
) : BaseRvAdapter<CirclesUserSummary, ProfileTabUsersViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileTabUsersViewHolder {
        return ProfileTabUsersViewHolder(
            parent,
            onUserClicked = { position -> onUserClicked(getItem(position).id) }
        )
    }

    override fun onBindViewHolder(holder: ProfileTabUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}